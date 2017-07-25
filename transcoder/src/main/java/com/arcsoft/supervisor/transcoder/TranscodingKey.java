package com.arcsoft.supervisor.transcoder;

/**
 * Task transcoding key
 * it is mainly for complex transcoder integration.
 *
 * @author Bing
 * @since transcoder v2.0
 */
public class TranscodingKey {
    /**
     * Null task key
     */
    public static final TranscodingKey NULL_KEY = new TranscodingKey(0);

    /**
     * task seqence number
     */
    private int sequenceNumber;
    /**
     * taskId
     */
    private int taskId;

    /**
     * constructor
     * (sequenceNumber = taskId)
     *
     * @param taskId
     */
    public TranscodingKey(int taskId) {
        this.sequenceNumber = taskId;
        this.taskId = taskId;
    }

    public TranscodingKey(int sequenceNumber, int taskId) {
        this.sequenceNumber = sequenceNumber;
        this.taskId = taskId;
    }

    public final int getSequenceNumber() {
        return sequenceNumber;
    }

    public final int getTaskId() {
        return taskId;
    }

    @Override
    public int hashCode() {
        return sequenceNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TranscodingKey other = (TranscodingKey) obj;
        if (sequenceNumber != other.sequenceNumber)
            return false;
        return true;
    }

    /**
     * string descriptor of this key
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(taskId);
        if (taskId != sequenceNumber) {
            buf.append('-').append(sequenceNumber);
        }
        return buf.toString();
    }

}
