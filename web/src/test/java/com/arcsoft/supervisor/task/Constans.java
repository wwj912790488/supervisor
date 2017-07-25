package com.arcsoft.supervisor.task;

/**
 *
 * Mock data of profile.
 *
 * @author zw.
 */
class Constans {

    public static final String OUTPUT_PROFILE_COMPLETED_JSON = "{\"id\":4, \"name\":\"output-profile-4\",\"description\" : \"desc-1\", \"videoprofiles\":[{\"videocodec\":\"H264\",\"videocodecprofile\":\"Main\",\"videocodeclevel\":\"-1\",\"videoheight\":480,\"videowidth\":640,\"videoPAR\":\"4:3\",\"videosourcePAR\":true,\"videoPARX\":3,\"videoPARY\":2,\"videosmartborder\":1,\"videoratecontrol\":\"VBR\",\"videobitrate\":1000,\"videomaxbitrate\":3000,\"videoqualityleveldisp\":3,\"videobufferfill\":1000,\"videobuffersize\":375,\"videoquantizer\":0,\"videogopsize\":30,\"videobframe\":1,\"videoreferenceframe\":1,\"videoCABAC\":true,\"videointraprediction\":false,\"videotransform\":false,\"videoSCD\":false,\"videodeinterlace\":2,\"videodeinterlacealg\":2,\"videoresizealg\":3,\"videodenoise\":0,\"videodeblock\":false,\"videosharpen\":0,\"videoantialias\":false,\"videobright\":0,\"videocontrast\":0,\"videosaturation\":0,\"videohue\":0,\"videodelight\":0,\"videoframerate\":\"40:1\",\"videosourceframerate\":true,\"videoframerateX\":40,\"videoframerateY\":1,\"videoframerateconversionmode\":false,\"videointerlace\":\"-1\",\"videotopfieldfirst\":\"-1\"}],\"audioprofiles\":[{\"audiopassthrough\":false,\"audiocodec\":\"AAC\",\"audiocodecprofile\":\"LC\",\"audiochannel\":2,\"audiosamplerate\":44100,\"audiobitrate\":64,\"audiovolumemode\":0,\"audioboostlevel\":0,\"audiobalancelevel\":0,\"audiobalancedb\":-30,\"audiochannelprocessing\":\"None\"}]}";

    public static final String OUTPUT_PROFILE_JSON = "{\"videoprofiles\":[{\"videocodec\":\"H264\",\"videocodecprofile\":\"Main\",\"videocodeclevel\":\"-1\",\"videoheight\":480,\"videowidth\":640,\"videoPAR\":\"4:3\",\"videosourcePAR\":true,\"videoPARX\":3,\"videoPARY\":2,\"videosmartborder\":1,\"videoratecontrol\":\"VBR\",\"videobitrate\":1000,\"videomaxbitrate\":3000,\"videoqualityleveldisp\":3,\"videobufferfill\":1000,\"videobuffersize\":375,\"videoquantizer\":0,\"videogopsize\":30,\"videobframe\":0,\"videoreferenceframe\":1,\"videoCABAC\":true,\"videointraprediction\":false,\"videotransform\":false,\"videoSCD\":false,\"videodeinterlace\":2,\"videodeinterlacealg\":2,\"videoresizealg\":3,\"videodenoise\":0,\"videodeblock\":false,\"videosharpen\":0,\"videoantialias\":false,\"videobright\":0,\"videocontrast\":0,\"videosaturation\":0,\"videohue\":0,\"videodelight\":0,\"videoframerate\":\"40:1\",\"videosourceframerate\":true,\"videoframerateX\":40,\"videoframerateY\":1,\"videoframerateconversionmode\":false,\"videointerlace\":\"-1\",\"videotopfieldfirst\":\"-1\"}],\"audioprofiles\":[{\"audiopassthrough\":false,\"audiocodec\":\"AAC\",\"audiocodecprofile\":\"LC\",\"audiochannel\":2,\"audiosamplerate\":44100,\"audiobitrate\":64,\"audiovolumemode\":0,\"audioboostlevel\":0,\"audiobalancelevel\":0,\"audiobalancedb\":-30,\"audiochannelprocessing\":\"None\"}]}";

