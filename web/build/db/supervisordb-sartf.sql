/*
Navicat MySQL Data Transfer

Source Server         : 172.17.230.168
Source Server Version : 50537
Source Host           : 172.17.230.168:3306
Source Database       : supervisordb

Target Server Type    : MYSQL
Target Server Version : 50537
File Encoding         : 65001

Date: 2015-06-01 11:06:44
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `supervisordb`;
CREATE DATABASE `supervisordb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `supervisordb`;
--
-- Table structure for table `audioprofile`
--

DROP TABLE IF EXISTS `audioprofile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `audioprofile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `audioProfileName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

-- ----------------------------
-- Table structure for channel
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
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for channel_content_detect_config
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
  `boomSonic_threshold_diff` int(11) DEFAULT NULL,
  `loudVolume_seconds` float DEFAULT NULL,
  `loudVolume_threshold` int(11) DEFAULT NULL,
  `lowVolume_seconds` float DEFAULT NULL,
  `lowVolume_threshold` int(11) DEFAULT NULL,
  `silence_threshold` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for channel_group
-- ----------------------------
DROP TABLE IF EXISTS `channel_group`;
CREATE TABLE `channel_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for channel_info
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
) ENGINE=InnoDB AUTO_INCREMENT=102 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for channel_mobile_config
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
) ENGINE=InnoDB AUTO_INCREMENT=153 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for channel_record_history
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
-- Table structure for channel_signal_detect_config
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
-- Table structure for channel_signal_detect_type_config
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

--
-- Table structure for table `content_detect_log`
--

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
-- Table structure for layout_template
-- ----------------------------
DROP TABLE IF EXISTS `layout_template`;
CREATE TABLE `layout_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lastUpdate` datetime DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for layout_template_cell
-- ----------------------------
DROP TABLE IF EXISTS `layout_template_cell`;
CREATE TABLE `layout_template_cell` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cellIndex` int(11) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  `xPos` int(11) DEFAULT NULL,
  `yPos` int(11) DEFAULT NULL,
  `template_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_fsnacid2smax6hiuqnloyajgy` (`template_id`),
  CONSTRAINT `FK_fsnacid2smax6hiuqnloyajgy` FOREIGN KEY (`template_id`) REFERENCES `layout_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for layout_template_info
-- ----------------------------
DROP TABLE IF EXISTS `layout_template_info`;
CREATE TABLE `layout_template_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `totalHeight` int(11) DEFAULT NULL,
  `totalWidth` int(11) DEFAULT NULL,
  `template_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_10oqjl60tugjwlsmrnhin94q7` (`template_id`),
  CONSTRAINT `FK_10oqjl60tugjwlsmrnhin94q7` FOREIGN KEY (`template_id`) REFERENCES `layout_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for message
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
-- Table structure for monitorlog
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
-- Table structure for ops_servers
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

--
-- Table structure for table `output_profile`
--

DROP TABLE IF EXISTS `output_profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `output_profile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `template` longtext,
  `video_audio_description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `profile`
--

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for profile_output
-- ----------------------------
DROP TABLE IF EXISTS `profile_output`;
CREATE TABLE `profile_output` (
  `video_audio_description` varchar(255) DEFAULT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_t78xhchli64psk4tbbb9232q8` FOREIGN KEY (`id`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for profile_task
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
-- Table structure for profile_template
-- ----------------------------
DROP TABLE IF EXISTS `profile_template`;
CREATE TABLE `profile_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for screen
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for screen_position
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
) ENGINE=InnoDB AUTO_INCREMENT=1014 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for screen_schema
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
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for server_component
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
-- Table structure for server_groups
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
-- Table structure for servers
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
-- Table structure for servicelog
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
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for settings
-- ----------------------------
DROP TABLE IF EXISTS `settings`;
CREATE TABLE `settings` (
  `key` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for storage
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
-- Table structure for systemlog
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
) ENGINE=InnoDB AUTO_INCREMENT=1078 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for task
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
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

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
-- Table structure for token
-- ----------------------------
DROP TABLE IF EXISTS `token`;
CREATE TABLE `token` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_g7im3j7f0g31yhl6qco2iboy5` (`user_id`),
  CONSTRAINT `FK_g7im3j7f0g31yhl6qco2iboy5` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for transcoder_template
-- ----------------------------
DROP TABLE IF EXISTS `transcoder_template`;
CREATE TABLE `transcoder_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `template` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `real_name` varchar(255) DEFAULT NULL,
  `role` int(11) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `current_config_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_793g820vdcsggic2fq1bnug09` (`current_config_id`),
  CONSTRAINT `FK_793g820vdcsggic2fq1bnug09` FOREIGN KEY (`current_config_id`) REFERENCES `user_config` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
--  Records of `user`
-- ----------------------------
BEGIN;
INSERT INTO `user` VALUES ('1', '21232f297a57a5a743894a0e4a801fc3', 'admin', '1', 'admin', NULL);
COMMIT;

-- ----------------------------
-- Table structure for user_config
-- ----------------------------
DROP TABLE IF EXISTS `user_config`;
CREATE TABLE `user_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lastUpdate` datetime DEFAULT NULL,
  `audio_channel_id` int(11) DEFAULT NULL,
  `template_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `cell_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2n0qf2hxs3q8h3v6e17ahy3c0` (`audio_channel_id`),
  KEY `FK_5y9t2knp1yuyott3rtx753lu2` (`template_id`),
  KEY `FK_kmjpdu8epwveocbc4nqxbyjo0` (`user_id`),
  KEY `FK_cvqwkujbnth3ugfhpt9hlfgwy` (`cell_id`),
  CONSTRAINT `FK_cvqwkujbnth3ugfhpt9hlfgwy` FOREIGN KEY (`cell_id`) REFERENCES `layout_template_cell` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_kmjpdu8epwveocbc4nqxbyjo0` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_2n0qf2hxs3q8h3v6e17ahy3c0` FOREIGN KEY (`audio_channel_id`) REFERENCES `channel` (`id`) ON DELETE SET NULL,
  CONSTRAINT `FK_5y9t2knp1yuyott3rtx753lu2` FOREIGN KEY (`template_id`) REFERENCES `layout_template` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_config_channel
-- ----------------------------
DROP TABLE IF EXISTS `user_config_channel`;
CREATE TABLE `user_config_channel` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `cell_id` int(11) DEFAULT NULL,
  `channel_id` int(11) DEFAULT NULL,
  `userconfig_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_p2ado18xiefoqd6pt91i1cvl3` (`cell_id`),
  KEY `FK_1ds799958x79vemvwsw9h4dlm` (`channel_id`),
  KEY `FK_s452khxg8v2mnlhsp45w46t9n` (`userconfig_id`),
  CONSTRAINT `FK_s452khxg8v2mnlhsp45w46t9n` FOREIGN KEY (`userconfig_id`) REFERENCES `user_config` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_1ds799958x79vemvwsw9h4dlm` FOREIGN KEY (`channel_id`) REFERENCES `channel` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_p2ado18xiefoqd6pt91i1cvl3` FOREIGN KEY (`cell_id`) REFERENCES `layout_template_cell` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_ops
-- ----------------------------
DROP TABLE IF EXISTS `user_ops`;
CREATE TABLE `user_ops` (
  `user_id` int(11) NOT NULL DEFAULT '0',
  `ops_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  KEY `FK_2mrnytltnau82ffoh0yul0xhd` (`ops_id`),
  CONSTRAINT `FK_2mrnytltnau82ffoh0yul0xhd` FOREIGN KEY (`ops_id`) REFERENCES `ops_servers` (`id`),
  CONSTRAINT `FK_qcswrynjs6itwsmjhcot5dc9m` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_task_info
-- ----------------------------
DROP TABLE IF EXISTS `user_task_info`;
CREATE TABLE `user_task_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `lastUpdate` datetime DEFAULT NULL,
  `rtspMobileFileName` varchar(255) DEFAULT NULL,
  `rtspOpsFileName` varchar(255) DEFAULT NULL,
  `task_id` int(11) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_asjp7ic68ju9ni13pig2hd4ws` (`task_id`),
  KEY `FK_jl74x8aih80m9qpk5n8vigsny` (`user_id`),
  CONSTRAINT `FK_jl74x8aih80m9qpk5n8vigsny` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `FK_asjp7ic68ju9ni13pig2hd4ws` FOREIGN KEY (`task_id`) REFERENCES `task` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `version`;
CREATE TABLE `version` (
  `version_number` double NOT NULL,
  `id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
BEGIN;
INSERT INTO `version`(`version_number`, `id`) VALUES (1.1, 1);
COMMIT;
--
-- Table structure for table `videoprofile`
--

DROP TABLE IF EXISTS `videoprofile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `videoprofile` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `videoProfileName` varchar(255) DEFAULT NULL,
  `videocodec` varchar(255) DEFAULT NULL,
  `videocodecprofile` varchar(255) DEFAULT NULL,
  `videoheight` int(11) DEFAULT NULL,
  `videopassthrough` int(11) DEFAULT NULL,
  `videowidth` int(11) DEFAULT NULL,
  `videoPARX` int(11) DEFAULT NULL,
  `videoPARY` int(11) DEFAULT NULL,
  `videosourcePAR` int(11) DEFAULT NULL,
  `videosmartborder` int(11) DEFAULT NULL,
  `videoframerateX` int(11) DEFAULT NULL,
  `videoframerateY` int(11) DEFAULT NULL,
  `videoframerateconversionmode` int(11) DEFAULT NULL,
  `videosourceframerate` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
-- Table structure for wall
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

-- ----------------------------
-- Table structure for wall_position
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
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

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
