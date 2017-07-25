package com.arcsoft.supervisor.model.domain.server;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QSartfOpsServer is a Querydsl query type for SartfOpsServer
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSartfOpsServer extends EntityPathBase<SartfOpsServer> {

    private static final long serialVersionUID = -879676500L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSartfOpsServer sartfOpsServer = new QSartfOpsServer("sartfOpsServer");

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

    public final com.arcsoft.supervisor.model.domain.user.QUser user;

    public QSartfOpsServer(String variable) {
        this(SartfOpsServer.class, forVariable(variable), INITS);
    }

    public QSartfOpsServer(Path<? extends SartfOpsServer> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSartfOpsServer(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSartfOpsServer(PathMetadata<?> metadata, PathInits inits) {
        this(SartfOpsServer.class, metadata, inits);
    }

    public QSartfOpsServer(Class<? extends SartfOpsServer> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.arcsoft.supervisor.model.domain.user.QUser(forProperty("user"), inits.get("user")) : null;
    }

}

