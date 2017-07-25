package com.arcsoft.supervisor.model.vo.task.compose;


/**
 *  A object contains the resolution{@code (width height)} and output index of task.
 *
 * @author zw.
 */
public class TaskOutputResolutionAndIndexMapper {

    /**
     * which index of task output belong to
     */
    private int index;

    private int width;

    private int height;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder implements org.apache.commons.lang3.builder.Builder<TaskOutputResolutionAndIndexMapper>{

        private final TaskOutputResolutionAndIndexMapper taskOutputResolutionAndIndexMapper;

        private Builder() {
            this.taskOutputResolutionAndIndexMapper = new TaskOutputResolutionAndIndexMapper();
        }

        public Builder index(int index) {
            taskOutputResolutionAndIndexMapper.setIndex(index);
            return this;
        }

        public Builder width(int width) {
            taskOutputResolutionAndIndexMapper.setWidth(width);
            return this;
        }

        public Builder height(int height) {
            taskOutputResolutionAndIndexMapper.setHeight(height);
            return this;
        }

        @Override
        public TaskOutputResolutionAndIndexMapper build() {
            return taskOutputResolutionAndIndexMapper;
        }
    }
}
