package com.arcsoft.supervisor.service.commons.mediainfo;

import com.arcsoft.supervisor.exception.ResolveMediainfoException;
import com.arcsoft.supervisor.model.domain.channel.ChannelInfo;
import com.arcsoft.supervisor.model.domain.channel.SDIChannel;
import com.arcsoft.supervisor.service.commons.mediainfo.impl.ProgramAndAudioMediainfo;

import java.util.List;

/**
 * A interface to analyze the mediainfo of file or udp stream.
 *
 * @author zw.
 */
public interface MediainfoService {

    /**
     * Returns program and audio info with specified <code>path</code>
     *
     * @param path the source to be analyze.It may be a file or a udp stream
     * @return {@code List<ProgramAndAudioMediainfo>}
     * @throws ResolveMediainfoException if failed when analyze mediainfo
     */
    public List<ProgramAndAudioMediainfo> getProgramAndAudio(String path) throws ResolveMediainfoException;


    /**
     * Returns <code>ChannelInfo</code> through the specific input source <code>path</code>
     * and <code>programId</code> and <code>audioId</code>.
     *
     * @param path      the source to be analyze.It may be a file or a udp stream
     * @param programId the value of selected program
     * @param audioId   the value of selected audio
     * @return {@link com.arcsoft.supervisor.model.domain.channel.ChannelInfo}
     * @throws ResolveMediainfoException if failed to analyze with <code>path</code>
     */
    public ChannelInfo getChannelInfo(String path, String programId, String audioId) throws ResolveMediainfoException;


    public SDIChannel getSDI(String path) throws ResolveMediainfoException;



    public ChannelInfo getSDIChannelInfo(Integer port) throws ResolveMediainfoException;

}
