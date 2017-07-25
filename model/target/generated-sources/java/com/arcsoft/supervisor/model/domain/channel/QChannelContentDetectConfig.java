package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QChannelContentDetectConfig is a Querydsl query type for ChannelContentDetectConfig
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelContentDetectConfig extends EntityPathBase<ChannelContentDetectConfig> {

    private static final long serialVersionUID = -1167690140L;

    public static final QChannelContentDetectConfig channelContentDetectConfig = new QChannelContentDetectConfig("channelContentDetectConfig");

    public final NumberPath<Float> blackSeconds = createNumber("blackSeconds", Float.class);

    public final NumberPath<Integer> boomSonicThreshold = createNumber("boomSonicThreshold", Integer.class);

    public final BooleanPath enableBoomSonic = createBoolean("enableBoomSonic");

    public final NumberPath<Float> greenSeconds = createNumber("greenSeconds", Float.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Float> loudVolumeSeconds = createNumber("loudVolumeSeconds", Float.class);

    public final NumberPath<Integer> loudVolumeThreshold = createNumber("loudVolumeThreshold", Integer.class);

    public final NumberPath<Float> lowVolumeSeconds = createNumber("lowVolumeSeconds", Float.class);

    public final NumberPath<Integer> lowVolumeThreshold = createNumber("lowVolumeThreshold", Integer.class);

    public final NumberPath<Float> noFrameSeconds = createNumber("noFrameSeconds", Float.class);

    public final NumberPath<Float> silenceSeconds = createNumber("silenceSeconds", Float.class);

    public final NumberPath<Integer> silenceThreshold = createNumber("silenceThreshold", Integer.class);

    public QChannelContentDetectConfig(String variable) {
        super(ChannelContentDetectConfig.class, forVariable(variable));
    }

    public QChannelContentDetectConfig(Path<? extends ChannelContentDetectConfig> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChannelContentDetectConfig(PathMetadata<?> metadata) {
        super(ChannelContentDetectConfig.class, metadata);
    }

}

