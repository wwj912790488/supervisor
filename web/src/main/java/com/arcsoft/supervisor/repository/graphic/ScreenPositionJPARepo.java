package com.arcsoft.supervisor.repository.graphic;

import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.graphic.ScreenPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ScreenPositionJPARepo extends JpaRepository<ScreenPosition, Integer>  {
    List<ScreenPosition> findByChannel(Channel channel);

}
