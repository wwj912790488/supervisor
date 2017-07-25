package com.arcsoft.supervisor.model.domain.alarm;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QAlarmPushLog is a Querydsl query type for AlarmPushLog
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAlarmPushLog extends EntityPathBase<AlarmPushLog> {

    private static final long serialVersionUID = 1021399156L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlarmPushLog alarmPushLog = new QAlarmPushLog("alarmPushLog");

    public final QAlarmConfig alarmConfig;

    public final NumberPath<Integer> channelId = createNumber("channelId", Integer.class);

    public final StringPath channelName = createString("channelName");

    public final NumberPath<Long> endTime = createNumber("endTime", Long.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> startTime = createNumber("startTime", Long.class);

    public final NumberPath<Integer> type = createNumber("type", Integer.class);

    public QAlarmPushLog(String variable) {
        this(AlarmPushLog.class, forVariable(variable), INITS);
    }

    public QAlarmPushLog(Path<? extends AlarmPushLog> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAlarmPushLog(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAlarmPushLog(PathMetadata<?> metadata, PathInits inits) {
        this(AlarmPushLog.class, metadata, inits);
    }

    public QAlarmPushLog(Class<? extends AlarmPushLog> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.alarmConfig = inits.isInitialized("alarmConfig") ? new QAlarmConfig(forProperty("alarmConfig"), inits.get("alarmConfig")) : null;
    }

}

