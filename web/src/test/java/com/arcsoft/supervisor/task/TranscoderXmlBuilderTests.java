package com.arcsoft.supervisor.task;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.commons.freemarker.FreemarkerService;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelSignalDetectTypeConfig;
import com.arcsoft.supervisor.model.dto.channel.SignalDetectSetting;
import com.arcsoft.supervisor.model.dto.graphic.ScreenPositionConfig;
import com.arcsoft.supervisor.model.vo.task.TaskType;
import com.arcsoft.supervisor.model.vo.task.compose.ComposeTaskParams;
import com.arcsoft.supervisor.model.vo.task.profile.VideoProfile;
import com.arcsoft.supervisor.model.vo.task.profile.VideoProfileWithH264;
import com.arcsoft.supervisor.service.task.TranscoderXmlBuilder;
import com.google.common.collect.ImmutableList;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.output.StringBuilderWriter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zw.
 */
public class TranscoderXmlBuilderTests extends ProductionTestSupport {

    @Autowired
    private TranscoderXmlBuilder transcoderXmlBuilder;

    @Autowired
    private FreemarkerService freemarkerService;

    @Test
    public void testBuild() throws IOException, TemplateException {

        Template tpl = freemarkerService.createTemplateFromString("transcoder-template", "" +
                " <#list a..b as i>${i}</#list> ");
        StringBuilderWriter writer = new StringBuilderWriter();
        VideoProfile videoProfile = new VideoProfileWithH264();
        videoProfile.setCodecLevel(VideoProfile.CodecLevel.L13);
        Map<String, Object> model = new HashMap<>();
        model.put("a", 2);
        model.put("b", 2);
        tpl.process(model, writer);
        System.out.println(writer.toString());

    }

    @Test
    public void testCreateTranscoderXmlBuilderResource() {
        List<ScreenPositionConfig> screenPositionConfigs = ImmutableList.<ScreenPositionConfig>builder()
                .add(createScreenPositionConfig(0, (byte) 0, (byte) 0, "channel-1"))
                .add(createScreenPositionConfig(1, (byte) 0, (byte) 1, "channel-2"))
//                .add(ScreenPositionConfig.placeHolderConfig(1, 0, 2))
//                .add(createScreenPositionConfig(2, (byte) 1, (byte) 0, "channel-3"))
//                .add(createScreenPositionConfig(3, (byte) 1, (byte) 1, "channel-4"))
//                .add(createScreenPositionConfig(4, (byte) 2, (byte) 1, "channel-5"))
                .build();

        TranscoderXmlBuilder.BuilderResourceAndXml builderResourceAndXml = transcoderXmlBuilder.build(
                new TranscoderXmlBuilder.BuilderParameters(createComposeTaskParams(), screenPositionConfigs, "fff",-1)
        );

//        List<ScreenPositionConfig> screenPositionConfigs2 = ImmutableList.<ScreenPositionConfig>builder()
//                .add(createScreenPositionConfig(0, (byte) 0, (byte) 0, "channel-1"))
//                .add(createScreenPositionConfig(1, (byte) 0, (byte) 1, "channel-2"))
//                .add(createScreenPositionConfig(2, (byte) 1, (byte) 0, "channel-3"))
//                .add(createScreenPositionConfig(3, (byte) 1, (byte) 1, "channel-4"))
////                .add(createScreenPositionConfig(4, (byte) 2, (byte) 1, "channel-5"))
//                .build();
//        TranscoderXmlBuilder.BuilderResourceAndXml builderResourceAndXml2 = transcoderXmlBuilder.build(
//                new TranscoderXmlBuilder.BuilderParameters(createComposeTaskParams(), screenPositionConfigs2, "fff")
//        );

        System.out.println(builderResourceAndXml.getTranscoderXml());
//        System.out.println(builderResourceAndXml2.getTranscoderXml());
    }

    private ComposeTaskParams createComposeTaskParams() {
        ComposeTaskParams composeTaskParams = new ComposeTaskParams();
        composeTaskParams.setId(1);
        composeTaskParams.setTaskType(TaskType.IP_STREAM_COMPOSE);
//        composeTaskParams.setTaskType(TaskType.SDI_STREAM_COMPOSE);
        composeTaskParams.setEnableRtsp(true);
        composeTaskParams.setTargetIp("192.168.10.11");
        composeTaskParams.setColumnCount( 3);
        composeTaskParams.setRowCount(3);

        return composeTaskParams;
    }

    private ScreenPositionConfig createScreenPositionConfig(int index, int row, int column, String channelName) {
        ScreenPositionConfig screenPositionConfig = new ScreenPositionConfig(false);
        screenPositionConfig.setIndex(index);
        screenPositionConfig.setChannelName(channelName);
        screenPositionConfig.setColumn(column);
        screenPositionConfig.setRow(row);
        screenPositionConfig.setProgramId("101");
        screenPositionConfig.setAudioId("102");
        screenPositionConfig.setUrl("udp://224.0.0.8:1024");
        Channel channel = new Channel();
        ChannelSignalDetectTypeConfig channelSignalDetectTypeConfig = new ChannelSignalDetectTypeConfig();
        channel.setEnableSignalDetectByType(true);

        channelSignalDetectTypeConfig.setEnableWarningAudioLoss(false);
        channelSignalDetectTypeConfig.setWarningAudioLossTimeout(6000);

        channelSignalDetectTypeConfig.setEnableWarningVideoLoss(false);
        channelSignalDetectTypeConfig.setWarningVideoLossTimeout(6000);

        channelSignalDetectTypeConfig.setEnableWarningSignalBroken(false);
        channelSignalDetectTypeConfig.setWarningSignalBrokenTimeout(6000);

        channelSignalDetectTypeConfig.setEnableWarningProgidLoss(true);
        channelSignalDetectTypeConfig.setWarningProgidLossTimeout(6000);

        channelSignalDetectTypeConfig.setEnableWarningCcError(true);
        channelSignalDetectTypeConfig.setWarningCcErrorTimeout(6000);
        channelSignalDetectTypeConfig.setWarningCcErrorCount(500);

        channel.setSignalDetectByTypeConfig(channelSignalDetectTypeConfig);

        screenPositionConfig.setSignalDetectSetting(SignalDetectSetting.builder().channel(channel).build());
        return screenPositionConfig;
    }
}
