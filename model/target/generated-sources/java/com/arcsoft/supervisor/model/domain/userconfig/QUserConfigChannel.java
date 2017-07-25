package com.arcsoft.supervisor.model.domain.userconfig;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QUserConfigChannel is a Querydsl query type for UserConfigChannel
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUserConfigChannel extends EntityPathBase<UserConfigChannel> {

    private static final long serialVersionUID = -133631885L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserConfigChannel userConfigChannel = new QUserConfigChannel("userConfigChannel");

    public final com.arcsoft.supervisor.model.domain.layouttemplate.QLayoutTemplateCell cell;

    public final com.arcsoft.supervisor.model.domain.channel.QChannel channel;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QUserConfig userconfig;

    public QUserConfigChannel(String variable) {
        this(UserConfigChannel.class, forVariable(variable), INITS);
    }

    public QUserConfigChannel(Path<? extends UserConfigChannel> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUserConfigChannel(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUserConfigChannel(PathMetadata<?> metadata, PathInits inits) {
        this(UserConfigChannel.class, metadata, inits);
    }

    public QUserConfigChannel(Class<? extends UserConfigChannel> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.cell = inits.isInitialized("cell") ? new com.arcsoft.supervisor.model.domain.layouttemplate.QLayoutTemplateCell(forProperty("cell"), inits.get("cell")) : null;
        this.channel = inits.isInitialized("channel") ? new com.arcsoft.supervisor.model.domain.channel.QChannel(forProperty("channel"), inits.get("channel")) : null;
        this.userconfig = inits.isInitialized("userconfig") ? new QUserConfig(forProperty("userconfig"), inits.get("userconfig")) : null;
    }

}

