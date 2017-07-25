package com.arcsoft.supervisor.model.domain.layouttemplate;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QLayoutPositionTemplate is a Querydsl query type for LayoutPositionTemplate
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLayoutPositionTemplate extends EntityPathBase<LayoutPositionTemplate> {

    private static final long serialVersionUID = -221066919L;

    public static final QLayoutPositionTemplate layoutPositionTemplate = new QLayoutPositionTemplate("layoutPositionTemplate");

    public final NumberPath<Integer> columnCount = createNumber("columnCount", Integer.class);

    public final StringPath guid = createString("guid");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ListPath<LayoutPosition, QLayoutPosition> positions = this.<LayoutPosition, QLayoutPosition>createList("positions", LayoutPosition.class, QLayoutPosition.class, PathInits.DIRECT2);

    public final NumberPath<Integer> rowCount = createNumber("rowCount", Integer.class);

    public QLayoutPositionTemplate(String variable) {
        super(LayoutPositionTemplate.class, forVariable(variable));
    }

    public QLayoutPositionTemplate(Path<? extends LayoutPositionTemplate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLayoutPositionTemplate(PathMetadata<?> metadata) {
        super(LayoutPositionTemplate.class, metadata);
    }

}

