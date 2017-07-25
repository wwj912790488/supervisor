package com.arcsoft.supervisor.model.domain.user;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -2127943472L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final QAbstractUser _super = new QAbstractUser(this);

    public final com.arcsoft.supervisor.model.domain.alarm.QAlarmConfig alarmConfig;

    //inherited
    public final NumberPath<Integer> id = _super.id;

    //inherited
    public final StringPath password = _super.password;

    public final StringPath phoneNumber = createString("phoneNumber");

    //inherited
    public final StringPath realName = _super.realName;

    //inherited
    public final NumberPath<Integer> role = _super.role;

    //inherited
    public final StringPath userName = _super.userName;

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUser(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUser(PathMetadata<?> metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.alarmConfig = inits.isInitialized("alarmConfig") ? new com.arcsoft.supervisor.model.domain.alarm.QAlarmConfig(forProperty("alarmConfig"), inits.get("alarmConfig")) : null;
    }

}

