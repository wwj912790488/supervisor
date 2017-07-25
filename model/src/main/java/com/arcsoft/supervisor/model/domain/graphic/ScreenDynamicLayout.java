package com.arcsoft.supervisor.model.domain.graphic;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yshe on 2016/12/20.
 */
@Entity
@Table(name = "screen_dynamic_layout")
@DynamicUpdate
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class ScreenDynamicLayout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "screen_id")
    private Integer screenid;
    @Column(name = "layout",columnDefinition="TEXT")
    private String layout;
    @Column(name = "lastUpdate")
    private Date lastupdate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScreenid() {
        return screenid;
    }

    public void setScreenid(Integer screenid) {
        this.screenid = screenid;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public UserScreenLayout getUserLayout(){
        if(StringUtils.isEmpty(layout))
            return null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        try{
            return mapper.readValue(layout,UserScreenLayout.class);
        }catch (Exception e){
        }
        return null;
    }

    public Date getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(Date lastupdate) {
        this.lastupdate = lastupdate;
    }
}
