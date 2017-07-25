package com.arcsoft.supervisor.model.domain.server;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QServerGroup is a Querydsl query type for ServerGroup
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QServerGroup extends EntityPathBase<ServerGroup> {

    private static final long serialVersionUID = 862186575L;

    public static final QServerGroup serverGroup = new QServerGroup("serverGroup");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> type = createNumber("type", Integer.class);

    public QServerGroup(String variable) {
        super(ServerGroup.class, forVariable(variable));
    }

    public QServerGroup(Path<? extends ServerGroup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QServerGroup(PathMetadata<?> metadata) {
        super(ServerGroup.class, metadata);
    }

}

