package com.arcsoft.supervisor.model.domain.graphic;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QScreenSchema is a Querydsl query type for ScreenSchema
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QScreenSchema extends EntityPathBase<ScreenSchema> {

    private static final long serialVersionUID = -763496271L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QScreenSchema screenSchema = new QScreenSchema("screenSchema");

    public final NumberPath<Integer> columnCount = createNumber("columnCount", Integer.class);

    public final NumberPath<Integer> groupCount = createNumber("groupCount", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> rowCount = createNumber("rowCount", Integer.class);

    public final QScreen screen;

    public final ListPath<ScreenPosition, QScreenPosition> screenPositions = this.<ScreenPosition, QScreenPosition>createList("screenPositions", ScreenPosition.class, QScreenPosition.class, PathInits.DIRECT2);

    public final NumberPath<Integer> switchTime = createNumber("switchTime", Integer.class);

    public final com.arcsoft.supervisor.model.domain.layouttemplate.QLayoutPositionTemplate template;

    public final NumberPath<Integer> value = createNumber("value", Integer.class);

    public QScreenSchema(String variable) {
        this(ScreenSchema.class, forVariable(variable), INITS);
    }

    public QScreenSchema(Path<? extends ScreenSchema> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QScreenSchema(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QScreenSchema(PathMetadata<?> metadata, PathInits inits) {
        this(ScreenSchema.class, metadata, inits);
    }

    public QScreenSchema(Class<? extends ScreenSchema> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.screen = inits.isInitialized("screen") ? new QScreen(forProperty("screen"), inits.get("screen")) : null;
        this.template = inits.isInitialized("template") ? new com.arcsoft.supervisor.model.domain.layouttemplate.QLayoutPositionTemplate(forProperty("template")) : null;
    }

}

