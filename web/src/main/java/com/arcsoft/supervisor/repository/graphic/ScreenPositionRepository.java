package com.arcsoft.supervisor.repository.graphic;

import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import com.arcsoft.supervisor.model.domain.graphic.ScreenSchema;
import org.springframework.data.repository.CrudRepository;

/**
 * @author zw.
 */
public interface ScreenPositionRepository extends CrudRepository<ScreenPosition, Integer> {
	public void deleteByScreenSchema(ScreenSchema screenSchema);
}
