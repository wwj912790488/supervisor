package com.arcsoft.supervisor.model.domain.master;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMaster is a Querydsl query type for Master
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMaster extends EntityPathBase<Master> {

    private static final long serialVersionUID = 1564619568L;

    public static final QMaster master = new QMaster("master");

    public final StringPath backupAdress = createString("backupAdress");

    public final StringPath backupFlag = createString("backupFlag");

    public final NumberPath<Integer> flag = createNumber("flag", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath ip = createString("ip");

    public final StringPath passWord = createString("passWord");

    public final NumberPath<Integer> port = createNumber("port", Integer.class);

    public final StringPath userName = createString("userName");

    public QMaster(String variable) {
        super(Master.class, forVariable(variable));
    }

    public QMaster(Path<? extends Master> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMaster(PathMetadata<?> metadata) {
        super(Master.class, metadata);
    }

}

