package com.arcsoft.supervisor.model.domain.graphic;

import javax.persistence.*;

@Entity
@Table(name="message_style")
public class MessageStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String font;

    private Integer x;

    private Integer y;

    private Integer width;

    private Integer height;

    private Integer size;

    private Integer color;

    private Integer alpha;

    private Integer bgcolor;

    private Integer bgalpha;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageStyle that = (MessageStyle) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        if (getFont() != null ? !getFont().equals(that.getFont()) : that.getFont() != null) return false;
        if (getX() != null ? !getX().equals(that.getX()) : that.getX() != null) return false;
        if (getY() != null ? !getY().equals(that.getY()) : that.getY() != null) return false;
        if (getWidth() != null ? !getWidth().equals(that.getWidth()) : that.getWidth() != null) return false;
        if (getHeight() != null ? !getHeight().equals(that.getHeight()) : that.getHeight() != null) return false;
        if (getSize() != null ? !getSize().equals(that.getSize()) : that.getSize() != null) return false;
        if (getColor() != null ? !getColor().equals(that.getColor()) : that.getColor() != null) return false;
        if (getAlpha() != null ? !getAlpha().equals(that.getAlpha()) : that.getAlpha() != null) return false;
        if (getBgcolor() != null ? !getBgcolor().equals(that.getBgcolor()) : that.getBgcolor() != null) return false;
        return !(getBgalpha() != null ? !getBgalpha().equals(that.getBgalpha()) : that.getBgalpha() != null);

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getFont() != null ? getFont().hashCode() : 0);
        result = 31 * result + (getX() != null ? getX().hashCode() : 0);
        result = 31 * result + (getY() != null ? getY().hashCode() : 0);
        result = 31 * result + (getWidth() != null ? getWidth().hashCode() : 0);
        result = 31 * result + (getHeight() != null ? getHeight().hashCode() : 0);
        result = 31 * result + (getSize() != null ? getSize().hashCode() : 0);
        result = 31 * result + (getColor() != null ? getColor().hashCode() : 0);
        result = 31 * result + (getAlpha() != null ? getAlpha().hashCode() : 0);
        result = 31 * result + (getBgcolor() != null ? getBgcolor().hashCode() : 0);
        result = 31 * result + (getBgalpha() != null ? getBgalpha().hashCode() : 0);
        return result;
    }
}
