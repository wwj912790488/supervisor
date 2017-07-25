package com.arcsoft.supervisor.service.converter.impl;

import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.arcsoft.supervisor.model.domain.graphic.ScreenSchema;
import com.arcsoft.supervisor.model.dto.rest.screen.RootScreenBean;
import com.arcsoft.supervisor.model.dto.rest.screen.ScreenBean;
import com.arcsoft.supervisor.model.dto.rest.screen.ScreenPositionBean;
import com.arcsoft.supervisor.model.dto.rest.screen.ScreenSchemaBean;
import com.arcsoft.supervisor.service.channel.ChannelService;
import com.arcsoft.supervisor.service.converter.ConverterAdapter;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A converter implementation to transform the {@link com.arcsoft.supervisor.model.dto.rest.screen.RootScreenBean}
 * to {@link com.arcsoft.supervisor.model.domain.graphic.Screen}.
 *
 * @author zw.
 */
@Service("screenBeanToScreenConverter")
public class ScreenBeanAndScreenDomainConverter extends ConverterAdapter<RootScreenBean, List<Screen>> {

    @Autowired
    private ScreenService screenService;
    @Autowired
    private ChannelService channelService;

    @Override
    public List<Screen> doForward(RootScreenBean source) {
        return toScreens(source.getScreenBeans());
    }

    private List<Screen> toScreens(List<ScreenBean> screenBeans) {
        if (screenBeans != null) {
            List<Screen> screens = new ArrayList<>();
            for (ScreenBean screenBean : screenBeans) {
                Screen persistScreen = screenService.getById(screenBean.getId());
                if (persistScreen != null) {
                    setScreenWithScreenBean(persistScreen, screenBean);
                    screens.add(persistScreen);
                }
            }
            return screens;
        }
        return Collections.emptyList();
    }

    private void setScreenWithScreenBean(Screen screen, ScreenBean screenBean) {
        if (screen != null) {
            screen.setActiveSchema(screenService.getScreenSchemaById(screenBean.getTemplateId()));
            for (ScreenSchemaBean screenSchemaBean : screenBean.getScreenSchemaBeans()) {
                ScreenSchema persistSchema = getScreenSchemaWithId(screen.getSchemas(), screenSchemaBean.getId());
                if (persistSchema != null) {
                    setScreenSchemaWithScreenSchemaBean(persistSchema, screenSchemaBean);
                }
            }
        }
    }

    private void setScreenSchemaWithScreenSchemaBean(ScreenSchema screenSchema, ScreenSchemaBean screenSchemaBean) {
        if (screenSchema != null) {
            screenSchema.setRowCount(screenSchemaBean.getRow());
            screenSchema.setColumnCount(screenSchemaBean.getColumn());
            screenSchema.setGroupCount(screenSchemaBean.getGroupCount());
            screenSchema.setSwitchTime(screenSchemaBean.getSwitchTime());
            setScreenPositionsWithScreenPositionBeans(screenSchemaBean.getPositionJsons(), screenSchema);
        }
    }

    private void setScreenPositionsWithScreenPositionBeans(List<ScreenPositionBean> screenPositionBeans, ScreenSchema schema) {
        for (ScreenPositionBean screenPositionBean : screenPositionBeans) {
            ScreenPosition persistScreenPosition = screenService.getScreenPositionByRowAndColumnAndGroup(
                    schema,
                    screenPositionBean.getRow().byteValue(),
                    screenPositionBean.getColumn().byteValue(),
                    screenPositionBean.getGroup());
            if (persistScreenPosition != null) {
                copyScreenPositionBeanToScreenPosition(screenPositionBean, persistScreenPosition);
            } else {
                schema.getScreenPositions().add(toScreenPosistion(screenPositionBean, schema));
            }
        }
    }

    private ScreenSchema getScreenSchemaWithId(List<ScreenSchema> persistScreenSchemas, int schemaId) {
        for (ScreenSchema schema : persistScreenSchemas) {
            if (schema.getId() == schemaId) {
                return schema;
            }
        }
        return null;
    }

    private void copyScreenPositionBeanToScreenPosition(ScreenPositionBean source, ScreenPosition target) {
        target.setRow(source.getRow());
        target.setColumn(source.getColumn());
        target.setGroupIndex(source.getGroup());
        target.setChannel(source.getChannel() == -1 ? null : channelService.getById(source.getChannel()));
    }

    private ScreenPosition toScreenPosistion(ScreenPositionBean bean, ScreenSchema schema) {
        ScreenPosition screenPosition = new ScreenPosition();
        screenPosition.setRow(bean.getRow());
        screenPosition.setColumn(bean.getColumn());
        screenPosition.setGroupIndex(bean.getGroup());
        screenPosition.setChannel(channelService.getById(bean.getChannel()));
        screenPosition.setScreenSchema(schema);
        return screenPosition;
    }
}
