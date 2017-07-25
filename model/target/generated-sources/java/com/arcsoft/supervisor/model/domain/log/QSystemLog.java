package com.arcsoft.supervisor.model.domain.log;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QSystemLog is a Querydsl query type for SystemLog
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSystemLog extends EntityPathBase<SystemLog> {

    private static final long serialVersionUID = 47274989L;

    public static final QSystemLog systemLog = new QSystemLog("systemLog");

    public final StringPath dateTime = createString("dateTime");

    public final NumberPath<Integer> funcType = createNumber("funcType", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath operationInfo = createString("operationInfo");

    public final StringPath operationResult = createString("operationResult");

    public final DateTimePath<java.util.Date> realDateTime = createDateTime("realDateTime", java.util.Date.class);

    public final StringPath userName = createString("userName");

    public QSystemLog(String variable) {
        super(SystemLog.class, forVariable(variable));
    }

    public QSystemLog(Path<? extends SystemLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSystemLog(PathMetadata<?> metadata) {
        super(SystemLog.class, metadata);
    }

}

