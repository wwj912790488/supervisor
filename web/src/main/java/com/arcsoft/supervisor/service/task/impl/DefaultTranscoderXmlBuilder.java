package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.commons.freemarker.FreemarkerService;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.server.Server;
import com.arcsoft.supervisor.model.domain.system.GpuConfiguration;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import com.arcsoft.supervisor.model.dto.graphic.ScreenPositionConfig;
import com.arcsoft.supervisor.model.vo.task.profile.OutputProfileDto;
import com.arcsoft.supervisor.model.vo.task.profile.TaskOutput;
import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;
import com.arcsoft.supervisor.model.vo.task.profile.VideoProfile;
import com.arcsoft.supervisor.repository.server.ServerJpaRepository;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.converter.Converter;
import com.arcsoft.supervisor.service.settings.ConfigurationService;
import com.arcsoft.supervisor.service.system.TranscoderTemplateService;
import com.arcsoft.supervisor.service.task.TranscoderXmlBuilder;
import com.arcsoft.supervisor.service.task.TranscoderXmlBuilderResource;
import com.arcsoft.supervisor.service.task.gpu.GpuLoadBalanceManager;
import com.arcsoft.supervisor.service.task.gpu.GpuLoadBalanceManager.GpuDecodeAndEncodeConfig;
import com.arcsoft.supervisor.utils.app.Environment;
import com.google.common.collect.ImmutableMap;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.io.output.StringBuilderWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zw.
 */
@Service
public class DefaultTranscoderXmlBuilder implements TranscoderXmlBuilder, TransactionSupport {

    private final TaskRepository taskRepository;
    private final FreemarkerService freemarkerService;
    private final Converter<TaskProfileDto, TaskProfile> taskProfileConverter;
    private final TranscoderTemplateService transcoderTemplateService;
    private final GpuLoadBalanceManager gpuLoadBalanceManager;
    private final ConfigurationService<GpuConfiguration> gpuConfigService;

    @Autowired
    private ServerJpaRepository serverJpaRepository;

    @Autowired
    public DefaultTranscoderXmlBuilder(TaskRepository taskRepository,
                                       FreemarkerService freemarkerService,
                                       @Qualifier("taskProfileAndTaskProfileDtoConverter")
                                       Converter<TaskProfileDto, TaskProfile> taskProfileConverter,
                                       TranscoderTemplateService transcoderTemplateService,
                                       GpuLoadBalanceManager gpuLoadBalanceManager,
                                       @Qualifier("gpuConfigurationService")
                                       ConfigurationService<GpuConfiguration> gpuConfigService) {
        this.taskRepository = taskRepository;
        this.freemarkerService = freemarkerService;
        this.taskProfileConverter = taskProfileConverter;
        this.transcoderTemplateService = transcoderTemplateService;
        this.gpuLoadBalanceManager = gpuLoadBalanceManager;
        this.gpuConfigService = gpuConfigService;
    }

