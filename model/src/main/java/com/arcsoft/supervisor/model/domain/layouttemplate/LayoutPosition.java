package com.arcsoft.supervisor.model.domain.layouttemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "layout_position")
public class LayoutPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id")
    private LayoutPositionTemplate template;

    @Column(name = "\"row\"")
    private int row;

    @Column(name = "\"column\"")
    private int column;

    private int x;

    private int y;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LayoutPositionTemplate getTemplate() {
        return template;
    }

    public void setTemplate(LayoutPositionTemplate template) {
        this.template = template;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

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
}
