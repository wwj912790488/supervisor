package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QChannelInfo is a Querydsl query type for ChannelInfo
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelInfo extends EntityPathBase<ChannelInfo> {

    private static final long serialVersionUID = 655785448L;

    public static final QChannelInfo channelInfo = new QChannelInfo("channelInfo");

    public final StringPath abitdepth = createString("abitdepth");

    public final StringPath abitrate = createString("abitrate");

    public final StringPath achannels = createString("achannels");

    public final StringPath acodec = createString("acodec");

    public final StringPath alanguage = createString("alanguage");

    public final StringPath asamplerate = createString("asamplerate");

    public final StringPath container = createString("container");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final StringPath vbitrate = createString("vbitrate");

    public final StringPath vcodec = createString("vcodec");

    public final StringPath vframerate = createString("vframerate");

    public final StringPath vratio = createString("vratio");

    public final StringPath vresolution = createString("vresolution");

    public QChannelInfo(String variable) {
        super(ChannelInfo.class, forVariable(variable));
    }

    public QChannelInfo(Path<? extends ChannelInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChannelInfo(PathMetadata<?> metadata) {
        super(ChannelInfo.class, metadata);
    }

}

