/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 50612
 Source Host           : 127.0.0.1
 Source Database       : supervisordb

 Target Server Type    : MySQL
 Target Server Version : 50612
 File Encoding         : utf-8

 Date: 12/26/2014 17:50:08 PM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `supervisordb`;
CREATE DATABASE `supervisordb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `supervisordb`;

-- ----------------------------
--  Table structure for `channel`
-- ----------------------------
DROP TABLE IF EXISTS `channel`;
CREATE TABLE `channel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `audio_id` varchar(255) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `enable_content_detect` bit(1) DEFAULT NULL,
  `enable_signal_detect` bit(1) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `is_support_mobile` bit(1) DEFAULT NULL,
  `max_persist_days` tinyint(4) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `program_id` varchar(255) DEFAULT NULL,
  `protocol` varchar(255) DEFAULT NULL,
  `content_detect_config_id` int(11) DEFAULT NULL,
  `channel_group_id` int(11) DEFAULT NULL,
  `signal_detect_config_id` int(11) DEFAULT NULL,
  `record_base_path` varchar(255) DEFAULT NULL,
  `channel_info_id` int(11) DEFAULT NULL,
  `record_format` tinyint(4) DEFAULT NULL,
  `enable_record` bit(1) DEFAULT NULL,
  `enable_signal_detect_by_Type` bit(1) DEFAULT NULL,
  `signal_detect_type_config_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_354vpa21lr2rmt6igdfbqy35h` (`content_detect_config_id`) USING BTREE,
  KEY `FK_i31dbhv2fd05bgula28daiwxn` (`channel_group_id`) USING BTREE,
  KEY `FK_5nimiyfxnlakl60jxcf2l3f45` (`signal_detect_config_id`) USING BTREE,
  KEY `FK_88gl7pw9d8pv75b2vvpcppa1` (`channel_info_id`),
  KEY `FK_r6q5asb93xm69ynn7ltxhyd2y` (`signal_detect_type_config_id`),
  CONSTRAINT `FK_r6q5asb93xm69ynn7ltxhyd2y` FOREIGN KEY (`signal_detect_type_config_id`) REFERENCES `channel_signal_detect_type_config` (`id`),
  CONSTRAINT `channel_ibfk_1` FOREIGN KEY (`content_detect_config_id`) REFERENCES `channel_content_detect_config` (`id`) ON DELETE SET NULL,
  CONSTRAINT `channel_ibfk_2` FOREIGN KEY (`signal_detect_config_id`) REFERENCES `channel_signal_detect_config` (`id`) ON DELETE SET NULL,
  CONSTRAINT `channel_ibfk_3` FOREIGN KEY (`channel_group_id`) REFERENCES `channel_group` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_88gl7pw9d8pv75b2vvpcppa1` FOREIGN KEY (`channel_info_id`) REFERENCES `channel_info` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `channel_content_detect_config`
-- ----------------------------
DROP TABLE IF EXISTS `channel_content_detect_config`;
CREATE TABLE `channel_content_detect_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `black_seconds` float DEFAULT NULL,
  `enable_boom_sonic` bit(1) DEFAULT NULL,
  `green_seconds` float DEFAULT NULL,
  `no_frame_seconds` float DEFAULT NULL,
  `silence_seconds` float DEFAULT NULL,
  `boomSonic_threshold` int(11) DEFAULT NULL,
  `loudVolume_seconds` float DEFAULT NULL,
  `loudVolume_threshold` int(11) DEFAULT NULL,
  `lowVolume_seconds` float DEFAULT NULL,
  `lowVolume_threshold` int(11) DEFAULT NULL,
  `silence_threshold` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `channel_group`
-- ----------------------------
DROP TABLE IF EXISTS `channel_group`;
CREATE TABLE `channel_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `channel_info`
-- ----------------------------
DROP TABLE IF EXISTS `channel_info`;
CREATE TABLE `channel_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `abitdepth` varchar(255) DEFAULT NULL,
  `abitrate` varchar(255) DEFAULT NULL,
  `achannels` varchar(255) DEFAULT NULL,
  `acodec` varchar(255) DEFAULT NULL,
  `alanguage` varchar(255) DEFAULT NULL,
  `asamplerate` varchar(255) DEFAULT NULL,
  `container` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `vbitrate` varchar(255) DEFAULT NULL,
  `vcodec` varchar(255) DEFAULT NULL,
  `vframerate` varchar(255) DEFAULT NULL,
  `vratio` varchar(255) DEFAULT NULL,
  `vresolution` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `channel_mobile_config`
