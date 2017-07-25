package com.arcsoft.supervisor.model.domain.layouttemplate;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QLayoutPosition is a Querydsl query type for LayoutPosition
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLayoutPosition extends EntityPathBase<LayoutPosition> {

    private static final long serialVersionUID = -1587165249L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLayoutPosition layoutPosition = new QLayoutPosition("layoutPosition");

    public final NumberPath<Integer> column = createNumber("column", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> row = createNumber("row", Integer.class);

    public final QLayoutPositionTemplate template;

    public final NumberPath<Integer> x = createNumber("x", Integer.class);

    public final NumberPath<Integer> y = createNumber("y", Integer.class);

    public QLayoutPosition(String variable) {
        this(LayoutPosition.class, forVariable(variable), INITS);
    }

    public QLayoutPosition(Path<? extends LayoutPosition> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLayoutPosition(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLayoutPosition(PathMetadata<?> metadata, PathInits inits) {
        this(LayoutPosition.class, metadata, inits);
    }

    public QLayoutPosition(Class<? extends LayoutPosition> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.template = inits.isInitialized("template") ? new QLayoutPositionTemplate(forProperty("template")) : null;
    }

}

