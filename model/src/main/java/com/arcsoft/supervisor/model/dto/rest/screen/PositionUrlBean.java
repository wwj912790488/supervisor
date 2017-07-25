package com.arcsoft.supervisor.model.dto.rest.screen;

/**
 * Holds the position info of screen and the rtsp url of specific position.
 *
 * @author zw.
 */
public class PositionUrlBean {

    /**
     *The channel id
     */
    private int id;

    /**
     * The row index of currently position
     */

    private int row;

    /**
     * The column index of currently position
     */
    private int col;

    /**
     * The rtsp url of channel of associated position.
     */
    private String url;

    public PositionUrlBean() {
    }

    public PositionUrlBean(int id,int row, int col, String url) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.url = url;
    }

    public int getId(){return id;}

    public void setId(int id){this.id=id;}

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
