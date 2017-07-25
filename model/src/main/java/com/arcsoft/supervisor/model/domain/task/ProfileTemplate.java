package com.arcsoft.supervisor.model.domain.task;

import javax.persistence.*;

/**
 *
 * A {@code ProfileTemplate} entity class contains the json string of a profile.
 *
 * @author zw.
 */
@Entity
@Table(name = "profile_template")
public class ProfileTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Lob
    private String template;

    public ProfileTemplate() {
    }

    public ProfileTemplate(String template) {
        this.template = template;
    }

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

    /**
     * Constructs a new instance with given profile.
     *
     * @param profile the json string of profile
     * @return a new instance
     */
    public static ProfileTemplate from(String profile) {
        return new ProfileTemplate(profile);
    }
}
