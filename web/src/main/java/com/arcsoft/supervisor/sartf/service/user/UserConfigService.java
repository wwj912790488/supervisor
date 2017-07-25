package com.arcsoft.supervisor.sartf.service.user;

import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.arcsoft.supervisor.model.domain.userconfig.UserConfig;
import com.arcsoft.supervisor.model.dto.rest.userconfig.UserConfigBean;

public interface UserConfigService {

    UserConfig findById(Integer id);

    void updateUserConfig(UserConfig userconfig, UserConfigBean bean);

    void saveUserConfig(SartfUser user, UserConfigBean bean);

    void updateUserConfigAudioChannel(Integer config_id, Integer channel_id);

    void updateUserConfigAudioCellIndex(Integer config_id, Integer cell_index);
}
