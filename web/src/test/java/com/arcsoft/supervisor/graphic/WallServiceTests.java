package com.arcsoft.supervisor.graphic;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.*;
import com.arcsoft.supervisor.model.dto.rest.screen.RootScreenBean;
import com.arcsoft.supervisor.model.dto.rest.wall.RootWallBean;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.service.graphic.WallService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * @author zw.
 */
public class WallServiceTests extends ProductionTestSupport {

    @Autowired
    private WallService wallService;
    @Autowired
    private ScreenService screenService;
    @Autowired
    private ChannelService channelService;


    @Test
    @Transactional
    @Rollback(false)
    public void test() {
        Wall wall = createWall("OPS墙-2", (byte) 2, (byte) 6, (byte) 1);
        WallPosition position1 = createWallPosition(wall, (byte) 0, (byte) 0);
        WallPosition position2 = createWallPosition(wall, (byte) 0, (byte) 1);
        WallPosition position3 = createWallPosition(wall, (byte) 0, (byte) 2);
        WallPosition position4 = createWallPosition(wall, (byte) 1, (byte) 0);
        WallPosition position5 = createWallPosition(wall, (byte) 1, (byte) 1);
        wall.getWallPositions().add(position1);
        wall.getWallPositions().add(position2);
        wall.getWallPositions().add(position3);
        wall.getWallPositions().add(position4);
        wall.getWallPositions().add(position5);

        wallService.save(wall);

        Screen screen = new Screen();
        screen.setWallPosition(position5);
        screenService.save(screen);

        ScreenSchema schema0 = createScreenSchema(screen, (byte) 2, (byte) 6, (byte) 1);
        ScreenPosition sposition0 = createScreenPosition(schema0, (byte) 0, (byte) 0, null);
        ScreenPosition sposition1 = createScreenPosition(schema0, (byte) 0, (byte) 1, null);
        ScreenPosition sposition2 = createScreenPosition(schema0, (byte) 1, (byte) 0, null);
        ScreenPosition sposition3 = createScreenPosition(schema0, (byte) 1, (byte) 1, null);
        schema0.getScreenPositions().add(sposition0);
        schema0.getScreenPositions().add(sposition1);
        schema0.getScreenPositions().add(sposition2);
        schema0.getScreenPositions().add(sposition3);
        screenService.save(schema0);

        screen.setActiveSchema(schema0);

    }

    @Test
    @Transactional
    @Rollback(false)
    public void testUpdateWallWithJson() {
        String wallUpdateJson = "{\"token\":\"111\",\"wall\":{\"id\":1,\"screens\":[{\"row\":2,\"col\":0,\"scrnid\":2},{\"row\":2,\"col\":1,\"scrnid\":1}]}}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            RootWallBean wallBean = mapper.readValue(wallUpdateJson, RootWallBean.class);
//            wallService.updateWith(wallBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testUpdateScreenWithJson(){
        String screenUpdateJson = "{\"token\":\"123\",\"screen\":[{\"id\":1,\"template_id\":1,\"template\":[{\"id\":1,\"row\":4,\"col\":4,\"subscrns\":[{\"row\":0,\"channel\":1,\"col\":0},{\"row\":0,\"channel\":2,\"col\":1},{\"row\":1,\"channel\":3,\"col\":0}]}]}]}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            RootScreenBean screenBean = mapper.readValue(screenUpdateJson, RootScreenBean.class);
            screenService.updateWith(screenBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Wall createWall(String name, int row, int column, byte type) {
        Wall wall = new Wall();
        wall.setName(name);
        wall.setColumnCount(column);
        wall.setRowCount(row);
        wall.setType(type);
        return wall;
    }

    private WallPosition createWallPosition(Wall wall, int row, int column) {
        WallPosition position = new WallPosition();
        position.setRow(row);
        position.setColumn(column);
        position.setWall(wall);
        return position;
    }

    private ScreenSchema createScreenSchema(Screen screen, int rowCount, int columnCount, int value) {
        ScreenSchema schema = new ScreenSchema();
        schema.setScreen(screen);
        schema.setColumnCount(columnCount);
        schema.setRowCount(rowCount);
        schema.setValue(value);
        return schema;
    }

    private ScreenPosition createScreenPosition(ScreenSchema schema, int row, int column, Channel channel) {
        ScreenPosition position = new ScreenPosition();
        position.setScreenSchema(schema);
        position.setRow(row);
        position.setColumn(column);
        position.setChannel(channel);
        return position;
    }

}
