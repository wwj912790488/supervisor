package com.arcsoft.supervisor.model.domain.layouttemplate;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QLayoutTemplateCell is a Querydsl query type for LayoutTemplateCell
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLayoutTemplateCell extends EntityPathBase<LayoutTemplateCell> {

    private static final long serialVersionUID = 697954450L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLayoutTemplateCell layoutTemplateCell = new QLayoutTemplateCell("layoutTemplateCell");

    public final NumberPath<Integer> cellIndex = createNumber("cellIndex", Integer.class);

    public final NumberPath<Integer> height = createNumber("height", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QLayoutTemplate template;

    public final NumberPath<Integer> width = createNumber("width", Integer.class);

    public final NumberPath<Integer> xPos = createNumber("xPos", Integer.class);

    public final NumberPath<Integer> yPos = createNumber("yPos", Integer.class);

    public QLayoutTemplateCell(String variable) {
        this(LayoutTemplateCell.class, forVariable(variable), INITS);
    }

    public QLayoutTemplateCell(Path<? extends LayoutTemplateCell> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLayoutTemplateCell(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLayoutTemplateCell(PathMetadata<?> metadata, PathInits inits) {
        this(LayoutTemplateCell.class, metadata, inits);
    }

    public QLayoutTemplateCell(Class<? extends LayoutTemplateCell> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.template = inits.isInitialized("template") ? new QLayoutTemplate(forProperty("template"), inits.get("template")) : null;
    }

}

