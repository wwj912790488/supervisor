package com.arcsoft.supervisor.model.domain.graphic;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@code Wall} indicate the collection of screen.
 *
 * @author zw.
 */
@Entity
@Table(name = "wall")
@DynamicUpdate
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class Wall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Version
    private int version;
    
    private String name;
    /**
     * Indicate the type of wall. 1: ops wall, 2: sdi wall
     */
    private Byte type;
    /**
     * The row count of currently wall
     */
    @Column(name = "row_count")
    private Integer rowCount;
    /**
     * The column count of currently wall
     */
    @Column(name = "column_count")
    private Integer columnCount;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "wall", fetch = FetchType.LAZY)
    private List<WallPosition> wallPositions = new ArrayList<>();
    
    public Wall() {}
    
    public Wall(String name) {
    	this.name = name;
    	this.type = 1;
    	this.rowCount = 2;
    	this.columnCount = 2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public Integer getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }

    public List<WallPosition> getWallPositions() {
        return wallPositions;
    }

    public void setWallPositions(List<WallPosition> wallPositions) {
        this.wallPositions = wallPositions;
    }

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
}
