package com.arcsoft.supervisor.model.domain.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.Builder;

import javax.persistence.*;

/**
 * Base entity for profile.
 *
 * @author zw.
 */
@Entity
@Table(name = "profile")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("1")
public abstract class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String description;

    private String name;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "template_id")
    private ProfileTemplate profileTemplate;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProfileTemplate getProfileTemplate() {
        return profileTemplate;
    }

    public void setProfileTemplate(ProfileTemplate profileTemplate) {
        this.profileTemplate = profileTemplate;
    }

    protected static abstract class ProfileBuilder<T extends Profile, B extends ProfileBuilder<T, B>> implements Builder<T> {

        protected final T profile;

        public ProfileBuilder(T profile) {
            this.profile = profile;
        }

        public B name(String name) {
            profile.setName(name);
            return getBuilder();
        }

        public B description(String description) {
            profile.setDescription(description);
            return getBuilder();
        }

        public B template(String template) {
            profile.setProfileTemplate(ProfileTemplate.from(template));
            return getBuilder();
        }

        @Override
        public T build() {
            return profile;
        }

        protected abstract B getBuilder();
    }
}
