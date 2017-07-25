package com.arcsoft.supervisor.repository.graphic;

import com.arcsoft.supervisor.model.domain.graphic.ScreenDynamicLayout;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yshe on 2016/12/20.
 */
public interface ScreenDynamicLayoutRepository extends CrudRepository<ScreenDynamicLayout, Integer> {
    public void deleteByScreenid(Integer screenid);

    public ScreenDynamicLayout findByScreenid(Integer screenid);
}
