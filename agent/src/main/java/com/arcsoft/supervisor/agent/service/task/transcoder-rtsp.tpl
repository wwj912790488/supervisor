<?xml version="1.0" encoding="UTF-8"?>
<TranscoderTask ID="${id}">
    <AVSettings>
        <#if isVideoPassthrough >
            <VideoSettings Count="0">
            </VideoSettings>
            <#else>
                <VideoSettings Count="<#if isSupportMobile>1<#else>1</#if>">
                    <VideoSetting idx="0">
                        <CodecType>H264</CodecType>
                        <Encoding>Custom</Encoding>
                        <Profile>High</Profile>
                        <Level>Auto</Level>
                        <Width>${((hdConfig.width)!1280)?c}</Width>
                        <Height>${((hdConfig.height)!720)?c}</Height>
                        <FrameRate>-1</FrameRate>
                        <FrameRateConversionMode>0</FrameRateConversionMode>
                        <RC>CBR</RC>
                        <Quantizer>0</Quantizer>
                        <BitRate>${((hdConfig.videoBitrate)!1024)?c}</BitRate>
                        <MaxBitRate>0</MaxBitRate>
                        <VBVSize>375</VBVSize>
                        <VBVDelay>1000</VBVDelay>
                        <GopSize>30</GopSize>
                        <Scenedetection>0</Scenedetection>
                        <BFrame>0</BFrame>
                        <CABAC>1</CABAC>
                        <Transform8x8>1</Transform8x8>
                        <Intra8x8>1</Intra8x8>
                        <LoopFilter>1</LoopFilter>
                        <RefFrame>1</RefFrame>
                        <Interlace>0</Interlace>
                        <ThreadCount>5</ThreadCount>
                        <LookHeadFrame>20</LookHeadFrame>
                        <Policy>
                            <TwoPass>0</TwoPass>
                            <DeviceID>1</DeviceID>
                            <QualityLevel>-1</QualityLevel>
                        </Policy>
                        <SmartStretch>
                            <DAR_X>-1</DAR_X>
                            <DAR_Y>-1</DAR_Y>
                            <Operate>1</Operate>
                            <FillColor>0</FillColor>
                        </SmartStretch>
                        <#if hdConfig.deinterlace>
                            <Deinterlace>2</Deinterlace>
                            <DeinterlaceAlg>1</DeinterlaceAlg>
                            <#else>
                                <Deinterlace>0</Deinterlace>
                                <DeinterlaceAlg>1</DeinterlaceAlg>
                        </#if>
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
                    </VideoSetting>
                </VideoSettings>
        </#if>
        <#if isAudioPassthrough >
            <AudioSettings Count="0">
            </AudioSettings>
            <#else>
                <AudioSettings Count="<#if isSupportMobile>1<#else>1</#if>">
                    <AudioSetting idx="0">
                        <CodecType>AAC</CodecType>
                        <Profile>LC</Profile>
                        <Channel>2</Channel>
                        <BitRate>
                            <#if (hdConfig.audioBitrate)??>${((hdConfig.audioBitrate) * 1000)?c}
                                <#else>64000
                            </#if>
                        </BitRate>
                        <SampleRate>32000</SampleRate>
                        <BoostLevel>0</BoostLevel>
                        <ChannelProcessing>None</ChannelProcessing>
                        <VolumeProcessMode>0</VolumeProcessMode>
                        <BalanceDB>-30</BalanceDB>
                        <BalanceLevel>0</BalanceLevel>
                    </AudioSetting>
                </AudioSettings>
        </#if>
    </AVSettings>
    <OutputGroups Count="<#if isSupportMobile && enableRecord>2<#elseif isSupportMobile>1<#else>1</#if>">
        <#if enableRecord>
            <OutputGroup idx="0">
                <#-- The value 0 is for mp4 -->
                    <#if recordFormat == 0>
                        <OutputType>FileArchive</OutputType>
                        <OutputCount>1</OutputCount>
                        <Container>MP4</Container>
                        <TimeSegment>${recordTimeSegment?c}</TimeSegment>
                        <TargetPath>${recordBasePath + recordFileName}</TargetPath>
                        <#else>
                            <#-- Otherwise is hls. for instances the value 1 -->
                                <OutputType>AppleStreaming</OutputType>
                                <OutputCount>1</OutputCount>
                                <Container>HLS</Container>
                                <TimeSegment>${recordTimeSegment?c}</TimeSegment>
                                <LiveMode>0</LiveMode>
                                <TargetPath>${recordBasePath}</TargetPath>
                                <TargetName>${recordFileName}</TargetName>
                                <SegmentName>${r"${starttime}"}-${r"${id}"}-${r"${seq}"}</SegmentName>
                                <PlaylistName>${r"${id}"}</PlaylistName>
                                <DeliverPoint></DeliverPoint>
                    </#if>
                    <Output idx="0">
                        <#if isVideoPassthrough>
                            <VideoSettingIdx>-2</VideoSettingIdx>
                            <#else>
                                <VideoSettingIdx>0</VideoSettingIdx>
                        </#if>
                        <#if isAudioPassthrough>
                            <AudioSettingIdx>-2</AudioSettingIdx>
                            <#else>
                                <AudioSettingIdx>0</AudioSettingIdx>
                        </#if>
                        <AudioInputIdx>-1</AudioInputIdx>
                    </Output>
            </OutputGroup>
        </#if>
        <#if isSupportMobile>
            <#assign outputType=isWowzaRtsp?string("UdpStreaming", "FlashStreaming") container=isWowzaRtsp?string("UDPOverTS", "RTMP")>
                <OutputGroup idx="<#if enableRecord>1<#else>0</#if>">
                    <OutputType>${outputType}</OutputType>
                    <OutputCount>1</OutputCount>
                    <Container>${container}</Container>
                    <#if isWowzaRtsp>
                        <TargetPath>${rtspServerIp}</TargetPath>
                        <Port>${hdUdpPort?c}</Port>
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
                        <#else>
                            <DeliverPoint>${hdUrl}</DeliverPoint>
                    </#if>
                    <Output idx="0">
                        <#if isVideoPassthrough>
                            <VideoSettingIdx>-2</VideoSettingIdx>
                            <#else>
                                <VideoSettingIdx>0</VideoSettingIdx>
                        </#if>
                        <#if isAudioPassthrough>
                            <AudioSettingIdx>-2</AudioSettingIdx>
                            <#else>
                                <AudioSettingIdx>0</AudioSettingIdx>
                        </#if>
                        <AudioInputIdx>-1</AudioInputIdx>
                    </Output>
                </OutputGroup>
        </#if>
    </OutputGroups>
    <Inputs Count="1">
        <Input idx="0">
        <#if .protocol?? && config.protocol == "sdi" >
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
                <URI>${udpUrl}</URI>
        </#if>
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
        <Program Id="${programId}">
            <VideoId>-1</VideoId>
            <AudioId>${audioId}</AudioId>
            <SubtitleId>-3</SubtitleId>
        </Program>
        <Preprocessor>
            <AudioDelay>0</AudioDelay>
        </Preprocessor>
        </Input>
    </Inputs>
</TranscoderTask>
