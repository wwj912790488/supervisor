package com.arcsoft.supervisor.service.commons.mediainfo.impl;

import com.arcsoft.supervisor.exception.ResolveMediainfoException;
import com.arcsoft.supervisor.model.domain.channel.ChannelInfo;
import com.arcsoft.supervisor.model.domain.channel.SDIChannel;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * A implementation to analyze the program and audio info.
 *
 * @author zw.
 */
@Service("defaultMediainfoService")
public class DefaultMediainfoServiceImpl extends AbstractMediainfoService {

    private ProgramAndAudioMediainfo getProgram(Element program) {
        ProgramAndAudioMediainfo programAndAudioMediainfo = new ProgramAndAudioMediainfo();
        programAndAudioMediainfo.setProgramId(Integer.valueOf(program.attributeValue("idx")));
        programAndAudioMediainfo.setProgramName(program.element("name").getText());
        Element element = program.element("audios");


        Element elevideo = null;
        try {
            List videoEles = program.element("videos").elements();
            if (videoEles != null && videoEles.size() > 0) {
                elevideo = (Element) videoEles.get(0);
            }
        } catch (Exception e) {
            elevideo = null;
        }
        try {
            String resolution = null;
            if (elevideo != null)
                resolution = elevideo.elementText("resolution");
            if (resolution != null) {
                String[] resolutions = resolution.split("x");

                Integer width = new Integer(resolutions[0]);
                Integer height = new Integer(resolutions[1]);
                programAndAudioMediainfo.setVwidth(width);
                programAndAudioMediainfo.setVheight(height);
            }
        } catch (Exception e) {
            programAndAudioMediainfo.setVwidth(0);
            programAndAudioMediainfo.setVheight(0);
        }
        programAndAudioMediainfo.setAudios(element != null ? getAudios(program.element("audios").elements()) : Arrays.asList(new Audio(-1, "None")));
        return programAndAudioMediainfo;
    }

    private List<Audio> getAudios(List audioEls) {
        List<Audio> audios = new ArrayList<>();
        for (int i = 0, len = audioEls.size(); i < len; i++) {
            Element audioEl = (Element) audioEls.get(i);
            audios.add(new Audio(Integer.valueOf(audioEl.element("pid").getText()), audioEl.element("name").getText()));
        }
        return audios;
    }

    @Override
    public SDIChannel getSDI(String path) throws ResolveMediainfoException {
        SDIChannel sdiChannel = new SDIChannel();
        List<Integer> ports = new ArrayList<>();
        sdiChannel.setType("sdi");
        Element root = getRootElementSDICount("sdi");
        Iterator iterator = root.element("Ports").elementIterator();
        String counts = root.element("Ports").attributeValue("Count");
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            String port = element.attributeValue("idx");
            ports.add(Integer.valueOf(port));
        }
        sdiChannel.setPorts(ports);
        sdiChannel.setCounts(Integer.valueOf(counts));
        return sdiChannel;
    }

    @Override
    public ChannelInfo getSDIChannelInfo(Integer port) throws ResolveMediainfoException {
        ChannelInfo channelInfo = new ChannelInfo();
        Element root = getRootElementSDIChannelInfo(port);
        String name = root.attributeValue("name");
        channelInfo.setName(name);
        channelInfo.setContainer(getElementText(root.element("container")));
        {
            Element video = root.element("video");
            channelInfo.setVcodec(getElementText(video.element("codec")));
            channelInfo.setVbitrate(getElementText(video.element("bitrate")));
            channelInfo.setVframerate(getElementText(video.element("framerate")));
            channelInfo.setVratio(getElementText(video.element("aspect_ratio")));
            channelInfo.setVresolution(getElementText(video.element("resolution")));
        }
        {
            Element audio = root.element("audio");
            channelInfo.setAbitdepth(getElementText(audio.element("bitdepth")));
            channelInfo.setAbitrate(getElementText(audio.element("bitrate")));
            channelInfo.setAchannels(getElementText(audio.element("channel")));
            channelInfo.setAcodec(getElementText(audio.element("codec")));
            channelInfo.setAlanguage(getElementText(audio.element("language")));
            channelInfo.setAsamplerate(getElementText(audio.element("samplerate")));
        }

        return channelInfo;
    }

    @Override
    public List<ProgramAndAudioMediainfo> getProgramAndAudio(String path) throws ResolveMediainfoException {
        try {
            List program = getRootElement(path).element("programs").elements();
            List<ProgramAndAudioMediainfo> programAndAudioMediainfoList = new ArrayList<>();
            for (int i = 0, len = program.size(); i < len; i++) {
                programAndAudioMediainfoList.add(getProgram((Element) program.get(i)));
            }
            return programAndAudioMediainfoList;
        } catch (Exception e) {
            throw new ResolveMediainfoException(e);
        }
    }

    @Override
    public ChannelInfo getChannelInfo(String path, String programId, String audioId) throws ResolveMediainfoException {
        try {
            ChannelInfo info = new ChannelInfo();
            Element root = getRootElement(path);
            info.setContainer(root.element("container").getText());
            Element programs = root.element("programs");
            Node program = programs.selectSingleNode("program[@idx='" + programId + "']");
            if (program != null) {
                Node name = program.selectSingleNode("name");
                info.setName(name.getText());

                Node video = program.selectSingleNode("videos/video");
                if (video != null) {
                    info.setVcodec(getNodeText(video, "codec"));
                    info.setVbitrate(getNodeText(video, "bitrate"));
                    info.setVframerate(getNodeText(video, "framerate"));
                    info.setVratio(getNodeText(video, "aspect_ratio"));
                    info.setVresolution(getNodeText(video, "resolution"));
                }

                Node audio = program.selectSingleNode("audios/audio[pid='" + audioId + "']");
                if (audio != null) {
                    info.setAbitdepth(getNodeText(audio, "bitdepth"));
                    info.setAbitrate(getNodeText(audio, "bitrate"));
                    info.setAchannels(getNodeText(audio, "channel"));
                    info.setAcodec(getNodeText(audio, "codec"));
                    info.setAlanguage(getNodeText(audio, "language"));
                    info.setAsamplerate(getNodeText(audio, "samplerate"));
                }
            }
            return info;
        } catch (Exception e) {
            throw new ResolveMediainfoException(e);
        }
    }

    private String getNodeText(Node parentNode, String expr) {
        Node node = parentNode.selectSingleNode(expr);
        return node == null ? "" : node.getText();
    }

    private String getElementText(Element element) {
        return element == null ? "" : element.getText();
    }
}
