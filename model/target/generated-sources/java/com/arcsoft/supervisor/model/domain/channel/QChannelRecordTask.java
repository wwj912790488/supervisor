package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QChannelRecordTask is a Querydsl query type for ChannelRecordTask
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelRecordTask extends EntityPathBase<ChannelRecordTask> {

    private static final long serialVersionUID = 1256540976L;

    public static final QChannelRecordTask channelRecordTask = new QChannelRecordTask("channelRecordTask");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> recordChannelId = createNumber("recordChannelId", Integer.class);

    public final NumberPath<Integer> recordTaskId = createNumber("recordTaskId", Integer.class);

    public final NumberPath<Integer> supervisorChannelId = createNumber("supervisorChannelId", Integer.class);

    public QChannelRecordTask(String variable) {
        super(ChannelRecordTask.class, forVariable(variable));
    }

    public QChannelRecordTask(Path<? extends ChannelRecordTask> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChannelRecordTask(PathMetadata<?> metadata) {
        super(ChannelRecordTask.class, metadata);
    }

}

