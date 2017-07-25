package com.arcsoft.supervisor.repository.master;

import com.arcsoft.supervisor.model.domain.master.Master;

/**
 * Created by wwj on 2016/6/30.
 */
public interface MasterRepositoryFlag {
    Master findByFlag();
    Master findById(int id);
    void  delete(int id);
}
