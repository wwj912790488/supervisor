package com.arcsoft.supervisor.model.domain.graphic;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QScreenDynamicLayout is a Querydsl query type for ScreenDynamicLayout
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QScreenDynamicLayout extends EntityPathBase<ScreenDynamicLayout> {

    private static final long serialVersionUID = 1680508729L;

    public static final QScreenDynamicLayout screenDynamicLayout = new QScreenDynamicLayout("screenDynamicLayout");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.util.Date> lastupdate = createDateTime("lastupdate", java.util.Date.class);

    public final StringPath layout = createString("layout");

    public final NumberPath<Integer> screenid = createNumber("screenid", Integer.class);

    public QScreenDynamicLayout(String variable) {
        super(ScreenDynamicLayout.class, forVariable(variable));
    }

    public QScreenDynamicLayout(Path<? extends ScreenDynamicLayout> path) {
        super(path.getType(), path.getMetadata());
    }

    public QScreenDynamicLayout(PathMetadata<?> metadata) {
        super(ScreenDynamicLayout.class, metadata);
    }

}

