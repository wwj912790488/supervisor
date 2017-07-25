package com.arcsoft.supervisor.model.domain.task;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTaskInputGpuUsage is a Querydsl query type for TaskInputGpuUsage
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTaskInputGpuUsage extends EntityPathBase<TaskInputGpuUsage> {

    private static final long serialVersionUID = -1681680625L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTaskInputGpuUsage taskInputGpuUsage = new QTaskInputGpuUsage("taskInputGpuUsage");

    public final NumberPath<Integer> gpu = createNumber("gpu", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> input = createNumber("input", Integer.class);

    public final QTask task;

    public QTaskInputGpuUsage(String variable) {
        this(TaskInputGpuUsage.class, forVariable(variable), INITS);
    }

    public QTaskInputGpuUsage(Path<? extends TaskInputGpuUsage> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaskInputGpuUsage(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaskInputGpuUsage(PathMetadata<?> metadata, PathInits inits) {
        this(TaskInputGpuUsage.class, metadata, inits);
    }

    public QTaskInputGpuUsage(Class<? extends TaskInputGpuUsage> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.task = inits.isInitialized("task") ? new QTask(forProperty("task"), inits.get("task")) : null;
    }

}

