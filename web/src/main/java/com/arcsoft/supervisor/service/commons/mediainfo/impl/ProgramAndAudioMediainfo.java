package com.arcsoft.supervisor.service.commons.mediainfo.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * A bean class to holding the <code>programId</code> and <code>programName</code>
 * of mediainfo.
 *
 * @author zw.
 */
public class ProgramAndAudioMediainfo {

    private Integer programId;
    private String programName;
    private Integer vwidth;
    private Integer vheight;

    private List<Audio> audios = new ArrayList<>();

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public List<Audio> getAudios() {
        return audios;
    }

    public void setAudios(List<Audio> audios) {
        this.audios = audios;
    }

    public Integer getVwidth(){return vwidth;}
    public void  setVwidth(Integer vwidth){this.vwidth=vwidth;}

    public Integer getVheight(){return vheight;}
    public void setVheight(Integer vheight){this.vheight=vheight;}
}
