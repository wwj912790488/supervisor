package com.arcsoft.supervisor.model.domain.setuppackage;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QSetupPackage is a Querydsl query type for SetupPackage
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSetupPackage extends EntityPathBase<SetupPackage> {

    private static final long serialVersionUID = -2043047312L;

    public static final QSetupPackage setupPackage = new QSetupPackage("setupPackage");

    public final StringPath fileHash = createString("fileHash");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final BooleanPath isDeployVersion = createBoolean("isDeployVersion");

    public final StringPath uploadDate = createString("uploadDate");

    public final StringPath uploadPath = createString("uploadPath");

    public final StringPath version = createString("version");

    public QSetupPackage(String variable) {
        super(SetupPackage.class, forVariable(variable));
    }

    public QSetupPackage(Path<? extends SetupPackage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSetupPackage(PathMetadata<?> metadata) {
        super(SetupPackage.class, metadata);
    }

}

