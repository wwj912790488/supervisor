package com.arcsoft.supervisor.model.domain.channel;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A channel is a actual ip stream and contains some config option(e.g: signal detect, content detect,
 * mobile terminal setting) for it.
 *
 * @author zw.
 */
@Entity
@Table(name = "channel")
@DynamicUpdate
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * The name of channel
     */
    private String name;
    /**
     * The protocol of stream
     */
    private String protocol;
    /**
     * The ip address of stream
     */
    @Column(name = "ip")
    private String ip;
    /**
     * The port number of stream
     */
    private Integer port;
    /**
     * A redundancy property to compose the integral stream address
     */
    @JsonProperty
    @Transient
    private String address;
    /**
     * Is currently channel support the mobile terminal or not
     */
    @Column(name = "is_support_mobile")
    private Boolean isSupportMobile = false;
    /**
     * The max days of ip stream persist to storage
     */
    @Column(name = "max_persist_days")
    private Byte maxPersistDays;

    /**
     * The base path of record file
     */
    @JsonIgnore
    @Column(name = "record_base_path")
    private String recordBasePath;

    @JsonIgnore
    @Transient
    private String recordFileName;

    /**
     * The video container of record file.For now, it can be as below:
     * <ul>
     * <li>The value 0 is mp4 container</li>
     * <li>The value 1 hls container</li>
     * </ul>
     */
    @Column(name = "record_format")
    private Byte recordFormat;

    /**
     * Enable or disable detects of the stream signal
     */
    @Column(name = "enable_signal_detect")
    private Boolean enableSignalDetect = false;
    
    /**
     * Enable or disable detects of the stream signal
     */
    @Column(name = "enable_signal_detect_by_Type")
    private Boolean enableSignalDetectByType = false;
    
    /**
     * Enable or disable detects of the stream content
     */
    @Column(name = "enable_content_detect")
    private Boolean enableContentDetect = false;

    @Column(name = "enable_record")
    private Boolean enableRecord = false;

    @Column(name = "enable_trigger_record")
    private Boolean enableTriggerRecord = false;

    @Column(name = "create_date", updatable = false)
    private Date createDate;
    @JsonProperty("pid")
    @Column(name = "program_id")
    private String programId;
    @Column(name = "audio_id")
    private String audioId;


    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_group_id")
    private ChannelGroup group;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="channel_tag_info",
    joinColumns = {@JoinColumn(name="channel_id", referencedColumnName = "id")},
    inverseJoinColumns = {@JoinColumn(name="tag_id", referencedColumnName = "id")})
    private List<ChannelTag> tags;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "channel")
    private List<ChannelMobileConfig> mobileConfigs = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_alarmtime_id")
    private ChannelAlarmTime channelAlarmTime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "signal_detect_config_id")
    private ChannelSignalDetectConfig signalDetectConfig;
    
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "signal_detect_type_config_id")
    private ChannelSignalDetectTypeConfig signalDetectByTypeConfig;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "content_detect_config_id")
    private ChannelContentDetectConfig contentDetectConfig;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_info_id")
    private ChannelInfo channelInfo;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "channel")
    @OrderBy("id asc")
    private List<ChannelRecordHistory> recordHistories = new ArrayList<>();

    @Column(name = "original_channel_id")
    private String origchannelid;

    @Column(name = "api_heart")
    private String apiHeart;

    @Transient
    private String sdUrl;
    @Transient
    private String hdUrl;

    public String getApiHeart() {
        return apiHeart;
    }

    public void setApiHeart(String apiHeart) {
        this.apiHeart = apiHeart;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getIsSupportMobile() {
        return isSupportMobile;
    }

    public void setIsSupportMobile(Boolean isSupportMobile) {
        this.isSupportMobile = isSupportMobile;
    }

    public Boolean getEnableRecord() {
        return enableRecord == null ? false : enableRecord;
    }

    public void setEnableRecord(Boolean enableRecord) {
        this.enableRecord = enableRecord;
    }

    public Byte getMaxPersistDays() {
        return maxPersistDays;
    }

    public void setMaxPersistDays(Byte maxPersistDays) {
        this.maxPersistDays = maxPersistDays;
    }

    public Boolean getEnableSignalDetect() {
        return enableSignalDetect;
    }

    public void setEnableSignalDetect(Boolean enableSignalDetect) {
        this.enableSignalDetect = enableSignalDetect;
    }
    
    public Boolean getEnableSignalDetectByType() {
        return enableSignalDetectByType;
    }

    public void setEnableSignalDetectByType(Boolean enableSignalDetectByType) {
        this.enableSignalDetectByType = enableSignalDetectByType;
    }

    public Boolean getEnableContentDetect() {
        return enableContentDetect;
    }

    public void setEnableContentDetect(Boolean enableContentDetect) {
        this.enableContentDetect = enableContentDetect;
    }

    public List<ChannelMobileConfig> getMobileConfigs() {
        return mobileConfigs;
    }

    public void setMobileConfigs(List<ChannelMobileConfig> mobileConfigs) {
        this.mobileConfigs = mobileConfigs;
    }

    public ChannelSignalDetectConfig getSignalDetectConfig() {
        return signalDetectConfig;
    }

    public void setSignalDetectConfig(ChannelSignalDetectConfig signalDetectConfig) {
        this.signalDetectConfig = signalDetectConfig;
    }

    public ChannelSignalDetectTypeConfig getSignalDetectByTypeConfig() {
        return signalDetectByTypeConfig;
    }

    public void setSignalDetectByTypeConfig(ChannelSignalDetectTypeConfig signalDetectByTypeConfig) {
        this.signalDetectByTypeConfig = signalDetectByTypeConfig;
    }
    
    public ChannelContentDetectConfig getContentDetectConfig() {
        return contentDetectConfig;
    }

    public void setContentDetectConfig(ChannelContentDetectConfig contentDetectConfig) {
        this.contentDetectConfig = contentDetectConfig;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public String getAddress() {
        return this.getIp();
    }

    public ChannelGroup getGroup() {
        return group;
    }

    public void setGroup(ChannelGroup group) {
        this.group = group;
    }

    public ChannelAlarmTime getChannelAlarmTime() {
        return channelAlarmTime;
    }

    public void setChannelAlarmTime(ChannelAlarmTime channelAlarmTime) {
        this.channelAlarmTime = channelAlarmTime;
    }

    @PrePersist
    public void prePersist() {
        this.createDate = new Date();
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getAudioId() {
        return audioId;
    }

    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    public List<ChannelTag> getTags() {
        return tags;
    }

    public void setTags(List<ChannelTag> tags) {
        this.tags = tags;
    }

    @JsonIgnore
    public ChannelMobileConfig getChannelMobileConfigByType(final byte type) {
        if (this.isSupportMobile && this.mobileConfigs != null && !this.mobileConfigs.isEmpty()) {
            return FluentIterable.from(mobileConfigs).firstMatch(new Predicate<ChannelMobileConfig>() {
                @Override
                public boolean apply(ChannelMobileConfig input) {
                    return input.getType() == type;
                }
            }).orNull();
        }
        return null;
    }

    public void clearMobileAddress() {
        List<ChannelMobileConfig> configs = getMobileConfigs();
        if (configs != null && !configs.isEmpty()) {
            for (ChannelMobileConfig channelMobileConfig : configs) {
                channelMobileConfig.setAddress(null);
            }
        }
    }

    public String getSdUrl() {
        return sdUrl;
    }

    public void setSdUrl(String sdUrl) {
        this.sdUrl = sdUrl;
    }

    public String getHdUrl() {
        return hdUrl;
    }

    public void setHdUrl(String hdUrl) {
        this.hdUrl = hdUrl;
    }

    public ChannelInfo getChannelInfo() {
        return channelInfo;
    }

    public void setChannelInfo(ChannelInfo channelInfo) {
        this.channelInfo = channelInfo;
    }

    public String getRecordBasePath() {
        return recordBasePath;
    }

    public void setRecordBasePath(String recordBasePath) {
        this.recordBasePath = recordBasePath;
    }

    public Byte getRecordFormat() {
        return recordFormat;
    }

    public void setRecordFormat(Byte recordFormat) {
        this.recordFormat = recordFormat;
    }

    private String getRecordExtensionByRecordFormat() {
        return getRecordFormatAsEnum().getFormat();
    }

    private RecordFormat getRecordFormatAsEnum() {
        return recordFormat == 0 ? RecordFormat.MP4 : RecordFormat.HLS;
    }

    public List<ChannelRecordHistory> getRecordHistories() {
        return recordHistories;
    }

    public void setRecordHistories(List<ChannelRecordHistory> recordHistories) {
        this.recordHistories = recordHistories;
    }

    public void addRecordHistory(ChannelRecordHistory recordHistory) {
        if (recordHistories != null) {
            recordHistories.size();
            recordHistories.add(recordHistory);
        }
    }

    public Boolean getEnableTriggerRecord() {
        return enableTriggerRecord;
    }

    public void setEnableTriggerRecord(Boolean enableTriggerRecord) {
        this.enableTriggerRecord = enableTriggerRecord;
    }

    /**
     * Defines the ordering to find last {@code ChannelRecordHistory}.
     */
    private static final Ordering<ChannelRecordHistory> recordHistoryOrdering = new Ordering<ChannelRecordHistory>() {
        @Override
        public int compare(ChannelRecordHistory left, ChannelRecordHistory right) {
            return Ints.compare(left.getId(), right.getId());
        }
    };

    /**
     * Returns the last {@code ChannelRecordHistory}.
     */
    @JsonIgnore
    public Optional<ChannelRecordHistory> getLastRecord() {
        return recordHistories != null && !recordHistories.isEmpty()
                ? Optional.of(recordHistoryOrdering.max(recordHistories))
                : Optional.<ChannelRecordHistory>absent();
    }

    public Optional<ChannelRecordHistory> getHistoryByStartTime(long startTime) {
        if (recordHistories != null) {
            recordHistories.size(); //trigger loading data
            for (ChannelRecordHistory history : recordHistories) {
                if (history.getEndTime() == null || startTime <= history.getEndTime().getTime()) {
                    return Optional.of(history);
                }
            }
        }
        return Optional.absent();
    }

    public void setEndTimeOfLastRecordHistory() {
        Optional<ChannelRecordHistory> recordHistoryOptional = getLastRecord();
        if (recordHistoryOptional.isPresent()) {
            recordHistoryOptional.get().end();
        }
    }


    /**
     * Returns the file name of the recorded ip-stream.
     * <p>The name is compose as below formatter:
     * <ul>
     * <li><code>name</code>-<code>programId</code>-<code>audioId</code>.{@link #getRecordExtensionByRecordFormat()}</li>
     * </ul>
     * </p>
     *
     * @return the name of recorded file
     */
    @JsonIgnore
    public String getRecordFileName() {
        return recordFileName == null ? (getEncodeName() + "." + getRecordExtensionByRecordFormat())
                : recordFileName;
    }

    @JsonIgnore
    public String getEncodeName(){
        return DigestUtils.md2Hex(this.name);
    }

    /**
     * Returns the file name of the recorded by {@code transcoder} used.
     */
    @JsonIgnore
    public String getTranscoderRecordFileName() {
        RecordFormat format = getRecordFormatAsEnum();
        return format == RecordFormat.MP4 ? (getEncodeName() + "." + format.getFormat()) : getEncodeName();
    }

    public String getOrigchannelid(){return origchannelid;}
    public void setOrigchannelid(String origchannelid){this.origchannelid=origchannelid;}

}
