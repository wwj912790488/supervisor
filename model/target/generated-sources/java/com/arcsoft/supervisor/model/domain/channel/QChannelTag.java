package com.arcsoft.supervisor.model.domain.channel;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QChannelTag is a Querydsl query type for ChannelTag
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QChannelTag extends EntityPathBase<ChannelTag> {

    private static final long serialVersionUID = 1406637856L;

    public static final QChannelTag channelTag = new QChannelTag("channelTag");

    public final ListPath<Channel, QChannel> channels = this.<Channel, QChannel>createList("channels", Channel.class, QChannel.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public QChannelTag(String variable) {
        super(ChannelTag.class, forVariable(variable));
    }

    public QChannelTag(Path<? extends ChannelTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChannelTag(PathMetadata<?> metadata) {
        super(ChannelTag.class, metadata);
    }

}

