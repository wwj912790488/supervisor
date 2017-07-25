package com.arcsoft.supervisor.model.domain.userconfig;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QUserConfig is a Querydsl query type for UserConfig
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUserConfig extends EntityPathBase<UserConfig> {

    private static final long serialVersionUID = 1129055856L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserConfig userConfig = new QUserConfig("userConfig");

    public final com.arcsoft.supervisor.model.domain.layouttemplate.QLayoutTemplateCell audioCell;

    public final com.arcsoft.supervisor.model.domain.channel.QChannel audioChannel;

    public final ListPath<UserConfigChannel, QUserConfigChannel> channels = this.<UserConfigChannel, QUserConfigChannel>createList("channels", UserConfigChannel.class, QUserConfigChannel.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.util.Date> lastUpdate = createDateTime("lastUpdate", java.util.Date.class);

    public final com.arcsoft.supervisor.model.domain.layouttemplate.QLayoutTemplate template;

    public final com.arcsoft.supervisor.model.domain.user.QSartfUser user;

    public QUserConfig(String variable) {
        this(UserConfig.class, forVariable(variable), INITS);
    }

    public QUserConfig(Path<? extends UserConfig> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUserConfig(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUserConfig(PathMetadata<?> metadata, PathInits inits) {
        this(UserConfig.class, metadata, inits);
    }

    public QUserConfig(Class<? extends UserConfig> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.audioCell = inits.isInitialized("audioCell") ? new com.arcsoft.supervisor.model.domain.layouttemplate.QLayoutTemplateCell(forProperty("audioCell"), inits.get("audioCell")) : null;
        this.audioChannel = inits.isInitialized("audioChannel") ? new com.arcsoft.supervisor.model.domain.channel.QChannel(forProperty("audioChannel"), inits.get("audioChannel")) : null;
        this.template = inits.isInitialized("template") ? new com.arcsoft.supervisor.model.domain.layouttemplate.QLayoutTemplate(forProperty("template"), inits.get("template")) : null;
        this.user = inits.isInitialized("user") ? new com.arcsoft.supervisor.model.domain.user.QSartfUser(forProperty("user"), inits.get("user")) : null;
    }

}

