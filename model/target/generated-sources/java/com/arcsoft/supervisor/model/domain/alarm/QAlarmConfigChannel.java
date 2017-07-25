package com.arcsoft.supervisor.model.domain.alarm;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QAlarmConfigChannel is a Querydsl query type for AlarmConfigChannel
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAlarmConfigChannel extends EntityPathBase<AlarmConfigChannel> {

    private static final long serialVersionUID = -227148149L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlarmConfigChannel alarmConfigChannel = new QAlarmConfigChannel("alarmConfigChannel");

    public final QAlarmConfig alarmConfig;

    public final com.arcsoft.supervisor.model.domain.channel.QChannel channel;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public QAlarmConfigChannel(String variable) {
        this(AlarmConfigChannel.class, forVariable(variable), INITS);
    }

    public QAlarmConfigChannel(Path<? extends AlarmConfigChannel> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAlarmConfigChannel(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAlarmConfigChannel(PathMetadata<?> metadata, PathInits inits) {
        this(AlarmConfigChannel.class, metadata, inits);
    }

    public QAlarmConfigChannel(Class<? extends AlarmConfigChannel> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.alarmConfig = inits.isInitialized("alarmConfig") ? new QAlarmConfig(forProperty("alarmConfig"), inits.get("alarmConfig")) : null;
        this.channel = inits.isInitialized("channel") ? new com.arcsoft.supervisor.model.domain.channel.QChannel(forProperty("channel"), inits.get("channel")) : null;
    }

}

