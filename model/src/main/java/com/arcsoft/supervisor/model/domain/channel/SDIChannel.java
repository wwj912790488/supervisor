package com.arcsoft.supervisor.model.domain.channel;

import java.util.List;

/**
 * Created by wwj on 2017/6/16.
 */
public class SDIChannel  {
    String   type;
    Integer counts;
    private List<Integer> ports;
    public SDIChannel(){

    }
    public SDIChannel(String type, Integer counts, List<Integer> ports) {
        this.type = type;
        this.counts = counts;
        this.ports = ports;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCounts() {
        return counts;
    }

    public void setCounts(Integer counts) {
        this.counts = counts;
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }
}