-- ----------------------------
DROP TABLE IF EXISTS `channel_mobile_config`;
CREATE TABLE `channel_mobile_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `audio_bitrate` int(11) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `type` tinyint(4) DEFAULT NULL,
  `video_bitrate` int(11) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  `channel_id` int(11) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_e7ogkgo4kkhqmp9nt13y514ux` (`channel_id`) USING BTREE,
  CONSTRAINT `channel_mobile_config_ibfk_1` FOREIGN KEY (`channel_id`) REFERENCES `channel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `channel_record_history`
-- ----------------------------
DROP TABLE IF EXISTS `channel_record_history`;
CREATE TABLE `channel_record_history` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `end_time` datetime DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `channel_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_lycmbfmurkdd87gfrjcm7wavg` (`channel_id`),
  CONSTRAINT `FK_lycmbfmurkdd87gfrjcm7wavg` FOREIGN KEY (`channel_id`) REFERENCES `channel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `channel_signal_detect_config`
-- ----------------------------
DROP TABLE IF EXISTS `channel_signal_detect_config`;
CREATE TABLE `channel_signal_detect_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `enable_l1_error` bit(1) DEFAULT NULL,
  `enable_l2_error` bit(1) DEFAULT NULL,
  `enable_l3_error` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `channel_signal_detect_type_config`
-- ----------------------------
DROP TABLE IF EXISTS `channel_signal_detect_type_config`;
CREATE TABLE `channel_signal_detect_type_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `enable_warning_audio_loss` bit(1) DEFAULT NULL,
  `enable_warning_cc_error` bit(1) DEFAULT NULL,
  `enable_warning_progid_loss` bit(1) DEFAULT NULL,
  `enable_warning_signal_broken` bit(1) DEFAULT NULL,
  `enable_warning_video_loss` bit(1) DEFAULT NULL,
  `notify_interval` int(11) DEFAULT NULL,
  `warning_audio_loss_timeout` int(11) DEFAULT NULL,
  `warning_cc_error_count` int(11) DEFAULT NULL,
  `warning_cc_error_timeout` int(11) DEFAULT NULL,
  `warning_progid_loss_timeout` int(11) DEFAULT NULL,
  `warning_signal_broken_timeout` int(11) DEFAULT NULL,
  `warning_video_loss_timeout` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `content_detect_log`
-- ----------------------------
DROP TABLE IF EXISTS `content_detect_log`;
CREATE TABLE `content_detect_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `end_time` bigint(20) DEFAULT NULL,
  `audio_sound_track` int(11) DEFAULT NULL,
  `start_time` bigint(20) DEFAULT NULL,
  `task_id` int(11) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `channel_name` varchar(255) DEFAULT NULL,
  `start_offset` bigint(20) DEFAULT NULL,
  `video_file_path` varchar(255) DEFAULT NULL,
  `guid` varchar(255) DEFAULT NULL,
  `channel_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `message`
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `datetime` varchar(255) DEFAULT NULL,
  `ipAddress` varchar(255) DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `real_name` varchar(255) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `realDateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `monitorlog`
-- ----------------------------
DROP TABLE IF EXISTS `monitorlog`;
CREATE TABLE `monitorlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `channelName` varchar(255) DEFAULT NULL,
  `endTime` varchar(255) DEFAULT NULL,
  `logNo` varchar(255) DEFAULT NULL,
  `logType` int(11) DEFAULT NULL,
  `startTime` varchar(255) DEFAULT NULL,
  `mlChannelName` varchar(255) DEFAULT NULL,
  `mlEndTime` varchar(255) DEFAULT NULL,
  `mlLogNo` varchar(255) DEFAULT NULL,
  `mlRealEndDateTime` datetime DEFAULT NULL,
  `mlRealStartDateTime` datetime DEFAULT NULL,
  `mlStartTime` varchar(255) DEFAULT NULL,
  `mlWarningType` int(11) DEFAULT NULL,
  `mloperationResult` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `ops_servers`