    public static final String TASK_PROFILE_COMPLETED_JSON = "{\"id\":4,\"taskname\":\"1234\",\"taskdescription\":\"4321\",\"taskencodingoption\":\"Balance\",\"taskpriority\":5,\"taskgpucores\":1,\"taskinputrow\":4,\"taskinputcolumn\":4,\"taskoutputprofiles\":[{\"videoprofiles\":[{\"videocodec\":\"H264\",\"videocodecprofile\":\"Main\",\"videocodeclevel\":null,\"videoheight\":480,\"videowidth\":640,\"videoPAR\":\"4:3\",\"videosourcePAR\":true,\"videoPARX\":4,\"videoPARY\":3,\"videosmartborder\":1,\"videoratecontrol\":\"VBR\",\"videobitrate\":1000,\"videomaxbitrate\":3000,\"videoqualityleveldisp\":3,\"videobufferfill\":1000,\"videobuffersize\":375,\"videoquantizer\":0,\"videogopsize\":30,\"videobframe\":4,\"videoreferenceframe\":1,\"videoCABAC\":true,\"videointraprediction\":false,\"videotransform\":false,\"videoSCD\":false,\"videodeinterlace\":2,\"videodeinterlacealg\":2,\"videoresizealg\":3,\"videodenoise\":0,\"videodeblock\":false,\"videosharpen\":0,\"videoantialias\":false,\"videobright\":0,\"videocontrast\":0,\"videosaturation\":0,\"videohue\":0,\"videodelight\":0,\"videoframerate\":\"40:1\",\"videosourceframerate\":true,\"videoframerateX\":40,\"videoframerateY\":1,\"videoframerateconversionmode\":false,\"videointerlace\":\"-1\",\"videotopfieldfirst\":\"-1\"}],\"audioprofiles\":[{\"audiopassthrough\":false,\"audiocodec\":\"AAC\",\"audiocodecprofile\":\"LC\",\"audiochannel\":2,\"audiosamplerate\":44100,\"audiobitrate\":64,\"audiovolumemode\":0,\"audioboostlevel\":0,\"audiobalancelevel\":0,\"audiobalancedb\":-30,\"audiochannelprocessing\":\"None\"}]}],\"taskoutputs\":[{\"uniqueType\":\"UdpStreaming-UDPOverTS\",\"linkedprofile\":0,\"outputtype\":\"UdpStreaming\",\"outputcontainer\":\"UDPOverTS\",\"outputbuffersize\":65535,\"outputTTL\":255,\"outputport\":1234,\"outputtsoption\":{\"tsservicename\":\"\",\"tspmtpid\":\"\",\"tsserviceprovider\":\"\",\"tsvideopid\":\"\",\"tsserviceid\":\"\",\"tsaudiopid\":\"\",\"tstotalbitrate\":\"\",\"tspcrpid\":\"\",\"tsnetworkid\":\"\",\"tstransportid\":\"\",\"tsinserttottdt\":\"false\",\"tstottdtperiod\":\"\",\"tspcrperiod\":\"\",\"tspatperiod\":\"\",\"tssdtperiod\":\"\",\"tsprivatemetadatapid\":\"\",\"tsprivatemetadatatype\":\"\"}}]}";


    public static final String TASK_PROFILE_UPDATE_JSON = "{\"id\":4,\"taskname\":\"update\",\"taskdescription\":\"4321\",\"taskencodingoption\":\"Balance\",\"taskpriority\":5,\"taskgpucores\":1,\"taskinputrow\":4,\"taskinputcolumn\":4,\"taskoutputprofiles\":[{\"videoprofiles\":[{\"videocodec\":\"H264\",\"videocodecprofile\":\"Main\",\"videocodeclevel\":null,\"videoheight\":480,\"videowidth\":640,\"videoPAR\":\"4:3\",\"videosourcePAR\":true,\"videoPARX\":4,\"videoPARY\":3,\"videosmartborder\":1,\"videoratecontrol\":\"VBR\",\"videobitrate\":1000,\"videomaxbitrate\":3000,\"videoqualityleveldisp\":3,\"videobufferfill\":1000,\"videobuffersize\":375,\"videoquantizer\":0,\"videogopsize\":30,\"videobframe\":3,\"videoreferenceframe\":1,\"videoCABAC\":true,\"videointraprediction\":false,\"videotransform\":false,\"videoSCD\":false,\"videodeinterlace\":2,\"videodeinterlacealg\":2,\"videoresizealg\":3,\"videodenoise\":0,\"videodeblock\":false,\"videosharpen\":0,\"videoantialias\":false,\"videobright\":0,\"videocontrast\":0,\"videosaturation\":0,\"videohue\":0,\"videodelight\":0,\"videoframerate\":\"40:1\",\"videosourceframerate\":true,\"videoframerateX\":40,\"videoframerateY\":1,\"videoframerateconversionmode\":false,\"videointerlace\":\"-1\",\"videotopfieldfirst\":\"-1\"}],\"audioprofiles\":[{\"audiopassthrough\":false,\"audiocodec\":\"AAC\",\"audiocodecprofile\":\"LC\",\"audiochannel\":2,\"audiosamplerate\":44100,\"audiobitrate\":64,\"audiovolumemode\":0,\"audioboostlevel\":0,\"audiobalancelevel\":0,\"audiobalancedb\":-30,\"audiochannelprocessing\":\"None\"}]}],\"taskoutputs\":[{\"uniqueType\":\"UdpStreaming-UDPOverTS\",\"linkedprofile\":0,\"outputtype\":\"UdpStreaming\",\"outputcontainer\":\"UDPOverTS\",\"outputbuffersize\":65535,\"outputTTL\":255,\"outputport\":1234,\"outputtsoption\":{\"tsservicename\":\"\",\"tspmtpid\":\"\",\"tsserviceprovider\":\"\",\"tsvideopid\":\"\",\"tsserviceid\":\"\",\"tsaudiopid\":\"\",\"tstotalbitrate\":\"\",\"tspcrpid\":\"\",\"tsnetworkid\":\"\",\"tstransportid\":\"\",\"tsinserttottdt\":\"false\",\"tstottdtperiod\":\"\",\"tspcrperiod\":\"\",\"tspatperiod\":\"\",\"tssdtperiod\":\"\",\"tsprivatemetadatapid\":\"\",\"tsprivatemetadatatype\":\"\"}}]}";

}
