package com.arcsoft.supervisor.model.domain.system;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QReportWarningConfiguration is a Querydsl query type for ReportWarningConfiguration
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QReportWarningConfiguration extends EntityPathBase<ReportWarningConfiguration> {

    private static final long serialVersionUID = 503640047L;

    public static final QReportWarningConfiguration reportWarningConfiguration = new QReportWarningConfiguration("reportWarningConfiguration");

    public final QConfiguration _super = new QConfiguration(this);

    //inherited
    public final NumberPath<Integer> id = _super.id;

    public final StringPath ip = createString("ip");

    public QReportWarningConfiguration(String variable) {
        super(ReportWarningConfiguration.class, forVariable(variable));
    }

    public QReportWarningConfiguration(Path<? extends ReportWarningConfiguration> path) {
        super(path.getType(), path.getMetadata());
    }

    public QReportWarningConfiguration(PathMetadata<?> metadata) {
        super(ReportWarningConfiguration.class, metadata);
    }

}

