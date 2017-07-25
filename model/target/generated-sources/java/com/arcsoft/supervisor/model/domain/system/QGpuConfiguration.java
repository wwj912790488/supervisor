package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QGpuConfiguration is a Querydsl query type for GpuConfiguration
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QGpuConfiguration extends EntityPathBase<GpuConfiguration> {

    private static final long serialVersionUID = 650843851L;

    public static final QGpuConfiguration gpuConfiguration = new QGpuConfiguration("gpuConfiguration");

    public final QConfiguration _super = new QConfiguration(this);

    public final BooleanPath enableSpan = createBoolean("enableSpan");

    //inherited
    public final NumberPath<Integer> id = _super.id;

    public QGpuConfiguration(String variable) {
        super(GpuConfiguration.class, forVariable(variable));
    }

    public QGpuConfiguration(Path<? extends GpuConfiguration> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGpuConfiguration(PathMetadata<?> metadata) {
        super(GpuConfiguration.class, metadata);
    }

}

