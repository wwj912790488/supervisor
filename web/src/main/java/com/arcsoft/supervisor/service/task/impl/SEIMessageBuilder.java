package com.arcsoft.supervisor.service.task.impl;

import com.arcsoft.supervisor.commons.freemarker.FreemarkerService;
import com.arcsoft.supervisor.exception.service.BusinessExceptionDescription;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.model.domain.task.TaskProfile;
import com.arcsoft.supervisor.model.dto.graphic.ScreenPositionConfig;
import com.arcsoft.supervisor.model.vo.task.profile.OutputProfileDto;
import com.arcsoft.supervisor.model.vo.task.profile.TaskProfileDto;
import com.arcsoft.supervisor.model.vo.task.profile.VideoProfile;
import com.arcsoft.supervisor.repository.task.TaskRepository;
import com.arcsoft.supervisor.service.converter.Converter;
import com.arcsoft.supervisor.service.task.TranscoderXmlBuilder;
import com.arcsoft.supervisor.utils.app.Environment;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yshe on 2016/6/21.
 */
public class SEIMessageBuilder {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private TaskRepository taskRepository;
    private FreemarkerService freemarkerService;
    private Converter<TaskProfileDto, TaskProfile> taskProfileConverter;
    private List<String>    seiMessages;
    private Integer AudioStartpid;

    public SEIMessageBuilder(Integer AudioStartpid,
                             TaskRepository taskRepository,
                             FreemarkerService freemarkerService,
                             Converter<TaskProfileDto, TaskProfile> taskProfileConverter)
    {
        this.AudioStartpid = AudioStartpid;
        this.taskRepository = taskRepository;
        this.freemarkerService = freemarkerService;
        this.taskProfileConverter = taskProfileConverter;
    }

    void build(TranscoderXmlBuilder.BuilderParameters parameters)
    {
        seiMessages = new ArrayList<String>();

        Task task = taskRepository.findOne(parameters.getTaskParams().getId());
        TaskProfileDto taskProfileDto;
        try {
            taskProfileDto = taskProfileConverter.doBack(task.getProfile());
        } catch (Exception e) {
            throw BusinessExceptionDescription.ERROR.withException(e);
        }

        List<OutputProfileDto> outputprofiles = taskProfileDto.getOutputProfiles();
        if(outputprofiles!=null)
        {
            for (int outputIndex = 0; outputIndex < outputprofiles.size(); outputIndex++) {
                OutputProfileDto outputProfileDto = outputprofiles.get(outputIndex);
                if(outputProfileDto!=null)
                {
                    VideoProfile videoprofile =outputProfileDto.getVideoprofiles().get(0);
                    if(videoprofile!=null)
                    {
                        List<ValidScreenPosition>   ValidConfigs = new ArrayList<>();
                        List<ScreenPositionConfig> ScreenConfigs = parameters.getConfigs();
                        Integer validIdx = 0;
                        for(Integer i = 0;i<ScreenConfigs.size();i++)
                        {
                            ScreenPositionConfig position = ScreenConfigs.get(i);
                            if(position.getIsPlaceHolder())
                            {
                                ValidConfigs.add(new ValidScreenPosition(position.getIndex(),-1,position));
                            }
                            else
                            {
                                ValidConfigs.add(new ValidScreenPosition(position.getIndex(),validIdx,position));
                                validIdx++;
                            }
                        }

                        Map<String, Object> model1 = ImmutableMap.<String, Object>builder()
                                .put("videoProfile", videoprofile)
                                .put("perWidth", videoprofile.getWidth()/parameters.getTaskParams().getColumnCount())
                                .put("perHeight", videoprofile.getHeight()/parameters.getTaskParams().getRowCount())
                                .put("columnCount", parameters.getTaskParams().getColumnCount())
                                .put("rowCount", parameters.getTaskParams().getRowCount())
                                .put("screenPositionConfigs", ValidConfigs)
                                .put("AudioStartpid",AudioStartpid).build();

                        try
                        {
                            String seimessage = freemarkerService.renderFromTemplateFile("seimessage_json.tpl",model1);
                            seimessage = seimessage.replaceAll("[\\t\\r\\n]","");//remove all \r\n\t
                            seimessage = seimessage.replaceAll("\\s*","");//remove all space
                            seiMessages.add(seimessage);
                        }
                        catch (Exception e)
                        {
                            logger.info(e.getMessage(),e);
                        }
                    }
                }
            }
        }
    }

    public  List<String> getSeiMessages(){return seiMessages;}
    public  void setSeiMessages(List<String> seiMessages){this.seiMessages=seiMessages;}

}
