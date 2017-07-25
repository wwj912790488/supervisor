package com.arcsoft.supervisor.service.system;

import com.arcsoft.supervisor.model.domain.system.TranscoderTemplate;

/**
 * A management interface for {@link TranscoderTemplate}.
 * <p><b>Implementation notes: </b> You should keep the id's
 * value of {@link TranscoderTemplate} always is {@code 1}.
 *
 * @author zw.
 */
public interface TranscoderTemplateService {

    void update(TranscoderTemplate template);

    TranscoderTemplate find();

}
