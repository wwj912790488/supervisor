package com.arcsoft.supervisor.model.domain.graphic;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A screen class can compose multiple {@link com.arcsoft.supervisor.model.domain.channel.Channel}s as a stream.
 *
 * @author zw.
 */
@Entity
@Table(name = "screen")
@DynamicUpdate
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * All of schemas
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "screen", fetch = FetchType.LAZY)
    private List<ScreenSchema> schemas = new ArrayList<>();

    /**
     * Currently using schema
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_schema_id")
    private ScreenSchema activeSchema;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wall_position_id")
    private WallPosition wallPosition;

    /** Indicate the output udp stream url for ops server */
    private String address;

    /**
     * The sdp file name of rtsp output.
     */
    @Column(name = "rtsp_file_name")
    private String rtspFileName;

    private String message;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="style_id")
    private MessageStyle style;

    @Column(name="user_layout_id")
    private Integer userLayoutId;


    @Column(name = "push_url")
    private String pushUrl;

    public Screen() {}
    
    public Screen(WallPosition wallPosition) {
    	this.wallPosition = wallPosition;
    	for(int i = 0; i < 3; i++) {
    		schemas.add(new ScreenSchema(this, i));
    	}
    	this.activeSchema = schemas.get(0);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public WallPosition getWallPosition() {
        return wallPosition;
    }

    public void setWallPosition(WallPosition wallPosition) {
        this.wallPosition = wallPosition;
    }

    public List<ScreenSchema> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<ScreenSchema> schemas) {
        this.schemas = schemas;
    }

    public ScreenSchema getActiveSchema() {
        return activeSchema;
    }

    public void setActiveSchema(ScreenSchema activeSchema) {
        this.activeSchema = activeSchema;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRtspFileName() {
        return rtspFileName;
    }

    public void setRtspFileName(String rtspFileName) {
        this.rtspFileName = rtspFileName;
    }

    public void clearAddress(){
        this.address = null;
    }

    public void clearRtspFileName(){
        this.rtspFileName = null;
    }

    public void clearAddressAndRtspFileName(){
        clearAddress();
        clearRtspFileName();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageStyle getStyle() {
        return style;
    }

    public void setStyle(MessageStyle style) {
        this.style = style;
    }

    public void setPushUrl(String pushUrl){this.pushUrl=pushUrl;}
    public String getPushUrl(){return pushUrl;}

    public Integer getUserLayoutId() {
        return userLayoutId;
    }

    public void setUserLayoutId(Integer userLayoutId) {
        this.userLayoutId = userLayoutId;
    }

    @PreRemove
    private void onPreRemove() {
        this.style = null;
    }
}
