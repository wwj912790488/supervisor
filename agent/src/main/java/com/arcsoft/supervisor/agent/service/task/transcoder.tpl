<?xml version="1.0" encoding="UTF-8"?>
<TranscoderTask ID="${task.id}">
<AVSettings>
<VideoSettings Count="2">
<VideoSetting idx = "0">
<CodecType>H264</CodecType>
<Encoding>Custom</Encoding>
<Profile>Main</Profile>
<Level>Auto</Level>
<Width>${rtspWidth?c}</Width>
<Height>${rtspHeight?c}</Height>
<FrameRate>2500</FrameRate>
<FrameRateConversionMode>0</FrameRateConversionMode>
<RC>CBR</RC>
<Quantizer>0</Quantizer>
<BitRate>2000</BitRate>
<MaxBitRate>0</MaxBitRate>
<VBVSize>500</VBVSize>
<VBVDelay>200</VBVDelay>
<GopSize>100</GopSize>
<Scenedetection>0</Scenedetection>
<BFrame>0</BFrame>
<CABAC>1</CABAC>
<Transform8x8>0</Transform8x8>
<Intra8x8>0</Intra8x8>
<LoopFilter>1</LoopFilter>
<RefFrame>1</RefFrame>
<Interlace>0</Interlace>
<Policy>
<TwoPass>0</TwoPass>
<DeviceID>1</DeviceID>
<QualityLevel>4</QualityLevel>
</Policy>
<SmartStretch>
<DAR_X>16</DAR_X>
<DAR_Y>9</DAR_Y>
<Operate>1</Operate>
<FillColor>0</FillColor>
</SmartStretch>
<Deinterlace>2</Deinterlace>
<Deblock>0</Deblock>
<Delight>0</Delight>
<Denoise>0</Denoise>
<Sharpen>0</Sharpen>
<AntiAlias>0</AntiAlias>
<AntiShaking>-1</AntiShaking>
<EdgeAA>0</EdgeAA>
<Bright>0</Bright>
<Contrast>0</Contrast>
<Hue>0</Hue>
<Saturation>0</Saturation>
<MultiScreenInfo Count="${(task.configs?size)?c}">
<CheckContent>0</CheckContent>
<#list task.configs as config>
<DisplayInfo idx="${(config.index)?c}">
<InputIdx>${(config.index)?c}</InputIdx>
<XPos>${(config.column * rtspPerWidth)?c}</XPos>
<YPos>${(config.row * rtspPerHeight)?c}</YPos>
<Width><#if config.column != (task.columnCount - 1)>${(rtspPerWidth - 10)?c}<#else>${rtspPerWidth?c}</#if></Width>
<Height><#if config.row != (task.rowCount - 1)>${(rtspPerHeight - 20)?c}<#else>${rtspPerHeight?c}</#if></Height>
<AudioColumn>6</AudioColumn>
<InfoHeight>20</InfoHeight>
<WarningWidth>10</WarningWidth>
</DisplayInfo>
</#list>
</MultiScreenInfo>
</VideoSetting>
<VideoSetting idx = "1">
<CodecType><#if type="sdi">RAW<#else>H264</#if></CodecType>
<Encoding>Custom</Encoding>
<Profile>Main</Profile>
<Level>Auto</Level>
<Width>${opsWidth?c}</Width>
<Height>${opsHeight?c}</Height>
<FrameRate>2500</FrameRate>
<FrameRateConversionMode>0</FrameRateConversionMode>
<RC>CBR</RC>
<Quantizer>0</Quantizer>
<BitRate>4000</BitRate>
<MaxBitRate>0</MaxBitRate>
<VBVSize>500</VBVSize>
<VBVDelay>200</VBVDelay>
<GopSize>100</GopSize>
<Scenedetection>0</Scenedetection>
<BFrame>0</BFrame>
<CABAC>1</CABAC>
<Transform8x8>0</Transform8x8>
<Intra8x8>0</Intra8x8>
<LoopFilter>1</LoopFilter>
<RefFrame>1</RefFrame>
<Interlace>0</Interlace>
<Policy>
<TwoPass>0</TwoPass>
<DeviceID>1</DeviceID>
<QualityLevel>4</QualityLevel>
</Policy>
<SmartStretch>
<DAR_X>16</DAR_X>
<DAR_Y>9</DAR_Y>
<Operate>1</Operate>
<FillColor>0</FillColor>
</SmartStretch>
<Deinterlace>2</Deinterlace>
<Deblock>0</Deblock>
<Delight>0</Delight>
<Denoise>0</Denoise>
<Sharpen>0</Sharpen>
<AntiAlias>0</AntiAlias>
<AntiShaking>-1</AntiShaking>
<EdgeAA>0</EdgeAA>
<Bright>0</Bright>
<Contrast>0</Contrast>
<Hue>0</Hue>
<Saturation>0</Saturation>
<MultiScreenInfo Count="${(task.configs?size)?c}">
<CheckContent>1</CheckContent>
<#list task.configs as config>
<DisplayInfo idx="${(config.index)?c}">
<InputIdx>${(config.index)?c}</InputIdx>
<XPos>${(config.column * opsPerWidth)?c}</XPos>
<YPos>${(config.row * opsPerHeight)?c}</YPos>
<Width><#if config.column != (task.columnCount - 1)>${(opsPerWidth - 10)?c}<#else>${opsPerWidth?c}</#if></Width>
<Height><#if config.row != (task.rowCount - 1)>${(opsPerHeight - 20)?c}<#else>${opsPerHeight?c}</#if></Height>
<AudioColumn>6</AudioColumn>
<InfoHeight>20</InfoHeight>
<WarningWidth>10</WarningWidth>
</DisplayInfo>
</#list>
</MultiScreenInfo>
</VideoSetting>
</VideoSettings>
<AudioSettings Count="1">
<AudioSetting idx="0">
<CodecType>AAC</CodecType>
<Profile>LC</Profile>
<Channel>2</Channel>
<BitRate>64000</BitRate>
<SampleRate>32000</SampleRate>
<BoostLevel>0</BoostLevel>
<ChannelProcessing>None</ChannelProcessing>
<VolumeProcessMode>0</VolumeProcessMode>
<BalanceDB>-30</BalanceDB><BalanceLevel>null</BalanceLevel></AudioSetting>
</AudioSettings>
</AVSettings>
<OutputGroups Count="<#if task.enableRtsp>2<#else>1</#if>">
<#if type="ip">
<OutputGroup idx="0">
<OutputType>UdpStreaming</OutputType>
<OutputCount>1</OutputCount>
<Container>UDPOverTS</Container>
<TargetPath>${outputIp}</TargetPath>
<Port>${outputPort?c}</Port>
<TTL>255</TTL>
<BufferSize>65535</BufferSize>
<SrcIp></SrcIp>
<SrcPort></SrcPort>
<TsSetting>
<ServerName></ServerName>
<ServiceProvider></ServiceProvider>
<SericeId>0</SericeId>
<PmtPid>0</PmtPid>
<VideoPid>0</VideoPid>
<AudioPid>0</AudioPid>
<PcrPid>0</PcrPid>
<TotalBitrate>0</TotalBitrate>
</TsSetting>
<#elseif type="sdi">
<OutputGroup idx="0">
<OutputType>AdaptiveStreaming</OutputType>
<OutputCount>1</OutputCount>
<Container>SDIStream</Container>
<TargetPath>${task.targetIp}</TargetPath>
<#else>
</#if>
<Output idx="0">
<VideoSettingIdx>1</VideoSettingIdx>
<AudioSettingIdx>0</AudioSettingIdx>
<AudioInputIdx>-1</AudioInputIdx>
</Output>
</OutputGroup>
<#if task.enableRtsp>
<OutputGroup idx="1">
<OutputType>UdpStreaming</OutputType>
<OutputCount>1</OutputCount>
<Container>UDPOverTS</Container>
<TargetPath>${rtspServerIp}</TargetPath>
<Port>${udpPort?c}</Port>
<TTL>255</TTL>
<BufferSize>65535</BufferSize>
<SrcIp></SrcIp>
<SrcPort></SrcPort>
<TsSetting>
<ServerName></ServerName>
<ServiceProvider></ServiceProvider>
<SericeId>0</SericeId>
<PmtPid>0</PmtPid>
<VideoPid>0</VideoPid>
<AudioPid>0</AudioPid>
<PcrPid>0</PcrPid>
<TotalBitrate>0</TotalBitrate>
</TsSetting>
<Output idx="0">
<VideoSettingIdx>0</VideoSettingIdx>
<AudioSettingIdx>0</AudioSettingIdx>
<AudioInputIdx>-1</AudioInputIdx>
</Output>
</OutputGroup>
</#if>
</OutputGroups>

<MultiScreenInputs Count="${task.configs?size}">
<ContentDetectXml>${contentDetectConfigPath}</ContentDetectXml>
<#list task.configs as config>
<Input idx="${config.index}">
<Type>Network</Type>
<FailoverTime>3000</FailoverTime>
<IPAddr></IPAddr>
<URI>${config.url}</URI>
<ExInputs Count="0"></ExInputs>
<Program Id="${config.programId}">
<VideoId>-1</VideoId>
<AudioId>${config.audioId}</AudioId>
<SubtitleId>-3</SubtitleId>
<Channel>${(config.channelName)!''}</Channel>
</Program>
</Input>
</#list>
</MultiScreenInputs>
</TranscoderTask>
