package com.arcsoft.supervisor.service.graphic;

import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.*;
import com.arcsoft.supervisor.model.dto.rest.screen.RootScreenBean;
import com.arcsoft.supervisor.model.dto.rest.screen.ScreenPreviewBean;
import com.arcsoft.supervisor.web.mosic.SchemaPosChannel;

import java.util.List;

/**
 * @author zw.
 */
public interface ScreenService {

    public Screen save(Screen screen);

    public Screen getById(int id);

    public ScreenPreviewBean getScreenPreviewBeanByScreen(int screenId);

    public List<Screen> findAll();

    public void updateWith(RootScreenBean screenBean);

    /**
     *
     * @param scheamId
     * @param row
     * @param column
     * @param channel
     * @return
     * @throws BusinessException with belows<ul><li>{@link BusinessExceptionDescription#WALL_SETTING_NOT_EXISTS}</li></ul>
     */
    public ScreenPosition updateScreenPositionChannel(int scheamId, int row, int column, int group, Channel channel);

    public ScreenPosition updateScreenPositionChannel(int scheamId, int index,Channel channel);

    public boolean updateScreenPositionChannels(int scheamId, List<SchemaPosChannel> list);

    public void saveOrUpdateScreenPosition(int schemaId, ScreenPosition position);

    /**
     * Retrieves the {@code ScreenPosition} with specify row and column.
     *
     * @param schemaId the identify value of schema
     * @param row      the row position of screen
     * @param column   the column position of screen
     * @return {@code ScreenPosition} or {@code null} if the row and column is not exists
     */
    public ScreenPosition getScreenPositionByRowAndColumnAndGroup(int schemaId, int row, int column, int group);

    public ScreenPosition getScreenPositionByIndex(int schemaId, int posindex);

    /**
     * Retrieves the {@code ScreenPosition} with specify row and column.
     *
     * @param screenSchema the schema object
     * @param row          the row position of screen
     * @param column       the column position of screen
     * @return {@code ScreenPosition} or {@code null} if the row and column is not exists
     */
    public ScreenPosition getScreenPositionByRowAndColumnAndGroup(ScreenSchema screenSchema, int row, int column, int group);


    public void save(ScreenSchema schema);

    public ScreenSchema getScreenSchemaById(int schemaId);

    public ScreenSchema updateScreenSchema(ScreenSchema screenSchema, Integer row, Integer column);

    /**
     *
     * @param schemaId
     * @param rowOne
     * @param columnOne
     * @param rowTwo
     * @param columnTow
     * @throws BusinessException with belows<ul><li>{@link BusinessExceptionDescription#WALL_SETTING_NOT_EXISTS}</li></ul>
     */
    public void updateScreenPositionChannel(Integer schemaId, Integer rowOne, Integer columnOne, Integer rowTwo, Integer columnTow, Integer group);

    public Screen updateStyle(Integer screenId, MessageStyle style);

    public Screen updateMessage(Integer screenId, String message);

    public List<Channel> getAllActiveChannels();

    public ScreenSchema updateScreenSchemaGroup(Integer schemaId, int group);

    public ScreenSchema updateScreenSchemaSwitchTime(Integer schemaId, Integer switchTime);

    public ScreenSchema updateScreenSchemaTemplate(Integer schemaId, Integer template);

    public ScreenSchema updateScreenSchemaChannels(Integer schemaId, int group, List<Integer> channelList);

    public boolean updateUserLayout(Integer screenId, UserScreenLayout layout);

    public ScreenDynamicLayout getScreenDynamicLaout(Integer screenId);
}
