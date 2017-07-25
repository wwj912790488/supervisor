package com.arcsoft.supervisor.model.domain.server;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAbstractOpsServer is a Querydsl query type for AbstractOpsServer
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAbstractOpsServer extends EntityPathBase<AbstractOpsServer> {

    private static final long serialVersionUID = 568306598L;

    public static final QAbstractOpsServer abstractOpsServer = new QAbstractOpsServer("abstractOpsServer");

    public final StringPath gateway = createString("gateway");

    public final StringPath id = createString("id");

    public final StringPath ip = createString("ip");

    public final StringPath mac = createString("mac");

    public final StringPath name = createString("name");

    public final StringPath netmask = createString("netmask");

    public final StringPath port = createString("port");

    public final StringPath resolution = createString("resolution");

    public final StringPath supportResolutions = createString("supportResolutions");

    public QAbstractOpsServer(String variable) {
        super(AbstractOpsServer.class, forVariable(variable));
    }

    public QAbstractOpsServer(Path<? extends AbstractOpsServer> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAbstractOpsServer(PathMetadata<?> metadata) {
        super(AbstractOpsServer.class, metadata);
    }

}

