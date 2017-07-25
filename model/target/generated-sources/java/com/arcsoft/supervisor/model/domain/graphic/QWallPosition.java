package com.arcsoft.supervisor.model.domain.graphic;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QWallPosition is a Querydsl query type for WallPosition
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QWallPosition extends EntityPathBase<WallPosition> {

    private static final long serialVersionUID = 33832087L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWallPosition wallPosition = new QWallPosition("wallPosition");

    public final NumberPath<Integer> column = createNumber("column", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final com.arcsoft.supervisor.model.domain.server.QOpsServer opsServer;

    public final StringPath output = createString("output");

    public final NumberPath<Integer> row = createNumber("row", Integer.class);

    public final QScreen screen;

    public final com.arcsoft.supervisor.model.domain.server.QServerComponent sdiOutput;

    public final QWall wall;

    public final StringPath wallName = createString("wallName");

    public QWallPosition(String variable) {
        this(WallPosition.class, forVariable(variable), INITS);
    }

    public QWallPosition(Path<? extends WallPosition> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QWallPosition(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QWallPosition(PathMetadata<?> metadata, PathInits inits) {
        this(WallPosition.class, metadata, inits);
    }

    public QWallPosition(Class<? extends WallPosition> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.opsServer = inits.isInitialized("opsServer") ? new com.arcsoft.supervisor.model.domain.server.QOpsServer(forProperty("opsServer"), inits.get("opsServer")) : null;
        this.screen = inits.isInitialized("screen") ? new QScreen(forProperty("screen"), inits.get("screen")) : null;
        this.sdiOutput = inits.isInitialized("sdiOutput") ? new com.arcsoft.supervisor.model.domain.server.QServerComponent(forProperty("sdiOutput"), inits.get("sdiOutput")) : null;
        this.wall = inits.isInitialized("wall") ? new QWall(forProperty("wall")) : null;
    }

}

