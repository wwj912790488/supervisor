package com.arcsoft.supervisor.cluster.action.task;

import com.arcsoft.supervisor.cluster.action.BaseRequest;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DisplayStyledMessageRequest extends BaseRequest{
    private Integer taskId;
    private String font;
    private Integer size;
    private Integer color;
    private Integer alpha;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer bgcolor;
    private Integer bgalpha;
    private String message;

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }

    public Integer getAlpha() {
        return alpha;
    }

    public void setAlpha(Integer alpha) {
        this.alpha = alpha;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(Integer bgcolor) {
        this.bgcolor = bgcolor;
    }

    public Integer getBgalpha() {
        return bgalpha;
    }

    public void setBgalpha(Integer bgalpha) {
        this.bgalpha = bgalpha;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
