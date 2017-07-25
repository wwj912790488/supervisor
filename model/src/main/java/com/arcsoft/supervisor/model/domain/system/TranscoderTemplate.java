package com.arcsoft.supervisor.model.domain.system;

import javax.persistence.*;

/**
 * A {@code TranscoderTemplate} contains the template string for build task.
 *
 * @author zw.
 */
@Entity
@Table(name = "transcoder_template")
public class TranscoderTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    private String template;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
