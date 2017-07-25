package com.arcsoft.supervisor.model.domain.log;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QContentDetectLog is a Querydsl query type for ContentDetectLog
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QContentDetectLog extends EntityPathBase<ContentDetectLog> {

    private static final long serialVersionUID = 1155958992L;

    public static final QContentDetectLog contentDetectLog = new QContentDetectLog("contentDetectLog");

    public final NumberPath<Integer> channelId = createNumber("channelId", Integer.class);

    public final StringPath channelName = createString("channelName");

    public final DateTimePath<java.util.Date> confirmdate = createDateTime("confirmdate", java.util.Date.class);

    public final NumberPath<Long> endTime = createNumber("endTime", Long.class);

    public final StringPath guid = createString("guid");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> soundTrack = createNumber("soundTrack", Integer.class);

    public final NumberPath<Long> startOffset = createNumber("startOffset", Long.class);

    public final NumberPath<Long> startTime = createNumber("startTime", Long.class);

    public final NumberPath<Integer> taskId = createNumber("taskId", Integer.class);

    public final NumberPath<Integer> type = createNumber("type", Integer.class);

    public final StringPath videoFilePath = createString("videoFilePath");

    public QContentDetectLog(String variable) {
        super(ContentDetectLog.class, forVariable(variable));
    }

    public QContentDetectLog(Path<? extends ContentDetectLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QContentDetectLog(PathMetadata<?> metadata) {
        super(ContentDetectLog.class, metadata);
    }

}

