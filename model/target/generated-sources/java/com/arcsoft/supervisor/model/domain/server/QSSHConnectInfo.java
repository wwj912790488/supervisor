package com.arcsoft.supervisor.model.domain.server;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QSSHConnectInfo is a Querydsl query type for SSHConnectInfo
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSSHConnectInfo extends EntityPathBase<SSHConnectInfo> {

    private static final long serialVersionUID = -267485859L;

    public static final QSSHConnectInfo sSHConnectInfo = new QSSHConnectInfo("sSHConnectInfo");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath ip = createString("ip");

    public final StringPath password = createString("password");

    public final NumberPath<Integer> port = createNumber("port", Integer.class);

    public final StringPath user = createString("user");

    public QSSHConnectInfo(String variable) {
        super(SSHConnectInfo.class, forVariable(variable));
    }

    public QSSHConnectInfo(Path<? extends SSHConnectInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSSHConnectInfo(PathMetadata<?> metadata) {
        super(SSHConnectInfo.class, metadata);
    }

}

