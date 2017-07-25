package com.arcsoft.supervisor.model.domain.task;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity for {@code OutputProfile}.
 *
 * @author zw.
 */
@Entity
@Table(name = "profile_output")
@DiscriminatorValue("2")
public class OutputProfile extends Profile{

    @Column(name = "video_audio_description")
    private String videoAndAudioDescription;

    public String getVideoAndAudioDescription() {
        return videoAndAudioDescription;
    }

    public void setVideoAndAudioDescription(String videoAndAudioDescription) {
        this.videoAndAudioDescription = videoAndAudioDescription;
    }

    public static class Builder extends ProfileBuilder<OutputProfile, Builder> {

        public Builder() {
            super(new OutputProfile());
        }

        public Builder videoAndAudioDescription(String videoAndAudioDescription) {
            profile.setVideoAndAudioDescription(videoAndAudioDescription);
            return getBuilder();
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }
}
