package com.arcsoft.supervisor.model.domain.task;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QOutputProfile is a Querydsl query type for OutputProfile
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QOutputProfile extends EntityPathBase<OutputProfile> {

    private static final long serialVersionUID = 52455997L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOutputProfile outputProfile = new QOutputProfile("outputProfile");

    public final QProfile _super;

    //inherited
    public final StringPath description;

    //inherited
    public final NumberPath<Integer> id;

    //inherited
    public final StringPath name;

    // inherited
    public final QProfileTemplate profileTemplate;

    public final StringPath videoAndAudioDescription = createString("videoAndAudioDescription");

    public QOutputProfile(String variable) {
        this(OutputProfile.class, forVariable(variable), INITS);
    }

    public QOutputProfile(Path<? extends OutputProfile> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QOutputProfile(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QOutputProfile(PathMetadata<?> metadata, PathInits inits) {
        this(OutputProfile.class, metadata, inits);
    }

    public QOutputProfile(Class<? extends OutputProfile> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QProfile(type, metadata, inits);
        this.description = _super.description;
        this.id = _super.id;
        this.name = _super.name;
        this.profileTemplate = _super.profileTemplate;
    }

}

