package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QChannelRecordConfiguration is a Querydsl query type for ChannelRecordConfiguration
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelRecordConfiguration extends EntityPathBase<ChannelRecordConfiguration> {

    private static final long serialVersionUID = 895027203L;

    public static final QChannelRecordConfiguration channelRecordConfiguration = new QChannelRecordConfiguration("channelRecordConfiguration");

    public final QConfiguration _super = new QConfiguration(this);

    public final NumberPath<Integer> contentDetectKeepTime = createNumber("contentDetectKeepTime", Integer.class);

    public final StringPath contentDetectStoragePath = createString("contentDetectStoragePath");

    public final StringPath domain = createString("domain");

    //inherited
    public final NumberPath<Integer> id = _super.id;

    public final NumberPath<Integer> keepTime = createNumber("keepTime", Integer.class);

    public final NumberPath<Integer> profileId = createNumber("profileId", Integer.class);

    public final StringPath recorderStoragePath = createString("recorderStoragePath");

    public final StringPath supervisorStoragePath = createString("supervisorStoragePath");

    public QChannelRecordConfiguration(String variable) {
        super(ChannelRecordConfiguration.class, forVariable(variable));
    }

    public QChannelRecordConfiguration(Path<? extends ChannelRecordConfiguration> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChannelRecordConfiguration(PathMetadata<?> metadata) {
        super(ChannelRecordConfiguration.class, metadata);
    }

}

