package com.arcsoft.supervisor.model.domain.graphic;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QScreen is a Querydsl query type for Screen
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QScreen extends EntityPathBase<Screen> {

    private static final long serialVersionUID = -1612723472L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QScreen screen = new QScreen("screen");

    public final QScreenSchema activeSchema;

    public final StringPath address = createString("address");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath message = createString("message");

    public final StringPath pushUrl = createString("pushUrl");

    public final StringPath rtspFileName = createString("rtspFileName");

    public final ListPath<ScreenSchema, QScreenSchema> schemas = this.<ScreenSchema, QScreenSchema>createList("schemas", ScreenSchema.class, QScreenSchema.class, PathInits.DIRECT2);

    public final QMessageStyle style;

    public final NumberPath<Integer> userLayoutId = createNumber("userLayoutId", Integer.class);

    public final QWallPosition wallPosition;

    public QScreen(String variable) {
        this(Screen.class, forVariable(variable), INITS);
    }

    public QScreen(Path<? extends Screen> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QScreen(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QScreen(PathMetadata<?> metadata, PathInits inits) {
        this(Screen.class, metadata, inits);
    }

    public QScreen(Class<? extends Screen> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.activeSchema = inits.isInitialized("activeSchema") ? new QScreenSchema(forProperty("activeSchema"), inits.get("activeSchema")) : null;
        this.style = inits.isInitialized("style") ? new QMessageStyle(forProperty("style")) : null;
        this.wallPosition = inits.isInitialized("wallPosition") ? new QWallPosition(forProperty("wallPosition"), inits.get("wallPosition")) : null;
    }

}

