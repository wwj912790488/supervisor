package com.arcsoft.supervisor.model.domain.graphic;

import com.arcsoft.supervisor.model.domain.channel.Channel;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * The position class for {@link com.arcsoft.supervisor.model.domain.graphic.Screen} to
 * set channel of each positions.
 *
 * @author zw.
 */
@Entity
@Table(name = "screen_position")
@DynamicUpdate
public class ScreenPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "\"row\"")
    private Integer row;
    @Column(name = "\"column\"")
    private Integer column;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schema_id")
    private ScreenSchema screenSchema;

    private Integer groupIndex;

    private Integer x;

    private Integer y;
    
    public ScreenPosition () {}
    
    public ScreenPosition(ScreenSchema screenSchema, Integer row, Integer column) {
    	this.screenSchema = screenSchema;
    	this.row = row;
    	this.column = column;
        this.x = 1;
        this.y = 1;
        this.groupIndex = 0;
    }

    public ScreenPosition(ScreenSchema screenSchema, Integer row, Integer column, Integer groupIndex) {
        this.screenSchema = screenSchema;
        this.row = row;
        this.column = column;
        this.groupIndex = groupIndex;
    }

    public ScreenPosition(ScreenSchema screenSchema, Integer row, Integer column, Integer x, Integer y) {
        this.screenSchema = screenSchema;
        this.row = row;
        this.column = column;
        this.x = x;
        this.y = y;
        this.groupIndex = 0;
    }

    public ScreenPosition(ScreenSchema screenSchema, Integer row, Integer column, Integer x, Integer y, Integer groupIndex) {
        this.screenSchema = screenSchema;
        this.row = row;
        this.column = column;
        this.x = x;
        this.y = y;
        this.groupIndex = groupIndex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public ScreenSchema getScreenSchema() {
        return screenSchema;
    }

    public void setScreenSchema(ScreenSchema screenSchema) {
        this.screenSchema = screenSchema;
    }

    public Integer getGroupIndex() {
        return groupIndex == null ? 0 : groupIndex;
    }

    public void setGroupIndex(Integer groupIndex) {
        this.groupIndex = groupIndex;
    }

    public Integer getX() {
        return x == null ? 1 : x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y == null ? 1 : y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
