package com.arcsoft.supervisor.model.domain.userconfig;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplate;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplateCell;
import com.arcsoft.supervisor.model.domain.user.SartfUser;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="user_config")
@Sartf
public class UserConfig {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
	private SartfUser user;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_id")
	private LayoutTemplate template;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "audio_channel_id")
	private Channel audioChannel;
	
	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cell_id")
	private LayoutTemplateCell audioCell;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "userconfig", fetch = FetchType.LAZY)
	private List<UserConfigChannel> channels = new ArrayList<UserConfigChannel>();
	
	@JsonIgnore
	private Date lastUpdate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public SartfUser getUser() {
        return user;
    }

    public void setUser(SartfUser user) {
        this.user = user;
    }

    public LayoutTemplate getTemplate() {
		return template;
	}

	public void setTemplate(LayoutTemplate template) {
		this.template = template;
	}

	public Channel getAudioChannel() {
		return audioChannel;
	}

	public void setAudioChannel(Channel audioChannel) {
		this.audioChannel = audioChannel;
	}

	public List<UserConfigChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<UserConfigChannel> channels) {
		this.channels = channels;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public LayoutTemplateCell getCell() {
		return audioCell;
	}

	public void setCell(LayoutTemplateCell cell) {
		this.audioCell = cell;
	}

	@JsonProperty("template_id")
	public Integer getTemplateId() {
		return this.template == null ? -1 : this.template.getId();
	}
	
	@JsonProperty("audio_cell_index")
	public Integer getAudioChannelId() {
		return this.audioCell == null ? -1 : this.audioCell.getCell_index();
	}
	
	@PreRemove
	private void preRemove() {
	    if(user.getCurrent() == this) {
	    	user.setCurrent(null);
	    }
	}
}
