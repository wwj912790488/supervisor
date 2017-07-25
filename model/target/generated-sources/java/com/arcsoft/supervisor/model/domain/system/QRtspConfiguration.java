package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QRtspConfiguration is a Querydsl query type for RtspConfiguration
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QRtspConfiguration extends EntityPathBase<RtspConfiguration> {

    private static final long serialVersionUID = -1194470378L;

    public static final QRtspConfiguration rtspConfiguration = new QRtspConfiguration("rtspConfiguration");

    public final QConfiguration _super = new QConfiguration(this);

    //inherited
    public final NumberPath<Integer> id = _super.id;

    public final StringPath ip = createString("ip");

    public final StringPath mixedPublishUrl = createString("mixedPublishUrl");

    public final StringPath publishFolderPath = createString("publishFolderPath");

    public final ListPath<String, StringPath> publishUrls = this.<String, StringPath>createList("publishUrls", String.class, StringPath.class, PathInits.DIRECT2);

    public QRtspConfiguration(String variable) {
        super(RtspConfiguration.class, forVariable(variable));
    }

    public QRtspConfiguration(Path<? extends RtspConfiguration> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRtspConfiguration(PathMetadata<?> metadata) {
        super(RtspConfiguration.class, metadata);
    }

}