-- ----------------------------
DROP TABLE IF EXISTS `ops_servers`;
CREATE TABLE `ops_servers` (
  `id` varchar(255) NOT NULL,
  `gateway` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `netmask` varchar(255) DEFAULT NULL,
  `port` varchar(255) DEFAULT NULL,
  `resolution` varchar(255) DEFAULT NULL,
  `support_resolutions` varchar(255) DEFAULT NULL,
  `mac` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `profile`
-- ----------------------------
DROP TABLE IF EXISTS `profile`;
CREATE TABLE `profile` (
  `type` char(1) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `template_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_s75nnnd4gypm84l4faqhdrkme` (`template_id`),
  CONSTRAINT `FK_s75nnnd4gypm84l4faqhdrkme` FOREIGN KEY (`template_id`) REFERENCES `profile_template` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `profile_output`
-- ----------------------------
DROP TABLE IF EXISTS `profile_output`;
CREATE TABLE `profile_output` (
  `video_audio_description` varchar(255) DEFAULT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_t78xhchli64psk4tbbb9232q8` FOREIGN KEY (`id`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `profile_task`
-- ----------------------------
DROP TABLE IF EXISTS `profile_task`;
CREATE TABLE `profile_task` (
  `amount_output` int(11) DEFAULT NULL,
  `encodingOption` varchar(255) DEFAULT NULL,
  `priority` varchar(255) DEFAULT NULL,
  `screenColumn` int(11) DEFAULT NULL,
  `screenRow` int(11) DEFAULT NULL,
  `used_gpu_core_amount` int(11) DEFAULT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_h7jqcic1bve5r7he2qd7tq6m1` FOREIGN KEY (`id`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `profile_template`
-- ----------------------------
DROP TABLE IF EXISTS `profile_template`;
CREATE TABLE `profile_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `screen`
-- ----------------------------
DROP TABLE IF EXISTS `screen`;
CREATE TABLE `screen` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `active_schema_id` int(11) DEFAULT NULL,
  `wall_position_id` int(11) DEFAULT NULL,
  `rtsp_file_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8ciuhfiu6u8ow6xtxu8j9yl0y` (`wall_position_id`) USING BTREE,
  KEY `FK_gco8isn4wmlwtse7lv3hwevxw` (`active_schema_id`) USING BTREE,
  CONSTRAINT `screen_ibfk_1` FOREIGN KEY (`wall_position_id`) REFERENCES `wall_position` (`id`) ON DELETE CASCADE,
  CONSTRAINT `screen_ibfk_2` FOREIGN KEY (`active_schema_id`) REFERENCES `screen_schema` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `screen_position`
-- ----------------------------
DROP TABLE IF EXISTS `screen_position`;
CREATE TABLE `screen_position` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `column` tinyint(4) DEFAULT NULL,
  `row` tinyint(4) DEFAULT NULL,
  `channel_id` int(11) DEFAULT NULL,
  `schema_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_tajsllhw3l0xs4lrvvnk14olq` (`channel_id`) USING BTREE,
  KEY `FK_6ssibw4ri1wrs8lfp79t1bmta` (`schema_id`) USING BTREE,
  CONSTRAINT `screen_position_ibfk_1` FOREIGN KEY (`schema_id`) REFERENCES `screen_schema` (`id`) ON DELETE CASCADE,
  CONSTRAINT `screen_position_ibfk_2` FOREIGN KEY (`channel_id`) REFERENCES `channel` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=505 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `screen_schema`
-- ----------------------------
DROP TABLE IF EXISTS `screen_schema`;
CREATE TABLE `screen_schema` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `column_count` tinyint(4) DEFAULT NULL,
  `row_count` tinyint(4) DEFAULT NULL,
  `schema_value` tinyint(4) DEFAULT NULL,
  `screen_id` int(11) DEFAULT NULL,
  `schema_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_18ni393jjse764l7dypg0v8hq` (`screen_id`) USING BTREE,
  CONSTRAINT `screen_schema_ibfk_1` FOREIGN KEY (`screen_id`) REFERENCES `screen` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `server_component`
-- ----------------------------
DROP TABLE IF EXISTS `server_component`;
CREATE TABLE `server_component` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `capacity_total` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `capacity_used` int(11) DEFAULT NULL,
  `server_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_iynpm9oytqubfbrnfw08v55jp` (`server_id`) USING BTREE,
  CONSTRAINT `server_component_ibfk_1` FOREIGN KEY (`server_id`) REFERENCES `servers` (`server_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `server_groups`
-- ----------------------------
DROP TABLE IF EXISTS `server_groups`;
CREATE TABLE `server_groups` (
  `group_id` int(11) NOT NULL AUTO_INCREMENT,
  `group_name` varchar(255) NOT NULL,
  `group_type` int(11) NOT NULL,
  PRIMARY KEY (`group_id`),
  UNIQUE KEY `UK_1b88u2mgiub6pvq09mwsingt4` (`group_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `servers`
-- ----------------------------
DROP TABLE IF EXISTS `servers`;
CREATE TABLE `servers` (
  `server_id` varchar(255) NOT NULL,
  `active_functions` varchar(255) DEFAULT NULL,
  `server_isalive` bit(1) DEFAULT NULL,
  `eth` varchar(255) DEFAULT NULL,
  `functions` varchar(255) DEFAULT NULL,
  `gateway` varchar(255) DEFAULT NULL,
  `server_ip` varchar(255) DEFAULT NULL,
  `joined` bit(1) DEFAULT NULL,
  `server_name` varchar(255) DEFAULT NULL,
  `netmask` varchar(255) DEFAULT NULL,
  `server_port` int(11) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `server_state` int(11) DEFAULT NULL,
  `server_type` int(11) NOT NULL,
  PRIMARY KEY (`server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `servicelog`
-- ----------------------------
DROP TABLE IF EXISTS `servicelog`;
CREATE TABLE `servicelog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `level` tinyint(4) NOT NULL,
  `module` tinyint(4) NOT NULL,
  `time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `settings`
-- ----------------------------
DROP TABLE IF EXISTS `settings`;
CREATE TABLE `settings` (
  `key` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `storage`
-- ----------------------------
DROP TABLE IF EXISTS `storage`;
CREATE TABLE `storage` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `pwd` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `user` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `systemlog`
-- ----------------------------
DROP TABLE IF EXISTS `systemlog`;
CREATE TABLE `systemlog` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `dateTime` varchar(255) DEFAULT NULL,
  `ipAddress` varchar(255) DEFAULT NULL,
  `logInfo` varchar(255) DEFAULT NULL,
  `logType` int(11) DEFAULT NULL,
  `realDateTime` datetime DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `funcType` int(11) DEFAULT NULL,
  `operationInfo` varchar(255) DEFAULT NULL,
  `operationResult` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=531 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `task`
-- ----------------------------
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ref_id` int(11) DEFAULT NULL,
  `server_id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `profile_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_sw654vhl8cuvn5o4mri1lblik` (`profile_id`),
  CONSTRAINT `FK_sw654vhl8cuvn5o4mri1lblik` FOREIGN KEY (`profile_id`) REFERENCES `profile_task` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for task_port
-- ----------------------------
DROP TABLE IF EXISTS `task_port`;
CREATE TABLE `task_port` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `port_number` int(11) NOT NULL,
  `type` varchar(255) DEFAULT NULL,
  `task_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_iklen0wyfawijvx0ca57a97l9` (`task_id`),
  CONSTRAINT `FK_iklen0wyfawijvx0ca57a97l9` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for `ssh_connection_info`
-- ----------------------------
DROP TABLE IF EXISTS `ssh_connection_info`;
CREATE TABLE `ssh_connection_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `port` int(11) NOT NULL,
  `user` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
--  Table structure for `transcoder_template`
-- ----------------------------
DROP TABLE IF EXISTS `transcoder_template`;
CREATE TABLE `transcoder_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- <CheckContent>${(videoProfileAndTaskOutputTargetType.targetType.name() == "SCREEN")?string(1, 0)}</CheckContent> can be  <CheckContent>1</CheckContent>
-- ----------------------------
INSERT INTO `transcoder_template`(`id`, `template`) VALUES(NULL, '<#function evenNumber a b>
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
');

-- ----------------------------
--  Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `real_name` varchar(255) DEFAULT NULL,
  `role` int(11) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `user`
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES ('1', '21232f297a57a5a743894a0e4a801fc3', 'admin', '1', 'admin', '');
COMMIT;

-- ----------------------------
--  Table structure for `wall`
-- ----------------------------
DROP TABLE IF EXISTS `wall`;
CREATE TABLE `wall` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `column_count` tinyint(4) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `row_count` tinyint(4) DEFAULT NULL,
  `type` tinyint(4) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Table structure for `wall_position`
-- ----------------------------
DROP TABLE IF EXISTS `wall_position`;
CREATE TABLE `wall_position` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `column` tinyint(4) DEFAULT NULL,
  `row` tinyint(4) DEFAULT NULL,
  `ops_id` varchar(255) DEFAULT NULL,
  `wall_id` int(11) DEFAULT NULL,
  `sdi_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `sdi_id` (`sdi_id`),
  KEY `FK_1et29fgugwthg312voador536` (`wall_id`) USING BTREE,
  KEY `FK_7nf1taid8ckck37hplkra4r5b` (`ops_id`) USING BTREE,
  CONSTRAINT `wall_position_ibfk_1` FOREIGN KEY (`wall_id`) REFERENCES `wall` (`id`) ON DELETE CASCADE,
  CONSTRAINT `wall_position_ibfk_2` FOREIGN KEY (`ops_id`) REFERENCES `ops_servers` (`id`) ON DELETE SET NULL,
  CONSTRAINT `wall_position_ibfk_3` FOREIGN KEY (`sdi_id`) REFERENCES `server_component` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for configuration
-- ----------------------------
DROP TABLE IF EXISTS `configuration`;
CREATE TABLE `configuration` (
  `type` char(1) NOT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for configuration_gpu
-- ----------------------------
DROP TABLE IF EXISTS `configuration_gpu`;
CREATE TABLE `configuration_gpu` (
  `enableSpan` bit(1) NOT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_shvs02wreeacgnlsvxggrdu1n` FOREIGN KEY (`id`) REFERENCES `configuration` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for configuration_rtsp
-- ----------------------------
DROP TABLE IF EXISTS `configuration_rtsp`;
CREATE TABLE `configuration_rtsp` (
  `publishFolderPath` varchar(255) DEFAULT NULL,
  `id` int(11) NOT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `mixedPublishUrl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_hpv3vx38hx6bio4nmms19h5cs` FOREIGN KEY (`id`) REFERENCES `configuration` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for configuration_warning_push
-- ----------------------------
CREATE TABLE `configuration_warning_push` (
  `ip` varchar(255) DEFAULT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_7m8bmhryv9jexcocn0t1p9hrx` FOREIGN KEY (`id`) REFERENCES `configuration` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for configuration_warning_sms
-- ----------------------------
CREATE TABLE `configuration_warning_sms` (
  `account` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_193vctkfv3svl1lw0xw93j5xj` FOREIGN KEY (`id`) REFERENCES `configuration` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `alarm_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alarm_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `signal_audio_loss` bit(1) DEFAULT NULL,
  `content_black` bit(1) DEFAULT NULL,
  `content_boom_sonic` bit(1) DEFAULT NULL,
  `signal_broken` bit(1) DEFAULT NULL,
  `signal_cc_error` bit(1) DEFAULT NULL,
  `content_green` bit(1) DEFAULT NULL,
  `content_loud_Volume` bit(1) DEFAULT NULL,
  `content_low_Volume` bit(1) DEFAULT NULL,
  `content_no_frame` bit(1) DEFAULT NULL,
  `signal_progid_loss` bit(1) DEFAULT NULL,
  `content_silence` bit(1) DEFAULT NULL,
  `signal_video_loss` bit(1) DEFAULT NULL,
  `content_detect` bit(1) DEFAULT NULL,
  `signal_detect` bit(1) DEFAULT NULL,
  `lastUpdate` datetime DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_3l8333jf930wruebhtbvwm115` (`user_id`),
  CONSTRAINT `FK_3l8333jf930wruebhtbvwm115` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alarm_config_channel`
--

DROP TABLE IF EXISTS `alarm_config_channel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alarm_config_channel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alarm_config_id` int(11) DEFAULT NULL,
  `channel_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_12mp55f8rmbhv9w6t42h50d8q` (`alarm_config_id`),
  KEY `FK_hd0naf6ol2l4dm1295hp7ogf7` (`channel_id`),
  CONSTRAINT `FK_12mp55f8rmbhv9w6t42h50d8q` FOREIGN KEY (`alarm_config_id`) REFERENCES `alarm_config` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_hd0naf6ol2l4dm1295hp7ogf7` FOREIGN KEY (`channel_id`) REFERENCES `channel` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alarm_device`
--

DROP TABLE IF EXISTS `alarm_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alarm_device` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `channelId` varchar(255) DEFAULT NULL,
  `deviceType` varchar(255) DEFAULT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `productType` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ceneu6nrhiet8tpjntqq3mpol` (`user_id`),
  CONSTRAINT `FK_ceneu6nrhiet8tpjntqq3mpol` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alarm_push_log`
--

DROP TABLE IF EXISTS `alarm_push_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alarm_push_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alarmConfigId` int(11) DEFAULT NULL,
  `channel_id` int(11) DEFAULT NULL,
  `channel_name` varchar(255) DEFAULT NULL,
  `contentDetectLogId` bigint(20) DEFAULT NULL,
  `end_time` bigint(20) DEFAULT NULL,
  `start_time` bigint(20) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `alarm_config_id` int(11) DEFAULT NULL,
  `content_detect_log_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8ti3q9ja7bidmrlqxgib7xd4p` (`alarm_config_id`),
  KEY `FK_nl5sllgivyp9qn2xkofh6uflo` (`content_detect_log_id`),
  CONSTRAINT `FK_8ti3q9ja7bidmrlqxgib7xd4p` FOREIGN KEY (`alarm_config_id`) REFERENCES `alarm_config` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_nl5sllgivyp9qn2xkofh6uflo` FOREIGN KEY (`content_detect_log_id`) REFERENCES `content_detect_log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `alarm_pushed_log_info`
--

DROP TABLE IF EXISTS `alarm_pushed_log_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alarm_pushed_log_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `alarmDeviceChannelId` varchar(255) DEFAULT NULL,
  `alarmDeviceId` int(11) DEFAULT NULL,
  `alarmPushLogId` bigint(20) DEFAULT NULL,
  `msgForAll` bit(1) DEFAULT NULL,
  `msgId` varchar(255) DEFAULT NULL,
  `msgSendTime` bigint(20) DEFAULT NULL,
  `alarm_device_id` int(11) DEFAULT NULL,
  `alarm_push_log_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_b2x32ecg6g0gtxuw4u0jt5qcq` (`alarm_device_id`),
  KEY `FK_2kx3kusmfgj03wt7b6m5rdyio` (`alarm_push_log_id`),
  CONSTRAINT `FK_2kx3kusmfgj03wt7b6m5rdyio` FOREIGN KEY (`alarm_push_log_id`) REFERENCES `alarm_push_log` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_b2x32ecg6g0gtxuw4u0jt5qcq` FOREIGN KEY (`alarm_device_id`) REFERENCES `alarm_device` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

-- ----------------------------
-- Add indexes for content_detect_log
-- ----------------------------
ALTER TABLE `content_detect_log` ADD INDEX `idx_guid` (`guid` ASC);
ALTER TABLE `content_detect_log` ADD INDEX `idx_start_time` (`start_time` ASC);
ALTER TABLE `content_detect_log` ADD INDEX `idx_channel_name` (`channel_name` ASC);
ALTER TABLE `content_detect_log` ADD INDEX `idx_type` (`type` ASC, `channel_name` ASC, `start_time` ASC);
-- ----------------------------
-- Add indexes for systemlog
-- ----------------------------
ALTER TABLE `systemlog` ADD INDEX `idx_dateTime` (`dateTime` ASC);
ALTER TABLE `systemlog` ADD INDEX `idx_funcType` (`funcType` ASC);
ALTER TABLE `systemlog` ADD INDEX `idx_operationinfo` (`operationInfo` ASC);
-- ----------------------------
-- Add indexes for servicelog
-- ----------------------------
ALTER TABLE `servicelog` ADD INDEX `idx_level` (`level` ASC);
ALTER TABLE `servicelog` ADD INDEX `idx_module` (`module` ASC);
ALTER TABLE `servicelog` ADD INDEX `idx_time` (`time` ASC);
ALTER TABLE `servicelog` ADD INDEX `idx_description` (`description` ASC);


SET FOREIGN_KEY_CHECKS = 1;