    @Override
    public BuilderResourceAndXml build(BuilderParameters parameters) {
        Task task = taskRepository.findOne(parameters.getTaskParams().getId());
        TaskProfileDto taskProfileDto;
        try {
            taskProfileDto = taskProfileConverter.doBack(task.getProfile());
        } catch (Exception e) {
            throw BusinessExceptionDescription.ERROR.withException(e);
        }

        Server server = serverJpaRepository.getServer(parameters.getServerId());
        boolean hasGpu = server.getGpus() > 0;

        GpuDecodeAndEncodeConfig gpuDecodeAndEncodeConfig;
        int server_gpus = 0;
        if(hasGpu) {
            gpuDecodeAndEncodeConfig = gpuLoadBalanceManager.acquireGpuItems(
                    task.getId(),
                    getActualUsedGpuCoreAmount(taskProfileDto.getUsedGpuCoreAmount()),
                    /*parameters.getTaskParams().getRowCount() * parameters.getTaskParams().getColumnCount() * parameters.getTaskParams().getGroupCount()*/parameters.getTaskParams().getAmountOfDecodedInputs(),
                    parameters.getServerId(),parameters.getGpuStartIdx()
            );
            server_gpus = gpuLoadBalanceManager.getGpuCount(parameters.getServerId());
        } else {
            gpuDecodeAndEncodeConfig = new GpuDecodeAndEncodeConfig(new HashMap<String, Integer>(), 0);
        }

        Boolean mosaic = Environment.getProfiler().isMosaic();
        Integer AudioStartpid = 101;
        List<String> seis;
        if(mosaic) {
            SEIMessageBuilder seiMessageBuilder = new SEIMessageBuilder(AudioStartpid, taskRepository, freemarkerService, taskProfileConverter);
            seiMessageBuilder.build(parameters);
            seis = seiMessageBuilder.getSeiMessages();
        }else {
            seis = null;
        }

        List<ValidScreenPosition>   ValidInputIndexs = new ArrayList<>();

        if(mosaic)
        {
            List<ScreenPositionConfig> ScreenConfigs = parameters.getConfigs();
            for(Integer i = 0;i<ScreenConfigs.size();i++)
            {
                ScreenPositionConfig position = ScreenConfigs.get(i);
                if(position.getIsPlaceHolder() )
                    continue;

                ValidInputIndexs.add(new ValidScreenPosition(position.getIndex(),ValidInputIndexs.size(),null));
            }
        }
        TranscoderXmlBuilderResource resource = new TranscoderXmlBuilderResource(parameters.getConfigs().size(),AudioStartpid,taskProfileDto,
                parameters.getTaskParams().getTaskType(),mosaic);

        int actualGpuIdx = 0;
        if(server_gpus > 0 && parameters.getGpuStartIdx() >= 0){
            actualGpuIdx = parameters.getGpuStartIdx()%server_gpus;
        }else{
            actualGpuIdx = gpuDecodeAndEncodeConfig.getIndexOfEncodeGpu();
        }

        Boolean mixAudio = false;
        if(mosaic){
            try{
                mixAudio = resource.getAudioProfiles().get(0).getAudiomix();
            }catch (Exception e){

            }finally {
                if(mixAudio==null)
                    mixAudio = false;
            }
        }

        ImmutableMap.Builder<String, Object> builder =  ImmutableMap.<String, Object>builder()
                .put("xmlBuilderResource", resource)
                .put("composeTask", parameters.getTaskParams())
                .put("encodingOption", taskProfileDto.getEncodingOption())
                .put("allowProgramIdChange", taskProfileDto.getAllowProgramIdChange())
                .put("screenSwitchTime", parameters.getTaskParams().getSwitchTime()*1000)
                .put("screenGroupCount", parameters.getTaskParams().getGroupCount())
                .put("screenPositionConfigs", parameters.getConfigs())
                .put("encodeGpuIndex",actualGpuIdx )
                .put("screenPositionAndGpuIndex", gpuDecodeAndEncodeConfig.getAssignedIndexPairOfInputAndDecodeGpu())
                .put("hasGpu", hasGpu)
                .put("isWowzaRtsp", Environment.getProfiler().isEnableWowza())
                .put("ValidInputIndexs",ValidInputIndexs)
                .put("mosaic",mosaic)
                .put("mixAudio",mixAudio);

        if(parameters.getTaskParams().getBackground()!=null)
            builder.put("bgImageUri",parameters.getTaskParams().getBackground());

        Map<String, Object> model  = builder.build();
        final String copyOfTemplate = transcoderTemplateService.find().getTemplate();
        try {
            Template tpl = freemarkerService.createTemplateFromString("transcoder-template", copyOfTemplate);
            try (StringBuilderWriter writer = new StringBuilderWriter()) {
                tpl.process(model, writer);
                return new BuilderResourceAndXml(resource, writer.toString(),seis);
            }
        } catch (IOException | TemplateException e) {
            throw BusinessExceptionDescription.ERROR.withException(e);
        }
    }

    /**
     * Returns the actually amount of used gpu core.
     * <p>The returns value decision by <code>{@link GpuConfiguration#enableSpan}</code>.
     *
     * @param expectUsedGpuCoreAmount the amount of expect used amount of gpu core
     * @return The value of <code>expectUsedGpuCoreAmount</code> if <code>enableSpan</code> is true,
     * otherwise will be 1.
     */
    private int getActualUsedGpuCoreAmount(int expectUsedGpuCoreAmount) {
        return gpuConfigService.getFromCache() != null && gpuConfigService.getFromCache().isEnableSpan() ?
                expectUsedGpuCoreAmount : 1;
    }

}
