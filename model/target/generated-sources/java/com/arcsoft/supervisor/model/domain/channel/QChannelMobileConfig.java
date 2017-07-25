package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QChannelMobileConfig is a Querydsl query type for ChannelMobileConfig
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelMobileConfig extends EntityPathBase<ChannelMobileConfig> {

    private static final long serialVersionUID = 212975038L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QChannelMobileConfig channelMobileConfig = new QChannelMobileConfig("channelMobileConfig");

    public final StringPath address = createString("address");

    public final NumberPath<Integer> audioBitrate = createNumber("audioBitrate", Integer.class);

    public final QChannel channel;

    public final BooleanPath deinterlace = createBoolean("deinterlace");

    public final NumberPath<Integer> height = createNumber("height", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Byte> type = createNumber("type", Byte.class);

    public final NumberPath<Integer> videoBitrate = createNumber("videoBitrate", Integer.class);

    public final NumberPath<Integer> width = createNumber("width", Integer.class);

    public QChannelMobileConfig(String variable) {
        this(ChannelMobileConfig.class, forVariable(variable), INITS);
    }

    public QChannelMobileConfig(Path<? extends ChannelMobileConfig> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QChannelMobileConfig(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QChannelMobileConfig(PathMetadata<?> metadata, PathInits inits) {
        this(ChannelMobileConfig.class, metadata, inits);
    }

    public QChannelMobileConfig(Class<? extends ChannelMobileConfig> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.channel = inits.isInitialized("channel") ? new QChannel(forProperty("channel"), inits.get("channel")) : null;
    }

}

