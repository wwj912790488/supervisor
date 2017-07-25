mysql supervisordb -u root -proot -se 'alter table screen_position modify column row int;'
mysql supervisordb -u root -proot -se 'alter table screen_position modify column `column` int;'
mysql supervisordb -u root -proot -se 'alter table wall_position modify column row int;'
mysql supervisordb -u root -proot -se 'alter table wall_position modify column `column` int;'
mysql supervisordb -u root -proot -se 'alter table wall modify column column_count int;'
mysql supervisordb -u root -proot -se 'alter table wall modify column row_count int;'
mysql supervisordb -u root -proot -se 'alter table screen_schema modify column schema_value int;'
mysql supervisordb -u root -proot -se 'alter table screen_schema modify column column_count int;'
mysql supervisordb -u root -proot -se 'alter table screen_schema modify column row_count int;'
mysql supervisordb -u root -proot -se 'alter table channel modify column original_channel_id varchar(255);'
mysql supervisordb -u root -proot -se 'update transcoder_template set template='"'"'<#function evenNumber a b>
<#assign value=(a / b)>
<#if (value % 2) == 0>
	<#return value>
<#else>
	<#return (value - 1)>
</#if>
</#function>
<#function toEvenNumber a>
<#if a== 1>
	<#return a>
</#if>
<#return a>
<#if (a % 2) == 0>
	<#return a>
<#else>
	<#return (a - 1)>
</#if>
</#function>
<?xml version="1.0" encoding="UTF-8"?>
<TranscoderTask ID="${composeTask.id}">
<#if hasGpu>
<GPUID>${encodeGpuIndex}</GPUID>
</#if>
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
<#if mosaic>
<SEIInput>${r"${seimessage_path_"}${videoProfileAndTaskOutputTargetType_index?c}${r"}"}</SEIInput>
</#if>
<#-- MultiScreenInfo -->
<MultiScreenInfo Count="${(screenPositionConfigs?size)?c}">
<#if bgImageUri??>
<BackgroundImg>${bgImageUri}</BackgroundImg>
</#if>
<#if screenGroupCount gt 1>
<GroupInfo>
  <Count>${screenGroupCount?c}</Count>
  <SwitchTime>${screenSwitchTime?c}</SwitchTime>
