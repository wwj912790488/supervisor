package com.arcsoft.supervisor.model.domain.channel;

/**
 * A class indicate the task's channel associated screen position config.
 *
 * @author zw.
 */
public class TaskChannelAssociatedScreenPosition {

    private int taskId;
    private byte rowCount;
    private byte columnCount;
    private byte row;
    private byte column;

    public TaskChannelAssociatedScreenPosition() {}


    public TaskChannelAssociatedScreenPosition(int taskId, byte rowCount, byte columnCount, byte row, byte column) {
        this.taskId = taskId;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        this.row = row;
        this.column = column;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public byte getRowCount() {
        return rowCount;
    }

    public void setRowCount(byte rowCount) {
        this.rowCount = rowCount;
    }

    public byte getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(byte columnCount) {
        this.columnCount = columnCount;
    }

    public byte getRow() {
        return row;
    }

    public void setRow(byte row) {
        this.row = row;
    }

    public byte getColumn() {
        return column;
    }

    public void setColumn(byte column) {
        this.column = column;
    }
}
