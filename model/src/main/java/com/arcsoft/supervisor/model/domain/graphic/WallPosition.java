package com.arcsoft.supervisor.model.domain.graphic;

import com.arcsoft.supervisor.model.domain.server.OpsServer;
import com.arcsoft.supervisor.model.domain.server.ServerComponent;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * The position class for {@link Wall} to set each screens.
 *
 * @author zw.
 */
@Entity
@Table(name = "wall_position")
@DynamicUpdate
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class WallPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "\"row\"")
    private Integer row;
    @Column(name = "\"column\"")
    private Integer column;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wall_id")
    private Wall wall;
    @OneToOne(mappedBy = "wallPosition", fetch = FetchType.LAZY, targetEntity = Screen.class)
    private Screen screen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ops_id")
    private OpsServer opsServer;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sdi_id")
    private ServerComponent sdiOutput;

    @Column(name = "\"wall_name\"")
    private String wallName;

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    private String output;

    public WallPosition() {
    }

    public WallPosition(Integer row, Integer column) {
        this.row = row;
        this.column = column;
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

    public Wall getWall() {
        return wall;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
    }

    public static WallPosition from(int row, int column, Wall wall, Screen screen) {
        WallPosition wallPosition = new WallPosition();
        wallPosition.setRow(row);
        wallPosition.setColumn(column);
        wallPosition.setWall(wall);
        wallPosition.setScreen(screen);
        return wallPosition;
    }

    public OpsServer getOpsServer() {
        return opsServer;
    }

    public void setOpsServer(OpsServer opsServer) {
        this.opsServer = opsServer;
    }

	public ServerComponent getSdiOutput() {
		return sdiOutput;
	}

	public void setSdiOutput(ServerComponent sdiOutput) {
		this.sdiOutput = sdiOutput;
	}

    public void setPosition(int row, int column){
        this.row = row;
        this.column = column;
    }

    public String getWallName() {
        return wallName;
    }

    public void setWallName(String wallName) {
        this.wallName = wallName;
    }
}
