package com.arcsoft.supervisor.model.domain.log;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QServiceLog is a Querydsl query type for ServiceLog
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QServiceLog extends EntityPathBase<ServiceLog> {

    private static final long serialVersionUID = -901901833L;

    public static final QServiceLog serviceLog = new QServiceLog("serviceLog");

    public final BooleanPath affix = createBoolean("affix");

    public final StringPath description = createString("description");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath ip = createString("ip");

    public final NumberPath<Byte> level = createNumber("level", Byte.class);

    public final NumberPath<Byte> module = createNumber("module", Byte.class);

    public final DateTimePath<java.util.Date> time = createDateTime("time", java.util.Date.class);

    public QServiceLog(String variable) {
        super(ServiceLog.class, forVariable(variable));
    }

    public QServiceLog(Path<? extends ServiceLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QServiceLog(PathMetadata<?> metadata) {
        super(ServiceLog.class, metadata);
    }

}

