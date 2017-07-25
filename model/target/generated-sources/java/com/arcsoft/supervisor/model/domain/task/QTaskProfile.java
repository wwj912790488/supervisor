package com.arcsoft.supervisor.model.domain.task;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTaskProfile is a Querydsl query type for TaskProfile
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTaskProfile extends EntityPathBase<TaskProfile> {

    private static final long serialVersionUID = -966253959L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTaskProfile taskProfile = new QTaskProfile("taskProfile");

    public final QProfile _super;

    public final BooleanPath allowProgramIdChange = createBoolean("allowProgramIdChange");

    public final NumberPath<Integer> amountOfOutput = createNumber("amountOfOutput", Integer.class);

    //inherited
    public final StringPath description;

    public final EnumPath<com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto.EncodingOption> encodingOption = createEnum("encodingOption", com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto.EncodingOption.class);

    //inherited
    public final NumberPath<Integer> id;

    //inherited
    public final StringPath name;

    public final EnumPath<com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto.Priority> priority = createEnum("priority", com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto.Priority.class);

    // inherited
    public final QProfileTemplate profileTemplate;

    public final NumberPath<Integer> screenColumn = createNumber("screenColumn", Integer.class);

    public final NumberPath<Integer> screenRow = createNumber("screenRow", Integer.class);

    public final ListPath<Task, QTask> tasks = this.<Task, QTask>createList("tasks", Task.class, QTask.class, PathInits.DIRECT2);

    public final NumberPath<Integer> usedGpuCoreAmount = createNumber("usedGpuCoreAmount", Integer.class);

    public QTaskProfile(String variable) {
        this(TaskProfile.class, forVariable(variable), INITS);
    }

    public QTaskProfile(Path<? extends TaskProfile> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaskProfile(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaskProfile(PathMetadata<?> metadata, PathInits inits) {
        this(TaskProfile.class, metadata, inits);
    }

    public QTaskProfile(Class<? extends TaskProfile> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QProfile(type, metadata, inits);
        this.description = _super.description;
        this.id = _super.id;
        this.name = _super.name;
        this.profileTemplate = _super.profileTemplate;
    }

}

