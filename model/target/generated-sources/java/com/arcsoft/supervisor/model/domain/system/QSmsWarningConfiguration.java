package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QSmsWarningConfiguration is a Querydsl query type for SmsWarningConfiguration
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSmsWarningConfiguration extends EntityPathBase<SmsWarningConfiguration> {

    private static final long serialVersionUID = -1382818958L;

    public static final QSmsWarningConfiguration smsWarningConfiguration = new QSmsWarningConfiguration("smsWarningConfiguration");

    public final QConfiguration _super = new QConfiguration(this);

    public final StringPath account = createString("account");

    //inherited
    public final NumberPath<Integer> id = _super.id;

    public final StringPath url = createString("url");

    public QSmsWarningConfiguration(String variable) {
        super(SmsWarningConfiguration.class, forVariable(variable));
    }

    public QSmsWarningConfiguration(Path<? extends SmsWarningConfiguration> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSmsWarningConfiguration(PathMetadata<?> metadata) {
        super(SmsWarningConfiguration.class, metadata);
    }

}

