package com.arcsoft.supervisor.system;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.model.domain.system.TranscoderTemplate;
import com.arcsoft.supervisor.service.system.TranscoderTemplateService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zw.
 */
public class TranscoderTemplateTests extends ProductionTestSupport {

    @Autowired
    private TranscoderTemplateService templateService;

    @Test
    public void testFind() {
        TranscoderTemplate transcoderTemplate = templateService.find();
        System.out.println(transcoderTemplate.getTemplate());
    }


}
