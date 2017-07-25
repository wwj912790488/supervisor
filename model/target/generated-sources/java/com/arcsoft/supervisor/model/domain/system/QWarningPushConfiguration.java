package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QWarningPushConfiguration is a Querydsl query type for WarningPushConfiguration
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QWarningPushConfiguration extends EntityPathBase<WarningPushConfiguration> {

    private static final long serialVersionUID = -363534879L;

    public static final QWarningPushConfiguration warningPushConfiguration = new QWarningPushConfiguration("warningPushConfiguration");

    public final QConfiguration _super = new QConfiguration(this);

    //inherited
    public final NumberPath<Integer> id = _super.id;

    public final StringPath ip = createString("ip");

    public final StringPath remoteUrl = createString("remoteUrl");

    public QWarningPushConfiguration(String variable) {
        super(WarningPushConfiguration.class, forVariable(variable));
    }

    public QWarningPushConfiguration(Path<? extends WarningPushConfiguration> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWarningPushConfiguration(PathMetadata<?> metadata) {
        super(WarningPushConfiguration.class, metadata);
    }

}

