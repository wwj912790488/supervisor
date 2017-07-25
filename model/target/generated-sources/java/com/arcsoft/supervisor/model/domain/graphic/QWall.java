package com.arcsoft.supervisor.model.domain.graphic;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QWall is a Querydsl query type for Wall
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QWall extends EntityPathBase<Wall> {

    private static final long serialVersionUID = -1686475442L;

    public static final QWall wall = new QWall("wall");

    public final NumberPath<Integer> columnCount = createNumber("columnCount", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> rowCount = createNumber("rowCount", Integer.class);

    public final NumberPath<Byte> type = createNumber("type", Byte.class);

    public final NumberPath<Integer> version = createNumber("version", Integer.class);

    public final ListPath<WallPosition, QWallPosition> wallPositions = this.<WallPosition, QWallPosition>createList("wallPositions", WallPosition.class, QWallPosition.class, PathInits.DIRECT2);

    public QWall(String variable) {
        super(Wall.class, forVariable(variable));
    }

    public QWall(Path<? extends Wall> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWall(PathMetadata<?> metadata) {
        super(Wall.class, metadata);
    }

}

