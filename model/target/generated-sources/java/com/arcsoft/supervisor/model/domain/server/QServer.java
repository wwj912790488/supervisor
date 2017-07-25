package com.arcsoft.supervisor.model.domain.server;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QServer is a Querydsl query type for Server
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QServer extends EntityPathBase<Server> {

    private static final long serialVersionUID = 1899897872L;

    public static final QServer server = new QServer("server");

    public final StringPath activeFunctions = createString("activeFunctions");

    public final BooleanPath alive = createBoolean("alive");

    public final StringPath eth = createString("eth");

    public final StringPath functions = createString("functions");

    public final StringPath gateway = createString("gateway");

    public final NumberPath<Integer> gpus = createNumber("gpus", Integer.class);

    public final StringPath id = createString("id");

    public final StringPath ip = createString("ip");

    public final BooleanPath joined = createBoolean("joined");

    public final StringPath name = createString("name");

    public final StringPath netmask = createString("netmask");

    public final NumberPath<Integer> port = createNumber("port", Integer.class);

    public final StringPath remark = createString("remark");

    public final NumberPath<Integer> state = createNumber("state", Integer.class);

    public final NumberPath<Integer> type = createNumber("type", Integer.class);

    public QServer(String variable) {
        super(Server.class, forVariable(variable));
    }

    public QServer(Path<? extends Server> path) {
        super(path.getType(), path.getMetadata());
    }

    public QServer(PathMetadata<?> metadata) {
        super(Server.class, metadata);
    }

}

