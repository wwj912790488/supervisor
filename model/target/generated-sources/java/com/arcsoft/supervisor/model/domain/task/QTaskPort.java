package com.arcsoft.supervisor.model.domain.task;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTaskPort is a Querydsl query type for TaskPort
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTaskPort extends EntityPathBase<TaskPort> {

    private static final long serialVersionUID = 274608561L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTaskPort taskPort = new QTaskPort("taskPort");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> portNumber = createNumber("portNumber", Integer.class);

    public final QTask task;

    public final EnumPath<TaskPort.PortType> type = createEnum("type", TaskPort.PortType.class);

    public QTaskPort(String variable) {
        this(TaskPort.class, forVariable(variable), INITS);
    }

    public QTaskPort(Path<? extends TaskPort> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaskPort(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaskPort(PathMetadata<?> metadata, PathInits inits) {
        this(TaskPort.class, metadata, inits);
    }

    public QTaskPort(Class<? extends TaskPort> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.task = inits.isInitialized("task") ? new QTask(forProperty("task"), inits.get("task")) : null;
    }

}

