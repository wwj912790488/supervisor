package com.arcsoft.supervisor.model.domain.channel;

import javax.persistence.*;

/**
 * Signal detect config options for channel.
 *
 * @author zw.
 */
@Entity
@Table(name = "channel_signal_detect_config")
public class ChannelSignalDetectConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "enable_l1_error")
    private Boolean enableLevel1Error = false;
    @Column(name = "enable_l2_error")
    private Boolean enableLevel2Error = false;
    @Column(name = "enable_l3_error")
    private Boolean enableLevel3Error = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getEnableLevel1Error() {
        return enableLevel1Error;
    }

    public void setEnableLevel1Error(Boolean enableLevel1Error) {
        this.enableLevel1Error = enableLevel1Error;
    }

    public Boolean getEnableLevel2Error() {
        return enableLevel2Error;
    }

    public void setEnableLevel2Error(Boolean enableLevel2Error) {
        this.enableLevel2Error = enableLevel2Error;
    }

    public Boolean getEnableLevel3Error() {
        return enableLevel3Error;
    }

    public void setEnableLevel3Error(Boolean enableLevel3Error) {
        this.enableLevel3Error = enableLevel3Error;
    }
}
