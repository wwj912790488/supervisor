package com.arcsoft.supervisor.model.domain.user;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QSartfUser is a Querydsl query type for SartfUser
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSartfUser extends EntityPathBase<SartfUser> {

    private static final long serialVersionUID = -1872673700L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSartfUser sartfUser = new QSartfUser("sartfUser");

    public final QAbstractUser _super = new QAbstractUser(this);

    public final ListPath<com.arcsoft.supervisor.model.domain.userconfig.UserConfig, com.arcsoft.supervisor.model.domain.userconfig.QUserConfig> configs = this.<com.arcsoft.supervisor.model.domain.userconfig.UserConfig, com.arcsoft.supervisor.model.domain.userconfig.QUserConfig>createList("configs", com.arcsoft.supervisor.model.domain.userconfig.UserConfig.class, com.arcsoft.supervisor.model.domain.userconfig.QUserConfig.class, PathInits.DIRECT2);

    public final com.arcsoft.supervisor.model.domain.userconfig.QUserConfig current;

    //inherited
    public final NumberPath<Integer> id = _super.id;

    public final com.arcsoft.supervisor.model.domain.task.QUserTaskInfo info;

    public final com.arcsoft.supervisor.model.domain.server.QSartfOpsServer ops;

    //inherited
    public final StringPath password = _super.password;

    //inherited
    public final StringPath realName = _super.realName;

    //inherited
    public final NumberPath<Integer> role = _super.role;

    //inherited
    public final StringPath userName = _super.userName;

    public QSartfUser(String variable) {
        this(SartfUser.class, forVariable(variable), INITS);
    }

    public QSartfUser(Path<? extends SartfUser> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSartfUser(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSartfUser(PathMetadata<?> metadata, PathInits inits) {
        this(SartfUser.class, metadata, inits);
    }

    public QSartfUser(Class<? extends SartfUser> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.current = inits.isInitialized("current") ? new com.arcsoft.supervisor.model.domain.userconfig.QUserConfig(forProperty("current"), inits.get("current")) : null;
        this.info = inits.isInitialized("info") ? new com.arcsoft.supervisor.model.domain.task.QUserTaskInfo(forProperty("info"), inits.get("info")) : null;
        this.ops = inits.isInitialized("ops") ? new com.arcsoft.supervisor.model.domain.server.QSartfOpsServer(forProperty("ops"), inits.get("ops")) : null;
    }

}

