package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QChannelSignalDetectTypeConfig is a Querydsl query type for ChannelSignalDetectTypeConfig
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelSignalDetectTypeConfig extends EntityPathBase<ChannelSignalDetectTypeConfig> {

    private static final long serialVersionUID = 1226956705L;

    public static final QChannelSignalDetectTypeConfig channelSignalDetectTypeConfig = new QChannelSignalDetectTypeConfig("channelSignalDetectTypeConfig");

    public final BooleanPath enableWarningAudioLoss = createBoolean("enableWarningAudioLoss");

    public final BooleanPath enableWarningCcError = createBoolean("enableWarningCcError");

    public final BooleanPath enableWarningProgidLoss = createBoolean("enableWarningProgidLoss");

    public final BooleanPath enableWarningSignalBroken = createBoolean("enableWarningSignalBroken");

    public final BooleanPath enableWarningVideoLoss = createBoolean("enableWarningVideoLoss");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> notifyInterval = createNumber("notifyInterval", Integer.class);

    public final NumberPath<Integer> warningAudioLossTimeout = createNumber("warningAudioLossTimeout", Integer.class);

    public final NumberPath<Integer> warningCcErrorCount = createNumber("warningCcErrorCount", Integer.class);

    public final NumberPath<Integer> warningCcErrorTimeout = createNumber("warningCcErrorTimeout", Integer.class);

    public final NumberPath<Integer> warningProgidLossTimeout = createNumber("warningProgidLossTimeout", Integer.class);

    public final NumberPath<Integer> warningSignalBrokenTimeout = createNumber("warningSignalBrokenTimeout", Integer.class);

    public final NumberPath<Integer> warningVideoLossTimeout = createNumber("warningVideoLossTimeout", Integer.class);

    public QChannelSignalDetectTypeConfig(String variable) {
        super(ChannelSignalDetectTypeConfig.class, forVariable(variable));
    }

    public QChannelSignalDetectTypeConfig(Path<? extends ChannelSignalDetectTypeConfig> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChannelSignalDetectTypeConfig(PathMetadata<?> metadata) {
        super(ChannelSignalDetectTypeConfig.class, metadata);
    }

}

