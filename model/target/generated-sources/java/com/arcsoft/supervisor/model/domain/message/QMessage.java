package com.arcsoft.supervisor.model.domain.message;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMessage is a Querydsl query type for Message
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMessage extends EntityPathBase<Message> {

    private static final long serialVersionUID = -820771806L;

    public static final QMessage message1 = new QMessage("message1");

    public final StringPath dateTime = createString("dateTime");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath ipAddress = createString("ipAddress");

    public final StringPath message = createString("message");

    public final DateTimePath<java.util.Date> realDateTime = createDateTime("realDateTime", java.util.Date.class);

    public final StringPath realName = createString("realName");

    public final StringPath userName = createString("userName");

    public QMessage(String variable) {
        super(Message.class, forVariable(variable));
    }

    public QMessage(Path<? extends Message> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMessage(PathMetadata<?> metadata) {
        super(Message.class, metadata);
    }

}

