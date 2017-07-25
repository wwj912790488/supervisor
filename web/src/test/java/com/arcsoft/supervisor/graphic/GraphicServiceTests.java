package com.arcsoft.supervisor.graphic;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.model.domain.graphic.Wall;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.graphic.WallService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author zw.
 */
public class GraphicServiceTests extends ProductionTestSupport {

    @Autowired
    private WallService wallService;
    @Autowired
    private ScreenService screenService;

    @Test
    public void testGetAll(){
        List<Wall> walls = wallService.findAll();
        Assert.assertNotNull(walls);
    }


}
