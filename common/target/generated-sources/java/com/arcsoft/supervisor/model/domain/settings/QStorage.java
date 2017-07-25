package com.arcsoft.supervisor.model.domain.settings;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QStorage is a Querydsl query type for Storage
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QStorage extends EntityPathBase<Storage> {

    private static final long serialVersionUID = 286975150L;

    public static final QStorage storage = new QStorage("storage");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final StringPath path = createString("path");

    public final StringPath pwd = createString("pwd");

    public final StringPath type = createString("type");

    public final StringPath user = createString("user");

    public QStorage(String variable) {
        super(Storage.class, forVariable(variable));
    }

    public QStorage(Path<? extends Storage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QStorage(PathMetadata<?> metadata) {
        super(Storage.class, metadata);
    }

}

