package com.arcsoft.supervisor.service.graphic.impl;

import com.arcsoft.supervisor.commons.spring.event.EventReceiver;
import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.graphic.Wall;
import com.arcsoft.supervisor.model.domain.graphic.WallPosition;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.server.ServerComponent;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.dto.graphic.PositionWebBean;
import com.arcsoft.supervisor.model.dto.graphic.WallWebBean;
import com.arcsoft.supervisor.model.dto.rest.wall.RootWallBean;
import com.arcsoft.supervisor.model.dto.rest.wall.WallPositionBean;
import com.arcsoft.supervisor.model.vo.task.TaskStatus;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.repository.graphic.WallPositionRepository;
import com.arcsoft.supervisor.repository.graphic.WallRepository;
import com.arcsoft.supervisor.repository.server.OpsServerRepository;
import com.arcsoft.supervisor.repository.server.ServerComponentRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.graphic.WallService;
import com.arcsoft.supervisor.service.graphic.event.WallRemoveEvent;
import com.arcsoft.supervisor.service.task.TaskExecutor;
import com.arcsoft.supervisor.service.task.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zw.
 */
@Service
public class DefaultWallServiceImpl extends ServiceSupport implements WallService, TransactionSupport {

    @Autowired
    private WallRepository wallRepository;
    @Autowired
    private WallPositionRepository wallPositionRepository;

    @Autowired
    private OpsServerRepository opsServerRepository;

    @Autowired
    private ServerComponentRepository serverComponentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskExecutor taskExecutor;

    private static final String UDP_URL = "udp:\\/\\/(([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]\\.|2[0-4][0-9]\\.|[0-9][0-9]\\.|25[0-5]\\.|[0-9]\\.)([01][0-9][0-9]|2[0-4][0-9]|25[0-5]|[0-9][0-9]|[0-9])):([0-9]+)";

    private static final String RTMP_URL = "^(rtmp|http)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-Z0-9\\.&amp;%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&amp;%\\$#\\=~_\\-@]*)*$";

    private static final Pattern PATTERN_UDP_URL = Pattern.compile(UDP_URL);

    private static final Pattern PATTERN_RTMP_URL = Pattern.compile(RTMP_URL);

    @Override
    public Wall save(String name) {
        if (isWallNameExists(-1, name)) {
            throw BusinessExceptionDescription.WALL_NAME_EXISTS.exception();
        }
        Wall wall = new Wall(name);
        updateWallPositions(wall);
        return wallRepository.save(wall);
    }

    private boolean isWallNameExists(Integer id, String name) {
        Wall wall = wallRepository.findOne(id);
        Long c = wallRepository.countByName(name);
        if (wall != null && wall.getName().equals(name)) {
            return c != null && c.longValue() > 1;
        } else {
            return c != null && c.longValue() > 0;
        }
    }

    @Override
    public void save(Wall wall) {
        wallRepository.save(wall);
    }

    private void updateWallPositions(Wall wall) {
        List<WallPosition> oldWallPositions = wall.getWallPositions();
        int oldWallPositionSize = oldWallPositions.size();
        int position = 0;
        for (int row = 0; row < wall.getRowCount(); row++) {
            for (int column = 0; column < wall.getColumnCount(); column++) {
                if(position < oldWallPositionSize) {
                    WallPosition wallPosition = oldWallPositions.get(position);
                    wallPosition.setRow(row);
                    wallPosition.setColumn(column);
                    Integer localtion=wall.getColumnCount()*row+column+1;
                } else {
                    WallPosition wallPosition = new WallPosition(row, column);
                    wallPosition.setWall(wall);
                    Integer localtion=wall.getColumnCount()*row+column+1;
                    wallPosition.setWallName("组别"+localtion);
                    oldWallPositions.add(wallPosition);
                }
                position += 1;
            }
        }

        if(position < oldWallPositionSize) {
            for(int i = position; i < oldWallPositionSize; i++) {
                WallPosition wallPosition = oldWallPositions.get(position);
                Screen screen = wallPosition.getScreen();
                if(screen != null) {
                    TaskType type = TaskType.IP_STREAM_COMPOSE;
                    if(wall.getType() == 1) {
                        type = TaskType.IP_STREAM_COMPOSE;
                    } else if(wall.getType() == 2) {
                        type = TaskType.SDI_STREAM_COMPOSE;
                    }
                    Task task = taskRepository.findByTypeAndReferenceId(type.getType(), screen.getId());
                    if(task != null && TaskStatus.RUNNING.name().equals(task.getStatus())) {
                        throw BusinessExceptionDescription.WALL_POSITION_WITH_RUNNING_TASK_CANT_DELETE.exception();
                    }
                }
                oldWallPositions.remove(position);
            }
        }
    }

