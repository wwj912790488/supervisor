--test data for user
INSERT INTO `user`(`id`, `password`, `real_name`, `role`, `userName`) VALUES ('1', '21232f297a57a5a743894a0e4a801fc3', 'admin', '1', 'admin');

--test data for transcoder template
INSERT INTO `transcoder_template`(`id`, `template`) VALUES(NULL, '<#function evenNumber a b>
<#assign value=(a / b)>
<#if (value % 2) == 0>
	<#return value>
<#else>
	<#return (value - 1)>
</#if>
</#function>
<?xml version="1.0" encoding="UTF-8"?>
<TranscoderTask ID="${composeTask.id}">
<GPUID>${encodeGpuIndex}</GPUID><#-- Gpu used for encode or preprocessing.Start with 0 -->
<AVSettings>
<#-- VideoSettings-->
<VideoSettings Count="${xmlBuilderResource.videoProfileAndTaskOutputTargetTypes?size}">
<#list xmlBuilderResource.videoProfileAndTaskOutputTargetTypes as videoProfileAndTaskOutputTargetType>
<#assign videoProfile=videoProfileAndTaskOutputTargetType.videoProfile targetType=videoProfileAndTaskOutputTargetType.targetType>
<VideoSetting idx = "${videoProfileAndTaskOutputTargetType_index?c}">
<#-- Notes: Because the CodecType of sdi must be RAW, we need special handle in here -->
<CodecType>${((videoProfileAndTaskOutputTargetType.targetType.name() == "SCREEN") && (composeTask.taskType.name() == "SDI_STREAM_COMPOSE"))?string("RAW", videoProfile.codec)}</CodecType>
<Encoding>${encodingOption}</Encoding>
<Profile>${videoProfile.codecProfile}</Profile>
<Level>${((videoProfile.codecLevel)?? && (videoProfile.codecLevel.level != "-1"))?string((videoProfile.codecLevel.level)!, "Auto")}</Level>
<Width><#if (videoProfile.width)?? && (videoProfile.width > 0)>${videoProfile.width?c}<#else>-1</#if></Width>
<Height><#if videoProfile.height?? && (videoProfile.height > 0)>${videoProfile.height?c}<#else>-1</#if></Height>
<#if videoProfile.modeOfWidthAndHeightSwitch.mode == 0 || videoProfile.modeOfWidthAndHeightSwitch.mode == 2>
<PAR_X>${videoProfile.parX}</PAR_X>
<PAR_Y>${videoProfile.parY}</PAR_Y>
</#if>
<#-- Set framerate to 2500 if it is follow source -->
<#assign isFollowSourceFramerate=((videoProfile.followSourceFramerate)?? && videoProfile.followSourceFramerate == true) fx=(videoProfile.framerateX)!0 fy=(videoProfile.framerateY)!0>
<FrameRate><#if isFollowSourceFramerate || (fx <= 0 || fy <= 0)>2500<#else>${(100 * videoProfile.framerateX / videoProfile.framerateY)?c}</#if></FrameRate>
<FrameRateConversionMode>${videoProfile.modeOfFramerateConversion?string(1, 0)}</FrameRateConversionMode>
<RC>${videoProfile.bitrateControl!0}</RC>
<Quantizer>${videoProfile.quantizer!0}</Quantizer>
<BitRate>${((videoProfile.bitrate)?? && (videoProfile.bitrate > 0))?string(videoProfile.bitrate?c, 0)}</BitRate>
<MaxBitRate><#if (videoProfile.maxBitrate)?? && (videoProfile.maxBitrate > 0)>${(videoProfile.maxBitrate)?c}<#else>0</#if></MaxBitRate>
<VBVSize><#if (videoProfile.bufferSize)?? && (videoProfile.bufferSize > 0)>${(videoProfile.bufferSize)?c}<#else>0</#if></VBVSize>
<VBVDelay><#if (videoProfile.initializedBufferFill)?? && (videoProfile.initializedBufferFill > 0)>${(videoProfile.initializedBufferFill)?c}<#else>0</#if></VBVDelay>
<GopSize>${videoProfile.gopsize?c}</GopSize>
<Scenedetection>${videoProfile.scd?string(1, 0)}</Scenedetection>
<BFrame><#if (videoProfile.bframe)?? && (videoProfile.bframe > 0)>${videoProfile.bframe?c}<#else>0</#if></BFrame>
<CABAC>${(videoProfile.cabac)?string(1, 0)}</CABAC>
<Transform8x8>${(videoProfile.transform)?string(1, 0)}</Transform8x8>
<Intra8x8>${(videoProfile.interFramePrediction)?string(1, 0)}</Intra8x8>
<LoopFilter>1</LoopFilter>
<RefFrame><#if (videoProfile.referenceFrame)?? && (videoProfile.referenceFrame > 0)>${(videoProfile.referenceFrame)?c}<#else>0</#if></RefFrame>
<Interlace>${(videoProfile.modeOfFrameAndFiled??)?string(videoProfile.modeOfFrameAndFiled.value, -1)}</Interlace>
<#if videoProfile.modeOfFrameAndFiled?? && videoProfile.modeOfFrameAndFiled.name() != "SOURCE" && videoProfile.modeOfFrameAndFiled.name() != "FRAME" >
<TopFieldFirst>${videoProfile.priorityOfField.value}</TopFieldFirst>
</#if>
<#if encodingOption.name() == "Custom">
<Policy>
<TwoPass>0</TwoPass>
<DeviceID>${(videoProfile.qualityLevel.level == -1)?string(1, 0)}</DeviceID>
<QualityLevel>${videoProfile.qualityLevel.level}</QualityLevel>
</Policy>
</#if>
<#if videoProfile.modeOfWidthAndHeightSwitch.mode == 1>
<SmartStretch>
<DAR_X>${videoProfile.parX?c}</DAR_X>
<DAR_Y>${videoProfile.parY?c}</DAR_Y>
<Operate>${videoProfile.modeOfWidthAndHeightSwitch.mode}</Operate>
<FillColor>0</FillColor>
</SmartStretch>
<#elseif videoProfile.modeOfWidthAndHeightSwitch.mode == 2>
<AutoTrimout>1</AutoTrimout>
</#if>
<Deinterlace>${videoProfile.deinterlace.value}</Deinterlace>
<Deblock>${videoProfile.deblock?string(1, 0)}</Deblock>
<Delight>${videoProfile.delight.value}</Delight>
<Denoise>${videoProfile.denoise.value}</Denoise>
<Sharpen>${videoProfile.sharpen.value}</Sharpen>
<AntiAlias>${videoProfile.antiAlias?string(1, 0)}</AntiAlias>
<AntiShaking>-1</AntiShaking>
<EdgeAA>0</EdgeAA>
<Bright>${(videoProfile.bright?? && videoProfile.bright > 0)?string(videoProfile.bright?c, 0)}</Bright>
<Contrast>${(videoProfile.contrast?? && videoProfile.contrast > 0)?string(videoProfile.contrast?c, 0)}</Contrast>
<Hue>${(videoProfile.hue?? && videoProfile.hue > 0)?string(videoProfile.hue?c, 0)}</Hue>
<Saturation>${(videoProfile.saturation?? && videoProfile.saturation > 0)?string(videoProfile.saturation?c, 0)}</Saturation>
<ReSizeAlg>${videoProfile.resizeAlgorithm.value}</ReSizeAlg>
<DeinterlaceAlg>${videoProfile.deinterlaceAlgorithm.value}</DeinterlaceAlg>
<#-- MultiScreenInfo -->
<MultiScreenInfo Count="${(screenPositionConfigs?size)?c}">
<CheckContent>${(videoProfileAndTaskOutputTargetType.targetType.name() == "SCREEN")?string(1, 0)}</CheckContent>
<#assign perWidth=(evenNumber(videoProfile.width, composeTask.columnCount))?int perHeight=(evenNumber(videoProfile.height, composeTask.rowCount))?int>
<#list screenPositionConfigs as config>
<DisplayInfo idx="${(config.index)?c}">
<InputIdx>${(config.index)?c}</InputIdx>
<XPos>${(config.column * perWidth)?c}</XPos>
<YPos>${(config.row * perHeight)?c}</YPos>
<Width><#if config.column != (composeTask.columnCount - 1)>${(perWidth - 10)?c}<#else>${perWidth?c}</#if></Width>
<Height><#if config.row != (composeTask.rowCount - 1)>${(perHeight - 20)?c}<#else>${perHeight?c}</#if></Height>
<AudioColumn>6</AudioColumn>
<InfoHeight>20</InfoHeight>
<WarningWidth>10</WarningWidth>
</DisplayInfo>
</#list>
</MultiScreenInfo>
</VideoSetting>
</#list>
</VideoSettings>
<#-- AudioSettings -->
<AudioSettings Count="${xmlBuilderResource.audioProfiles?size}">
<#list xmlBuilderResource.audioProfiles as audioProfile>
<AudioSetting idx="${audioProfile_index}">
<CodecType>${audioProfile.audiocodec}</CodecType>
<Profile>${audioProfile.audiocodecprofile}</Profile>
<Channel>${audioProfile.audiochannel.value}</Channel>
<BitRate>${((audioProfile.audiobitrate * 1000)?int)?c}</BitRate>
<SampleRate>${(audioProfile.audiosamplerate.value)?c}</SampleRate>
<BoostLevel>${audioProfile.audioboostlevel.value}</BoostLevel>
<ChannelProcessing>${audioProfile.audiochannelprocessing}</ChannelProcessing>
<VolumeProcessMode>${audioProfile.audiovolumemode.value}</VolumeProcessMode>
<BalanceDB>${(audioProfile.audiobalancedb??)?string(audioProfile.audiobalancedb?c, -30)}</BalanceDB>
<BalanceLevel>${audioProfile.audiobalancelevel.value}</BalanceLevel>
</AudioSetting>
</#list>
</AudioSettings>
</AVSettings>
<#-- OutputGroups -->
<OutputGroups Count="${xmlBuilderResource.taskOutputVideoAndAudiosMappers?size}">
<#list xmlBuilderResource.taskOutputVideoAndAudiosMappers as taskOutputVideoAndAudioMapper>
<OutputGroup idx="${taskOutputVideoAndAudioMapper_index}">
<#assign taskOutput=taskOutputVideoAndAudioMapper.taskOutput>
<#-- Generate node by type of task -->
<#if composeTask.taskType.name() == "IP_STREAM_COMPOSE" || taskOutput.targetType.name() == "MOBILE">
<OutputType>${taskOutput.type}</OutputType>
<OutputCount>1</OutputCount><#-- The value of OutputCount always 1 -->
<Container>${taskOutput.container}</Container>
<TargetPath><#if (taskOutput.targetType.name() == "MOBILE") && composeTask.enableRtsp>${r"${rtspOutputIp}"}<#else>${r"${outputIp}"}</#if></TargetPath>
<Port><#if (taskOutput.targetType.name() == "MOBILE") && composeTask.enableRtsp>${r"${rtspOutputPort?c}"}<#else>${r"${outputPort?c}"}</#if></Port>
<TTL>${(taskOutput.ttl)?c}</TTL>
<BufferSize>${(taskOutput.bufferSize)?c}</BufferSize>
<SrcIp></SrcIp>
<SrcPort></SrcPort>
<TsSetting>
<#assign tsOption=taskOutput.tsOption>
<ServerName>${tsOption.tsservicename}</ServerName>
<ServiceProvider>${tsOption.tsserviceprovider}</ServiceProvider>
<SericeId>${tsOption.tsserviceid}</SericeId>
<PmtPid>${tsOption.tspmtpid}</PmtPid>
<VideoPid>${tsOption.tsvideopid}</VideoPid>
<AudioPid>${tsOption.tsaudiopid}</AudioPid>
<PcrPid>${tsOption.tspcrpid}</PcrPid>
<TotalBitrate>${tsOption.tstotalbitrate}</TotalBitrate>
</TsSetting>
<#elseif composeTask.taskType.name() == "SDI_STREAM_COMPOSE">
<OutputType>AdaptiveStreaming</OutputType>
<OutputCount>1</OutputCount>
<Container>SDIStream</Container>
<TargetPath>${r"${outputIp}"}</TargetPath><#-- The device of sdi -->
</#if>
<Output idx="0">
<VideoSettingIdx>${taskOutputVideoAndAudioMapper.videoProfileIndexMapper.index}</VideoSettingIdx>
<AudioSettingIdx>${taskOutputVideoAndAudioMapper.audioProfileIndexMappers[0].index}</AudioSettingIdx>
<AudioInputIdx>-1</AudioInputIdx>
</Output>
</OutputGroup>
</#list>
</OutputGroups>
<#-- MultiScreenInputs -->
<MultiScreenInputs Count="${(screenPositionConfigs?size)?c}">
<ContentDetectXml>${r"${contentDetectConfigPath}"}</ContentDetectXml>
<Type>3</Type><#-- Used for switch audio -->
<#list screenPositionConfigs as config>
<Input idx="${config.index}">
<Type>Network</Type>
<FailoverTime>3000</FailoverTime>
<IPAddr></IPAddr>
<URI>${config.url}</URI>
<DeviceId>${screenPositionAndGpuIndex[(config_index)?c]}</DeviceId><#-- The index of gpu for decode.The index start with 1.Indicates used cpu if value is 0. -->
<ExInputs Count="0"></ExInputs>
<Program Id="${config.programId}">
<VideoId>-1</VideoId>
<AudioId>${config.audioId}</AudioId>
<SubtitleId>-3</SubtitleId>
<Channel>${(config.channelName)!""}</Channel>
</Program>
<#if (config.signalDetectSetting)?? && (config.signalDetectSetting.warningItems?size > 0)>
<#assign signalDetectSetting=config.signalDetectSetting>
<SignalSetting>
<SwitchMode>${signalDetectSetting.switchMode}</SwitchMode>
<WarningPeriod>${(signalDetectSetting.periodMilliSecondsOfWarning)?c}</WarningPeriod>
<SwitchItems Count="${signalDetectSetting.switchItems?size}">
<#list signalDetectSetting.switchItems as switchItem>
<SignalItem idx="${switchItem_index}">
<Type>${switchItem.type}</Type>
<Check>${switchItem.valueOfChecked}</Check>
<Timeout>${(switchItem.millisecondsOfTimeout)?c}</Timeout>
<Para>${(switchItem.para)?c}</Para>
</SignalItem>
</#list>
</SwitchItems>
<WarningItems Count="${signalDetectSetting.warningItems?size}">
<#list signalDetectSetting.warningItems as warningItem>
<SignalItem idx="${warningItem_index}">
<Type>${warningItem.type}</Type>
<Check>${warningItem.valueOfChecked}</Check>
<Timeout>${(warningItem.millisecondsOfTimeout)?c}</Timeout>
<Para>${(warningItem.para)?c}</Para>
</SignalItem>
</#list>
</WarningItems>
</SignalSetting>
</#if>
</Input>
</#list>
</MultiScreenInputs>
</TranscoderTask>
');

--test data for output profile
INSERT INTO `profile_template` (`id`, `template`) values (null, '{"videoprofiles":[{"videocodec":"H264","videocodecprofile":"Main","videocodeclevel":"-1","videoheight":480,"videowidth":640,"videoPAR":"4:3","videosourcePAR":true,"videoPARX":3,"videoPARY":2,"videosmartborder":1,"videoratecontrol":"VBR","videobitrate":1000,"videomaxbitrate":3000,"videoqualityleveldisp":3,"videobufferfill":1000,"videobuffersize":375,"videoquantizer":0,"videogopsize":30,"videobframe":0,"videoreferenceframe":1,"videoCABAC":true,"videointraprediction":false,"videotransform":false,"videoSCD":false,"videodeinterlace":2,"videodeinterlacealg":2,"videoresizealg":3,"videodenoise":0,"videodeblock":false,"videosharpen":0,"videoantialias":false,"videobright":0,"videocontrast":0,"videosaturation":0,"videohue":0,"videodelight":0,"videoframerate":"40:1","videosourceframerate":true,"videoframerateX":40,"videoframerateY":1,"videoframerateconversionmode":false,"videointerlace":"-1","videotopfieldfirst":"-1"}],"audioprofiles":[{"audiopassthrough":false,"audiocodec":"AAC","audiocodecprofile":"LC","audiochannel":2,"audiosamplerate":44100,"audiobitrate":64,"audiovolumemode":0,"audioboostlevel":0,"audiobalancelevel":0,"audiobalancedb":-30,"audiochannelprocessing":"None"}]}');
INSERT INTO `profile`(`id`, `description`,`name`, `template_id`, `type`) VALUES (NULL, 'desc-1', 'output-profile-1', 1, '2');
INSERT INTO `profile_output`(`video_audio_description`, `id`) VALUES ('H264 640x480 VBR 1000Kbps | AAC 44.1KHz 2 声道 64Kbps', 1);

INSERT INTO `profile_template` (`id`, `template`) values (null, '{"videoprofiles":[{"videocodec":"H264","videocodecprofile":"Main","videocodeclevel":"-1","videoheight":480,"videowidth":640,"videoPAR":"4:3","videosourcePAR":true,"videoPARX":3,"videoPARY":2,"videosmartborder":1,"videoratecontrol":"VBR","videobitrate":1000,"videomaxbitrate":3000,"videoqualityleveldisp":3,"videobufferfill":1000,"videobuffersize":375,"videoquantizer":0,"videogopsize":30,"videobframe":0,"videoreferenceframe":1,"videoCABAC":true,"videointraprediction":false,"videotransform":false,"videoSCD":false,"videodeinterlace":2,"videodeinterlacealg":2,"videoresizealg":3,"videodenoise":0,"videodeblock":false,"videosharpen":0,"videoantialias":false,"videobright":0,"videocontrast":0,"videosaturation":0,"videohue":0,"videodelight":0,"videoframerate":"40:1","videosourceframerate":true,"videoframerateX":40,"videoframerateY":1,"videoframerateconversionmode":false,"videointerlace":"-1","videotopfieldfirst":"-1"}],"audioprofiles":[{"audiopassthrough":false,"audiocodec":"AAC","audiocodecprofile":"LC","audiochannel":2,"audiosamplerate":44100,"audiobitrate":64,"audiovolumemode":0,"audioboostlevel":0,"audiobalancelevel":0,"audiobalancedb":-30,"audiochannelprocessing":"None"}]}');
INSERT INTO `profile`(`id`, `description`,`name`, `template_id`, `type`) VALUES (NULL, 'desc-2', 'output-profile-2', 2, '2');
INSERT INTO `profile_output`(`video_audio_description`, `id`) VALUES ('H264 640x480 VBR 1000Kbps | AAC 44.1KHz 2 声道 64Kbps', 2);


INSERT INTO `profile_template` (`id`, `template`) values (null, '{"videoprofiles":[{"videocodec":"H264","videocodecprofile":"Main","videocodeclevel":"-1","videoheight":480,"videowidth":640,"videoPAR":"4:3","videosourcePAR":true,"videoPARX":3,"videoPARY":2,"videosmartborder":1,"videoratecontrol":"VBR","videobitrate":1000,"videomaxbitrate":3000,"videoqualityleveldisp":3,"videobufferfill":1000,"videobuffersize":375,"videoquantizer":0,"videogopsize":30,"videobframe":0,"videoreferenceframe":1,"videoCABAC":true,"videointraprediction":false,"videotransform":false,"videoSCD":false,"videodeinterlace":2,"videodeinterlacealg":2,"videoresizealg":3,"videodenoise":0,"videodeblock":false,"videosharpen":0,"videoantialias":false,"videobright":0,"videocontrast":0,"videosaturation":0,"videohue":0,"videodelight":0,"videoframerate":"40:1","videosourceframerate":true,"videoframerateX":40,"videoframerateY":1,"videoframerateconversionmode":false,"videointerlace":"-1","videotopfieldfirst":"-1"}],"audioprofiles":[{"audiopassthrough":false,"audiocodec":"AAC","audiocodecprofile":"LC","audiochannel":2,"audiosamplerate":44100,"audiobitrate":64,"audiovolumemode":0,"audioboostlevel":0,"audiobalancelevel":0,"audiobalancedb":-30,"audiochannelprocessing":"None"}]}');
INSERT INTO `profile`(`id`, `description`,`name`, `template_id`, `type`) VALUES (NULL, 'desc-3', 'output-profile-3', 3, '2');
INSERT INTO `profile_output`(`video_audio_description`, `id`) VALUES ('H264 640x480 VBR 1000Kbps | AAC 44.1KHz 2 声道 64Kbps', 3);


--test data for task profile
INSERT INTO `profile_template` (`id`, `template`) values (null, '{"taskoutputprofiles":[{"videoprofiles":[{"videocodec":"H264","videocodecprofile":"Main","videocodeclevel":"-1","videoheight":480,"videowidth":640,"videoPAR":"4:3","videosourcePAR":true,"videoPARX":4,"videoPARY":3,"videosmartborder":1,"videoratecontrol":"VBR","videobitrate":1000,"videomaxbitrate":3000,"videoqualityleveldisp":3,"videobufferfill":1000,"videobuffersize":375,"videoquantizer":0,"videogopsize":30,"videobframe":0,"videoreferenceframe":1,"videoCABAC":true,"videointraprediction":false,"videotransform":false,"videoSCD":false,"videodeinterlace":2,"videodeinterlacealg":2,"videoresizealg":3,"videodenoise":0,"videodeblock":false,"videosharpen":0,"videoantialias":false,"videobright":0,"videocontrast":0,"videosaturation":0,"videohue":0,"videodelight":0,"videoframerate":"40:1","videosourceframerate":true,"videoframerateX":40,"videoframerateY":1,"videoframerateconversionmode":false,"videointerlace":"-1","videotopfieldfirst":"-1"}],"audioprofiles":[{"audiopassthrough":false,"audiocodec":"AAC","audiocodecprofile":"LC","audiochannel":2,"audiosamplerate":44100,"audiobitrate":64,"audiovolumemode":0,"audioboostlevel":0,"audiobalancelevel":0,"audiobalancedb":-30,"audiochannelprocessing":"None"}]}],"taskoutputs":[{"uniqueType":"UdpStreaming-UDPOverTS","outputDest": 0,"linkedprofile":0,"outputtype":"UdpStreaming","outputcontainer":"UDPOverTS","outputbuffersize":65535,"outputTTL":255,"outputport":1234,"outputtsoption":{"tsservicename":"","tspmtpid":"","tsserviceprovider":"","tsvideopid":"","tsserviceid":"","tsaudiopid":"","tstotalbitrate":"","tspcrpid":"","tsnetworkid":"","tstransportid":"","tsinserttottdt":"false","tstottdtperiod":"","tspcrperiod":"","tspatperiod":"","tssdtperiod":"","tsprivatemetadatapid":"","tsprivatemetadatatype":""}}]}');
INSERT INTO `profile`(`id`, `description`,`name`, `template_id`, `type`) VALUES (NULL, 'desc-1', 'task-profile-1', 4, '3');
INSERT INTO `profile_task`(`id`, `encodingOption`, `priority`, `used_gpu_core_amount`, `screenRow`,`screenColumn`, `amount_output`) VALUES (4, 'Balance', 'P5', 2, 3, 3, 1);


INSERT INTO `profile_template` (`id`, `template`) values (null, '{"taskoutputprofiles":[{"videoprofiles":[{"videocodec":"H264","videocodecprofile":"Main","videocodeclevel":"-1","videoheight":480,"videowidth":640,"videoPAR":"4:3","videosourcePAR":true,"videoPARX":4,"videoPARY":3,"videosmartborder":1,"videoratecontrol":"VBR","videobitrate":1000,"videomaxbitrate":3000,"videoqualityleveldisp":3,"videobufferfill":1000,"videobuffersize":375,"videoquantizer":0,"videogopsize":30,"videobframe":0,"videoreferenceframe":1,"videoCABAC":true,"videointraprediction":false,"videotransform":false,"videoSCD":false,"videodeinterlace":2,"videodeinterlacealg":2,"videoresizealg":3,"videodenoise":0,"videodeblock":false,"videosharpen":0,"videoantialias":false,"videobright":0,"videocontrast":0,"videosaturation":0,"videohue":0,"videodelight":0,"videoframerate":"40:1","videosourceframerate":true,"videoframerateX":40,"videoframerateY":1,"videoframerateconversionmode":false,"videointerlace":"-1","videotopfieldfirst":"-1"}],"audioprofiles":[{"audiopassthrough":false,"audiocodec":"AAC","audiocodecprofile":"LC","audiochannel":2,"audiosamplerate":44100,"audiobitrate":64,"audiovolumemode":0,"audioboostlevel":0,"audiobalancelevel":0,"audiobalancedb":-30,"audiochannelprocessing":"None"}]}],"taskoutputs":[{"uniqueType":"UdpStreaming-UDPOverTS","outputDest": 0,"linkedprofile":0,"outputtype":"UdpStreaming","outputcontainer":"UDPOverTS","outputbuffersize":65535,"outputTTL":255,"outputport":1234,"outputtsoption":{"tsservicename":"","tspmtpid":"","tsserviceprovider":"","tsvideopid":"","tsserviceid":"","tsaudiopid":"","tstotalbitrate":"","tspcrpid":"","tsnetworkid":"","tstransportid":"","tsinserttottdt":"false","tstottdtperiod":"","tspcrperiod":"","tspatperiod":"","tssdtperiod":"","tsprivatemetadatapid":"","tsprivatemetadatatype":""}}]}');
INSERT INTO `profile`(`id`, `description`,`name`, `template_id`, `type`) VALUES (NULL, 'desc-2', 'task-profile-2', 5, '3');
INSERT INTO `profile_task`(`id`, `encodingOption`, `priority`, `used_gpu_core_amount`, `screenRow`,`screenColumn`, `amount_output`) VALUES (5, 'Balance', 'P5', 2, 3, 3, 1);


INSERT INTO `profile_template` (`id`, `template`) values (null, '{"taskoutputprofiles":[{"videoprofiles":[{"videocodec":"H264","videocodecprofile":"Main","videocodeclevel":"-1","videoheight":480,"videowidth":640,"videoPAR":"4:3","videosourcePAR":true,"videoPARX":4,"videoPARY":3,"videosmartborder":1,"videoratecontrol":"VBR","videobitrate":1000,"videomaxbitrate":null,"videoqualityleveldisp":3,"videobufferfill":1000,"videobuffersize":375,"videoquantizer":0,"videogopsize":30,"videobframe":4,"videoreferenceframe":1,"videoCABAC":true,"videointraprediction":false,"videotransform":false,"videoSCD":false,"videodeinterlace":2,"videodeinterlacealg":2,"videoresizealg":3,"videodenoise":0,"videodeblock":false,"videosharpen":0,"videoantialias":false,"videobright":0,"videocontrast":0,"videosaturation":0,"videohue":0,"videodelight":0,"videoframerate":"40:1","videosourceframerate":true,"videoframerateX":40,"videoframerateY":1,"videoframerateconversionmode":false,"videointerlace":"-1","videotopfieldfirst":"-1"}],"audioprofiles":[{"audiopassthrough":false,"audiocodec":"AAC","audiocodecprofile":"LC","audiochannel":2,"audiosamplerate":44100,"audiobitrate":64,"audiovolumemode":0,"audioboostlevel":0,"audiobalancelevel":0,"audiobalancedb":-30,"audiochannelprocessing":"None"}]}],"taskoutputs":[{"uniqueType":"UdpStreaming-UDPOverTS","outputDest": 0,"linkedprofile":0,"outputtype":"UdpStreaming","outputcontainer":"UDPOverTS","outputbuffersize":65535,"outputTTL":255,"outputport":1234,"outputtsoption":{"tsservicename":"","tspmtpid":"","tsserviceprovider":"","tsvideopid":"","tsserviceid":"","tsaudiopid":"","tstotalbitrate":"","tspcrpid":"","tsnetworkid":"","tstransportid":"","tsinserttottdt":"false","tstottdtperiod":"","tspcrperiod":"","tspatperiod":"","tssdtperiod":"","tsprivatemetadatapid":"","tsprivatemetadatatype":""}}]}');
INSERT INTO `profile`(`id`, `description`,`name`, `template_id`, `type`) VALUES (NULL, 'desc-3', 'task-profile-3', 6, '3');
INSERT INTO `profile_task`(`id`, `encodingOption`, `priority`, `used_gpu_core_amount`, `screenRow`,`screenColumn`, `amount_output`) VALUES (6, 'Balance', 'P5', 2, 3, 3, 1);

INSERT INTO `wall`(`id`, `column_count`, `name`, `row_count`, `type`, `version`) VALUES (NULL, 1, 'test', 1, 1, 0);
INSERT INTO `wall_position`(`id`, "column", "row", `wall_id`) VALUES(NULL, 0, 0, 1);
INSERT INTO `screen`(`id`, `wall_position_id`) VALUES (NULL, 1);
INSERT INTO `task`(`id`, `ref_id`, `type`, `profile_id`, `server_id`) VALUES(NULL, 1, 2, 6, 'ffxxx');

INSERT INTO `wall`(`id`, `column_count`, `name`, `row_count`, `type`, `version`) VALUES (NULL, 1, 'test-2', 1, 1, 0);
INSERT INTO `wall_position`(`id`, "column", "row", `wall_id`) VALUES(NULL, 0, 0, 2);
INSERT INTO `screen`(`id`, `wall_position_id`) VALUES (NULL, 2);
INSERT INTO `task`(`id`, `ref_id`, `type`, `profile_id`, `server_id`) VALUES(NULL, 1, 2, 6, 'ffxxx');


