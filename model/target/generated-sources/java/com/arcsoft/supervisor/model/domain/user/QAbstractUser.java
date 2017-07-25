package com.arcsoft.supervisor.model.domain.user;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAbstractUser is a Querydsl query type for AbstractUser
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAbstractUser extends EntityPathBase<AbstractUser> {

    private static final long serialVersionUID = -459942126L;

    public static final QAbstractUser abstractUser = new QAbstractUser("abstractUser");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath password = createString("password");

    public final StringPath realName = createString("realName");

    public final NumberPath<Integer> role = createNumber("role", Integer.class);

    public final StringPath userName = createString("userName");

    public QAbstractUser(String variable) {
        super(AbstractUser.class, forVariable(variable));
    }

    public QAbstractUser(Path<? extends AbstractUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAbstractUser(PathMetadata<?> metadata) {
        super(AbstractUser.class, metadata);
    }

}

