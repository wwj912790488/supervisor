package com.arcsoft.supervisor.sartf.service.task;

import com.arcsoft.supervisor.service.task.TaskDispatcherFacade;


public interface SartfTaskDispatcherFacade extends TaskDispatcherFacade {

    void startUserTask(int userId);

    void stopUserTask(int userId);

    void switchAudioChannel(int userId, int cell_index);

}
