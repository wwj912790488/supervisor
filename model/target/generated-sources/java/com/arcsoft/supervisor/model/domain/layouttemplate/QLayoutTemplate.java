package com.arcsoft.supervisor.model.domain.layouttemplate;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QLayoutTemplate is a Querydsl query type for LayoutTemplate
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLayoutTemplate extends EntityPathBase<LayoutTemplate> {

    private static final long serialVersionUID = 638450448L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLayoutTemplate layoutTemplate = new QLayoutTemplate("layoutTemplate");

    public final ListPath<LayoutTemplateCell, QLayoutTemplateCell> cells = this.<LayoutTemplateCell, QLayoutTemplateCell>createList("cells", LayoutTemplateCell.class, QLayoutTemplateCell.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QLayoutTemplateInfo info;

    public final DateTimePath<java.util.Date> lastUpdate = createDateTime("lastUpdate", java.util.Date.class);

    public final StringPath path = createString("path");

    public final ListPath<com.arcsoft.supervisor.model.domain.userconfig.UserConfig, com.arcsoft.supervisor.model.domain.userconfig.QUserConfig> userconfigs = this.<com.arcsoft.supervisor.model.domain.userconfig.UserConfig, com.arcsoft.supervisor.model.domain.userconfig.QUserConfig>createList("userconfigs", com.arcsoft.supervisor.model.domain.userconfig.UserConfig.class, com.arcsoft.supervisor.model.domain.userconfig.QUserConfig.class, PathInits.DIRECT2);

    public QLayoutTemplate(String variable) {
        this(LayoutTemplate.class, forVariable(variable), INITS);
    }

    public QLayoutTemplate(Path<? extends LayoutTemplate> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLayoutTemplate(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QLayoutTemplate(PathMetadata<?> metadata, PathInits inits) {
        this(LayoutTemplate.class, metadata, inits);
    }

    public QLayoutTemplate(Class<? extends LayoutTemplate> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.info = inits.isInitialized("info") ? new QLayoutTemplateInfo(forProperty("info"), inits.get("info")) : null;
    }

}

