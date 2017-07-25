package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMailSenderInfoConfiguration is a Querydsl query type for MailSenderInfoConfiguration
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMailSenderInfoConfiguration extends EntityPathBase<MailSenderInfoConfiguration> {

    private static final long serialVersionUID = -233292869L;

    public static final QMailSenderInfoConfiguration mailSenderInfoConfiguration = new QMailSenderInfoConfiguration("mailSenderInfoConfiguration");

    public final QConfiguration _super = new QConfiguration(this);

    public final ArrayPath<String[], String> attachFileNames = createArray("attachFileNames", String[].class);

    public final BooleanPath choosessl = createBoolean("choosessl");

    public final StringPath content = createString("content");

    public final StringPath fromAddress = createString("fromAddress");

    //inherited
    public final NumberPath<Integer> id = _super.id;

    public final StringPath mailServerHost = createString("mailServerHost");

    public final StringPath mailServerPort = createString("mailServerPort");

    public final StringPath password = createString("password");

    public final MapPath<Object, Object, SimplePath<Object>> properties = this.<Object, Object, SimplePath<Object>>createMap("properties", Object.class, Object.class, SimplePath.class);

    public final StringPath subject = createString("subject");

    public final StringPath toAddress = createString("toAddress");

    public final StringPath typeAudio = createString("typeAudio");

    public final StringPath typeBass = createString("typeBass");

    public final StringPath typeBlack = createString("typeBlack");

    public final StringPath typeCc = createString("typeCc");

    public final StringPath typeMute = createString("typeMute");

    public final StringPath typePitch = createString("typePitch");

    public final StringPath typeSignal = createString("typeSignal");

    public final StringPath typeStatic = createString("typeStatic");

    public final StringPath typeVideo = createString("typeVideo");

    public final StringPath userName = createString("userName");

    public final BooleanPath validate = createBoolean("validate");

    public QMailSenderInfoConfiguration(String variable) {
        super(MailSenderInfoConfiguration.class, forVariable(variable));
    }

    public QMailSenderInfoConfiguration(Path<? extends MailSenderInfoConfiguration> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMailSenderInfoConfiguration(PathMetadata<?> metadata) {
        super(MailSenderInfoConfiguration.class, metadata);
    }

}

