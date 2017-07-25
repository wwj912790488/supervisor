package com.arcsoft.supervisor.service.graphic.impl;

import com.arcsoft.supervisor.commons.json.JsonMapper;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.*;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutPosition;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutPositionTemplate;
import com.arcsoft.supervisor.model.dto.rest.screen.RootScreenBean;
import com.arcsoft.supervisor.model.dto.rest.screen.ScreenPreviewBean;
import com.arcsoft.supervisor.repository.channel.ChannelRepository;
import com.arcsoft.supervisor.repository.graphic.ScreenDynamicLayoutRepository;
import com.arcsoft.supervisor.repository.graphic.ScreenPositionRepository;
import com.arcsoft.supervisor.repository.graphic.ScreenRepository;
import com.arcsoft.supervisor.repository.graphic.ScreenSchemaRepository;
import com.arcsoft.supervisor.repository.layouttemplate.LayoutPositionTemplateRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.converter.Converter;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.arcsoft.supervisor.web.mosic.SchemaPosChannel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The default implementation of {@link com.arcsoft.supervisor.service.graphic.ScreenService}.
 * Provide all of logic of {@code Screen}.
 *
 * @author zw.
 */
@Service
public class DefaultScreenServiceImpl implements ScreenService, TransactionSupport {

    private Logger logger = Logger.getLogger(DefaultScreenServiceImpl.class);

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private ScreenPositionRepository screenPositionRepository;

    @Autowired
    private ScreenSchemaRepository schemaRepository;

    @Autowired
    private LayoutPositionTemplateRepository layoutPositionTemplateRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ScreenDynamicLayoutRepository screenDynamicLayoutRepository;

    @Autowired
    @Qualifier("screenBeanToScreenConverter")
    private Converter<RootScreenBean, List<Screen>> rootScreenBeanScreenConverter;

    @Autowired
    @Qualifier("screenToScreenPreviewBeanConverter")
    private Converter<Integer, ScreenPreviewBean> screenPreviewBeanConverter;


    @Override
    public Screen save(Screen screen) {
        return screenRepository.save(screen);
    }

    @Override
    public Screen getById(int id) {
        return screenRepository.findOne(id);
    }

    @Override
    public ScreenPreviewBean getScreenPreviewBeanByScreen(int screenId) {
        try {
            return screenPreviewBeanConverter.doForward(screenId);
        } catch (Exception e) {
            throw BusinessExceptionDescription.CONVERT_INPUT_ARGUMENTS_FAILED.withException(e);
        }
    }

    @Override
    public List<Screen> findAll() {
        return screenRepository.findAll();
    }

