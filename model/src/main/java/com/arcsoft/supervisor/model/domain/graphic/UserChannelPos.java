package com.arcsoft.supervisor.model.domain.graphic;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.awt.*;

/**
 * Created by yshe on 2016/12/20.
 */
public class UserChannelPos {
    public int x;
    public int y;
    public int width;
    public int height;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @JsonIgnore
    public Rectangle getRectangle(){
        return new Rectangle(x,y,width,height);
    }

    @JsonIgnore
    public String toString(){
        return String.format("{%d,%d,%d,%d}",x,y,width,height);
    }
}
