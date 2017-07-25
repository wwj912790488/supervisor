<?xml version="1.0" encoding="UTF-8"?>
<TranscoderTask ID="${task.id}">
<AVSettings>
<VideoSettings Count="2">
<VideoSetting idx = "0">
<CodecType>H264</CodecType>
<Encoding>Custom</Encoding>
<Profile>High</Profile>
<Level>Auto</Level>
<Width>${rtspWidth?c}</Width>
<Height>${rtspHeight?c}</Height>
<FrameRate>2500</FrameRate>
<FrameRateConversionMode>0</FrameRateConversionMode>
<RC>CBR</RC>
<Quantizer>0</Quantizer>
<BitRate>2000</BitRate>
<MaxBitRate></MaxBitRate>
<VBVSize>200</VBVSize>
<VBVDelay>200</VBVDelay>
<GopSize>100</GopSize>
<Scenedetection>0</Scenedetection>
<BFrame>0</BFrame>
<CABAC>1</CABAC>
<Transform8x8>1</Transform8x8>
<Intra8x8>1</Intra8x8>
<LoopFilter>1</LoopFilter>
<RefFrame>1</RefFrame>
<Interlace>0</Interlace>
<Policy>
<TwoPass>0</TwoPass>
<DeviceID>0</DeviceID>
<QualityLevel>0</QualityLevel>
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
<MultiScreenInfo Count="${(task.cellConfigs?size)?c}">
<CheckContent>0</CheckContent>
<#list task.cellConfigs as config>
<DisplayInfo idx="${(config.index)?c}">
<InputIdx>${(config.index)?c}</InputIdx>
<XPos>${((config.xPos*rtspWidth/task.totalWidth)?floor - ((config.xPos*rtspWidth/task.totalWidth)?floor)%2)?c}</XPos>
<YPos>${((config.yPos*rtspHeight/task.totalHeight)?floor - ((config.yPos*rtspHeight/task.totalHeight)?floor)%2)?c}</YPos>
<Width><#if config.width == 0>0<#else>${((config.width*rtspWidth/task.totalWidth)?floor - ((config.width*rtspWidth/task.totalWidth)?floor)%2)?c}</#if></Width>
<Height><#if config.height == 0>0<#else>${((config.height*rtspHeight/task.totalHeight)?floor - ((config.height*rtspHeight/task.totalHeight)?floor)%2)?c}</#if></Height>
<AudioColumn>0</AudioColumn>
<InfoHeight>20</InfoHeight>
<WarningWidth>10</WarningWidth>
</DisplayInfo>
</#list>
</MultiScreenInfo>
</VideoSetting>
<VideoSetting idx = "1">
<CodecType>H264</CodecType>
<Encoding>Custom</Encoding>
<Profile>High</Profile>
<Level>Auto</Level>
<Width>${opsWidth?c}</Width>
<Height>${opsHeight?c}</Height>
<FrameRate>2500</FrameRate>
<FrameRateConversionMode>0</FrameRateConversionMode>
<RC>CBR</RC>
<Quantizer>0</Quantizer>
<BitRate>4000</BitRate>
<MaxBitRate></MaxBitRate>
<VBVSize>450</VBVSize>
<VBVDelay>200</VBVDelay>
<GopSize>100</GopSize>
<Scenedetection>0</Scenedetection>
<BFrame>0</BFrame>
<CABAC>1</CABAC>
<Transform8x8>1</Transform8x8>
<Intra8x8>1</Intra8x8>
<LoopFilter>1</LoopFilter>
<RefFrame>1</RefFrame>
<Interlace>0</Interlace>
<Policy>
<TwoPass>0</TwoPass>
<DeviceID>0</DeviceID>
<QualityLevel>0</QualityLevel>
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
<MultiScreenInfo Count="${(task.cellConfigs?size)?c}">
<CheckContent>0</CheckContent>
<#list task.cellConfigs as config>
<DisplayInfo idx="${(config.index)?c}">
<InputIdx>${(config.index)?c}</InputIdx>
<XPos>${((config.xPos*opsWidth/task.totalWidth)?floor - ((config.xPos*opsWidth/task.totalWidth)?floor)%2)?c}</XPos>
<YPos>${((config.yPos*opsHeight/task.totalHeight)?floor - ((config.yPos*opsHeight/task.totalHeight)?floor)%2)?c}</YPos>
<Width><#if config.width == 0>0<#else>${((config.width*opsWidth/task.totalWidth)?floor - ((config.width*opsWidth/task.totalWidth)?floor)%2)?c}</#if></Width>
<Height><#if config.height == 0>0<#else>${((config.height*opsHeight/task.totalHeight)?floor - ((config.height*opsHeight/task.totalHeight)?floor)%2)?c}</#if></Height>
<AudioColumn>0</AudioColumn>
<InfoHeight>20</InfoHeight>
<WarningWidth>10</WarningWidth>
</DisplayInfo>
</#list>
</MultiScreenInfo>
</VideoSetting>
</VideoSettings>
<AudioSettings Count="2">
<AudioSetting idx="0">
<CodecType>AAC</CodecType>
<Profile>LC</Profile>
<Channel>2</Channel>
<BitRate>64000</BitRate>
<SampleRate>32000</SampleRate>
<BoostLevel>0</BoostLevel>
<ChannelProcessing>None</ChannelProcessing>
<VolumeProcessMode>0</VolumeProcessMode>
<BalanceDB>-30</BalanceDB><BalanceLevel>null</BalanceLevel>
</AudioSetting>
<AudioSetting idx="1">
<CodecType>AAC</CodecType>
<Profile>LC</Profile>
<Channel>2</Channel>
<BitRate>64000</BitRate>
<SampleRate>32000</SampleRate>
<BoostLevel>0</BoostLevel>
<ChannelProcessing>None</ChannelProcessing>
<VolumeProcessMode>0</VolumeProcessMode>
<BalanceDB>-30</BalanceDB><BalanceLevel>null</BalanceLevel>
</AudioSetting>
</AudioSettings>
</AVSettings>
<OutputGroups Count="2">
<OutputGroup idx="0">
<OutputType>UdpStreaming</OutputType>
<OutputCount>1</OutputCount>
<Container>UDPOverTS</Container>
<TargetPath>${rtspServerIp}</TargetPath>
<Port>${opsUdpPort?c}</Port>
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
<VideoSettingIdx>1</VideoSettingIdx>
<AudioSettingIdx>1</AudioSettingIdx>
<AudioInputIdx>-1</AudioInputIdx>
</Output>
</OutputGroup>
<OutputGroup idx="1">
<OutputType>UdpStreaming</OutputType>
<OutputCount>1</OutputCount>
<Container>UDPOverTS</Container>
<TargetPath>${rtspServerIp}</TargetPath>
<Port>${rtspUdpPort?c}</Port>
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
</OutputGroups>

<MultiScreenInputs Count="${task.cellConfigs?size}">
<Type>3</Type>
<#list task.cellConfigs as config>
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
