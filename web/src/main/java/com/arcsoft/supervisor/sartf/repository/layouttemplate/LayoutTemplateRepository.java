package com.arcsoft.supervisor.sartf.repository.layouttemplate;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

@Sartf
public interface LayoutTemplateRepository extends JpaRepository<LayoutTemplate, Integer>, JpaSpecificationExecutor<LayoutTemplate> {

    void deleteByPath(String path);

    LayoutTemplate findByPath(String path);

    @Query("select max(t.lastUpdate) from LayoutTemplate t")
    Date getNewest();
}