    @Override
    public void updateWith(RootScreenBean screenBean) {
        try {
            rootScreenBeanScreenConverter.doForward(screenBean);
        } catch (Exception e) {
            throw BusinessExceptionDescription.CONVERT_INPUT_ARGUMENTS_FAILED.withException(e);
        }
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ScreenPosition updateScreenPositionChannel(int scheamId, int row, int column, int group, Channel channel) {
        ScreenPosition screenPosition = getScreenPositionByRowAndColumnAndGroup(scheamId, row, column, group);
        if (screenPosition == null) {
            throw BusinessExceptionDescription.WALL_SETTING_NOT_EXISTS.exception();
        }
        screenPosition.setChannel(channel);
        saveOrUpdateScreenPosition(scheamId, screenPosition);
        return screenPosition;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ScreenPosition updateScreenPositionChannel(int scheamId, int index,Channel channel) {
        ScreenPosition screenPosition = getScreenPositionByIndex(scheamId, index);
        if (screenPosition == null) {
            throw BusinessExceptionDescription.WALL_SETTING_NOT_EXISTS.exception();
        }
        screenPosition.setChannel(channel);
        saveOrUpdateScreenPosition(scheamId, screenPosition);
        return screenPosition;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean updateScreenPositionChannels(int scheamId, List<SchemaPosChannel> list)
    {
        try
        {
            for (SchemaPosChannel pos:list) {
                if(pos==null)
                    continue;

                ScreenPosition screenPosition = getScreenPositionByIndex(scheamId, pos.getPos());
                if(screenPosition==null)
                    continue;

 //               if(pos.getChannel()!=null)
                {
                    screenPosition.setChannel(pos.getChannel());
                    saveOrUpdateScreenPosition(scheamId, screenPosition);
                }
            }
        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }

    @Override
    public void saveOrUpdateScreenPosition(int schemaId, ScreenPosition position) {
        ScreenSchema schema = schemaRepository.findOne(schemaId);
        ScreenPosition persistPosition = getScreenPositionByRowAndColumnAndGroup(schema, position.getRow(), position.getColumn(), position.getGroupIndex());
        if (persistPosition != null) {
            BeanUtils.copyProperties(position, persistPosition, "id");
        } else {
            position.setScreenSchema(schema);
            screenPositionRepository.save(position);
        }
    }

    @Override
    public ScreenPosition getScreenPositionByIndex(int schemaId, int posindex)
    {
        ScreenPosition position = null;
        try{
            ScreenSchema schema = schemaRepository.findOne(schemaId);
            List<ScreenPosition> screenPositions = schema.getScreenPositions();
            position = screenPositions.get(posindex);
        }
        catch (Exception e)
        {
            position = null;
        }


        return position;
    }

    @Override
    public ScreenPosition getScreenPositionByRowAndColumnAndGroup(int schemaId, int row, int column, int group) {
        return getScreenPositionByRowAndColumnAndGroup(schemaRepository.findOne(schemaId), row, column, group);
    }

    @Override
    public ScreenPosition getScreenPositionByRowAndColumnAndGroup(ScreenSchema screenSchema, int row, int column, int group) {
        if (screenSchema != null) {
            List<ScreenPosition> screenPositions = screenSchema.getScreenPositions();
            if (screenPositions != null) {
                for (ScreenPosition position : screenPositions) {
                    if (position.getRow() != null
                            && position.getColumn() != null
                            && position.getRow() == row
                            && position.getColumn() == column
                            && position.getGroupIndex() == group) {
                                return position;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void save(ScreenSchema schema) {
        schemaRepository.save(schema);
    }

    @Override
    public ScreenSchema getScreenSchemaById(int schemaId) {
        return schemaRepository.findOne(schemaId);
    }

    @Override
    public ScreenSchema updateScreenSchema(ScreenSchema screenSchema, Integer row, Integer column) {
        //screenPositionRepository.deleteByScreenSchema(screenSchema);
        screenSchema.setRowCount(row);
        screenSchema.setColumnCount(column);
        screenSchema.setGroupCount(1);
        screenSchema.getScreenPositions().clear();
        screenSchema.setTemplate(null);

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                screenSchema.getScreenPositions().add(new ScreenPosition(screenSchema, i, j));
            }
        }
        return schemaRepository.save(screenSchema);
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateScreenPositionChannel(Integer schemaId, Integer rowOne, Integer columnOne, Integer rowTwo, Integer columnTwo, Integer group) {
        ScreenPosition screenPositionOne = getScreenPositionByRowAndColumnAndGroup(schemaId, rowOne, columnOne, group);
        ScreenPosition screenPositionTwo = getScreenPositionByRowAndColumnAndGroup(schemaId, rowTwo, columnTwo, group);
        if (screenPositionOne == null || screenPositionTwo == null) {
            throw BusinessExceptionDescription.WALL_SETTING_NOT_EXISTS.exception();
        }
        Channel channelOne = screenPositionOne.getChannel();
        Channel channelTwo = screenPositionTwo.getChannel();
        screenPositionOne.setChannel(channelTwo);
        screenPositionTwo.setChannel(channelOne);
        saveOrUpdateScreenPosition(schemaId, screenPositionOne);
        saveOrUpdateScreenPosition(schemaId, screenPositionTwo);
    }

    @Override
    public Screen updateStyle(Integer screenId, MessageStyle style) {
        Screen screen = screenRepository.findOne(screenId);
        if(screen != null) {
            screen.setStyle(style);
        }
        return screen;
    }

    @Override
    public Screen updateMessage(Integer screenId, String message) {
        Screen screen = screenRepository.findOne(screenId);
        if(screen != null){
            screen.setMessage(message);
        }
        return screen;
    }

    @Override
    public List<Channel> getAllActiveChannels() {
        List<Screen> screens = screenRepository.findAll();
        List<Channel> channels = new ArrayList<Channel>();
        for(Screen screen : screens) {
            ScreenSchema activeSchema = screen.getActiveSchema();
            if(activeSchema != null && activeSchema.getScreenPositions() != null) {
                for(ScreenPosition screenPosition : activeSchema.getScreenPositions()) {
                    if(screenPosition.getChannel() != null) {
                        channels.add(screenPosition.getChannel());
                    }
                }
            }
        }
        return channels;
    }

    @Override
    public ScreenSchema updateScreenSchemaGroup(Integer schemaId, int group) {
        ScreenSchema schema = schemaRepository.findOne(schemaId);
        List<ScreenPosition> positions = schema.getScreenPositions();
        if(schema.getGroupCount() > group) {
            List<ScreenPosition> removePositions = new ArrayList<>();
            for(ScreenPosition position : positions) {
                int groupIndex = position.getGroupIndex();
                if(groupIndex >= group) {
                    removePositions.add(position);
                }
            }
            positions.removeAll(removePositions);
        } else if(schema.getGroupCount() < group) {
            LayoutPositionTemplate template = schema.getTemplate();
            if(template == null) {
                for (int g = schema.getGroupCount(); g < group; g++) {
                    for (int i = 0; i < schema.getRowCount(); i++) {
                        for (int j = 0; j < schema.getColumnCount(); j++) {
                            positions.add(new ScreenPosition(schema, i,  j, g));
                        }
                    }
                }
            } else {
                for (int g = schema.getGroupCount(); g < group; g++) {
                    for(LayoutPosition layoutPosition : template.getPositions()) {
                        positions.add(new ScreenPosition(schema, layoutPosition.getRow(), layoutPosition.getColumn(), layoutPosition.getX(), layoutPosition.getY(), g));
                    }
                }
            }
        }
        schema.setGroupCount(group);
        return schema;
    }

    @Override
    public ScreenSchema updateScreenSchemaSwitchTime(Integer schemaId, Integer switchTime) {
        ScreenSchema schema = schemaRepository.findOne(schemaId);
        schema.setSwitchTime(switchTime);
        return schema;
    }

    @Override
    public ScreenSchema updateScreenSchemaTemplate(Integer schemaId, Integer template) {
        LayoutPositionTemplate lpTemplate = layoutPositionTemplateRepository.findOne(template);
        ScreenSchema schema = schemaRepository.findOne(schemaId);
        schema.setTemplate(lpTemplate);
        schema.setRowCount( lpTemplate.getRowCount());
        schema.setColumnCount( lpTemplate.getColumnCount());
        schema.setGroupCount(1);
        List<ScreenPosition> positions = schema.getScreenPositions();
        positions.clear();
        for(LayoutPosition layoutPosition : lpTemplate.getPositions()) {
            positions.add(new ScreenPosition(schema, layoutPosition.getRow(), layoutPosition.getColumn(), layoutPosition.getX(), layoutPosition.getY()));
        }
        return schema;
    }

    @Override
    public ScreenSchema updateScreenSchemaChannels(Integer schemaId, int group, List<Integer> channelList) {
        ScreenSchema schema = schemaRepository.findOne(schemaId);
        List<ScreenPosition> positions = schema.getScreenPositions();
        List<ScreenPosition> groupPositions = new ArrayList<>();
        for(ScreenPosition position : positions) {
            if(position.getGroupIndex() == group) {
                groupPositions.add(position);
            }
        }
        int i = 0;
        int channelLength = channelList.size();
        for(ScreenPosition position : groupPositions) {
            if(i < channelLength) {
                Channel channel = channelRepository.findOne(channelList.get(i));
                if(channel != null) {
                    position.setChannel(channel);
                }
            }
            i++;
        }
        return schema;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public boolean updateUserLayout(Integer screenId, UserScreenLayout layout)
    {
        Screen screen = screenRepository.findOne(screenId);
        if(screen==null)
            return false;


        ScreenDynamicLayout screenDynamicLayout = screenDynamicLayoutRepository.findByScreenid(screenId);

        if(layout==null){
            screen.setUserLayoutId(null);
            screenRepository.save(screen);

            screenDynamicLayoutRepository.deleteByScreenid(screenId);
            return true;
        }


        if(screenDynamicLayout==null){
            screenDynamicLayout = new ScreenDynamicLayout();
        }

        screenDynamicLayout.setScreenid(screenId);
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            screenDynamicLayout.setLayout(mapper.writeValueAsString(layout));
            screenDynamicLayout.setLastupdate(new Date());
        }catch (Exception e){
            logger.info(e.getMessage(),e);
            return false;
        }

        ScreenDynamicLayout newLayout = screenDynamicLayoutRepository.save(screenDynamicLayout);
        if(newLayout!=null){
            screen.setUserLayoutId(newLayout.getId());

            return true;
        }

        return false;
    }

    @Override
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ScreenDynamicLayout getScreenDynamicLaout(Integer screenId){
        Screen screen = screenRepository.findOne(screenId);
        if(screen==null)
            return null;

        return screenDynamicLayoutRepository.findByScreenid(screenId);
    }

}
