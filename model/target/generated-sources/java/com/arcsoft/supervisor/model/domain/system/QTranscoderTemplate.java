package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QTranscoderTemplate is a Querydsl query type for TranscoderTemplate
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTranscoderTemplate extends EntityPathBase<TranscoderTemplate> {

    private static final long serialVersionUID = 1349791992L;

    public static final QTranscoderTemplate transcoderTemplate = new QTranscoderTemplate("transcoderTemplate");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath template = createString("template");

    public QTranscoderTemplate(String variable) {
        super(TranscoderTemplate.class, forVariable(variable));
    }

    public QTranscoderTemplate(Path<? extends TranscoderTemplate> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTranscoderTemplate(PathMetadata<?> metadata) {
        super(TranscoderTemplate.class, metadata);
    }

}

