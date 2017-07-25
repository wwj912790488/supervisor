package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QConfiguration is a Querydsl query type for Configuration
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QConfiguration extends EntityPathBase<Configuration> {

    private static final long serialVersionUID = -2064472715L;

    public static final QConfiguration configuration = new QConfiguration("configuration");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public QConfiguration(String variable) {
        super(Configuration.class, forVariable(variable));
    }

    public QConfiguration(Path<? extends Configuration> path) {
        super(path.getType(), path.getMetadata());
    }

    public QConfiguration(PathMetadata<?> metadata) {
        super(Configuration.class, metadata);
    }

}

