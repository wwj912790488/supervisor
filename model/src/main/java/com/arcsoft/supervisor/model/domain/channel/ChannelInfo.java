package com.arcsoft.supervisor.model.domain.channel;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="channel_info")
@DynamicUpdate
public class ChannelInfo {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	private String name;
	
	private String container;
	
	private String vcodec;
	
	private String vbitrate;
	
	private String vframerate;
	
	private String vresolution;
	
	private String vratio;
	
	private String alanguage;
	
	private String acodec;
	
	private String abitrate;
	
	private String achannels;
	
	private String asamplerate;
	
	private String abitdepth;



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContainer() {
		return container;
	}

	public void setContainer(String container) {
		this.container = container;
	}

	public String getVcodec() {
		return vcodec;
	}

	public void setVcodec(String vcodec) {
		this.vcodec = vcodec;
	}

	public String getVbitrate() {
		return vbitrate;
	}

	public void setVbitrate(String vbitrate) {
		this.vbitrate = vbitrate;
	}

	public String getVframerate() {
		return vframerate;
	}

	public void setVframerate(String vframerate) {
		this.vframerate = vframerate;
	}

	public String getVresolution() {
		return vresolution;
	}

	public void setVresolution(String vresolution) {
		this.vresolution = vresolution;
	}

	public String getVratio() {
		return vratio;
	}

	public void setVratio(String vratio) {
		this.vratio = vratio;
	}

	public String getAlanguage() {
		return alanguage;
	}

	public void setAlanguage(String alanguage) {
		this.alanguage = alanguage;
	}

	public String getAcodec() {
		return acodec;
	}

	public void setAcodec(String acodec) {
		this.acodec = acodec;
	}

	public String getAbitrate() {
		return abitrate;
	}

	public void setAbitrate(String abitrate) {
		this.abitrate = abitrate;
	}

	public String getAchannels() {
		return achannels;
	}

	public void setAchannels(String achannels) {
		this.achannels = achannels;
	}

	public String getAsamplerate() {
		return asamplerate;
	}

	public void setAsamplerate(String asamplerate) {
		this.asamplerate = asamplerate;
	}

	public String getAbitdepth() {
		return abitdepth;
	}

	public void setAbitdepth(String abitdepth) {
		this.abitdepth = abitdepth;
	}


}
