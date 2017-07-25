package com.arcsoft.supervisor.model.domain.task;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QUserTaskInfo is a Querydsl query type for UserTaskInfo
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUserTaskInfo extends EntityPathBase<UserTaskInfo> {

    private static final long serialVersionUID = 1535333705L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserTaskInfo userTaskInfo = new QUserTaskInfo("userTaskInfo");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.util.Date> lastUpdate = createDateTime("lastUpdate", java.util.Date.class);

    public final StringPath rtspMobileFileName = createString("rtspMobileFileName");

    public final StringPath rtspOpsFileName = createString("rtspOpsFileName");

    public final QTask task;

    public final com.arcsoft.supervisor.model.domain.user.QSartfUser user;

    public QUserTaskInfo(String variable) {
        this(UserTaskInfo.class, forVariable(variable), INITS);
    }

    public QUserTaskInfo(Path<? extends UserTaskInfo> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUserTaskInfo(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUserTaskInfo(PathMetadata<?> metadata, PathInits inits) {
        this(UserTaskInfo.class, metadata, inits);
    }

    public QUserTaskInfo(Class<? extends UserTaskInfo> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.task = inits.isInitialized("task") ? new QTask(forProperty("task"), inits.get("task")) : null;
        this.user = inits.isInitialized("user") ? new com.arcsoft.supervisor.model.domain.user.QSartfUser(forProperty("user"), inits.get("user")) : null;
    }

}

