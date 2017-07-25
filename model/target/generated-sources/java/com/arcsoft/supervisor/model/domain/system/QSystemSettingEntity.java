package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QSystemSettingEntity is a Querydsl query type for SystemSettingEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSystemSettingEntity extends EntityPathBase<SystemSettingEntity> {

    private static final long serialVersionUID = 195590563L;

    public static final QSystemSettingEntity systemSettingEntity = new QSystemSettingEntity("systemSettingEntity");

    public final StringPath key = createString("key");

    public final StringPath value = createString("value");

    public QSystemSettingEntity(String variable) {
        super(SystemSettingEntity.class, forVariable(variable));
    }

    public QSystemSettingEntity(Path<? extends SystemSettingEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSystemSettingEntity(PathMetadata<?> metadata) {
        super(SystemSettingEntity.class, metadata);
    }

}

