package com.arcsoft.supervisor.model.domain.userconfig;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.layouttemplate.LayoutTemplateCell;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_config_channel")
@Sartf
public class UserConfigChannel {
	
	@JsonIgnore
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "channel_id")
	private Channel channel;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userconfig_id")
	private UserConfig userconfig;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cell_id")
	private LayoutTemplateCell cell;
	
	@JsonProperty("id")
	public Integer getChannelId() {
		return channel == null ? -1 : channel.getId();
	}
	
	@JsonProperty("cell_index")
	public Integer getCellIndex() {
		return cell == null ? -1 : cell.getCell_index();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public UserConfig getUserconfig() {
		return userconfig;
	}

	public void setUserconfig(UserConfig userconfig) {
		this.userconfig = userconfig;
	}

	public LayoutTemplateCell getCell() {
		return cell;
	}

	public void setCell(LayoutTemplateCell cell) {
		this.cell = cell;
	}
	
	@PreRemove
	private void preRemove() {
	    if(userconfig != null) {
	    	userconfig.setLastUpdate(new Date());
	    }
	}
	
}