    @Override
    public Wall updateWith(WallWebBean wallbean) {
        List<OpsServer> opsServerList = new ArrayList<>();
        List<ServerComponent> sdiOutputList = new ArrayList<>();

        for (String opsId : wallbean.getOpsIds()) {
            OpsServer ops = opsServerRepository.findOne(opsId);
            if (ops != null) {
                opsServerList.add(ops);
            }
        }

        for (Integer sdiId : wallbean.getSdiIds()) {
            ServerComponent sdi = serverComponentRepository.findOne(sdiId);
            if (sdi != null) {
                sdiOutputList.add(sdi);
            }
        }

        Wall wall = wallRepository.findOne(wallbean.getId());
        if (wall == null) {
            wall = new Wall("");
            updateWallPositions(wall);
        } else {
            if (wallbean.getVersion() != wall.getVersion()) {
                throw BusinessExceptionDescription.WALL_SETTING_OPTIMISTIC_LOCK.exception();
            }

        }
        if (!isWallNameExists(wallbean.getId(), wallbean.getName())) {
            wall.setName(wallbean.getName());
        } else {
            throw BusinessExceptionDescription.WALL_NAME_EXISTS.exception();
        }

        if (wall.getRowCount().equals(wallbean.getRowCount()) && wall.getColumnCount().equals(wallbean.getColumnCount())) {
            if (wallbean.getType() == 1) {
                //updateWallOpsServer(wall, opsServerList);
            } else if (wallbean.getType() == 2) {
                //updateWallSdiOutput(wall, sdiOutputList);
            }
        } else {
            wall.setRowCount(wallbean.getRowCount());
            wall.setColumnCount(wallbean.getColumnCount());

            updateWallPositions(wall);
        }
        wall.setType(wallbean.getType());
        return wallRepository.save(wall);
    }

    private void updateWallOpsServer(Wall wall, List<OpsServer> opsServers) {
        for (int i = 0; i < wall.getRowCount() * wall.getColumnCount(); i++) {
            if (i < opsServers.size()) {
                wall.getWallPositions().get(i).setOpsServer(opsServers.get(i));
            } else {
                wall.getWallPositions().get(i).setOpsServer(null);
            }
            wall.getWallPositions().get(i).setSdiOutput(null);
        }
    }

    private void updateWallSdiOutput(Wall wall, List<ServerComponent> sdiOutputs) {
        for (int i = 0; i < wall.getRowCount() * wall.getColumnCount(); i++) {
            if (i < sdiOutputs.size()) {
                wall.getWallPositions().get(i).setSdiOutput(sdiOutputs.get(i));
            } else {
                wall.getWallPositions().get(i).setSdiOutput(null);
            }
            wall.getWallPositions().get(i).setOpsServer(null);
        }
    }

