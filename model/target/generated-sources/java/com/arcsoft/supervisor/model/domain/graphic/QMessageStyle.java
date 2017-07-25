package com.arcsoft.supervisor.model.domain.graphic;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMessageStyle is a Querydsl query type for MessageStyle
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMessageStyle extends EntityPathBase<MessageStyle> {

    private static final long serialVersionUID = -365940370L;

    public static final QMessageStyle messageStyle = new QMessageStyle("messageStyle");

    public final NumberPath<Integer> alpha = createNumber("alpha", Integer.class);

    public final NumberPath<Integer> bgalpha = createNumber("bgalpha", Integer.class);

    public final NumberPath<Integer> bgcolor = createNumber("bgcolor", Integer.class);

    public final NumberPath<Integer> color = createNumber("color", Integer.class);

    public final StringPath font = createString("font");

    public final NumberPath<Integer> height = createNumber("height", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> size = createNumber("size", Integer.class);

    public final NumberPath<Integer> width = createNumber("width", Integer.class);

    public final NumberPath<Integer> x = createNumber("x", Integer.class);

    public final NumberPath<Integer> y = createNumber("y", Integer.class);

    public QMessageStyle(String variable) {
        super(MessageStyle.class, forVariable(variable));
    }

    public QMessageStyle(Path<? extends MessageStyle> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMessageStyle(PathMetadata<?> metadata) {
        super(MessageStyle.class, metadata);
    }

}

