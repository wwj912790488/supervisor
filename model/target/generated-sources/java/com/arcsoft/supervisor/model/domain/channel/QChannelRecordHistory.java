package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QChannelRecordHistory is a Querydsl query type for ChannelRecordHistory
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelRecordHistory extends EntityPathBase<ChannelRecordHistory> {

    private static final long serialVersionUID = 1141533449L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChannelRecordHistory channelRecordHistory = new QChannelRecordHistory("channelRecordHistory");

    public final QChannel channel;

    public final DateTimePath<java.util.Date> endTime = createDateTime("endTime", java.util.Date.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath recordBasePath = createString("recordBasePath");

    public final DateTimePath<java.util.Date> startTime = createDateTime("startTime", java.util.Date.class);

    public QChannelRecordHistory(String variable) {
        this(ChannelRecordHistory.class, forVariable(variable), INITS);
    }

    public QChannelRecordHistory(Path<? extends ChannelRecordHistory> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QChannelRecordHistory(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QChannelRecordHistory(PathMetadata<?> metadata, PathInits inits) {
        this(ChannelRecordHistory.class, metadata, inits);
    }

    public QChannelRecordHistory(Class<? extends ChannelRecordHistory> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.channel = inits.isInitialized("channel") ? new QChannel(forProperty("channel"), inits.get("channel")) : null;
    }

}

