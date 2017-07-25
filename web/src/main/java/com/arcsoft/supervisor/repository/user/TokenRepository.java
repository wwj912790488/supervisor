package com.arcsoft.supervisor.repository.user;

import com.arcsoft.supervisor.model.domain.user.Token;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TokenRepository extends JpaRepository<Token, Integer> {

	Token findByUserId(Integer userId);
	
	Token findByName(String name);

	void deleteById(Integer id);

}
