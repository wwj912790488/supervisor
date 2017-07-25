package com.arcsoft.supervisor.service.comparator;

import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.Screen;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.arcsoft.supervisor.model.domain.graphic.ScreenSchema;
import com.arcsoft.supervisor.model.dto.rest.screen.RootScreenBean;
import com.arcsoft.supervisor.model.dto.rest.screen.ScreenBean;
import com.arcsoft.supervisor.model.dto.rest.screen.ScreenPositionBean;
import com.arcsoft.supervisor.model.dto.rest.screen.ScreenSchemaBean;
import com.arcsoft.supervisor.service.graphic.ScreenService;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A <code>Comparator</code> implementation to compare the <code>Screen</code> and <code>RootScreenBean</code>.
 *
 * @author zw.
 */
@Service("rootScreenBeanComparator")
public class RootScreenBeanComparator implements Comparator<Integer, RootScreenBean> {

    @Autowired
    private ScreenService screenService;

    @Transactional(readOnly = true)
    @Override
    public boolean compare(Integer integer, RootScreenBean rootScreenBean) {
        List<ScreenBean> screenBeans = rootScreenBean.getScreenBeans();
        for (ScreenBean screenBean : screenBeans) {
            Screen screen = screenService.getById(screenBean.getId());
            if (screen == null) {
                return false;
            }
            ScreenSchema activeSchema = screen.getActiveSchema();
            if (activeSchema == null || activeSchema.getId() != screenBean.getTemplateId()) {
                return false;
            }

            Optional<ScreenSchemaBean> activeSchemaBeanOptional = findActiveScreenSchemaBean(screenBean.getScreenSchemaBeans(), screenBean.getTemplateId());
            if (!activeSchemaBeanOptional.isPresent()) {
                return false;
            }

            ScreenSchemaBean activeSchemaBean = activeSchemaBeanOptional.get();
            if (activeSchemaBean.getColumn() != activeSchema.getColumnCount().intValue()
                    || activeSchemaBean.getRow() != activeSchema.getRowCount().intValue()
                    || !compareScreenPositions(activeSchemaBean.getPositionJsons(), activeSchema.getScreenPositions())) {
                return false;
            }


        }
        return true;
    }

    private boolean compareScreenPositions(List<ScreenPositionBean> positionBeans, List<ScreenPosition> positions) {
        for (ScreenPositionBean positionBean : positionBeans) {
            Optional<ScreenPosition> positionOptional = findByRowAndColumn(positionBean.getRow().byteValue(), positionBean.getColumn().byteValue(), positions);
            if (!positionOptional.isPresent()) {
                return false;
            }
            ScreenPosition position = positionOptional.get();
            if (!compareScreenPosition(positionBean, position)) {
                return false;
            }
        }
        return true;
    }

    private boolean compareScreenPosition(ScreenPositionBean positionBean, ScreenPosition position) {

        if (positionBean.getRow() != position.getRow().intValue()
                || positionBean.getColumn() != position.getColumn().intValue()) {
            return false;
        }

        Channel channel = position.getChannel();
        return (channel == null
                && (positionBean.getChannel() == null || positionBean.getChannel() == -1))
                || (channel != null && channel.getId() == positionBean.getChannel());
    }


    private Optional<ScreenPosition> findByRowAndColumn(byte row, byte column, List<ScreenPosition> positions) {
        for (ScreenPosition position : positions) {
            if (position.getRow() == row && position.getColumn() == column) {
                return Optional.of(position);
            }
        }
        return Optional.absent();

    }


    private Optional<ScreenSchemaBean> findActiveScreenSchemaBean(List<ScreenSchemaBean> screenSchemaBeans, int activeSchemaId) {
        for (ScreenSchemaBean schemaBean : screenSchemaBeans) {
            if (schemaBean.getId() == activeSchemaId) {
                return Optional.of(schemaBean);
            }
        }
        return Optional.absent();
    }


}
