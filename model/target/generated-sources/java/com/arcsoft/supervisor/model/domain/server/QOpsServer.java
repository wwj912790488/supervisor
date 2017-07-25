package com.arcsoft.supervisor.model.domain.server;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QOpsServer is a Querydsl query type for OpsServer
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QOpsServer extends EntityPathBase<OpsServer> {

    private static final long serialVersionUID = 1295005864L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOpsServer opsServer = new QOpsServer("opsServer");

    public final QAbstractOpsServer _super = new QAbstractOpsServer(this);

    //inherited
    public final StringPath gateway = _super.gateway;

    //inherited
    public final StringPath id = _super.id;

    //inherited
    public final StringPath ip = _super.ip;

    //inherited
    public final StringPath mac = _super.mac;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final StringPath netmask = _super.netmask;

    //inherited
    public final StringPath port = _super.port;

    //inherited
    public final StringPath resolution = _super.resolution;

    //inherited
    public final StringPath supportResolutions = _super.supportResolutions;

    public final com.arcsoft.supervisor.model.domain.graphic.QWallPosition wallPosition;

    public QOpsServer(String variable) {
        this(OpsServer.class, forVariable(variable), INITS);
    }

    public QOpsServer(Path<? extends OpsServer> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QOpsServer(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QOpsServer(PathMetadata<?> metadata, PathInits inits) {
        this(OpsServer.class, metadata, inits);
    }

    public QOpsServer(Class<? extends OpsServer> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.wallPosition = inits.isInitialized("wallPosition") ? new com.arcsoft.supervisor.model.domain.graphic.QWallPosition(forProperty("wallPosition"), inits.get("wallPosition")) : null;
    }

}

