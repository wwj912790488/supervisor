package com.arcsoft.supervisor.service.alarm.impl;


import com.arcsoft.supervisor.model.domain.alarm.*;
import com.arcsoft.supervisor.model.vo.task.MediaCheckType;
import com.arcsoft.supervisor.repository.alarm.AlarmConfigChannelRepository;
import com.arcsoft.supervisor.repository.alarm.AlarmDeviceRepository;
import com.arcsoft.supervisor.repository.alarm.AlarmPushLogRepository;
import com.arcsoft.supervisor.repository.alarm.AlarmPushedLogInfoRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.alarm.AlarmBaiduPushService;
import com.arcsoft.supervisor.thirdparty.baidupush.ContentDetectLogData;
import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.baidu.yun.push.auth.PushKeyPair;
import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.constants.BaiduPushConstants;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.PushMsgToAllRequest;
import com.baidu.yun.push.model.PushMsgToAllResponse;
import com.baidu.yun.push.model.PushMsgToSingleDeviceRequest;
import com.baidu.yun.push.model.PushMsgToSingleDeviceResponse;
import net.sf.json.JSONObject;
import java.beans.Transient;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author jt.
 */

@Service
public class DefaultAlarmBaiduPushService extends ServiceSupport implements AlarmBaiduPushService, TransactionSupport {

     @Autowired
     private AlarmConfigChannelRepository alarmConfigChannelRepository;

     @Autowired
     private AlarmPushLogRepository alarmPushLogRepository;

    @Autowired
    private AlarmDeviceRepository alarmDeviceRepository;

    @Autowired
    private AlarmPushedLogInfoRepository alarmPushedLogInfoRepository;

    //  get form config
    private String AndroidApiKey = "j0b4bcgkjBml2GvgPVXLOTS3";
    private String AndroidSecretKey = "wekGqeeYdO5qOGgR2TrIdlhHUVTSp3BR";
    private String IOSApiKey = "z9sZViqqFyu0sCs1uiatGur4";
    private String IOSSecretKey = "trp9vhXbsGgQGuqHvNLGhwYsnLGO2WB1";
    // IOS,
    // DeployStatus
    // 1: Developer
    // 2: Production.
    private Integer IOSDepolyStatus = 1;
    private String IOSsound = "";


    @Override
    public void updateAndPush(ContentDetectLogData contentDetectLogData){

        List<AlarmConfigChannel> list = alarmConfigChannelRepository.findByChannelId(contentDetectLogData.getId());
        for(AlarmConfigChannel alarmConfigChannel : list){
            AlarmConfig config = alarmConfigChannel.getAlarmConfig();
            if(!checkType(config, contentDetectLogData.getType())){
                continue;
            }
            //  save in DB
            AlarmPushLog alarmPushLog = saveToAlarmPushLog(contentDetectLogData, config);

            //  Do Push
            Integer UserId = config.getUser().getId();
            List<AlarmDevice> devList = alarmDeviceRepository.findByUserId(UserId);
            for(AlarmDevice dev : devList){
                if(dev.getDeviceType().equals("3")){
                    //  3:Android;
                    pushToAndroidDevice(contentDetectLogData, alarmPushLog,  dev.getChannelId());
                }else if(dev.getDeviceType().equals("4")){
                    //  4:IOS;
                    pushToIOSDevice(contentDetectLogData, alarmPushLog, dev.getChannelId());
                }
            }
        }
    }

