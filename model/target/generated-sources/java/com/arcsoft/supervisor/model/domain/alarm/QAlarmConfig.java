package com.arcsoft.supervisor.model.domain.alarm;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QAlarmConfig is a Querydsl query type for AlarmConfig
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAlarmConfig extends EntityPathBase<AlarmConfig> {

    private static final long serialVersionUID = -2007489704L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAlarmConfig alarmConfig = new QAlarmConfig("alarmConfig");

    public final ListPath<AlarmConfigChannel, QAlarmConfigChannel> channels = this.<AlarmConfigChannel, QAlarmConfigChannel>createList("channels", AlarmConfigChannel.class, QAlarmConfigChannel.class, PathInits.DIRECT2);

    public final BooleanPath enableAudioLoss = createBoolean("enableAudioLoss");

    public final BooleanPath enableBlack = createBoolean("enableBlack");

    public final BooleanPath enableBoomSonic = createBoolean("enableBoomSonic");

    public final BooleanPath enableBroken = createBoolean("enableBroken");

    public final BooleanPath enableCcError = createBoolean("enableCcError");

    public final BooleanPath enablecontentdetect = createBoolean("enablecontentdetect");

    public final BooleanPath enableGreen = createBoolean("enableGreen");

    public final BooleanPath enableLoudVolume = createBoolean("enableLoudVolume");

    public final BooleanPath enableLowVolume = createBoolean("enableLowVolume");

    public final BooleanPath enableNoFrame = createBoolean("enableNoFrame");

    public final BooleanPath enableProgidLoss = createBoolean("enableProgidLoss");

    public final BooleanPath enablesignaldetect = createBoolean("enablesignaldetect");

    public final BooleanPath enableSilence = createBoolean("enableSilence");

    public final BooleanPath enableVideoLoss = createBoolean("enableVideoLoss");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.util.Date> lastUpdate = createDateTime("lastUpdate", java.util.Date.class);

    public final com.arcsoft.supervisor.model.domain.user.QUser user;

    public QAlarmConfig(String variable) {
        this(AlarmConfig.class, forVariable(variable), INITS);
    }

    public QAlarmConfig(Path<? extends AlarmConfig> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAlarmConfig(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QAlarmConfig(PathMetadata<?> metadata, PathInits inits) {
        this(AlarmConfig.class, metadata, inits);
    }

    public QAlarmConfig(Class<? extends AlarmConfig> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.arcsoft.supervisor.model.domain.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

