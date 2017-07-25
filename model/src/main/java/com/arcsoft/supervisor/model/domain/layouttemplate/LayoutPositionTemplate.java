package com.arcsoft.supervisor.model.domain.layouttemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "layout_position_template")
public class LayoutPositionTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String guid;

    private int rowCount;

    private int columnCount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "template", fetch = FetchType.LAZY)
    private List<LayoutPosition> positions = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<LayoutPosition> getPositions() {
        return positions;
    }

    public void setPositions(List<LayoutPosition> positions) {
        this.positions = positions;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }
}
