package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QChannelAlarmTime is a Querydsl query type for ChannelAlarmTime
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelAlarmTime extends EntityPathBase<ChannelAlarmTime> {

    private static final long serialVersionUID = 1094011844L;

    public static final QChannelAlarmTime channelAlarmTime = new QChannelAlarmTime("channelAlarmTime");

    public final StringPath alarmEndTime1 = createString("alarmEndTime1");

    public final StringPath alarmEndTime2 = createString("alarmEndTime2");

    public final StringPath alarmEndTime3 = createString("alarmEndTime3");

    public final StringPath alarmEndTime4 = createString("alarmEndTime4");

    public final StringPath alarmEndTime5 = createString("alarmEndTime5");

    public final StringPath alarmStartTime1 = createString("alarmStartTime1");

    public final StringPath alarmStartTime2 = createString("alarmStartTime2");

    public final StringPath alarmStartTime3 = createString("alarmStartTime3");

    public final StringPath alarmStartTime4 = createString("alarmStartTime4");

    public final StringPath alarmStartTime5 = createString("alarmStartTime5");

    public final BooleanPath enableTime1 = createBoolean("enableTime1");

    public final BooleanPath enableTime2 = createBoolean("enableTime2");

    public final BooleanPath enableTime3 = createBoolean("enableTime3");

    public final BooleanPath enableTime4 = createBoolean("enableTime4");

    public final BooleanPath enableTime5 = createBoolean("enableTime5");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public QChannelAlarmTime(String variable) {
        super(ChannelAlarmTime.class, forVariable(variable));
    }

    public QChannelAlarmTime(Path<? extends ChannelAlarmTime> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChannelAlarmTime(PathMetadata<?> metadata) {
        super(ChannelAlarmTime.class, metadata);
    }

}

