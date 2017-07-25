package com.arcsoft.supervisor.transcoder;


import com.arcsoft.supervisor.transcoder.type.Framerate;
import com.arcsoft.supervisor.transcoder.type.TaskStatus;
import com.arcsoft.supervisor.transcoder.util.DateTimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

/**
 * class TranscodingInfo
 *
 * @author Bing
 */
public class TranscodingInfo {

    private static final Logger LOG = LoggerFactory.getLogger(TranscodingInfo.class);
    /**
     * get progress filter
     */
    public static final byte GET_PROGRESS = 1;//TransSvrCmd.CMDCODE_GET_PROGRESS ;
    public static final byte GET_FRAMERATE = 2;//TransSvrCmd.CMDCODE_GET_FPS	  ;
    public static final byte GET_THUMB = 3;//TransSvrCmd.CMDCODE_GET_THUMB	  ;
    public static final byte GET_CPU_COUNT = 4;//TransSvrCmd.CMDCODE_GET_CPU_COUNT;
    public static final byte GET_GPU_COUNT = 5;//TransSvrCmd.CMDCODE_GET_GPU_COUNT;
    public static final byte GET_CPU_USAGE = 6;//TransSvrCmd.CMDCODE_GET_CPU_USAGE;
    public static final byte GET_GPU_USAGE = 7;//TransSvrCmd.CMDCODE_GET_GPU_USAGE;

    private String taskGuid = null;
    private Object taskId;
    private TaskStatus status = null;
    private String startAt = null;
    private String completeAt = null;
    /**
     * null if no req
     */
    private InputProgress[] progress = null;
    /**
     * null if no req
     */
    private Framerate framerate = null;
    /**
     * null if no req
     */
    private Integer cpuCount = null;
    /**
     * null if no req
     */
    private Integer gpuCount = null;
    /**
     * null if no req
     */
    private Integer cpuUsage = null;
    /**
     * null if no req
     */
    private Integer gpuUsage = null;

    public TranscodingInfo(Object taskId) {
        this.taskId = taskId;
    }

    public String getTaskGuid() {
        return taskGuid;
    }

    public void setTaskGuid(String taskGuid) {
        this.taskGuid = taskGuid;
    }

    public Object getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getStartAt() {
        return startAt;
    }

    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    public String getCompleteAt() {
        return completeAt;
    }

    public void setCompleteAt(String completeAt) {
        this.completeAt = completeAt;
    }

    public List<InputProgress> getProgress() {
        return Arrays.asList((progress == null ? new InputProgress[0] : progress));
    }

    public void setProgress(InputProgress[] progress) {
        this.progress = progress;
    }

    public Framerate getFramerate() {
        return framerate;
    }

    public String getFramerateDesc() {
        return this.framerate == null ? null : this.framerate.toString();
    }

    public void setFramerate(Framerate fps) {
        this.framerate = fps;
    }

    public Integer getCpuCount() {
        return cpuCount;
    }

    public void setCpuCount(Integer cpuCount) {
        this.cpuCount = cpuCount;
    }

    public Integer getGpuCount() {
        return gpuCount;
    }

    public void setGpuCount(Integer gpuCount) {
        this.gpuCount = gpuCount;
    }

    public Integer getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(Integer cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public Integer getGpuUsage() {
        return gpuUsage;
    }

    public void setGpuUsage(Integer gpuUsage) {
        this.gpuUsage = gpuUsage;
    }

    /**
     * a sample to DOM/XML
     *
     * @return
     */
    public Document toXmlDOM() {
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Element e;
            Element root;
            //root
            root = doc.createElement("result");
            root.setAttribute("id", String.valueOf(this.taskId));

            //status
            if (this.status != null) {
                e = doc.createElement("status");
                e.appendChild(doc.createTextNode(this.status.getKey()));
                root.appendChild(e);
            }
            //startAt
            if (this.startAt != null) {
                e = doc.createElement("startAt");
                e.appendChild(doc.createTextNode(this.startAt));
                root.appendChild(e);
            }
            //completeAt
            if (this.completeAt != null) {
                e = doc.createElement("completeAt");
                e.appendChild(doc.createTextNode(this.completeAt));
                root.appendChild(e);
            }
            //progress
            if (this.progress != null) {
                Element p = doc.createElement("progress");
                for (int i = 0; i < this.progress.length; i++) {
                    e = doc.createElement("input");
                    e.setAttribute("index", String.valueOf(i));
                    e.setAttribute("time", DateTimeHelper.formatDuration(this.progress[i].getTimeConsumed()));
                    e.setAttribute("power", String.format("%.1f", this.progress[i].getPower() / 100.0f));
                    e.appendChild(doc.createTextNode(String.valueOf(this.progress[i].getValue())));

                    p.appendChild(e);
                }
                root.appendChild(p);
            }
            //fps
            if (this.framerate != null) {
                e = doc.createElement("framerate");
                e.appendChild(doc.createTextNode(String.valueOf(this.framerate.toString())));
                root.appendChild(e);
            }
            //cpu_count
            if (this.cpuCount != null) {
                e = doc.createElement("cpu_count");
                e.appendChild(doc.createTextNode(String.valueOf(this.cpuCount.intValue())));
                root.appendChild(e);
            }
            //gpu_count
            if (this.gpuCount != null) {
                e = doc.createElement("gpu_count");
                e.appendChild(doc.createTextNode(String.valueOf(this.gpuCount.intValue())));
                root.appendChild(e);
            }
            //cpu_usage
            if (this.cpuUsage != null) {
                e = doc.createElement("cpu_usage");
                e.appendChild(doc.createTextNode(String.valueOf(this.cpuUsage.intValue())));
                root.appendChild(e);
            }
            //gpu_usage
            if (this.gpuUsage != null) {
                e = doc.createElement("gpu_usage");
                e.appendChild(doc.createTextNode(String.valueOf(this.gpuUsage.intValue())));
                root.appendChild(e);
            }

            doc.appendChild(root);
        } catch (Exception e) {
            LOG.error("", e);
        }
        return doc;
    }

    @Override
    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Document doc = toXmlDOM();
            Element elem = doc.getDocumentElement();
            Transformer trans = TransformerFactory.newInstance().newTransformer();
            trans.transform(new DOMSource(elem), new StreamResult(out));
        } catch (Exception e) {
            LOG.error("", e);
        }
        try {
            return out.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
        }
        return "";
    }

    public static class InputProgress {
        private int index;
        /**
         * 0~100 (percent) or >100 (SDI timestamp)
         */
        private long value;
        /**
         * time consumed
         */
        private int timeConsumed;
        /**
         * val*100
         */
        private int power;

        private int duration;

        public InputProgress(int index, long value, int timeConsumed, int power) {
            this.index = index;
            this.value = value;
            this.timeConsumed = timeConsumed;
            this.power = power;
        }

        public boolean isInputSDI() {
            return value > 100;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public long getValue() {
            return value;
        }

        /**
         * @return s
         */
        public int getTimeConsumed() {
            return timeConsumed;
        }

        public void setTimeConsumed(int timeConsumed) {
            this.timeConsumed = timeConsumed;
        }

        public int getIndex() {
            return index;
        }

        public int getPower() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }


        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        @Override
        public String toString() {
            return "InputProgress [index=" + index + ", value=" + value
                    + ", timeConsumed=" + timeConsumed + ", power=" + power
                    + ", duration=" + duration + "]";
        }

    }
}
