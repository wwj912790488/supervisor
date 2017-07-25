package com.arcsoft.supervisor.model.domain.alarm;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QAlarmPushedLogInfo is a Querydsl query type for AlarmPushedLogInfo
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAlarmPushedLogInfo extends EntityPathBase<AlarmPushedLogInfo> {

    private static final long serialVersionUID = 1844055491L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlarmPushedLogInfo alarmPushedLogInfo = new QAlarmPushedLogInfo("alarmPushedLogInfo");

    public final QAlarmDevice alarmDevice;

    public final QAlarmPushLog alarmPushLog;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath msgForAll = createBoolean("msgForAll");

    public final StringPath msgId = createString("msgId");

    public final NumberPath<Long> msgSendTime = createNumber("msgSendTime", Long.class);

    public QAlarmPushedLogInfo(String variable) {
        this(AlarmPushedLogInfo.class, forVariable(variable), INITS);
    }

    public QAlarmPushedLogInfo(Path<? extends AlarmPushedLogInfo> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAlarmPushedLogInfo(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAlarmPushedLogInfo(PathMetadata<?> metadata, PathInits inits) {
        this(AlarmPushedLogInfo.class, metadata, inits);
    }

    public QAlarmPushedLogInfo(Class<? extends AlarmPushedLogInfo> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.alarmDevice = inits.isInitialized("alarmDevice") ? new QAlarmDevice(forProperty("alarmDevice"), inits.get("alarmDevice")) : null;
        this.alarmPushLog = inits.isInitialized("alarmPushLog") ? new QAlarmPushLog(forProperty("alarmPushLog"), inits.get("alarmPushLog")) : null;
    }

}

