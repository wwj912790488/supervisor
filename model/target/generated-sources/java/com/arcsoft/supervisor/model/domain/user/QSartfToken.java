package com.arcsoft.supervisor.model.domain.user;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QSartfToken is a Querydsl query type for SartfToken
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSartfToken extends EntityPathBase<SartfToken> {

    private static final long serialVersionUID = 2075620232L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSartfToken sartfToken = new QSartfToken("sartfToken");

    public final NumberPath<Integer> count = createNumber("count", Integer.class);

    public final NumberPath<Long> create_time = createNumber("create_time", Long.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final QSartfUser user;

    public QSartfToken(String variable) {
        this(SartfToken.class, forVariable(variable), INITS);
    }

    public QSartfToken(Path<? extends SartfToken> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSartfToken(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSartfToken(PathMetadata<?> metadata, PathInits inits) {
        this(SartfToken.class, metadata, inits);
    }

    public QSartfToken(Class<? extends SartfToken> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QSartfUser(forProperty("user"), inits.get("user")) : null;
    }

}