    private Boolean checkType(AlarmConfig config, int type){

        logger.debug("checkType user id[{}],by type[{}]", config.getUser().getId(),type);
        Boolean bNeedPush = false;

        switch(type) {
            case MediaCheckType.CHECK_TYPE_BLACK_FIELD_INDEX:
                bNeedPush = config.getEnablecontentdetect() && config.getEnableBlack();
                break;
            case MediaCheckType.CHECK_TYPE_GREEN_FIELD_INDEX:
                bNeedPush = config.getEnablecontentdetect() && config.getEnableGreen();
                break;
            case MediaCheckType.CHECK_TYPE_STATIC_FRAME_INDEX:
                bNeedPush = config.getEnablecontentdetect() && config.getEnableNoFrame();
                break;
            case MediaCheckType.CHECK_TYPE_COLOR_BAR_INDEX:
                break;
            case MediaCheckType.CHECK_TYPE_MOSAIC_INDEX:
                break;
            case MediaCheckType.CHECK_TYPE_BREAK_INDEX:
                bNeedPush = config.getEnablecontentdetect() && config.getEnableBoomSonic();
                break;
            case MediaCheckType.CHECK_TYPE_MUTE_THRESHOLD_INDEX:
                bNeedPush = config.getEnablecontentdetect() && config.getEnableSilence();
                break;
            case MediaCheckType.CHECK_TYPE_VOLUME_LOW_INDEX:
                bNeedPush = config.getEnablecontentdetect() && config.getEnableLowVolume();
                break;
            case MediaCheckType.CHECK_TYPE_VOLUME_LOUD_INDEX:
                bNeedPush = config.getEnablecontentdetect() && config.getEnableLoudVolume();
                break;
            case MediaCheckType.CHECK_TYPE_VOLUME_LOUDN_INDEX:
                bNeedPush = config.getEnablecontentdetect() && config.getEnableLoudVolume();
                break;
            case MediaCheckType.CHECK_TYPE_TONE_INDEX:
                break;
            case MediaCheckType.CHECK_TYPE_STREAM_INTERRUPT_INDEX:
                break;
            case MediaCheckType.SIGNAL_STREAM_CCERROR:
                bNeedPush = config.getEnablesignaldetect() && config.getEnableCcError();
                break;
            case MediaCheckType.SIGNAL_STREAM_NOAUDIO:
                bNeedPush = config.getEnablesignaldetect() && config.getEnableAudioLoss();
                break;
            case MediaCheckType.SIGNAL_STREAM_NOVIDEO:
                bNeedPush = config.getEnablesignaldetect() && config.getEnableVideoLoss();
                break;
            case MediaCheckType.SIGNAL_STREAM_INTERRUPT:
                bNeedPush = config.getEnablesignaldetect() && config.getEnableBroken();
                break;
            default:
                break;
        }
        logger.debug("checkType [{}]", bNeedPush?"bNeedPush true":"bNeedPush false");
        return bNeedPush;
    }

    @Transient
    private AlarmPushLog saveToAlarmPushLog(ContentDetectLogData contentDetectLogData, AlarmConfig config) {
        AlarmPushLog log = new AlarmPushLog();
        log.setType(contentDetectLogData.getType());
        log.setChannelId(contentDetectLogData.getId());
        log.setChannelName(contentDetectLogData.getName());
        log.setStartTime(contentDetectLogData.getStartTime());
        log.setEndTime(contentDetectLogData.getEndTime());
        log.setAlarmConfig(config);
        return alarmPushLogRepository.save(log);
    }

