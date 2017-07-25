package com.arcsoft.supervisor.model.domain.task;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTask is a Querydsl query type for Task
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTask extends EntityPathBase<Task> {

    private static final long serialVersionUID = 2008362256L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTask task = new QTask("task");

    public final NumberPath<Integer> gpudIndex = createNumber("gpudIndex", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> pid = createNumber("pid", Integer.class);

    public final QTaskProfile profile;

    public final NumberPath<Integer> referenceId = createNumber("referenceId", Integer.class);

    public final StringPath serverId = createString("serverId");

    public final StringPath status = createString("status");

    public final ListPath<TaskInputGpuUsage, QTaskInputGpuUsage> taskInputGpuUsages = this.<TaskInputGpuUsage, QTaskInputGpuUsage>createList("taskInputGpuUsages", TaskInputGpuUsage.class, QTaskInputGpuUsage.class, PathInits.DIRECT2);

    public final ListPath<TaskPort, QTaskPort> taskPorts = this.<TaskPort, QTaskPort>createList("taskPorts", TaskPort.class, QTaskPort.class, PathInits.DIRECT2);

    public final NumberPath<Integer> type = createNumber("type", Integer.class);

    public QTask(String variable) {
        this(Task.class, forVariable(variable), INITS);
    }

    public QTask(Path<? extends Task> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTask(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTask(PathMetadata<?> metadata, PathInits inits) {
        this(Task.class, metadata, inits);
    }

    public QTask(Class<? extends Task> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.profile = inits.isInitialized("profile") ? new QTaskProfile(forProperty("profile"), inits.get("profile")) : null;
    }

}

