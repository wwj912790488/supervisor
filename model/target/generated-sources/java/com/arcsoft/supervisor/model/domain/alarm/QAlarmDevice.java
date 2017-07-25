package com.arcsoft.supervisor.model.domain.alarm;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QAlarmDevice is a Querydsl query type for AlarmDevice
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAlarmDevice extends EntityPathBase<AlarmDevice> {

    private static final long serialVersionUID = -1987854740L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlarmDevice alarmDevice = new QAlarmDevice("alarmDevice");

    public final StringPath channelId = createString("channelId");

    public final StringPath deviceType = createString("deviceType");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath tags = createString("tags");

    public final com.arcsoft.supervisor.model.domain.user.QUser user;

    public QAlarmDevice(String variable) {
        this(AlarmDevice.class, forVariable(variable), INITS);
    }

    public QAlarmDevice(Path<? extends AlarmDevice> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAlarmDevice(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAlarmDevice(PathMetadata<?> metadata, PathInits inits) {
        this(AlarmDevice.class, metadata, inits);
    }

    public QAlarmDevice(Class<? extends AlarmDevice> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.arcsoft.supervisor.model.domain.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

