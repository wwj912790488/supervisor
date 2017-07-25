package com.arcsoft.supervisor.model.domain.server;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QServerComponent is a Querydsl query type for ServerComponent
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QServerComponent extends EntityPathBase<ServerComponent> {

    private static final long serialVersionUID = -788354355L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QServerComponent serverComponent = new QServerComponent("serverComponent");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final QServer server;

    public final NumberPath<Integer> total = createNumber("total", Integer.class);

    public final EnumPath<ComponentType> type = createEnum("type", ComponentType.class);

    public final NumberPath<Integer> usage = createNumber("usage", Integer.class);

    public QServerComponent(String variable) {
        this(ServerComponent.class, forVariable(variable), INITS);
    }

    public QServerComponent(Path<? extends ServerComponent> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QServerComponent(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QServerComponent(PathMetadata<?> metadata, PathInits inits) {
        this(ServerComponent.class, metadata, inits);
    }

    public QServerComponent(Class<? extends ServerComponent> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.server = inits.isInitialized("server") ? new QServer(forProperty("server")) : null;
    }

}

