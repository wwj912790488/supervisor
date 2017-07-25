package com.arcsoft.supervisor.model.dto.graphic;


public class WallScreenName {
    private Integer id;
    private Integer row;
    private Integer column;
    private String wallName;

    public WallScreenName(Integer id, Integer row, Integer column, String wallName) {
        this.id = id;
        this.row = row;
        this.column = column;
        this.wallName = wallName;
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

    public String getWallName() {
        return wallName;
    }

    
    public void setWallName(String wallName) {
        this.wallName = wallName;
    }
}
