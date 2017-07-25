package com.arcsoft.supervisor.repository.master;

import com.arcsoft.supervisor.model.domain.master.Master;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Created by wwj on 2016/6/30.
 */
public interface MasterRepository extends  JpaRepository<Master,Long> {

}
