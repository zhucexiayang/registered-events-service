/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50719
Source Host           : localhost:3306
Source Database       : zx_registered_event

Target Server Type    : MYSQL
Target Server Version : 50719
File Encoding         : 65001

Date: 2019-07-19 18:46:43
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for application_service
-- ----------------------------
DROP TABLE IF EXISTS `application_service`;
CREATE TABLE `application_service` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_key` varchar(32) DEFAULT NULL COMMENT '应用key值标识',
  `app_encoding_key` varchar(64) DEFAULT NULL COMMENT '加密秘钥',
  `callback_url` varchar(255) DEFAULT NULL COMMENT '事件接收url地址',
  `create_date` varchar(32) DEFAULT NULL,
  `modify_date` varchar(32) DEFAULT NULL,
  `flag` varchar(1) DEFAULT '1' COMMENT '删除标识，0，删除 1，正常',
  `status` varchar(20) DEFAULT NULL COMMENT '状态',
  `create_by` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of application_service
-- ----------------------------
INSERT INTO `application_service` VALUES ('1', 'app08eeb76b0000010a', 'mjzOwWzcqkpBzaCpCpcDu4WkSDFskWMSE7iaNMSr0S0', 'http://127.0.0.1', '2019-07-19 14:35:33', null, '1', null, null);
INSERT INTO `application_service` VALUES ('2', 'app08eeb76b0000020a', 'waLMd4QWMzoGZPPZJxQ0JnN5IfJqZrJov5Sf6XzdPyU', 'http://127.0.0.1', '2019-07-19 14:36:35', null, '1', null, null);
INSERT INTO `application_service` VALUES ('3', 'app08eeb76b0000030a', 'vQCk4Yc7mhdRGctLNpVDdj8myGPyujBCVOiebD1WIil', 'http://127.0.0.1', '2019-07-19 14:37:55', null, '1', null, null);
INSERT INTO `application_service` VALUES ('4', 'app08eeb76b0000040a', 'B9vfkH1bNRoVWXoC5RiRnKXWIj5BFxySnqwqvsk76G2', null, '2019-07-19 14:38:20', null, '1', null, null);

-- ----------------------------
-- Table structure for event_failure_record
-- ----------------------------
DROP TABLE IF EXISTS `event_failure_record`;
CREATE TABLE `event_failure_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_id` bigint(20) DEFAULT NULL COMMENT '应用id',
  `status` varchar(12) DEFAULT NULL,
  `content` varchar(2000) DEFAULT NULL COMMENT '内容',
  `create_date` varchar(32) DEFAULT NULL,
  `modify_date` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of event_failure_record
-- ----------------------------

-- ----------------------------
-- Table structure for register_event
-- ----------------------------
DROP TABLE IF EXISTS `register_event`;
CREATE TABLE `register_event` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_id` bigint(20) DEFAULT NULL,
  `event_type` varchar(64) DEFAULT NULL COMMENT '注册的事件类型',
  `create_date` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `modify_date` varchar(32) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of register_event
-- ----------------------------
INSERT INTO `register_event` VALUES ('3', '4', 'log_in233', '2019-07-19 15:40:40', null);
INSERT INTO `register_event` VALUES ('4', '4', 'register343443', '2019-07-19 15:40:40', null);
