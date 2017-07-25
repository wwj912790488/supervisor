package com.arcsoft.supervisor.service.task;

import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.dto.graphic.ScreenPositionConfig;
import com.arcsoft.supervisor.model.vo.task.compose.ComposeTaskParams;

import java.util.List;

/**
 * A {@code TranscoderXmlBuilder} can be build transcoder xml through a {@link Task}.
 *
 * @author zw.
 */
public interface TranscoderXmlBuilder {

    class BuilderResourceAndXml {
        private final TranscoderXmlBuilderResource resource;
        private final String transcoderXml;
        private final List<String> seimessageXmls;

        public BuilderResourceAndXml(TranscoderXmlBuilderResource resource, String transcoderXml,List<String> seimessageXmls) {
            this.resource = resource;
            this.transcoderXml = transcoderXml;
            this.seimessageXmls = seimessageXmls;
        }

        public TranscoderXmlBuilderResource getResource() {
            return resource;
        }

        public String getTranscoderXml() {
            return transcoderXml;
        }

        public List<String> getSeimessageXmls(){return seimessageXmls;}
    }

    class BuilderParameters{
        private final ComposeTaskParams taskParams;
        private final List<ScreenPositionConfig> configs;
        private final String serverId;
        private final Integer gpuStartIdx;

        public BuilderParameters(ComposeTaskParams taskParams, List<ScreenPositionConfig> configs, String serverId,Integer gpuStartIdx) {
            this.taskParams = taskParams;
            this.configs = configs;
            this.serverId = serverId;
            this.gpuStartIdx = gpuStartIdx;
        }

        public ComposeTaskParams getTaskParams() {
            return taskParams;
        }

        public List<ScreenPositionConfig> getConfigs() {
            return configs;
        }

        public String getServerId() {
            return serverId;
        }

        public Integer getGpuStartIdx() {
            return gpuStartIdx;
        }
    }

    BuilderResourceAndXml build(BuilderParameters parameters);

}
