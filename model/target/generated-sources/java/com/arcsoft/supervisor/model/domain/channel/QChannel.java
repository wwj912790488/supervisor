package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QChannel is a Querydsl query type for Channel
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannel extends EntityPathBase<Channel> {

    private static final long serialVersionUID = -1367981542L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChannel channel = new QChannel("channel");

    public final StringPath apiHeart = createString("apiHeart");

    public final StringPath audioId = createString("audioId");

    public final QChannelAlarmTime channelAlarmTime;

    public final QChannelInfo channelInfo;

    public final QChannelContentDetectConfig contentDetectConfig;

    public final DateTimePath<java.util.Date> createDate = createDateTime("createDate", java.util.Date.class);

    public final BooleanPath enableContentDetect = createBoolean("enableContentDetect");

    public final BooleanPath enableRecord = createBoolean("enableRecord");

    public final BooleanPath enableSignalDetect = createBoolean("enableSignalDetect");

    public final BooleanPath enableSignalDetectByType = createBoolean("enableSignalDetectByType");

    public final BooleanPath enableTriggerRecord = createBoolean("enableTriggerRecord");

    public final QChannelGroup group;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath ip = createString("ip");

    public final BooleanPath isSupportMobile = createBoolean("isSupportMobile");

    public final NumberPath<Byte> maxPersistDays = createNumber("maxPersistDays", Byte.class);

    public final ListPath<ChannelMobileConfig, QChannelMobileConfig> mobileConfigs = this.<ChannelMobileConfig, QChannelMobileConfig>createList("mobileConfigs", ChannelMobileConfig.class, QChannelMobileConfig.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath origchannelid = createString("origchannelid");

    public final NumberPath<Integer> port = createNumber("port", Integer.class);

    public final StringPath programId = createString("programId");

    public final StringPath protocol = createString("protocol");

    public final StringPath recordBasePath = createString("recordBasePath");

    public final NumberPath<Byte> recordFormat = createNumber("recordFormat", Byte.class);

    public final ListPath<ChannelRecordHistory, QChannelRecordHistory> recordHistories = this.<ChannelRecordHistory, QChannelRecordHistory>createList("recordHistories", ChannelRecordHistory.class, QChannelRecordHistory.class, PathInits.DIRECT2);

    public final QChannelSignalDetectTypeConfig signalDetectByTypeConfig;

    public final QChannelSignalDetectConfig signalDetectConfig;

    public final ListPath<ChannelTag, QChannelTag> tags = this.<ChannelTag, QChannelTag>createList("tags", ChannelTag.class, QChannelTag.class, PathInits.DIRECT2);

    public QChannel(String variable) {
        this(Channel.class, forVariable(variable), INITS);
    }

    public QChannel(Path<? extends Channel> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QChannel(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QChannel(PathMetadata<?> metadata, PathInits inits) {
        this(Channel.class, metadata, inits);
    }

    public QChannel(Class<? extends Channel> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.channelAlarmTime = inits.isInitialized("channelAlarmTime") ? new QChannelAlarmTime(forProperty("channelAlarmTime")) : null;
        this.channelInfo = inits.isInitialized("channelInfo") ? new QChannelInfo(forProperty("channelInfo")) : null;
        this.contentDetectConfig = inits.isInitialized("contentDetectConfig") ? new QChannelContentDetectConfig(forProperty("contentDetectConfig")) : null;
        this.group = inits.isInitialized("group") ? new QChannelGroup(forProperty("group")) : null;
        this.signalDetectByTypeConfig = inits.isInitialized("signalDetectByTypeConfig") ? new QChannelSignalDetectTypeConfig(forProperty("signalDetectByTypeConfig")) : null;
        this.signalDetectConfig = inits.isInitialized("signalDetectConfig") ? new QChannelSignalDetectConfig(forProperty("signalDetectConfig")) : null;
    }

}

