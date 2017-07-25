package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QChannelSignalDetectConfig is a Querydsl query type for ChannelSignalDetectConfig
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelSignalDetectConfig extends EntityPathBase<ChannelSignalDetectConfig> {

    private static final long serialVersionUID = 125125191L;

    public static final QChannelSignalDetectConfig channelSignalDetectConfig = new QChannelSignalDetectConfig("channelSignalDetectConfig");

    public final BooleanPath enableLevel1Error = createBoolean("enableLevel1Error");

    public final BooleanPath enableLevel2Error = createBoolean("enableLevel2Error");

    public final BooleanPath enableLevel3Error = createBoolean("enableLevel3Error");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public QChannelSignalDetectConfig(String variable) {
        super(ChannelSignalDetectConfig.class, forVariable(variable));
    }

    public QChannelSignalDetectConfig(Path<? extends ChannelSignalDetectConfig> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChannelSignalDetectConfig(PathMetadata<?> metadata) {
        super(ChannelSignalDetectConfig.class, metadata);
    }

}

