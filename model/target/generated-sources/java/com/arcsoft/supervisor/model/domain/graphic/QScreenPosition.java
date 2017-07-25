package com.arcsoft.supervisor.model.domain.graphic;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QScreenPosition is a Querydsl query type for ScreenPosition
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QScreenPosition extends EntityPathBase<ScreenPosition> {

    private static final long serialVersionUID = -2129995719L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QScreenPosition screenPosition = new QScreenPosition("screenPosition");

    public final com.arcsoft.supervisor.model.domain.channel.QChannel channel;

    public final NumberPath<Integer> column = createNumber("column", Integer.class);

    public final NumberPath<Integer> groupIndex = createNumber("groupIndex", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> row = createNumber("row", Integer.class);

    public final QScreenSchema screenSchema;

    public final NumberPath<Integer> x = createNumber("x", Integer.class);

    public final NumberPath<Integer> y = createNumber("y", Integer.class);

    public QScreenPosition(String variable) {
        this(ScreenPosition.class, forVariable(variable), INITS);
    }

    public QScreenPosition(Path<? extends ScreenPosition> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QScreenPosition(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QScreenPosition(PathMetadata<?> metadata, PathInits inits) {
        this(ScreenPosition.class, metadata, inits);
    }

    public QScreenPosition(Class<? extends ScreenPosition> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.channel = inits.isInitialized("channel") ? new com.arcsoft.supervisor.model.domain.channel.QChannel(forProperty("channel"), inits.get("channel")) : null;
        this.screenSchema = inits.isInitialized("screenSchema") ? new QScreenSchema(forProperty("screenSchema"), inits.get("screenSchema")) : null;
    }

}