    private void pushToAndroidDevice(ContentDetectLogData contentDetectLogData, AlarmPushLog alarmPushLog, String channelId){

        // 1. get apiKey and secretKey from developer console
        PushKeyPair pair = new PushKeyPair(AndroidApiKey, AndroidSecretKey);

        // 2. build a BaidupushClient object to access released interfaces
        BaiduPushClient pushClient = new BaiduPushClient(pair,
                BaiduPushConstants.CHANNEL_REST_URL);

        // 3. register a YunLogHandler to get detail interacting information
        // in this request.
        pushClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
//                logger.info("pushToAndroidDevice onHandle: " + event.getMessage());
            }
        });

        try {
            String pushMsg = contentDetectLogData.getName() + " "
                    + contentDetectLogData.getStartTimeStr() + " 出现"
                    + contentDetectLogData.getTypeTranslate();

            // 4. specify request arguments
            JSONObject notification = new JSONObject();
            notification.put("title", "蝈蝈告警");
            notification.put("description",pushMsg);
            notification.put("notification_builder_id", 0);
            notification.put("notification_basic_style", 7);
            notification.put("open_type", 3);
            notification.put("url", "http://www.arcvideo.cn");
            JSONObject jsonCustormCont = new JSONObject();
            jsonCustormCont.put("id", contentDetectLogData.getId());
            jsonCustormCont.put("name", contentDetectLogData.getName());
            jsonCustormCont.put("type", contentDetectLogData.getType());
            jsonCustormCont.put("starttime", contentDetectLogData.getStartTime());
            jsonCustormCont.put("endtime", contentDetectLogData.getEndTime());
            notification.put("custom_content", jsonCustormCont);

            PushMsgToSingleDeviceRequest request = new PushMsgToSingleDeviceRequest()
                    .addChannelId(channelId)
                    .addMsgExpires(new Integer(3600))
                    .addMessageType(1)
                    .addMessage(notification.toString())
                    .addDeviceType(3);
            // 5. http request
            PushMsgToSingleDeviceResponse response = pushClient
                    .pushMsgToSingleDevice(request);

            logger.debug("pushToAndroidDevice To channelId: " +channelId + ", msgId: " + response.getMsgId()
                    + ",sendTime: " + response.getSendTime());

            // save in pushed msg db
            AlarmPushedLogInfo alarmPushedLogInfo = new AlarmPushedLogInfo();
            alarmPushedLogInfo.setMsgId(response.getMsgId());
            alarmPushedLogInfo.setMsgSendTime(response.getSendTime());
            alarmPushedLogInfo.setAlarmDevice(alarmDeviceRepository.findByChannelId(channelId));
            alarmPushedLogInfo.setAlarmPushLog(alarmPushLog);
            alarmPushedLogInfo.setMsgForAll(false);
            alarmPushedLogInfoRepository.save(alarmPushedLogInfo);

        } catch (PushClientException e) {
            logger.error("pushToAndroidDevice PushClientException: errorMessage:" + e.getMessage());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "pushToAndroidDevice PushServerException: requestId: %d, errorCode: %d, errorMessage: %s",
                    e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
        }
    }

    private void pushToIOSDevice(ContentDetectLogData contentDetectLogData, AlarmPushLog alarmPushLog, String channelId) {
        // 1. get apiKey and secretKey from developer console
        PushKeyPair pair = new PushKeyPair(IOSApiKey, IOSSecretKey);

        // 2. build a BaidupushClient object to access released interfaces
        BaiduPushClient pushClient = new BaiduPushClient(pair,
                BaiduPushConstants.CHANNEL_REST_URL);

        // 3. register a YunLogHandler to get detail interacting information
        // in this request.
        pushClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
//                logger.info("pushToIOSDevice onHandle: " + event.getMessage());
            }
        });

        try {
            String pushMsg = "蝈蝈告警:" + contentDetectLogData.getName() + " "
                    + contentDetectLogData.getStartTimeStr() + " 出现"
                    + contentDetectLogData.getTypeTranslate();
            // 4. specify request arguments
            // make IOS Notification
            JSONObject notification = new JSONObject();
            JSONObject jsonAPS = new JSONObject();
            jsonAPS.put("alert", pushMsg);
            jsonAPS.put("sound", IOSsound);
            notification.put("aps", jsonAPS);
            notification.put("id", contentDetectLogData.getId());
            notification.put("name", contentDetectLogData.getName());
            notification.put("type", contentDetectLogData.getType());
            notification.put("starttime", contentDetectLogData.getStartTime());
            notification.put("endtime", contentDetectLogData.getEndTime());

            PushMsgToSingleDeviceRequest request = new PushMsgToSingleDeviceRequest()
                    .addChannelId(channelId)
                    .addMsgExpires(new Integer(3600))
                    .addMessageType(1)
                    .addMessage(notification.toString()).addDeployStatus(IOSDepolyStatus)
                    .addDeviceType(4);// deviceType => 3:android, 4:ios
            // 5. http request
            PushMsgToSingleDeviceResponse response = pushClient.pushMsgToSingleDevice(request);

            logger.debug("pushToIOSDevice to channelId:" + channelId + ", msgId: " + response.getMsgId() + ",sendTime: "
                    + response.getSendTime());

            // save in pushed msg db
            AlarmPushedLogInfo alarmPushedLogInfo = new AlarmPushedLogInfo();
            alarmPushedLogInfo.setMsgId(response.getMsgId());
            alarmPushedLogInfo.setMsgSendTime(response.getSendTime());
            alarmPushedLogInfo.setAlarmDevice(alarmDeviceRepository.findByChannelId(channelId));
            alarmPushedLogInfo.setAlarmPushLog(alarmPushLog);
            alarmPushedLogInfo.setMsgForAll(false);
            alarmPushedLogInfoRepository.save(alarmPushedLogInfo);

        } catch (PushClientException e) {
            logger.error("pushToIOSDevice PushClientException: " + e.getMessage());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "pushToIOSDevice PushServerException: requestId: %d, errorCode: %d, errorMessage: %s",
                    e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
        }

    }

    public void pushToAllDevice(String pushMsg){
        //All Android Device
        pushToAllAndroidDevice(pushMsg);
        //All IOS Device
        pushToAllIOSDevice(pushMsg);
    }

    private void pushToAllAndroidDevice(String pushMsg){
        // 1. get apiKey and secretKey from developer console
        PushKeyPair pair = new PushKeyPair(AndroidApiKey, AndroidSecretKey);

        // 2. build a BaidupushClient object to access released interfaces
        BaiduPushClient pushClient = new BaiduPushClient(pair,
                BaiduPushConstants.CHANNEL_REST_URL);

        // 3. register a YunLogHandler to get detail interacting information
        // in this request.
        pushClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
//                logger.info("pushToAllAndroidDevice onHandle: " + event.getMessage());
            }
        });

        try {
            // 4. specify request arguments
            PushMsgToAllRequest request = new PushMsgToAllRequest()
                    .addMsgExpires(new Integer(3600)).addMessageType(1)
                    .addMessage(pushMsg)
                    .addSendTime(System.currentTimeMillis() / 1000 + 120) // 设置定时推送时间，必需超过当前时间一分钟，单位秒.实例2分钟后推送
                    .addDeviceType(3);
            // 5. http request
            PushMsgToAllResponse response = pushClient.pushMsgToAll(request);

            logger.debug("pushToAllAndroidDevice " + "msgId: " + response.getMsgId() + ",sendTime: "
                    + response.getSendTime() + ",timerId: "
                    + response.getTimerId());

        } catch (PushClientException e) {
            logger.error("pushToAllAndroidDevice PushClientException: " + e.getMessage());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "pushToAllAndroidDevice PushServerException: requestId: %d, errorCode: %d, errorMessage: %s",
                    e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
        }
    }

    private void pushToAllIOSDevice(String pushMsg) {
        // 1. get apiKey and secretKey from developer console
        PushKeyPair pair = new PushKeyPair(IOSApiKey, IOSSecretKey);

        // 2. build a BaidupushClient object to access released interfaces
        BaiduPushClient pushClient = new BaiduPushClient(pair,
                BaiduPushConstants.CHANNEL_REST_URL);

        // 3. register a YunLogHandler to get detail interacting information
        // in this request.
        pushClient.setChannelLogHandler(new YunLogHandler() {
            @Override
            public void onHandle(YunLogEvent event) {
//                logger.info("pushToAllIOSDevice onHandle: " + event.getMessage());
            }
        });

        try {
            // 4. specify request arguments
            // 创建IOS通知
            JSONObject notification = new JSONObject();
            JSONObject jsonAPS = new JSONObject();
            jsonAPS.put("alert", pushMsg);
            jsonAPS.put("sound", IOSsound);
            notification.put("aps", jsonAPS);
            notification.put("msg", pushMsg);

            PushMsgToAllRequest request = new PushMsgToAllRequest()
                    .addMsgExpires(new Integer(3600)).addMessageType(1)
                    .addMessage(notification.toString())
                    .addSendTime(System.currentTimeMillis() / 1000 + 120) // 设置定时推送时间，必需超过当前时间一分钟，单位秒.实例2分钟后推送
                    .addDepolyStatus(IOSDepolyStatus).addDeviceType(4);
            // 5. http request
            PushMsgToAllResponse response = pushClient.pushMsgToAll(request);

            logger.debug("pushToAllIOSDevice " + "msgId: " + response.getMsgId() + ",sendTime: "
                    + response.getSendTime() + ",timerId: "
                    + response.getTimerId());

        } catch (PushClientException e) {
            logger.error("pushToAllIOSDevice PushClientException: " + e.getMessage());
        } catch (PushServerException e) {
            logger.error(String.format(
                    "pushToAllIOSDevice PushServerException: requestId: %d, errorCode: %d, errorMessage: %s",
                    e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
        }
    }

    public List<AlarmPushLog> findAll(CustomAlarmLogQueryParams params)
    {
        return alarmPushLogRepository.findAll(params);
    }
}
