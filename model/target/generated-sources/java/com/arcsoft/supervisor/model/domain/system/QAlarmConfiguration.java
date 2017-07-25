package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAlarmConfiguration is a Querydsl query type for AlarmConfiguration
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAlarmConfiguration extends EntityPathBase<AlarmConfiguration> {

    private static final long serialVersionUID = -1138590458L;

    public static final QAlarmConfiguration alarmConfiguration = new QAlarmConfiguration("alarmConfiguration");

    public final QConfiguration _super = new QConfiguration(this);

    public final StringPath androidapikey = createString("androidapikey");

    public final StringPath androidsecretkey = createString("androidsecretkey");

    //inherited
    public final NumberPath<Integer> id = _super.id;

    public final StringPath iosapikey = createString("iosapikey");

    public final StringPath iossecretsey = createString("iossecretsey");

    public QAlarmConfiguration(String variable) {
        super(AlarmConfiguration.class, forVariable(variable));
    }

    public QAlarmConfiguration(Path<? extends AlarmConfiguration> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAlarmConfiguration(PathMetadata<?> metadata) {
        super(AlarmConfiguration.class, metadata);
    }

}