    @Override
    public void updateWallPositionOps(int wallId, List<String> opsIds, List<PositionWebBean> positions) {
        Wall wall = wallRepository.findOne(wallId);
        List<OpsServer> opsList = new ArrayList<OpsServer>();
        List<ServerComponent> sdiList = new ArrayList<ServerComponent>();
        byte type = wall.getType();
        if (type == 1) {
            for (int i = 0; i < wall.getWallPositions().size(); i++) {
                OpsServer ops = wall.getWallPositions().get(i).getOpsServer();
                if (ops != null) {
                    opsList.add(ops);
                }
            }
        } else if (type == 2) {
            for (int i = 0; i < wall.getWallPositions().size(); i++) {
                ServerComponent sdi = wall.getWallPositions().get(i).getSdiOutput();
                if (sdi != null) {
                    sdiList.add(sdi);
                }
            }
        }

        TaskType taskType = TaskType.IP_STREAM_COMPOSE;
        if (type == 1) {
            taskType = TaskType.IP_STREAM_COMPOSE;
        } else if (type == 2) {
            taskType = TaskType.SDI_STREAM_COMPOSE;
        }

        boolean hasTaskRunning = false;
        for (WallPosition wallPosition : wall.getWallPositions()) {
            Screen screen = wallPosition.getScreen();
            if (screen != null) {
                Task task = taskService.getByTypeAndReferenceId(screen.getId(), taskType);
                if (task != null && task.isStatusEqual(TaskStatus.RUNNING)) {
                    hasTaskRunning = true;
                }
            }
        }
        if (hasTaskRunning) {
            throw BusinessExceptionDescription.WALL_SCREEN_TASK_RUNNING.exception();
        }

        for (WallPosition wallPosition : wall.getWallPositions()) {
            if (type == 1) {
                wallPosition.setOpsServer(null);
            } else if (type == 2) {
                wallPosition.setSdiOutput(null);
            }
        }
        int index = 0;
        for (PositionWebBean position : positions) {
            WallPosition wallPosition = getWallPositionWithRowAndColumn(wallId, position.getRow(), position.getColumn());
            if (type == 1) {
                if (wallPosition != null && index < opsList.size()) {
                    wallPosition.setOpsServer(opsList.get(index));
                }
            } else if (type == 2) {
                if (wallPosition != null && index < sdiList.size()) {
                    wallPosition.setSdiOutput(sdiList.get(index));
                }
            }
            index++;
        }
    }

    @Override
    public void resetWallPositionOps(int wallPosition) {
        WallPosition wallPos = wallPositionRepository.findOne(wallPosition);
        OpsServer ops = wallPos.getOpsServer();
        if(ops != null) {
            ops.setWallPosition(null);
        }
        wallPos.setOpsServer(null);
    }

    @Override
    public OpsServer updateWallPositionOps(int wallPosition, String opsId) {
        OpsServer ops = opsServerRepository.findOne(opsId);
        if(ops.getWallPosition() != null) {
            ops.getWallPosition().setOpsServer(null);
            ops.setWallPosition(null);
        }
        WallPosition wallPos = wallPositionRepository.findOne(wallPosition);
        if(wallPos.getOpsServer() != null) {
            wallPos.getOpsServer().setWallPosition(null);
        }
        wallPos.setOutput(null);
        wallPos.setOpsServer(ops);
        return ops;
    }

    @Override
    public void updateWallPositionOutput(int wallPosition, String output) {
        Matcher matcher = PATTERN_UDP_URL.matcher(output);
        Matcher matcher2 = PATTERN_RTMP_URL.matcher(output);
        if(!matcher.matches() && !matcher2.matches()) {
            throw BusinessExceptionDescription.TASK_OUTPUT_INVALID.exception();
        }
        WallPosition wallPos = wallPositionRepository.findOne(wallPosition);
        WallPosition wallPosWithOutput = wallPositionRepository.findByOutput(output);
        if(wallPosWithOutput != null && wallPosWithOutput.getId() != wallPos.getId()) {
            throw BusinessExceptionDescription.TASK_OUTPUT_CONFLICT.exception();
        } else {
            OpsServer ops = wallPos.getOpsServer();
            if (ops != null) {
                ops.setWallPosition(null);
                wallPos.setOpsServer(null);
            }
            wallPos.setOutput(output);
        }
    }


    @Override
    public Wall getById(int id) {
        return wallRepository.findOne(id);
    }

    @Override
    public void update(Wall wall) {
        if (getById(wall.getId()) != null) {
            wallRepository.save(wall);
        }
    }


