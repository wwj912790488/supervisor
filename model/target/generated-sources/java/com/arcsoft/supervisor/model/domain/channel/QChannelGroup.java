package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QChannelGroup is a Querydsl query type for ChannelGroup
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelGroup extends EntityPathBase<ChannelGroup> {

    private static final long serialVersionUID = -1147206523L;

    public static final QChannelGroup channelGroup = new QChannelGroup("channelGroup");

    public final ListPath<Channel, QChannel> channels = this.<Channel, QChannel>createList("channels", Channel.class, QChannel.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public QChannelGroup(String variable) {
        super(ChannelGroup.class, forVariable(variable));
    }

    public QChannelGroup(Path<? extends ChannelGroup> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChannelGroup(PathMetadata<?> metadata) {
        super(ChannelGroup.class, metadata);
    }

}

