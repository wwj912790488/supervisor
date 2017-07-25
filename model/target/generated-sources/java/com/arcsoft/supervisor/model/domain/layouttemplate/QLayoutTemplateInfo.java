package com.arcsoft.supervisor.model.domain.layouttemplate;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QLayoutTemplateInfo is a Querydsl query type for LayoutTemplateInfo
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLayoutTemplateInfo extends EntityPathBase<LayoutTemplateInfo> {

    private static final long serialVersionUID = 698141662L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLayoutTemplateInfo layoutTemplateInfo = new QLayoutTemplateInfo("layoutTemplateInfo");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QLayoutTemplate template;

    public final NumberPath<Integer> totalHeight = createNumber("totalHeight", Integer.class);

    public final NumberPath<Integer> totalWidth = createNumber("totalWidth", Integer.class);

    public QLayoutTemplateInfo(String variable) {
        this(LayoutTemplateInfo.class, forVariable(variable), INITS);
    }

    public QLayoutTemplateInfo(Path<? extends LayoutTemplateInfo> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLayoutTemplateInfo(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLayoutTemplateInfo(PathMetadata<?> metadata, PathInits inits) {
        this(LayoutTemplateInfo.class, metadata, inits);
    }

    public QLayoutTemplateInfo(Class<? extends LayoutTemplateInfo> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.template = inits.isInitialized("template") ? new QLayoutTemplate(forProperty("template"), inits.get("template")) : null;
    }

}