</GroupInfo>
</#if>
<#if mosaic>
<CheckContent>0</CheckContent>
<#else>
<CheckContent>1</CheckContent>
</#if>
<#assign perWidth=toEvenNumber(((videoProfile.width?float)/(composeTask.columnCount?float))?int) perHeight=toEvenNumber(((videoProfile.height?float)/(composeTask.rowCount?float))?int)>
<#--<#assign perWidth=videoProfile.width/composeTask.columnCount?int perHeight=videoProfile.height/composeTask.rowCount?int>-->
<#list screenPositionConfigs as config>
<DisplayInfo idx="${(config.index)?c}">
<#if screenGroupCount gt 1>
<GroupIdx>${(config.group)?c}</GroupIdx>
</#if>
<InputIdx>${(config.index)?c}</InputIdx>
<#-- mixaudio primary audio -->
<#if mixAudio && (config.index == 0)>
<Primary>1</Primary>
</#if>
<XPos>${(toEvenNumber((config.column * perWidth)?round)?int)?c}</XPos>
<YPos>${(toEvenNumber((config.row * perHeight)?round)?int)?c}</YPos>
<#if mosaic>
<Width><#if (config.column + config.x) != composeTask.columnCount>${(toEvenNumber((perWidth*config.x)?round)?int)?c}<#else>${(toEvenNumber((perWidth*config.x)?round)?int)?c}</#if></Width>
<Height><#if (config.row + config.y) != composeTask.rowCount>${(toEvenNumber((perHeight*config.y)?round)?int)?c}<#else>${(toEvenNumber((perHeight*config.y)?round)?int)?c}</#if></Height>
<AudioColumn>0</AudioColumn>
<InfoHeight>0</InfoHeight>
<WarningWidth>0</WarningWidth>
<#else>
<Width><#if (config.column + config.x) != composeTask.columnCount>${(toEvenNumber((perWidth*config.x)?round)?int- 10)?c}<#else>${(toEvenNumber((perWidth*config.x)?round)?int)?c}</#if></Width>
<Height><#if (config.row + config.y) != composeTask.rowCount>${(toEvenNumber((perHeight*config.y)?round)?int- 20)?c}<#else>${(toEvenNumber((perHeight*config.y)?round)?int)?c}</#if></Height>
<AudioColumn>6</AudioColumn>
<InfoHeight>20</InfoHeight>
<WarningWidth>10</WarningWidth>
</#if>
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
<#if audioProfile.audiocodec=="AAC">
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
<#if mixAudio && (ValidInputIndexs?size > 0)>
<MixInfos Count="${(ValidInputIndexs?size)?c}">
<#list ValidInputIndexs as config>
<MixInfo idx="${config.validindex}">
<InputIdx>${config.validindex}</InputIdx>
</MixInfo>
</#list>
</MixInfos>
</#if>
<#elseif audioProfile.audiocodec=="MP2">
<CodecType>MPA</CodecType>
<Profile>L2</Profile>
<Channel>${audioProfile.audiochannel.value}</Channel>
<BitRate>${((audioProfile.audiobitrate * 1000)?int)?c}</BitRate>
<SampleRate>${(audioProfile.audiosamplerate.value)?c}</SampleRate>
<Denoise>0</Denoise>
<BoostLevel>${audioProfile.audioboostlevel.value}</BoostLevel>
<ChannelProcessing>${audioProfile.audiochannelprocessing}</ChannelProcessing>
<VolumeProcessMode>${audioProfile.audiovolumemode.value}</VolumeProcessMode>
<BalanceDB>${(audioProfile.audiobalancedb??)?string(audioProfile.audiobalancedb?c, -30)}</BalanceDB>
<BalanceLevel>${audioProfile.audiobalancelevel.value}</BalanceLevel>
<#if mixAudio && (ValidInputIndexs?size > 0)>
</#if>
</#if>
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
<#if (taskOutput.targetType.name() == "MOBILE") && composeTask.enableRtsp>
<#-- rtmp -->
<OutputType>FlashStreaming</OutputType>
<#if mosaic && !mixAudio>
<OutputCount><#if ((taskOutputVideoAndAudioMapper.audioProfileIndexMappers?size)>0)>${(ValidInputIndexs?size)?c}<#else>1</#if></OutputCount>
<#else>
<OutputCount>1</OutputCount><#-- The value of OutputCount always 1 -->
</#if>
<Container>RTMP</Container>
<DeliverPoint>${r"${rtmpUrl}"}</DeliverPoint>
<#else>
<#if taskOutput.type.name() == "UdpStreaming">
<OutputType>${taskOutput.type}</OutputType>
<#if mosaic && !mixAudio>
<OutputCount><#if ((taskOutputVideoAndAudioMapper.audioProfileIndexMappers?size)>0)>${(ValidInputIndexs?size)?c}<#else>1</#if></OutputCount>
<#else>
<OutputCount>1</OutputCount><#-- The value of OutputCount always 1 -->
</#if>
<Container>${taskOutput.container}</Container>
<TargetPath>${r"${outputIp}"}</TargetPath>
<Port>${r"${outputPort?c}"}</Port>
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
<#elseif taskOutput.type.name() == "FlashStreaming">
<OutputType>FlashStreaming</OutputType>
<#if mosaic && !mixAudio>
<OutputCount><#if ((taskOutputVideoAndAudioMapper.audioProfileIndexMappers?size)>0)>${(ValidInputIndexs?size)?c}<#else>1</#if></OutputCount><#-- The value of OutputCount always 1 -->
<#else>
<OutputCount>1</OutputCount><#-- The value of OutputCount always 1 -->
</#if>
<Container>RTMP</Container>
<DeliverPoint>${r"${rtmpopsUrl}"}</DeliverPoint>
</#if>
</#if>
<#elseif composeTask.taskType.name() == "SDI_STREAM_COMPOSE">
<OutputType>AdaptiveStreaming</OutputType>
<#if mosaic && !mixAudio>
<OutputCount><#if ((taskOutputVideoAndAudioMapper.audioProfileIndexMappers?size)>0)>${(ValidInputIndexs?size)?c}<#else>1</#if></OutputCount>
<#else>
<OutputCount>1</OutputCount><#-- The value of OutputCount always 1 -->
</#if>
<Container>SDIStream</Container>
<TargetPath>${r"${outputIp}"}</TargetPath><#-- The device of sdi -->
</#if>
<#if ((taskOutputVideoAndAudioMapper.audioProfileIndexMappers?size)>0) && mosaic>
<#-- mixauidio output setting -->
<#if mixAudio>
<Output idx="0">
<VideoSettingIdx>${taskOutputVideoAndAudioMapper.videoProfileIndexMapper.index}</VideoSettingIdx>
<AudioSettingIdx><#if ((taskOutputVideoAndAudioMapper.audioProfileIndexMappers?size)>0)>${taskOutputVideoAndAudioMapper.audioProfileIndexMappers[0].index}<#else>-1</#if></AudioSettingIdx>
<AudioInputIdx>-1</AudioInputIdx>
</Output>
<#else>
<#list ValidInputIndexs as config>
<Output idx="${config.validindex}">
<VideoSettingIdx><#if config.validindex == 0>${taskOutputVideoAndAudioMapper.videoProfileIndexMapper.index}<#else>-1</#if></VideoSettingIdx>
<AudioSettingIdx><#if ((taskOutputVideoAndAudioMapper.audioProfileIndexMappers?size)>0)>${taskOutputVideoAndAudioMapper.audioProfileIndexMappers[0].index}<#else>-1</#if></AudioSettingIdx>
<AudioInputIdx>${config.index}</AudioInputIdx>
</Output>
</#list>
</#if>
<#else>
<Output idx="0">
<VideoSettingIdx>${taskOutputVideoAndAudioMapper.videoProfileIndexMapper.index}</VideoSettingIdx>
<AudioSettingIdx><#if ((taskOutputVideoAndAudioMapper.audioProfileIndexMappers?size)>0)>${taskOutputVideoAndAudioMapper.audioProfileIndexMappers[0].index}<#else>-1</#if></AudioSettingIdx>
<AudioInputIdx>-1</AudioInputIdx>
</Output>
</#if>
</OutputGroup>
</#list>
</OutputGroups>
<#-- MultiScreenInputs -->
<MultiScreenInputs Count="${(screenPositionConfigs?size)?c}">
<#if mosaic>
<Type>3</Type>
<#else>
<ContentDetectXml>${r"${contentDetectConfigPath}"}</ContentDetectXml>
</#if>
<#list screenPositionConfigs as config>
<Input idx="${config.index}">
<#if config.protocol?? && config.protocol == "sdi" >
<Type>SDI</Type>
<URI>Auto</URI>
<PortID>${config.port}</PortID>
<LiveSyncMode>0</LiveSyncMode>
<Input4k>0</Input4k>
<SourceMux>0</SourceMux>
<SDIAudioType>0</SDIAudioType>
<#else>
<Type>Network</Type>
<FailoverTime>3000</FailoverTime>
<IPAddr></IPAddr>
<URI>${config.url}</URI>
<#if allowProgramIdChange>
<AllowProgramIDChange>1</AllowProgramIDChange>
</#if>
</#if>
<#if hasGpu>
<DeviceId>${screenPositionAndGpuIndex[(config_index)?c]}</DeviceId>
</#if>
<ExInputs Count="0"></ExInputs>
<Program Id="${config.programId}">
<VideoId>-1</VideoId>
<AudioId>${config.audioId}</AudioId>
<SubtitleId>-3</SubtitleId>
<Channel>${(config.channelName)!''''}</Channel>
</Program>
<#if mosaic>
<SignalSetting>
<SwitchMode>0</SwitchMode>
<WarningPeriod>2147483647</WarningPeriod>
<SwitchItems Count="7">
<SignalItem idx="0">
<Type>0</Type>
<Check>1</Check>
<Timeout>6000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="1">
<Type>1</Type>
<Check>1</Check>
<Timeout>6000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="2">
<Type>2</Type>
<Check>1</Check>
<Timeout>6000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="3">
<Type>3</Type>
<Check>1</Check>
<Timeout>6000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="4">
<Type>4</Type>
<Check>1</Check>
<Timeout>6000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="5">
<Type>5</Type>
<Check>1</Check>
<Timeout>6000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="6">
<Type>6</Type>
<Check>1</Check>
<Timeout>5000</Timeout>
<Para>250</Para>
</SignalItem>
</SwitchItems>
<WarningItems Count="7">
<SignalItem idx="0">
<Type>0</Type>
<Check>1</Check>
<Timeout>5000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="1">
<Type>1</Type>
<Check>1</Check>
<Timeout>5000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="2">
<Type>2</Type>
<Check>1</Check>
<Timeout>5000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="3">
<Type>3</Type>
<Check>1</Check>
<Timeout>5000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="4">
<Type>4</Type>
<Check>1</Check>
<Timeout>5000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="5">
<Type>5</Type>
<Check>1</Check>
<Timeout>5000</Timeout>
<Para>0</Para>
</SignalItem>
<SignalItem idx="6">
<Type>6</Type>
<Check>1</Check>
<Timeout>5000</Timeout>
<Para>200</Para>
</SignalItem>
</WarningItems>
</SignalSetting>
<#else>
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
</#if>
</Input>
</#list>
</MultiScreenInputs>
</TranscoderTask>
'"'"' where id=1;'