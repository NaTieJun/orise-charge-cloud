/*
 Navicat Premium Data Transfer

 Source Server         : 抢占式-preemptively.trytowish.cn
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : preemptively.trytowish.cn:3306
 Source Schema         : xxl_job

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 23/02/2024 11:14:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for xxl_job_group
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_group`;
CREATE TABLE `xxl_job_group` (
  `id` int NOT NULL AUTO_INCREMENT,
  `app_name` varchar(64) NOT NULL COMMENT '执行器AppName',
  `title` varchar(12) NOT NULL COMMENT '执行器名称',
  `address_type` tinyint NOT NULL DEFAULT '0' COMMENT '执行器地址类型：0=自动注册、1=手动录入',
  `address_list` text COMMENT '执行器地址列表，多地址逗号分隔',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of xxl_job_group
-- ----------------------------
BEGIN;
INSERT INTO `xxl_job_group` VALUES (1, 'xxl-job-executor-sample', '示例执行器', 0, NULL, '2024-02-23 11:14:51');
INSERT INTO `xxl_job_group` VALUES (2, 'omind-scheduler-mp', '小程序平台', 0, 'http://172.25.0.131:18087/', '2024-02-23 11:14:51');
INSERT INTO `xxl_job_group` VALUES (3, 'omind-scheduler-userplat', '用户平台', 0, 'http://172.25.0.111:18085/', '2024-02-23 11:14:51');
INSERT INTO `xxl_job_group` VALUES (4, 'omind-scheduler-baseplat', '基础设备服务平台', 0, 'http://172.25.0.101:18090/', '2024-02-23 11:14:51');
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_info
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_info`;
CREATE TABLE `xxl_job_info` (
  `id` int NOT NULL AUTO_INCREMENT,
  `job_group` int NOT NULL COMMENT '执行器主键ID',
  `job_desc` varchar(255) NOT NULL,
  `add_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `author` varchar(64) DEFAULT NULL COMMENT '作者',
  `alarm_email` varchar(255) DEFAULT NULL COMMENT '报警邮件',
  `schedule_type` varchar(50) NOT NULL DEFAULT 'NONE' COMMENT '调度类型',
  `schedule_conf` varchar(128) DEFAULT NULL COMMENT '调度配置，值含义取决于调度类型',
  `misfire_strategy` varchar(50) NOT NULL DEFAULT 'DO_NOTHING' COMMENT '调度过期策略',
  `executor_route_strategy` varchar(50) DEFAULT NULL COMMENT '执行器路由策略',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_block_strategy` varchar(50) DEFAULT NULL COMMENT '阻塞处理策略',
  `executor_timeout` int NOT NULL DEFAULT '0' COMMENT '任务执行超时时间，单位秒',
  `executor_fail_retry_count` int NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `glue_type` varchar(50) NOT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) DEFAULT NULL COMMENT 'GLUE备注',
  `glue_updatetime` datetime DEFAULT NULL COMMENT 'GLUE更新时间',
  `child_jobid` varchar(255) DEFAULT NULL COMMENT '子任务ID，多个逗号分隔',
  `trigger_status` tinyint NOT NULL DEFAULT '0' COMMENT '调度状态：0-停止，1-运行',
  `trigger_last_time` bigint NOT NULL DEFAULT '0' COMMENT '上次调度时间',
  `trigger_next_time` bigint NOT NULL DEFAULT '0' COMMENT '下次调度时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of xxl_job_info
-- ----------------------------
BEGIN;
INSERT INTO `xxl_job_info` VALUES (1, 1, '测试任务1', '2018-11-03 22:21:31', '2018-11-03 22:21:31', 'XXL', '', 'CRON', '0 0 0 * * ? *', 'DO_NOTHING', 'FIRST', 'demoJobHandler', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2018-11-03 22:21:31', '', 0, 0, 0);
INSERT INTO `xxl_job_info` VALUES (2, 2, '更新地图', '2024-01-22 12:43:50', '2024-01-22 16:25:12', 'li', 'li@qq.com', 'CRON', '0 0/1 * * * ?', 'DO_NOTHING', 'FIRST', 'GeoBuildTask', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2024-01-22 12:43:50', '', 1, 1708658100000, 1708658160000);
INSERT INTO `xxl_job_info` VALUES (3, 3, '平台间同步站点桩数据', '2024-01-22 12:46:26', '2024-01-22 12:46:41', 'li', 'li@qq.com', 'CRON', '0 0/10 * * * ?', 'DO_NOTHING', 'FIRST', 'stationSyncTask', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2024-01-22 12:46:26', '', 1, 1708657800000, 1708658400000);
INSERT INTO `xxl_job_info` VALUES (4, 4, '踢出离线桩', '2024-01-26 17:51:47', '2024-01-26 17:56:57', 'li', '', 'CRON', '0 0/1 * * * ?', 'DO_NOTHING', 'FIRST', 'UnliveRemoveTask', '', 'SERIAL_EXECUTION', 0, 0, 'BEAN', '', 'GLUE代码初始化', '2024-01-26 17:51:47', '', 1, 1708658100000, 1708658160000);
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_lock
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_lock`;
CREATE TABLE `xxl_job_lock` (
  `lock_name` varchar(50) NOT NULL COMMENT '锁名称',
  PRIMARY KEY (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of xxl_job_lock
-- ----------------------------
BEGIN;
INSERT INTO `xxl_job_lock` VALUES ('schedule_lock');
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_log
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_log`;
CREATE TABLE `xxl_job_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `job_group` int NOT NULL COMMENT '执行器主键ID',
  `job_id` int NOT NULL COMMENT '任务，主键ID',
  `executor_address` varchar(255) DEFAULT NULL COMMENT '执行器地址，本次执行的地址',
  `executor_handler` varchar(255) DEFAULT NULL COMMENT '执行器任务handler',
  `executor_param` varchar(512) DEFAULT NULL COMMENT '执行器任务参数',
  `executor_sharding_param` varchar(20) DEFAULT NULL COMMENT '执行器任务分片参数，格式如 1/2',
  `executor_fail_retry_count` int NOT NULL DEFAULT '0' COMMENT '失败重试次数',
  `trigger_time` datetime DEFAULT NULL COMMENT '调度-时间',
  `trigger_code` int NOT NULL COMMENT '调度-结果',
  `trigger_msg` text COMMENT '调度-日志',
  `handle_time` datetime DEFAULT NULL COMMENT '执行-时间',
  `handle_code` int NOT NULL COMMENT '执行-状态',
  `handle_msg` text COMMENT '执行-日志',
  `alarm_status` tinyint NOT NULL DEFAULT '0' COMMENT '告警状态：0-默认、1-无需告警、2-告警成功、3-告警失败',
  PRIMARY KEY (`id`),
  KEY `I_trigger_time` (`trigger_time`),
  KEY `I_handle_code` (`handle_code`)
) ENGINE=InnoDB AUTO_INCREMENT=21307 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of xxl_job_log
-- ----------------------------
BEGIN;
INSERT INTO `xxl_job_log` VALUES (18664, 2, 2, 'http://172.25.0.131:18087/', 'GeoBuildTask', '', NULL, 0, '2024-02-22 14:16:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.131:18087/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.131:18087/<br>code：200<br>msg：null', '2024-02-22 14:16:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18665, 2, 2, 'http://172.25.0.131:18087/', 'GeoBuildTask', '', NULL, 0, '2024-02-22 14:17:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.131:18087/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.131:18087/<br>code：200<br>msg：null', '2024-02-22 14:17:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18666, 4, 4, 'http://172.25.0.101:18090/', 'UnliveRemoveTask', '', NULL, 0, '2024-02-22 14:17:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.101:18090/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.101:18090/<br>code：200<br>msg：null', '2024-02-22 14:17:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18667, 2, 2, 'http://172.25.0.131:18087/', 'GeoBuildTask', '', NULL, 0, '2024-02-22 14:18:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.131:18087/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.131:18087/<br>code：200<br>msg：null', '2024-02-22 14:18:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18668, 4, 4, 'http://172.25.0.101:18090/', 'UnliveRemoveTask', '', NULL, 0, '2024-02-22 14:18:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.101:18090/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.101:18090/<br>code：200<br>msg：null', '2024-02-22 14:18:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18669, 4, 4, 'http://172.25.0.101:18090/', 'UnliveRemoveTask', '', NULL, 0, '2024-02-22 14:19:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.101:18090/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.101:18090/<br>code：200<br>msg：null', '2024-02-22 14:19:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18670, 2, 2, 'http://172.25.0.131:18087/', 'GeoBuildTask', '', NULL, 0, '2024-02-22 14:19:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.131:18087/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.131:18087/<br>code：200<br>msg：null', '2024-02-22 14:19:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18671, 4, 4, 'http://172.25.0.101:18090/', 'UnliveRemoveTask', '', NULL, 0, '2024-02-22 14:20:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.101:18090/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.101:18090/<br>code：200<br>msg：null', '2024-02-22 14:20:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18672, 3, 3, 'http://172.25.0.111:18085/', 'stationSyncTask', '', NULL, 0, '2024-02-22 14:20:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.111:18085/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.111:18085/<br>code：200<br>msg：null', '2024-02-22 14:20:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18673, 2, 2, 'http://172.25.0.131:18087/', 'GeoBuildTask', '', NULL, 0, '2024-02-22 14:20:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.131:18087/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.131:18087/<br>code：200<br>msg：null', '2024-02-22 14:20:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18674, 2, 2, 'http://172.25.0.131:18087/', 'GeoBuildTask', '', NULL, 0, '2024-02-22 14:21:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.131:18087/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.131:18087/<br>code：200<br>msg：null', '2024-02-22 14:21:00', 200, '', 0);
INSERT INTO `xxl_job_log` VALUES (18675, 4, 4, 'http://172.25.0.101:18090/', 'UnliveRemoveTask', '', NULL, 0, '2024-02-22 14:21:00', 200, '任务触发类型：Cron触发<br>调度机器：172.25.0.9<br>执行器-注册方式：自动注册<br>执行器-地址列表：[http://172.25.0.101:18090/]<br>路由策略：第一个<br>阻塞处理策略：单机串行<br>任务超时时间：0<br>失败重试次数：0<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>触发调度<<<<<<<<<<< </span><br>触发调度：<br>address：http://172.25.0.101:18090/<br>code：200<br>msg：null', '2024-02-22 14:21:00', 200, '', 0);
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_log_report
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_log_report`;
CREATE TABLE `xxl_job_log_report` (
  `id` int NOT NULL AUTO_INCREMENT,
  `trigger_day` datetime DEFAULT NULL COMMENT '调度-时间',
  `running_count` int NOT NULL DEFAULT '0' COMMENT '运行中-日志数量',
  `suc_count` int NOT NULL DEFAULT '0' COMMENT '执行成功-日志数量',
  `fail_count` int NOT NULL DEFAULT '0' COMMENT '执行失败-日志数量',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_trigger_day` (`trigger_day`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of xxl_job_log_report
-- ----------------------------
BEGIN;
INSERT INTO `xxl_job_log_report` VALUES (1, '2023-12-27 00:00:00', 0, 0, 0, NULL);
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_logglue
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_logglue`;
CREATE TABLE `xxl_job_logglue` (
  `id` int NOT NULL AUTO_INCREMENT,
  `job_id` int NOT NULL COMMENT '任务，主键ID',
  `glue_type` varchar(50) DEFAULT NULL COMMENT 'GLUE类型',
  `glue_source` mediumtext COMMENT 'GLUE源代码',
  `glue_remark` varchar(128) NOT NULL COMMENT 'GLUE备注',
  `add_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for xxl_job_registry
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_registry`;
CREATE TABLE `xxl_job_registry` (
  `id` int NOT NULL AUTO_INCREMENT,
  `registry_group` varchar(50) NOT NULL,
  `registry_key` varchar(255) NOT NULL,
  `registry_value` varchar(255) NOT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `i_g_k_v` (`registry_group`,`registry_key`,`registry_value`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of xxl_job_registry
-- ----------------------------
BEGIN;
INSERT INTO `xxl_job_registry` VALUES (40, 'EXECUTOR', 'omind-scheduler-mp', 'http://172.25.0.131:18087/', '2024-02-23 11:14:46');
INSERT INTO `xxl_job_registry` VALUES (41, 'EXECUTOR', 'omind-scheduler-baseplat', 'http://172.25.0.101:18090/', '2024-02-23 11:14:55');
INSERT INTO `xxl_job_registry` VALUES (42, 'EXECUTOR', 'omind-scheduler-userplat', 'http://172.25.0.111:18085/', '2024-02-23 11:14:56');
COMMIT;

-- ----------------------------
-- Table structure for xxl_job_user
-- ----------------------------
DROP TABLE IF EXISTS `xxl_job_user`;
CREATE TABLE `xxl_job_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(50) NOT NULL COMMENT '密码',
  `role` tinyint NOT NULL COMMENT '角色：0-普通用户、1-管理员',
  `permission` varchar(255) DEFAULT NULL COMMENT '权限：执行器ID列表，多个逗号分割',
  PRIMARY KEY (`id`),
  UNIQUE KEY `i_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of xxl_job_user
-- ----------------------------
BEGIN;
INSERT INTO `xxl_job_user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
