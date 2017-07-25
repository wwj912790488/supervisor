package com.arcsoft.supervisor.service.channel.impl;

import com.arcsoft.supervisor.model.domain.channel.ChannelRecordTask;
import com.arcsoft.supervisor.repository.channel.ChannelRecordTaskRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.channel.ChannelRecordService;
import com.arcsoft.supervisor.service.system.SystemContextListener;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class RecordServerMonitorService extends ServiceSupport implements SystemContextListener{

    @Autowired
    private ChannelRecordService channelRecordService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ChannelRecordTaskRepository channelRecordTaskRepository;

    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private boolean channelServerRunning = false;


    @Override
    public void contextInit() {
        if(executor.isShutdown()){
            executor = Executors.newSingleThreadScheduledExecutor();
        }
        executor.scheduleWithFixedDelay(new ChannelServerRunningTester(), 0, 2, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestory() {
        executor.shutdown();
    }

    private void onRecordServerStart() {
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus status) {
                List<ChannelRecordTask> tasks = channelRecordTaskRepository.findAll();
                Set<Integer> channelIds = FluentIterable.from(tasks).transform(new Function<ChannelRecordTask, Integer>() {

                    @Nullable
                    @Override
                    public Integer apply(ChannelRecordTask input) {
                        return input.getSupervisorChannelId();
                    }
                }).toSet();
                for(Integer channelId : channelIds) {
                    channelRecordService.stopRecord(channelId);
                    channelRecordService.startRecord(channelId);
                }
                return null;
            }
        });
    }

    private void onRecordServerStop() {

    }

    private class ChannelServerRunningTester implements Runnable {

        @Override
        public void run() {
            boolean exist = channelRecordService.exist();
            if(channelServerRunning != exist){
                channelServerRunning = exist;
                if(channelServerRunning) {
                    onRecordServerStart();
                } else {
                    onRecordServerStop();
                }
            }
        }
    }
}
