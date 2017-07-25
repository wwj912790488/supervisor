package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.exception.server.NoServerAvailableException;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.server.ServerFunction;
import com.arcsoft.supervisor.model.domain.server.ServerType;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.system.SystemContextListener;
import com.arcsoft.supervisor.service.task.ServerLoadBalance;
import com.arcsoft.supervisor.service.task.TaskDispatcherFacade;
import com.arcsoft.supervisor.utils.NamedThreadFactory;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Restart <tt>channel</tt> task after context is initialized.
 *
 * @author zw.
 */
@Service
public class TaskInitializer extends ServiceSupport implements SystemContextListener {

    /**
     * A thread pool to restart channel task asynchronous.
     */
    private ExecutorService pool;

    @Autowired
    private TaskDispatcherFacade dispatcherFacade;

    @Autowired
    private ServerLoadBalance loadBalance;

    @Autowired
    private TaskRepository repository;


    @Override
    public void contextInit() {
        if (SystemUtils.IS_OS_LINUX) {
            pool = Executors.newSingleThreadExecutor(NamedThreadFactory.create("TaskInitializer"));
            pool.execute(new TaskRestartWorker());
        }
    }

    @Override
    public void contextDestory() {
        if (SystemUtils.IS_OS_LINUX) {
            if (!pool.isShutdown()) {
                pool.shutdown();
            }
        }
    }
    /**
     * A worker to restart task after system is initialized.
     */
    private class TaskRestartWorker implements Runnable {

        final String noServerAvailableFormat = "Failed to do restart channel task [id=%s] " +
                "Can't find any available server which will retry later";

        final String failedFormat = "Failed to restart channel task [id=%s]";

        @Override
        public void run() {
            List<Task> needRestartTasks = repository.findByTypeAndStatus/*IsNotNull*/(TaskType.RTSP.getType(), TaskStatus.RUNNING.toString());
            while (!needRestartTasks.isEmpty()) {

                Server server = loadBalance.getServer(ServerType.AGENT, ServerFunction.ENCODER);
                if (server != null) {
                    for (Iterator<Task> taskIterator = needRestartTasks.iterator(); taskIterator.hasNext(); ) {
                        Task task = taskIterator.next();
                        try {
                            dispatcherFacade.restartChannelTask(task.getReferenceId());
                            taskIterator.remove();
                        } catch (NoServerAvailableException e) {
                            logger.warn(String.format(noServerAvailableFormat, task.getId()));
                        } catch (Exception e) {
                            logger.error(String.format(failedFormat, task.getId()), e);
                            taskIterator.remove();
                        }
                    }
                }

                if (needRestartTasks.isEmpty()) {
                    break;
                }

                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Task Initializer exit with interrupt.");
                    break;
                }
            }

            pool.shutdown();
        }
    }

}
