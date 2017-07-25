package com.arcsoft.supervisor.repository.graphic;

import com.arcsoft.supervisor.model.domain.graphic.Wall;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for {@code Wall}.
 *
 * @author zw.
 */
public interface WallRepository extends JpaRepository<Wall, Integer> {
	
	public Long countByName(String name);
}
