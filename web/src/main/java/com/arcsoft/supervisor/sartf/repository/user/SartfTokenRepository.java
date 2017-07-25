package com.arcsoft.supervisor.sartf.repository.user;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.user.SartfToken;
import com.arcsoft.supervisor.model.domain.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;

@Sartf
public interface SartfTokenRepository extends JpaRepository<SartfToken, Integer> {

	SartfToken findByUserId(Integer userId);
	
	SartfToken findByName(String name);

}
