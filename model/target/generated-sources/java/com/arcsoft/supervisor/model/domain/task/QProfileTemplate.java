package com.arcsoft.supervisor.model.domain.task;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QProfileTemplate is a Querydsl query type for ProfileTemplate
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QProfileTemplate extends EntityPathBase<ProfileTemplate> {

    private static final long serialVersionUID = -688032552L;

    public static final QProfileTemplate profileTemplate = new QProfileTemplate("profileTemplate");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath template = createString("template");

    public QProfileTemplate(String variable) {
        super(ProfileTemplate.class, forVariable(variable));
    }

    public QProfileTemplate(Path<? extends ProfileTemplate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QProfileTemplate(PathMetadata<?> metadata) {
        super(ProfileTemplate.class, metadata);
    }

}

