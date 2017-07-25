package com.arcsoft.supervisor.sartf.repository.user;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;

@Sartf
public interface UserConfigRepository extends JpaRepository<UserConfig, Integer> {

}