    @Override
    @Transactional
    public void switchWallPosition(RootWallBean wallBean) {
        Wall persistWall = getById(wallBean.getWall().getId());
        if (persistWall == null) {
            throw BusinessExceptionDescription.WALL_NOT_EXISTS.exception();
        }
        List<WallPositionBean> wallPositionBeans = wallBean.getWall().getPositions();

        WallPositionBean firstPosition = wallPositionBeans.get(0);
        WallPositionBean twoPosition = wallPositionBeans.get(1);

        WallPosition first = getWallPositionWithRowAndColumn(persistWall, firstPosition.getRow(),
                firstPosition.getColumn());
        WallPosition two = getWallPositionWithRowAndColumn(persistWall, twoPosition.getRow(),
                twoPosition.getColumn());

        first.setPosition(twoPosition.getRow(), twoPosition.getColumn());
        two.setPosition(firstPosition.getRow(), firstPosition.getColumn());

    }

    @Override
    public void saveOrUpdateWallPosition(int wallId, WallPosition position) {
        Wall wall = getById(wallId);
        WallPosition wallPosition = getWallPositionWithRowAndColumn(wall, position.getRow(), position.getColumn());
        if (wallPosition != null) {
            BeanUtils.copyProperties(position, wallPosition, "id", "screen");
        } else {
            position.setWall(wall);
            wallPositionRepository.save(position);
        }
    }

    @Override
    public WallPosition getWallPositionWithRowAndColumn(int wallId, int row, int column) {
        return getWallPositionWithRowAndColumn(getById(wallId), row, column);
    }

    @Override
    public WallPosition getWallPositionWithRowAndColumn(Wall wall, int row, int column) {
        if (wall != null) {
            List<WallPosition> wallPositionList = wall.getWallPositions();
            if (wallPositionList != null) {
                for (WallPosition position : wallPositionList) {
                    if (position.getRow() != null
                            && position.getColumn() != null
                            && position.getRow() == row
                            && position.getColumn() == column) {
                        return position;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public WallPosition getWallPositionWithOpsServerId(String opsId) {
        OpsServer server = opsServerRepository.findOne(opsId);
        if (server != null) {
            return wallPositionRepository.findByOpsServer(server);
        }
        return null;
    }

    @Override
    public WallPosition getWallPositionById(Integer id) {
        return wallPositionRepository.findOne(id);
    }

    @Override
    public List<Wall> findAll() {
        return wallRepository.findAll();
    }

    @Override
    public void delete(Integer id) {
        List<Task> associatedTasks = taskRepository.findComposeTasksByWallIdAndTypesAndStatus(
                id,
                new TaskStatus[]{TaskStatus.STOP, TaskStatus.ERROR, TaskStatus.RUNNING},
                TaskType.IP_STREAM_COMPOSE.getType(), TaskType.SDI_STREAM_COMPOSE.getType()
        );
        wallRepository.delete(id);
        getEventManager().submit(new WallRemoveEvent(associatedTasks, id));
    }


    /**
     * Handles the <code>WallRemoveEvent</code>.
     * <p>It will be stop and remove all of running tasks under the specific wall.</p>
     *
     * @param event the object of <code>WallRemoveEvent</code>
     */
    @EventReceiver(value = WallRemoveEvent.class)
    @Transactional
    public void onWallRemoved(WallRemoveEvent event) {
        List<Task> allOfTasks = event.getRunningTasks();
        for (Task task : allOfTasks) {

            if (TaskStatus.RUNNING.name().equals(task.getStatus())) {
                if (StringUtils.isBlank(task.getServerId())) {
                    logger.error("The serverId of task [id={}] on the wall [id={}] is empty.", new Object[]{
                            task.getId(), event.getWallId()
                    });
                    continue;
                }

                try {
                    taskExecutor.stop(task.getId());
                } catch (Exception e) {
                    logger.error("Failed to stop task [id={}] during remove wall [id={}]", new Object[]{
                            task.getId(), event.getWallId()
                    });
                }
            }
            taskService.delete(task.getId());
        }

    }
}
