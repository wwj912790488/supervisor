package com.arcsoft.supervisor.service.graphic;

import com.arcsoft.supervisor.exception.service.BusinessException;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.graphic.Wall;
import com.arcsoft.supervisor.model.domain.graphic.WallPosition;
import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.dto.graphic.PositionWebBean;
import com.arcsoft.supervisor.model.dto.graphic.WallWebBean;
import com.arcsoft.supervisor.model.dto.rest.wall.RootWallBean;

import java.util.List;

/**
 * @author zw.
 */
public interface WallService {

    /**
     * Persists the wall with givens {@code name}.
     *
     * @param name the unique name of wall
     * @return
     * @throws BusinessException with below<ul><li>{@link BusinessExceptionDescription#WALL_NAME_EXISTS}</li></ul>
     */
    public Wall save(String name);

    public void save(Wall wall);

    public Wall updateWith(WallWebBean wallbean);

    public void delete(Integer id);

    public Wall getById(int id);

    public void update(Wall wall);

    /**
     * Switch the wall position with {@code wallBean}.
     *
     * @param wallBean a object contains wall config
     * @throws BusinessException Thrown with below<ul><li>{@link BusinessExceptionDescription#WALL_NOT_EXISTS} if the
     *                           wall not exist.</li></ul>
     */
    public void switchWallPosition(RootWallBean wallBean);

    public List<Wall> findAll();

    public void saveOrUpdateWallPosition(int wallId, WallPosition position);

    public WallPosition getWallPositionWithRowAndColumn(int wallId, int row, int column);

    public WallPosition getWallPositionWithRowAndColumn(Wall wall, int row, int column);

    public WallPosition getWallPositionWithOpsServerId(String opsId);

    public WallPosition getWallPositionById(Integer id);

    /**
     *
     * @param wallId
     * @param opsIds
     * @param positions
     * @throws BusinessException with below<ul><li>{@link BusinessExceptionDescription#WALL_SCREEN_TASK_RUNNING}</li></ul>
     */
    public void updateWallPositionOps(int wallId, List<String> opsIds, List<PositionWebBean> positions);

    public OpsServer updateWallPositionOps(int wallPosition, String opsId);

    public void resetWallPositionOps(int wallPosition);

    public void updateWallPositionOutput(int wallPostion, String output);

}
