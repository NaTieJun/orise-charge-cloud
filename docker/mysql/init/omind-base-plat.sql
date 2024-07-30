/*
 Navicat Premium Data Transfer

 Source Server         : 抢占式-preemptively.trytowish.cn
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : preemptively.trytowish.cn:3306
 Source Schema         : omind-base-plat

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 23/02/2024 11:36:20
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for plat_connector_realtime_data
-- ----------------------------
DROP TABLE IF EXISTS `plat_connector_realtime_data`;
CREATE TABLE `plat_connector_realtime_data` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增字段',
  `trade_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '交易流水号32位；',
  `connector_id` varchar(26) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '充电设备接口编码，同一运营商内唯一；组织机构9位+桩14+枪3，补0',
  `pile_no` varchar(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '桩编号',
  `gun_no` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '枪编号',
  `state` tinyint(1) DEFAULT '0' COMMENT '0离线 1故障 2空闲 3充电',
  `gun_state` tinyint(1) DEFAULT '2' COMMENT '枪是否归位 0否 1是 2未知',
  `gun_link` tinyint(1) DEFAULT '0' COMMENT '是否插枪 0否 1是',
  `out_voltage` decimal(10,1) DEFAULT '0.0' COMMENT '输出电压 精确到小数后一位；待机置零',
  `out_current` decimal(10,1) DEFAULT '0.0' COMMENT '输出电流 精确到小数后一位；待机置零',
  `gunline_temp` int DEFAULT '0' COMMENT '枪线温度 偏移量-50 待机置0',
  `gunline_no` bigint DEFAULT '0' COMMENT '枪线编码 8位bin码',
  `soc` decimal(6,2) DEFAULT '0.00' COMMENT '0-100 剩余电量',
  `battery_max_temp` int DEFAULT '0' COMMENT '电池组最高温度 偏移量-50 待机置0',
  `total_charge_durant` int DEFAULT '0' COMMENT '累计充电时间 单位Min，待机置零',
  `remain_charge_durant` int DEFAULT '0' COMMENT '剩余充电时间 单位Min，待机置零',
  `charge_kwh` decimal(10,4) DEFAULT '0.0000' COMMENT '充电读数，精确到小数点后4位；待机置零',
  `lose_kwh` decimal(10,4) DEFAULT '0.0000' COMMENT '计损充电读书，精确到小数点后四位;待机置零 未设置计损比例时等于充电度数',
  `charge_money` decimal(10,4) DEFAULT '0.0000' COMMENT '精确到小数点后四位;待机置零 (电费+服务费)*计损充电度数',
  `hd_error` tinyint DEFAULT '0' COMMENT '硬件故障，位运算解析',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `plat_connector_realtime_data_trade_no_IDX` (`trade_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3460337 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='基础平台数据——充电接口实时监测数据表';

-- ----------------------------
-- Table structure for plat_trade_record
-- ----------------------------
DROP TABLE IF EXISTS `plat_trade_record`;
CREATE TABLE `plat_trade_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `trade_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '交易流水号',
  `pile_no` varchar(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '桩编号',
  `gun_no` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '枪编号',
  `start_time` datetime NOT NULL COMMENT '开始充电时间',
  `end_time` datetime NOT NULL COMMENT '本次采样时间',
  `sharp_per_price` decimal(10,5) NOT NULL DEFAULT '0.00000' COMMENT '尖单价',
  `sharp_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '尖电量',
  `sharp_all_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '记损尖电量',
  `sharp_price` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '尖金额',
  `peak_per_price` decimal(10,5) NOT NULL DEFAULT '0.00000' COMMENT '峰单价',
  `peak_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '峰电量',
  `peak_all_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '计损峰电量',
  `peak_price` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '峰金额',
  `flat_per_price` decimal(10,5) NOT NULL DEFAULT '0.00000' COMMENT '平单价',
  `flat_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '平电量',
  `flat_all_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '计损平电量',
  `flat_price` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '平金额',
  `valley_per_price` decimal(10,5) NOT NULL DEFAULT '0.00000' COMMENT '谷单价',
  `valley_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '谷电量',
  `valley_all_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '计损谷电量',
  `valley_price` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '谷金额',
  `start_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '电表总起值',
  `end_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '电报总止值',
  `final_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '总电量',
  `final_all_kwh` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '总计损电量',
  `cost` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '消费金额',
  `vin` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '车辆VIN码',
  `trade_type` tinyint NOT NULL DEFAULT '0' COMMENT '交易标识 0x01app启动 0x02卡启动 0x04离线卡启动 0x05 vin码启动充电',
  `trade_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '交易日期、时间',
  `stop_type` int NOT NULL DEFAULT '0' COMMENT '停止原因 ',
  `system_card_no` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '物理卡号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10456 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for sys_charge_order
-- ----------------------------
DROP TABLE IF EXISTS `sys_charge_order`;
CREATE TABLE `sys_charge_order` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `start_charge_seq` varchar(27) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '充电订单号：运营商ID+唯一编号 27个字符； 组织机构9位+id18位',
  `operator_id` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '运营商ID',
  `trade_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '交易流水号（基础平台和桩的规则 32位 16位BCD）',
  `start_charge_seq_stat` tinyint NOT NULL DEFAULT '5' COMMENT '充电订单状态；1启动中 2充电中 3停止中 4已结束 5未知',
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关联充电站ID，运营商自定义的唯一编码 小于等于20字符',
  `connector_id` varchar(26) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '充电设备接口编码',
  `connector_status` int NOT NULL DEFAULT '1' COMMENT '1空闲 2占用（未充电） 3占用（充电中） 4占用（预约锁定） 255故障',
  `current_a` decimal(10,1) NOT NULL DEFAULT '0.0' COMMENT 'A相电流 单位A 默认0，含直流（输出）',
  `current_b` decimal(10,1) DEFAULT '0.0' COMMENT 'B相电流',
  `current_c` decimal(10,1) DEFAULT '0.0' COMMENT 'C相电流',
  `voltage_a` decimal(10,1) NOT NULL DEFAULT '0.0' COMMENT 'A相电压 单位V 默认0，含直流（输出）',
  `voltage_b` decimal(10,1) DEFAULT '0.0' COMMENT 'B相电压',
  `voltage_c` decimal(10,1) DEFAULT '0.0' COMMENT 'C相电压',
  `soc` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '电池剩余电量0-1.00',
  `start_time` datetime NOT NULL COMMENT '开始充电时间',
  `end_time` datetime DEFAULT NULL COMMENT '最新采样时间',
  `total_power` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '累计充电量（度）',
  `elec_money` decimal(10,2) DEFAULT '0.00' COMMENT '累计电费（元）',
  `service_money` decimal(10,2) DEFAULT '0.00' COMMENT '累计服务费（元）',
  `total_money` decimal(10,2) DEFAULT '0.00' COMMENT '累计总金额（元）',
  `sum_period` tinyint DEFAULT '0' COMMENT '时段数0-32；实际约定为1-24时段，小时为单位',
  `fail_reason` int DEFAULT '0' COMMENT '故障原因 0无 1此设备不存在 2此设备离线 3设备已停止充电 4-99自定义（参考12.1 充电停止原因代码表）',
  `car_vin` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'vin码',
  `sync_flag` tinyint(1) DEFAULT '0' COMMENT '订单结束后是否已和用户平台同步 0否 1是',
  `report_gov` tinyint(1) DEFAULT '0' COMMENT '是否已上报至市政平台',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标记',
  `plate_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '车牌号',
  `phone_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '手机号',
  `price_info` varchar(3200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '下单时计价',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sys_charge_order_trade_no_IDX` (`trade_no`) USING BTREE,
  KEY `sys_charge_order_start_charge_seq_IDX` (`start_charge_seq`) USING BTREE,
  KEY `sys_charge_order_connector_id_IDX` (`connector_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='充电订单信息表';

-- ----------------------------
-- Records of sys_charge_order
-- ----------------------------
BEGIN;
INSERT INTO `sys_charge_order` VALUES (1, 'MA01D1QR8946840178218250240', 'MA01D1QR8', '24110108100001012401221605550001', 2, '11010810001', '2411010810000101', 3, 226.1, 0.0, 0.0, 144.5, 0.0, 0.0, 0.3500, '2024-01-22 16:05:55', '2024-01-22 16:07:47', 2.76, 3.32, 0.00, 3.32, 0, 0, '', 0, 0, '2024-01-22 16:05:56', '2024-01-22 16:07:47', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (2, 'MA01D1QR8946840862661554176', 'MA01D1QR8', '24110108100001012401221608380001', 2, '11010810001', '2411010810000101', 3, 77.1, 0.0, 0.0, 13.6, 0.0, 0.0, 0.0900, '2024-01-22 16:08:38', '2024-01-22 16:08:58', 0.69, 0.83, 0.00, 0.83, 0, 0, '', 0, 0, '2024-01-22 16:08:38', '2024-01-22 16:08:58', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (3, 'MA01D1QR8946841013052518400', 'MA01D1QR8', '24110108100001012401221609140001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.3200, '2024-01-22 16:09:14', '2024-01-22 16:09:42', 0.54, 0.11, 0.33, 0.44, 1, 65, 'TRLTOZLGSYQFN0FSD', 100, 0, '2024-01-22 16:09:14', '2024-01-22 16:09:43', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (4, 'MA01D1QR8946841334579474432', 'MA01D1QR8', '24110108100001012401221610310001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.2200, '2024-01-22 16:10:31', '2024-01-22 16:10:49', 0.91, 0.18, 0.55, 0.73, 1, 65, 'P0VHYL9SK7OV7CWP6', 100, 0, '2024-01-22 16:10:31', '2024-01-22 16:10:49', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (5, 'MA01D1QR8946842207078592512', 'MA01D1QR8', '24110108100001012401221613590001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1500, '2024-01-22 16:13:59', '2024-01-22 16:14:03', 0.39, 0.08, 0.24, 0.32, 1, 65, 'MYMJBMNSRDTQH6XEL', 100, 0, '2024-01-22 16:13:59', '2024-01-22 16:14:04', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (6, 'MA01D1QR8946843452543610880', 'MA01D1QR8', '24110108100001012401221618560001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.3400, '2024-01-22 16:18:56', '2024-01-22 16:19:00', 0.33, 0.07, 0.20, 0.27, 1, 65, 'ET4CJDNCERSQQ0CDR', 100, 0, '2024-01-22 16:18:56', '2024-01-22 16:19:01', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (7, 'MA01D1QR8946843832212008960', 'MA01D1QR8', '24110108100001012401221620260001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.3700, '2024-01-22 16:20:26', '2024-01-22 16:20:56', 0.88, 0.18, 0.53, 0.71, 1, 65, 'PV6SPTVF3QCB33SVM', 100, 0, '2024-01-22 16:20:26', '2024-01-22 16:20:57', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (8, 'MA01D1QR8946844054275239936', 'MA01D1QR8', '24110108100001012401221621190001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.2800, '2024-01-22 16:21:19', '2024-01-22 16:31:10', 14.36, 2.88, 8.62, 11.50, 1, 65, 'JHETYPH7RTC6VMR8R', 100, 0, '2024-01-22 16:21:19', '2024-01-22 16:31:11', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (9, 'MA01D1QR8947188467878866944', 'MA01D1QR8', '24110108100003012401231509540001', 4, '11010810001', '2411010810000301', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.3600, '2024-01-23 15:09:54', '2024-01-23 16:22:57', 103.72, 20.76, 62.24, 83.00, 2, 65, 'VGM75EF9GAPXCQNLW', 100, 0, '2024-01-23 15:09:55', '2024-01-23 16:22:57', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (10, 'MA01D1QR8947189336695386112', 'MA01D1QR8', '24110108100001012401231513210001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.2500, '2024-01-23 15:13:21', '2024-01-23 15:17:47', 6.09, 1.22, 3.66, 4.88, 1, 65, 'B6QI6A93QV47WCKC5', 100, 0, '2024-01-23 15:13:21', '2024-01-23 15:17:48', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (11, 'MA01D1QR8947192333093580800', 'MA01D1QR8', '24110108100001012401231525150001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.4100, '2024-01-23 15:25:15', '2024-01-23 16:11:14', 64.99, 13.00, 39.00, 52.00, 2, 65, 'RRVGT36FBMZEPJYZG', 100, 0, '2024-01-23 15:25:15', '2024-01-23 16:11:15', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (12, 'MA01D1QR8947203928091799552', 'MA01D1QR8', '24110108100001012401231611200001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.3400, '2024-01-23 16:11:20', '2024-01-23 16:12:29', 1.82, 0.37, 1.10, 1.47, 1, 65, 'OG8IRJUMQVVV4UJU9', 100, 0, '2024-01-23 16:11:20', '2024-01-23 16:12:30', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (13, 'MA01D1QR8947204568423608320', 'MA01D1QR8', '24110108100001012401231613530001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0400, '2024-01-23 16:13:53', '2024-01-23 16:15:51', 2.98, 0.60, 1.79, 2.39, 1, 65, '62PRASO3EQRSZUDW5', 100, 0, '2024-01-23 16:13:53', '2024-01-23 16:15:51', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (14, 'MA01D1QR8947205667801018368', 'MA01D1QR8', '24110108100001012401231618150001', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.4500, '2024-01-23 16:18:15', '2024-01-23 18:02:19', 147.70, 29.55, 88.63, 118.18, 3, 65, 'DAWM1BZPAFLTFO0M1', 100, 0, '2024-01-23 16:18:15', '2024-01-23 18:02:20', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (15, 'MA01D1QR8947208916234940416', 'MA01D1QR8', '24110108100003012401231631090001', 4, '11010810001', '2411010810000301', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5100, '2024-01-23 16:31:09', '2024-01-23 16:42:27', 16.77, 3.35, 10.06, 13.42, 1, 65, 'Y5B0X9H44FFFCAMBH', 100, 0, '2024-01-23 16:31:09', '2024-01-23 16:42:28', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (16, 'MA01D1QR8947212789846650880', 'MA01D1QR8', '24110108100003012401231646330001', 4, '11010810001', '2411010810000301', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.4600, '2024-01-23 16:46:33', '2024-01-23 16:47:45', 1.75, 0.35, 1.05, 1.40, 1, 65, '0BUKVDUUDDBDFBTRJ', 100, 0, '2024-01-23 16:46:33', '2024-01-23 16:47:45', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (17, 'MA01D1QR8947216729963245568', 'MA01D1QR8', '24110108100003012401231702120001', 4, '11010810001', '2411010810000301', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.4300, '2024-01-23 17:02:12', '2024-01-23 17:17:02', 21.20, 4.24, 12.72, 16.96, 1, 65, 'RO1LU3EJOLR2O70MK', 100, 0, '2024-01-23 17:02:12', '2024-01-23 17:17:04', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (18, 'MA01D1QR8947223384251707392', 'MA01D1QR8', '24110108100003012401231728390001', 4, '11010810001', '2411010810000301', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1900, '2024-01-23 17:28:39', '2024-01-23 17:28:50', 0.50, 0.10, 0.30, 0.40, 1, 65, '35DTQJWYHOVJDBNYF', 100, 0, '2024-01-23 17:28:39', '2024-01-23 17:28:50', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (19, 'MA01D1QR8947227111180218368', 'MA01D1QR8', '24110108100003012401231743270001', 4, '11010810001', '2411010810000301', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.4100, '2024-01-23 17:43:27', '2024-01-23 17:43:36', 0.50, 0.10, 0.30, 0.40, 1, 65, 'IOVHAVC2MH14WYBVX', 100, 0, '2024-01-23 17:43:27', '2024-01-23 17:43:37', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (20, 'MA01D1QR8947230968924876800', 'MA01D1QR8', '24110108100003012401231758470001', 4, '11010810001', '2411010810000301', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.4500, '2024-01-23 17:58:47', '2024-01-23 17:59:23', 1.11, 0.23, 0.67, 0.90, 1, 65, 'KEYJMGLPANIGHSDZN', 100, 0, '2024-01-23 17:58:47', '2024-01-23 17:59:24', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (21, 'MA01D1QR8947234996954083328', 'MA01D1QR8', '24110108100003012401231814470001', 4, '11010810001', '2411010810000301', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.2700, '2024-01-23 18:14:47', '2024-01-23 18:14:58', 0.29, 0.06, 0.18, 0.24, 1, 65, 'BIVOBJNZOFMTVQHU9', 100, 0, '2024-01-23 18:14:47', '2024-01-23 18:14:59', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"SevicePrice\":\"0.6000\",\"PriceType\":3},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.5000\",\"PriceType\":1},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"SevicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"SevicePrice\":\"0.5000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (22, 'MA01D1QR8948313966218850304', 'MA01D1QR8', 'MA01D1QR8948313966218850304', 2, '11010810001', '2411010810000101', 3, 159.0, 0.0, 0.0, 242.6, 0.0, 0.0, 0.1500, '2024-01-26 17:42:14', '2024-01-26 17:42:57', 0.76, 1.53, 0.00, 1.53, 0, 0, '', 100, 0, '2024-01-26 17:42:14', '2024-01-26 17:42:57', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"SevicePrice\":\"1.0000\",\"PriceType\":0}]', '000000');
INSERT INTO `sys_charge_order` VALUES (23, 'MA01D1QR8949288193096953856', 'MA01D1QR8', 'MA01D1QR8949288193096953856', 2, '11010810001', '2411010810000101', 3, 195.7, 0.0, 0.0, 67.9, 0.0, 0.0, 0.0300, '2024-01-29 10:13:28', '2024-01-29 10:17:09', 5.32, 10.65, 0.00, 10.65, 0, 0, '', 100, 0, '2024-01-29 10:13:28', '2024-01-29 10:17:09', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"SevicePrice\":\"1.0000\",\"PriceType\":0}]', '000000');
INSERT INTO `sys_charge_order` VALUES (24, 'MA01D1QR8949307066349268992', 'MA01D1QR8', 'MA01D1QR8949307066349268992', 2, '11010810001', '2411010810000101', 3, 96.3, 0.0, 0.0, 157.3, 0.0, 0.0, 0.1300, '2024-01-29 11:28:27', '2024-01-29 11:33:04', 6.62, 9.26, 0.00, 9.26, 0, 0, '', 100, 0, '2024-01-29 11:28:27', '2024-01-29 11:33:04', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (25, 'MA01D1QR8957366544634228736', 'MA01D1QR8', 'MA01D1QR8957366544634228736', 2, '11010810001', '2411010810000101', 3, 134.8, 0.0, 0.0, 200.3, 0.0, 0.0, 0.2300, '2024-02-20 17:13:57', '2024-02-20 17:24:49', 15.38, 16.91, 0.00, 16.91, 0, 0, '', 0, 0, '2024-02-20 17:13:57', '2024-02-20 17:24:49', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (26, 'MA01D1QR8957367470648471552', 'MA01D1QR8', 'MA01D1QR8957367470648471552', 2, '11010810001', '2411010810000201', 3, 166.8, 0.0, 0.0, 227.1, 0.0, 0.0, 0.3000, '2024-02-20 17:17:37', '2024-02-20 17:24:57', 10.65, 11.72, 0.00, 11.72, 0, 0, '', 0, 0, '2024-02-20 17:17:37', '2024-02-20 17:24:57', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (27, 'MA01D1QR8957369457985204224', 'MA01D1QR8', 'MA01D1QR8957369457985204224', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.9700, '2024-02-20 17:25:31', '2024-02-20 21:04:12', 307.21, 123.07, 92.32, 215.39, 5, 0, '', 100, 0, '2024-02-20 17:25:31', '2024-02-20 21:04:12', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (28, 'MA01D1QR8957627081653760000', 'MA01D1QR8', 'MA01D1QR8957627081653760000', 2, '11010810001', '2411010810000201', 3, 115.5, 0.0, 0.0, 346.5, 0.0, 0.0, 0.4100, '2024-02-21 10:29:13', '2024-02-21 11:58:57', 125.49, 148.99, 0.00, 148.99, 0, 0, '', 0, 0, '2024-02-21 10:29:13', '2024-02-21 11:58:57', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (29, 'MA01D1QR8957649799107325952', 'MA01D1QR8', 'MA01D1QR8957649799107325952', 2, '11010810001', '2411010810000101', 3, 129.7, 0.0, 0.0, 254.2, 0.0, 0.0, 0.6100, '2024-02-21 11:59:30', '2024-02-21 12:15:19', 22.19, 31.07, 0.00, 31.07, 0, 0, '', 0, 0, '2024-02-21 11:59:30', '2024-02-21 12:15:19', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (30, 'MA01D1QR8957650366298861568', 'MA01D1QR8', 'MA01D1QR8957650366298861568', 2, '11010810001', '2411010810000201', 3, 19.2, 0.0, 0.0, 108.9, 0.0, 0.0, 0.5200, '2024-02-21 12:01:45', '2024-02-21 12:15:49', 20.05, 28.07, 0.00, 28.07, 0, 0, '', 0, 0, '2024-02-21 12:01:45', '2024-02-21 12:15:49', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (31, 'MA01D1QR8957651485897011200', 'MA01D1QR8', 'MA01D1QR8957651485897011200', 2, '11010810001', '2411010810000301', 3, 60.4, 0.0, 0.0, 132.3, 0.0, 0.0, 0.6000, '2024-02-21 12:06:12', '2024-02-21 12:15:31', 12.97, 18.16, 0.00, 18.16, 0, 0, '', 0, 0, '2024-02-21 12:06:12', '2024-02-21 12:15:31', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (32, 'MA01D1QR8957653938163953664', 'MA01D1QR8', 'MA01D1QR8957653938163953664', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5600, '2024-02-21 12:15:56', '2024-02-21 12:16:13', 0.20, 0.09, 0.07, 0.16, 1, 1, '', 100, 0, '2024-02-21 12:15:56', '2024-02-21 12:16:13', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (33, 'MA01D1QR8957656880858279936', 'MA01D1QR8', 'MA01D1QR8957656880858279936', 4, '11010810001', '2411010810000301', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.9700, '2024-02-21 12:27:38', '2024-02-21 15:00:45', 216.53, 86.84, 65.13, 151.97, 4, 0, '', 100, 0, '2024-02-21 12:27:38', '2024-02-21 15:00:45', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (34, 'MA01D1QR8957681099843317760', 'MA01D1QR8', 'MA01D1QR8957681099843317760', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0800, '2024-02-21 14:03:52', '2024-02-21 14:04:07', 0.39, 0.16, 0.12, 0.28, 1, 1, '', 100, 0, '2024-02-21 14:03:52', '2024-02-21 14:04:08', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (35, 'MA01D1QR8957681292630306816', 'MA01D1QR8', 'MA01D1QR8957681292630306816', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.2200, '2024-02-21 14:04:38', '2024-02-21 14:05:04', 0.52, 0.22, 0.16, 0.38, 1, 1, '', 100, 0, '2024-02-21 14:04:38', '2024-02-21 14:05:04', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (36, 'MA01D1QR8958077727880261632', 'MA01D1QR8', 'MA01D1QR8958077727880261632', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.6900, '2024-02-22 16:19:56', '2024-02-22 16:48:24', 39.27, 15.86, 11.90, 27.76, 1, 1, '', 100, 0, '2024-02-22 16:19:56', '2024-02-22 16:48:24', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (37, 'MA01D1QR8958086206674186240', 'MA01D1QR8', 'MA01D1QR8958086206674186240', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.1600, '2024-02-22 16:53:37', '2024-02-22 16:59:17', 8.10, 3.25, 2.44, 5.69, 1, 1, '', 100, 0, '2024-02-22 16:53:37', '2024-02-22 16:59:17', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (38, 'MA01D1QR8958087712089583616', 'MA01D1QR8', 'MA01D1QR8958087712089583616', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.6200, '2024-02-22 16:59:36', '2024-02-22 17:11:44', 17.17, 7.05, 5.29, 12.34, 2, 1, '', 100, 0, '2024-02-22 16:59:36', '2024-02-22 17:11:44', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (39, 'MA01D1QR8958091711073234944', 'MA01D1QR8', 'MA01D1QR8958091711073234944', 4, '11010810001', '2411010810000101', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.6000, '2024-02-22 17:15:30', '2024-02-22 17:15:39', 0.38, 0.16, 0.12, 0.28, 1, 1, '', 100, 0, '2024-02-22 17:15:30', '2024-02-22 17:15:39', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
INSERT INTO `sys_charge_order` VALUES (40, 'MA01D1QR8958345544768696320', 'MA01D1QR8', 'MA01D1QR8958345544768696320', 4, '11010810001', '2411010810000301', 2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.4800, '2024-02-23 10:04:08', '2024-02-23 10:04:24', 0.47, 0.20, 0.15, 0.35, 1, 1, '', 100, 0, '2024-02-23 10:04:08', '2024-02-23 10:04:24', 0, '', '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"SevicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"SevicePrice\":\"0.8000\",\"PriceType\":1},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"SevicePrice\":\"0.4000\",\"PriceType\":2}]', '000000');
COMMIT;

-- ----------------------------
-- Table structure for sys_charge_order_item
-- ----------------------------
DROP TABLE IF EXISTS `sys_charge_order_item`;
CREATE TABLE `sys_charge_order_item` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `start_charge_seq` varchar(27) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '充电订单号',
  `start_time` datetime NOT NULL COMMENT '开始时间 yyyy-MM-dd HH:mm:ss',
  `end_time` datetime NOT NULL COMMENT '结束时间 yyyy-MM-dd HH:mm:ss',
  `elec_price` decimal(10,4) DEFAULT '0.0000' COMMENT '时段电价 小数点后4位',
  `service_price` decimal(10,4) DEFAULT '0.0000' COMMENT '时段服务费价格 小数点后4位',
  `power` decimal(10,2) DEFAULT '0.00' COMMENT '时段充电量 度 小数点后2位',
  `elec_money` decimal(10,2) DEFAULT '0.00' COMMENT '时段电费 小数点后2位',
  `service_money` decimal(10,2) DEFAULT '0.00' COMMENT '时段服务费 小数点后2位',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `sys_charge_order_item_start_charge_seq_IDX` (`start_charge_seq`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='充电订单计费信息明细表（分时段数组）';

-- ----------------------------
-- Records of sys_charge_order_item
-- ----------------------------
BEGIN;
INSERT INTO `sys_charge_order_item` VALUES (1, 'MA01D1QR8946841013052518400', '2024-01-22 16:09:14', '2024-01-22 16:09:34', 0.2000, 0.6000, 0.54, 0.11, 0.33, '000000');
INSERT INTO `sys_charge_order_item` VALUES (2, 'MA01D1QR8946841334579474432', '2024-01-22 16:10:31', '2024-01-22 16:10:34', 0.2000, 0.6000, 0.91, 0.18, 0.55, '000000');
INSERT INTO `sys_charge_order_item` VALUES (3, 'MA01D1QR8946842207078592512', '2024-01-22 16:13:59', '2024-01-22 16:14:02', 0.2000, 0.6000, 0.39, 0.08, 0.24, '000000');
INSERT INTO `sys_charge_order_item` VALUES (4, 'MA01D1QR8946843452543610880', '2024-01-22 16:18:56', '2024-01-22 16:18:59', 0.2000, 0.6000, 0.33, 0.07, 0.20, '000000');
INSERT INTO `sys_charge_order_item` VALUES (5, 'MA01D1QR8946843832212008960', '2024-01-22 16:20:26', '2024-01-22 16:20:45', 0.2000, 0.6000, 0.88, 0.18, 0.53, '000000');
INSERT INTO `sys_charge_order_item` VALUES (6, 'MA01D1QR8946844054275239936', '2024-01-22 16:21:19', '2024-01-22 16:31:10', 0.2000, 0.6000, 14.36, 2.88, 8.62, '000000');
INSERT INTO `sys_charge_order_item` VALUES (7, 'MA01D1QR8947189336695386112', '2024-01-23 15:13:21', '2024-01-23 15:17:42', 0.2000, 0.6000, 6.09, 1.22, 3.66, '000000');
INSERT INTO `sys_charge_order_item` VALUES (8, 'MA01D1QR8947192333093580800', '2024-01-23 15:25:15', '2024-01-23 15:59:59', 0.2000, 0.6000, 48.95, 9.79, 29.37, '000000');
INSERT INTO `sys_charge_order_item` VALUES (9, 'MA01D1QR8947192333093580800', '2024-01-23 16:00:00', '2024-01-23 16:11:07', 0.2000, 0.6000, 16.05, 3.21, 9.63, '000000');
INSERT INTO `sys_charge_order_item` VALUES (10, 'MA01D1QR8947203928091799552', '2024-01-23 16:11:20', '2024-01-23 16:12:24', 0.2000, 0.6000, 1.82, 0.37, 1.10, '000000');
INSERT INTO `sys_charge_order_item` VALUES (11, 'MA01D1QR8947204568423608320', '2024-01-23 16:13:53', '2024-01-23 16:15:43', 0.2000, 0.6000, 2.98, 0.60, 1.79, '000000');
INSERT INTO `sys_charge_order_item` VALUES (12, 'MA01D1QR8947188467878866944', '2024-01-23 15:09:54', '2024-01-23 15:59:59', 0.2000, 0.6000, 70.56, 14.12, 42.34, '000000');
INSERT INTO `sys_charge_order_item` VALUES (13, 'MA01D1QR8947188467878866944', '2024-01-23 16:00:00', '2024-01-23 16:22:46', 0.2000, 0.6000, 33.16, 6.64, 19.90, '000000');
INSERT INTO `sys_charge_order_item` VALUES (14, 'MA01D1QR8947208916234940416', '2024-01-23 16:31:09', '2024-01-23 16:42:13', 0.2000, 0.6000, 16.77, 3.35, 10.06, '000000');
INSERT INTO `sys_charge_order_item` VALUES (15, 'MA01D1QR8947212789846650880', '2024-01-23 16:46:33', '2024-01-23 16:47:36', 0.2000, 0.6000, 1.75, 0.35, 1.05, '000000');
INSERT INTO `sys_charge_order_item` VALUES (16, 'MA01D1QR8947216729963245568', '2024-01-23 17:02:12', '2024-01-23 17:16:34', 0.2000, 0.6000, 21.20, 4.24, 12.72, '000000');
INSERT INTO `sys_charge_order_item` VALUES (17, 'MA01D1QR8947223384251707392', '2024-01-23 17:28:39', '2024-01-23 17:28:42', 0.2000, 0.6000, 0.50, 0.10, 0.30, '000000');
INSERT INTO `sys_charge_order_item` VALUES (18, 'MA01D1QR8947227111180218368', '2024-01-23 17:43:27', '2024-01-23 17:43:31', 0.2000, 0.6000, 0.50, 0.10, 0.30, '000000');
INSERT INTO `sys_charge_order_item` VALUES (19, 'MA01D1QR8947230968924876800', '2024-01-23 17:58:47', '2024-01-23 17:59:22', 0.2000, 0.6000, 1.11, 0.23, 0.67, '000000');
INSERT INTO `sys_charge_order_item` VALUES (20, 'MA01D1QR8947205667801018368', '2024-01-23 16:18:15', '2024-01-23 16:59:59', 0.2000, 0.6000, 58.18, 11.64, 34.91, '000000');
INSERT INTO `sys_charge_order_item` VALUES (21, 'MA01D1QR8947205667801018368', '2024-01-23 17:00:00', '2024-01-23 17:59:59', 0.2000, 0.6000, 86.05, 17.21, 51.63, '000000');
INSERT INTO `sys_charge_order_item` VALUES (22, 'MA01D1QR8947205667801018368', '2024-01-23 18:00:00', '2024-01-23 18:01:52', 0.2000, 0.6000, 3.48, 0.70, 2.09, '000000');
INSERT INTO `sys_charge_order_item` VALUES (23, 'MA01D1QR8947234996954083328', '2024-01-23 18:14:47', '2024-01-23 18:14:51', 0.2000, 0.6000, 0.29, 0.06, 0.18, '000000');
INSERT INTO `sys_charge_order_item` VALUES (24, 'MA01D1QR8948313966218850304', '2024-01-26 17:42:14', '2024-01-26 17:42:47', 1.0000, 1.0000, 0.77, 0.77, 0.77, '000000');
INSERT INTO `sys_charge_order_item` VALUES (25, 'MA01D1QR8949288193096953856', '2024-01-29 10:13:28', '2024-01-29 10:17:03', 1.0000, 1.0000, 5.33, 5.33, 5.33, '000000');
INSERT INTO `sys_charge_order_item` VALUES (26, 'MA01D1QR8949307066349268992', '2024-01-29 11:28:27', '2024-01-29 11:33:04', 0.4000, 0.3000, 6.62, 2.65, 1.99, '000000');
INSERT INTO `sys_charge_order_item` VALUES (27, 'MA01D1QR8957369457985204224', '2024-02-20 17:25:31', '2024-02-20 17:59:59', 0.4000, 0.3000, 48.74, 19.50, 14.63, '000000');
INSERT INTO `sys_charge_order_item` VALUES (28, 'MA01D1QR8957369457985204224', '2024-02-20 18:00:00', '2024-02-20 18:59:59', 0.4000, 0.3000, 85.50, 34.20, 25.65, '000000');
INSERT INTO `sys_charge_order_item` VALUES (29, 'MA01D1QR8957369457985204224', '2024-02-20 19:00:00', '2024-02-20 19:59:59', 0.4000, 0.3000, 83.94, 33.58, 25.19, '000000');
INSERT INTO `sys_charge_order_item` VALUES (30, 'MA01D1QR8957369457985204224', '2024-02-20 20:00:00', '2024-02-20 20:59:59', 0.4000, 0.3000, 84.25, 33.70, 25.28, '000000');
INSERT INTO `sys_charge_order_item` VALUES (31, 'MA01D1QR8957369457985204224', '2024-02-20 21:00:00', '2024-02-20 21:03:44', 0.4000, 0.3000, 5.22, 2.09, 1.57, '000000');
INSERT INTO `sys_charge_order_item` VALUES (32, 'MA01D1QR8957653938163953664', '2024-02-21 12:15:56', '2024-02-21 12:15:59', 0.4000, 0.3000, 0.21, 0.09, 0.07, '000000');
INSERT INTO `sys_charge_order_item` VALUES (33, 'MA01D1QR8957681099843317760', '2024-02-21 14:03:52', '2024-02-21 14:03:55', 0.4000, 0.3000, 0.39, 0.16, 0.12, '000000');
INSERT INTO `sys_charge_order_item` VALUES (34, 'MA01D1QR8957681292630306816', '2024-02-21 14:04:38', '2024-02-21 14:05:00', 0.4000, 0.3000, 0.53, 0.22, 0.16, '000000');
INSERT INTO `sys_charge_order_item` VALUES (35, 'MA01D1QR8957656880858279936', '2024-02-21 12:27:38', '2024-02-21 12:59:59', 0.4000, 0.3000, 45.73, 18.30, 13.72, '000000');
INSERT INTO `sys_charge_order_item` VALUES (36, 'MA01D1QR8957656880858279936', '2024-02-21 13:00:00', '2024-02-21 13:59:59', 0.4000, 0.3000, 85.25, 34.10, 25.58, '000000');
INSERT INTO `sys_charge_order_item` VALUES (37, 'MA01D1QR8957656880858279936', '2024-02-21 14:00:00', '2024-02-21 14:59:59', 0.4000, 0.3000, 84.64, 33.86, 25.40, '000000');
INSERT INTO `sys_charge_order_item` VALUES (38, 'MA01D1QR8957656880858279936', '2024-02-21 15:00:00', '2024-02-21 15:00:17', 0.4000, 0.3000, 1.43, 0.58, 0.43, '000000');
INSERT INTO `sys_charge_order_item` VALUES (39, 'MA01D1QR8958077727880261632', '2024-02-22 16:19:56', '2024-02-22 16:48:01', 0.4000, 0.3000, 39.64, 15.86, 11.90, '000000');
INSERT INTO `sys_charge_order_item` VALUES (40, 'MA01D1QR8958086206674186240', '2024-02-22 16:53:37', '2024-02-22 16:59:11', 0.4000, 0.3000, 8.11, 3.25, 2.44, '000000');
INSERT INTO `sys_charge_order_item` VALUES (41, 'MA01D1QR8958087712089583616', '2024-02-22 16:59:36', '2024-02-22 16:59:59', 0.4000, 0.3000, 0.60, 0.24, 0.18, '000000');
INSERT INTO `sys_charge_order_item` VALUES (42, 'MA01D1QR8958087712089583616', '2024-02-22 17:00:00', '2024-02-22 17:11:26', 0.4000, 0.3000, 17.01, 6.81, 5.11, '000000');
INSERT INTO `sys_charge_order_item` VALUES (43, 'MA01D1QR8958091711073234944', '2024-02-22 17:15:30', '2024-02-22 17:15:32', 0.4000, 0.3000, 0.38, 0.16, 0.12, '000000');
INSERT INTO `sys_charge_order_item` VALUES (44, 'MA01D1QR8958345544768696320', '2024-02-23 10:04:08', '2024-02-23 10:04:11', 0.4000, 0.3000, 0.48, 0.20, 0.15, '000000');
COMMIT;

-- ----------------------------
-- Table structure for sys_connector
-- ----------------------------
DROP TABLE IF EXISTS `sys_connector`;
CREATE TABLE `sys_connector` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `connector_id` varchar(26) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '充电设备接口编码，同一运营商内唯一；9位组织机构编码+14桩编码+3枪号',
  `equipment_id` varchar(23) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关联的设备ID，对同一运营商保证唯一',
  `gun_no` tinyint unsigned DEFAULT '0' COMMENT '枪号',
  `connector_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '充电设备接口名称',
  `connector_type` tinyint NOT NULL DEFAULT '0' COMMENT '1家用插座 2交流接口插座 3交流接口插头 4直流接口枪头 5无线充电座 6其他',
  `voltage_upper_limits` int NOT NULL DEFAULT '0' COMMENT '额定电压上限',
  `voltage_lower_limits` int NOT NULL DEFAULT '0' COMMENT '额定电压下限',
  `current_value` int DEFAULT '0' COMMENT '额定电流',
  `power` decimal(10,1) DEFAULT '0.0' COMMENT '额定功率',
  `park_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '车位号',
  `national_standard` tinyint NOT NULL DEFAULT '0' COMMENT '国家标准 1:2011 2:2015',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0离网 1空闲 2占用（未充电）3占用（充电）4占用（预约锁定）255故障',
  `park_status` tinyint unsigned DEFAULT '0' COMMENT '0未知 10空闲 50占用',
  `lock_status` tinyint unsigned DEFAULT '0' COMMENT '0未知 10已解锁 50已上锁',
  `price_code` int unsigned DEFAULT '0' COMMENT '价格模版ID',
  `state` tinyint unsigned DEFAULT '0' COMMENT '0正常 1故障',
  `ping_tm` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后一次心跳时间，超过40秒视为离线',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by_id` bigint DEFAULT '0' COMMENT '创建人ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by_id` bigint DEFAULT '0' COMMENT '更新人ID',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标记',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `connector_id` (`connector_id`) USING BTREE COMMENT '充电接口编号索引',
  KEY `sys_connector_equipment_id_IDX` (`equipment_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='充电设备接口信息表';

-- ----------------------------
-- Records of sys_connector
-- ----------------------------
BEGIN;
INSERT INTO `sys_connector` VALUES (1, '2411010810000101', '24110108100001', 1, '快充01', 4, 960, 180, 120, 120.0, 'A001', 2, 2, 0, 0, 0, 0, '2024-02-23 11:36:20', NULL, 1, '2024-02-23 11:36:20', NULL, 0, '000000');
INSERT INTO `sys_connector` VALUES (2, '2411010810000201', '24110108100002', 1, '快充02', 4, 180, 960, 120, 120.0, 'A002', 2, 2, 0, 0, 0, 0, '2024-02-23 11:36:20', NULL, 1, '2024-02-23 11:36:20', NULL, 0, '000000');
INSERT INTO `sys_connector` VALUES (3, '2411010810000301', '24110108100003', 1, '快充03', 4, 180, 960, 120, 120.0, 'A003', 2, 0, 0, 0, 0, 0, '2024-02-23 10:28:15', NULL, 1, '2024-02-23 10:28:15', NULL, 0, '000000');
INSERT INTO `sys_connector` VALUES (4, '2411010810000401', '24110108100004', 1, '快充04', 4, 180, 960, 120, 120.0, 'A004', 2, 0, 0, 0, 0, 0, '2024-02-19 09:22:16', NULL, 1, '2024-02-19 09:22:16', NULL, 0, '000000');
INSERT INTO `sys_connector` VALUES (5, '2411010810000501', '24110108100005', 1, '快充05', 4, 180, 960, 120, 120.0, 'A005', 2, 0, 0, 0, 0, 0, '2024-02-19 09:22:16', NULL, 1, '2024-02-19 09:22:16', NULL, 0, '000000');
INSERT INTO `sys_connector` VALUES (6, '2411010810000601', '24110108100006', 1, '快充06', 4, 180, 960, 120, 120.0, 'A006', 2, 0, 0, 0, 0, 0, '2024-02-19 09:22:16', NULL, 1, '2024-02-19 09:22:16', NULL, 0, '000000');
COMMIT;

-- ----------------------------
-- Table structure for sys_equipment
-- ----------------------------
DROP TABLE IF EXISTS `sys_equipment`;
CREATE TABLE `sys_equipment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `equipment_id` varchar(23) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备唯一编码，对同一运营商保证唯一；9组织机构+14桩编号',
  `pile_no` varchar(14) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '桩编号',
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '关联充电站ID，运营商自定义的唯一编码 小于等于20字符',
  `price_code` bigint unsigned DEFAULT '0' COMMENT '价格模版ID',
  `manufacturer_id` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '设备生产商组织机构代码',
  `manufacturer_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '设备生产商名称',
  `equipment_model` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '设备型号',
  `production_date` datetime DEFAULT NULL COMMENT '设备生产日期',
  `equipment_type` tinyint NOT NULL COMMENT '1直流设备 2交流设备 3交直流一体设备 4无线设备 5其他',
  `equipment_lng` decimal(10,6) DEFAULT NULL COMMENT '充电设备经度',
  `equipment_lat` decimal(10,6) DEFAULT NULL COMMENT '充电设备纬度',
  `power` decimal(10,1) NOT NULL DEFAULT '0.0' COMMENT '充电设备总功率kW 保留小数点后1位',
  `max_power` decimal(3,2) unsigned DEFAULT '1.00' COMMENT '充电桩最大允许输出功率 30%-100% 1Bin表示1%',
  `is_working` tinyint unsigned DEFAULT '0' COMMENT '是否启用 0 正常工作 1停止使用锁定',
  `sync_tm` datetime DEFAULT NULL COMMENT '最近对时时间',
  `equipment_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '充电设备名称',
  `net_type` tinyint unsigned DEFAULT '3' COMMENT '0、sim卡 1、LAN 2、WAN 3其他',
  `m_operator` tinyint(1) DEFAULT '4' COMMENT '0移动 2电信 3联通 4其他',
  `online_tm` datetime DEFAULT NULL COMMENT '最近上线时间（登录验证）',
  `serv_ip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '连接主机IP',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by_id` bigint DEFAULT '0' COMMENT '创建人ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by_id` bigint DEFAULT '0' COMMENT '更新人ID',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标记',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `equipment_id` (`equipment_id`) USING BTREE COMMENT '充电设备编号索引',
  KEY `sys_equipment_station_id_IDX` (`station_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='充电设备信息表';

-- ----------------------------
-- Records of sys_equipment
-- ----------------------------
BEGIN;
INSERT INTO `sys_equipment` VALUES (1, '24110108100001', '24110108100001', '11010810001', 0, 'MA01D1QR8', '奥陌科技', 'A1', '2024-01-05 16:18:17', 1, 0.000000, 0.000000, 120.0, 1.00, 0, NULL, 'A1直流桩', 0, 0, '2024-01-23 15:08:52', '172.25.0.122', '2024-01-05 16:18:04', 1, '2024-02-22 16:19:21', NULL, 0, '000000');
INSERT INTO `sys_equipment` VALUES (2, '24110108100002', '24110108100002', '11010810001', 0, 'MA01D1QR8', '奥陌科技', 'A1', NULL, 1, 0.000000, 0.000000, 120.0, 1.00, 0, NULL, 'A2直流桩', 0, 0, '2024-01-23 16:56:09', '172.25.0.122', '2024-01-05 16:19:11', 1, '2024-02-23 10:27:19', 1, 0, '000000');
INSERT INTO `sys_equipment` VALUES (3, '24110108100003', '24110108100003', '11010810001', 0, 'MA01D1QR8', '奥陌科技', 'A1', NULL, 1, 0.000000, 0.000000, 120.0, 1.00, 0, NULL, 'A3直流桩', 0, 0, '2024-01-23 14:54:27', '172.25.0.122', '2024-01-05 16:19:14', 1, '2024-02-23 10:27:23', 1, 0, '000000');
INSERT INTO `sys_equipment` VALUES (4, '24110108100004', '24110108100004', '11010810001', 0, 'MA01D1QR8', '奥陌科技', 'A1', NULL, 1, 0.000000, 0.000000, 120.0, 1.00, 0, NULL, 'A4直流桩', 0, 0, '2024-01-24 12:19:51', '172.25.0.122', '2024-01-05 16:19:16', 1, '2024-02-23 10:27:27', 1, 0, '000000');
INSERT INTO `sys_equipment` VALUES (5, '24110108100005', '24110108100005', '11010810001', 0, 'MA01D1QR8', '奥陌科技', 'A1', NULL, 1, 0.000000, 0.000000, 120.0, 1.00, 0, NULL, 'A5直流桩', 0, 0, '2024-01-23 15:03:18', '172.25.0.122', '2024-01-05 16:19:20', 1, '2024-02-23 10:27:31', 1, 0, '000000');
INSERT INTO `sys_equipment` VALUES (6, '24110108100006', '24110108100006', '11010810001', 0, 'MA01D1QR8', '奥陌科技', 'A1', NULL, 1, 0.000000, 0.000000, 120.0, 1.00, 0, NULL, 'A6直流桩', 0, 0, '2024-01-23 17:01:17', '172.25.0.122', '2024-01-05 16:19:24', 1, '2024-02-23 10:27:34', 1, 0, '000000');
COMMIT;

-- ----------------------------
-- Table structure for sys_operator
-- ----------------------------
DROP TABLE IF EXISTS `sys_operator`;
CREATE TABLE `sys_operator` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `operator_id` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '运营商ID（组织机构代码）',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '机构全称',
  `operator_tel_1` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '运营商电话1',
  `operator_tel_2` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '运营商电话2',
  `operator_reg_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '运营商注册地址',
  `operator_note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注信息',
  `host` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '运营商国标接口http请求前缀，以/结尾',
  `operator_secret` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '运营商密钥0-F字符组成,可采用32H、48H、64H',
  `data_secret` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '消息密钥',
  `data_secret_iv` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '消息密钥初始化向量 固定16位 用户AES加密过程的混合加密',
  `sig_secret` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '签名密钥0-F字符组成,可采用32H、48H、64H 为签名的加密密钥',
  `my_operator_id` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户平台我方组织机构编码',
  `user_operator_secret` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户平台密钥',
  `user_data_secret` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户平台消息密钥',
  `user_data_secret_iv` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户平台消息密钥初始化向量',
  `user_sig_secret` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户平台签名密钥',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by_id` bigint DEFAULT '0' COMMENT '创建人ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by_id` bigint DEFAULT '0' COMMENT '更新人ID',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标记',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `operator_id` (`operator_id`) USING BTREE COMMENT '运营商组织机构索引'
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='基础设施运营商信息表';

-- ----------------------------
-- Records of sys_operator
-- ----------------------------
BEGIN;
INSERT INTO `sys_operator` VALUES (4, 'MA01D1QR8', '北京奥陌科技有限公司', '13800000000', '13800000000', '北京市海淀区中关村大街18号11层1126-158', '', 'http://172.25.0.111:9805/omind/evcs/v1/', 'e3bb15a6f95f8bb7', '37ruITzn5kL0hFZG', 'idPU4cFo4MAn2JEL', '57fd20184e19ca9a', 'MA80GFQM8', 'o6gpvB9uo87UBHGy', '7sEQg8caT0kpAUNO', 'l9JC1bDWFLidvdv8', 'RnKZnW2RxxuXPOIz', '2024-01-05 15:55:38', NULL, '2024-01-05 15:55:38', NULL, 0, '000000');
INSERT INTO `sys_operator` VALUES (5, 'MA01D1QWW', '运营商一', '180000000000', '', '', '', '', '8b1bb8291ec0875e', '1FnnXhQ18wj9cpf7', 'u6buSEdrhmkZ1cje', 'c05c2389e9a49bbf', 'MA01D1QWW', '', '', '', '', NULL, NULL, NULL, NULL, 0, '000000');
COMMIT;

-- ----------------------------
-- Table structure for sys_price
-- ----------------------------
DROP TABLE IF EXISTS `sys_price`;
CREATE TABLE `sys_price` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `price_code` bigint unsigned DEFAULT '0' COMMENT '价格模版ID，0为默认价格',
  `start_time` datetime NOT NULL COMMENT '时段起始时间点 6位 HHmmss',
  `price_type` tinyint unsigned NOT NULL COMMENT '0尖1峰2平3谷',
  `elec_price` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '时段电费（小数点后4位）',
  `service_price` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '小数点后4位',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标记',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  `main_point` tinyint(1) DEFAULT '0' COMMENT '主行',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sys_connector_price_price_code_IDX` (`price_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of sys_price
-- ----------------------------
BEGIN;
INSERT INTO `sys_price` VALUES (1, 0, '2024-01-05 00:00:57', 3, 0.2000, 0.6000, NULL, '默认模版', 0, '000000', 1);
INSERT INTO `sys_price` VALUES (2, 0, '2024-01-05 06:00:22', 2, 0.5000, 0.5000, NULL, '', 0, '000000', 0);
INSERT INTO `sys_price` VALUES (3, 0, '2024-01-05 10:00:00', 1, 0.6000, 0.5000, NULL, '', 0, '000000', 0);
INSERT INTO `sys_price` VALUES (4, 0, '2024-01-05 18:00:00', 0, 0.8000, 0.4000, NULL, '', 0, '000000', 0);
INSERT INTO `sys_price` VALUES (5, 0, '2024-01-05 22:00:00', 2, 0.5000, 0.5000, NULL, '', 0, '000000', 0);
INSERT INTO `sys_price` VALUES (6, 3, '2000-02-01 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 13:05:51', '模板2', 0, '000000', 1);
INSERT INTO `sys_price` VALUES (7, 4, '2000-02-01 00:00:00', 0, 0.4000, 0.3000, '2024-01-29 10:23:59', '模板3', 0, '000000', 1);
INSERT INTO `sys_price` VALUES (8, 4, '2000-02-01 09:00:00', 1, 0.6000, 0.8000, '2024-01-29 10:23:59', '', 0, '000000', 0);
INSERT INTO `sys_price` VALUES (9, 4, '2000-02-01 13:00:00', 2, 0.7000, 0.4000, '2024-01-29 10:23:59', '', 0, '000000', 0);
INSERT INTO `sys_price` VALUES (10, 4, '2000-02-02 00:00:00', 3, 0.5000, 0.5000, '2024-01-29 10:23:59', '', 0, '000000', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_station
-- ----------------------------
DROP TABLE IF EXISTS `sys_station`;
CREATE TABLE `sys_station` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '充电站ID，运营商自定义的唯一编码 小于等于20字符',
  `operator_id` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '运营商ID',
  `equipment_owner_id` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '设备所属方ID，设备所属运营平台组织机构代码',
  `station_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '充电站名称',
  `country_code` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '充电站国家代码',
  `area_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '充电站省市辖区编码 20个字符',
  `address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '详细地址',
  `station_tel` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '站点电话 小于30字符',
  `service_tel` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '服务电话 小于30字符',
  `station_type` int NOT NULL DEFAULT '255' COMMENT '1公共 50个人 100公交 101环卫 102物流 103出租车 255其他',
  `station_status` tinyint NOT NULL DEFAULT '0' COMMENT '0未知 1建设中 5关闭下线 6维护中 50正常使用',
  `park_nums` int NOT NULL DEFAULT '0' COMMENT '车位数量 0未知',
  `station_lng` decimal(10,6) NOT NULL DEFAULT '0.000000' COMMENT '经度 保留小数点后6位',
  `station_lat` decimal(10,6) NOT NULL DEFAULT '0.000000' COMMENT '纬度 保留小数点后6位',
  `site_guide` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '站点引导',
  `construction` int DEFAULT '0' COMMENT '建设场所',
  `pictures` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '充电设备照片、充电车位照片、停车场入口照片，字符串数组',
  `match_cars` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '使用车型描述，描述该站点接受的车大小及类型，如大车、物流车、私家乘用车、出租车等',
  `park_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '车位楼层及数量描述，车位楼层以及数量信息',
  `business_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '营业时间描述',
  `create_by_id` bigint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by_id` bigint DEFAULT '0',
  `update_time` datetime DEFAULT NULL,
  `del_flag` tinyint(1) DEFAULT '0',
  `electricity_fee` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '充电费率',
  `service_fee` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '服务费率',
  `park_fee` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '停车费',
  `payment` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '支付方式：刷卡、线上、现金。其中电子钱包类卡为刷卡，身份鉴权卡】微信/支付宝、APP为线上',
  `support_order` tinyint DEFAULT '0' COMMENT '充电设备是否需要提前预约后才能使用。0为不支持预约；1为支持预约。默认0',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
  `report_gov_flag` tinyint unsigned DEFAULT '0' COMMENT '是否需要上报市政平台（0否 其他为平台编号）',
  `report_gov` tinyint(1) DEFAULT '0' COMMENT '是否已上报至政府平台',
  `is_alone_apply` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否独立报装 0否 1是；如果是独立报桩，需要填写户号以及容量',
  `account_number` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '户号：国网电费账单户号(小于20字符)',
  `capacity` decimal(20,4) NOT NULL DEFAULT '0.0000' COMMENT '容量：独立电表申请的功率（保留小数点后4位）',
  `is_public_parking_lot` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是公共停车场库0否1是；如果是则需要填写场库编号',
  `parking_lot_number` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '公共停车场库编号(小于20字符)',
  `open_all_day` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否全天开放0否1是',
  `park_free` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否停车免费0否1是',
  `unit_flag` int NOT NULL DEFAULT '0' COMMENT '二进制，每一位代表一个信号量，从低位开始，1位卫生间，2位便利店，3位餐厅，4位休息室，5位雨棚，6位小票即，7位道闸，8位地锁',
  `min_electricity_price` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '最低充电电费率',
  `park_fee_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0停车收费1停车免费2限时免费3充电限免',
  `subsidy_per_kwh` decimal(5,2) NOT NULL DEFAULT '0.00' COMMENT '每度电综合补贴单价',
  `subsidy_year_max` decimal(16,4) NOT NULL DEFAULT '0.0000' COMMENT '站点年最大补贴金额',
  `subsidy_operator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '订单补贴，空：不补贴；1全补贴；运营商ID，补贴，多运营商逗号分隔',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `station_id` (`station_id`) USING BTREE COMMENT '充电站ID索引'
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='充电站信息表';

-- ----------------------------
-- Records of sys_station
-- ----------------------------
BEGIN;
INSERT INTO `sys_station` VALUES (1, '11010810001', 'MA01D1QR8', 'MA01D1QR8', '海淀区001充电站', '86', '110108', '中关村大街', '13900000000', '13900000000', 0, 50, 0, 116.315792, 39.984553, '地下一层', 1, '[\"http://172.25.0.15:9000/ruoyi/2024/01/26/c855d51293b8453baa8c00019b891fe7.jpeg\"]', '小轿车', '收费15元/小时', '1', 1, '2024-01-05 16:00:04', 1, '2024-01-26 17:40:14', 0, '1', '1', '', '微信', 0, '', 0, 0, 0, '', 0.0000, 0, '', 0, 0, 0, 0.0000, 0, 0.00, 0.0000, '', '000000');
INSERT INTO `sys_station` VALUES (2, '11010810002', 'MA01D1QR8', 'MA01D1QR8', '海淀区002充电站', '86', '110108', '中关村大街', '13900000000', '13900000000', 0, 50, 0, 116.416792, 39.987553, '地下一层', 0, '[]', '小轿车', '收费15元/小时', '', 1, '2024-01-05 16:00:35', 1, '2024-01-05 16:00:35', 0, '', '', '', '微信', 0, '', 0, 0, 0, '', 0.0000, 0, '', 0, 0, 0, 0.0000, 0, 0.00, 0.0000, '', '000000');
INSERT INTO `sys_station` VALUES (3, '11010810003', 'MA01D1QR8', 'MA01D1QR8', '海淀区003充电站', '86', '110108', '中关村大街', '13900000000', '13900000000', 0, 50, 0, 116.516792, 39.987553, '地下一层', 0, '[]', '小轿车', '收费15元/小时', '', 1, '2024-01-05 16:00:50', 1, '2024-01-05 16:00:50', 0, '', '', '', '微信', 0, '', 0, 0, 0, '', 0.0000, 0, '', 0, 0, 0, 0.0000, 0, 0.00, 0.0000, '', '000000');
COMMIT;

-- ----------------------------
-- Table structure for sys_station_operator_link
-- ----------------------------
DROP TABLE IF EXISTS `sys_station_operator_link`;
CREATE TABLE `sys_station_operator_link` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '站点ID',
  `operator_id` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '运营商ID',
  `is_sync_trade` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0否 1是 是否强制同步全量订单数据，主要应用与地方补贴场景',
  `is_enable` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '0不启用 1启用',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by_id` bigint DEFAULT '0' COMMENT '创建人ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `update_by_id` bigint DEFAULT '0' COMMENT '更新人ID',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标记',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sys_station_operator_link_station_id_IDX` (`station_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Records of sys_station_operator_link
-- ----------------------------
BEGIN;
INSERT INTO `sys_station_operator_link` VALUES (1, '11010810001', 'MA01D1QR8', 0, 1, '', '2024-01-05 16:00:04', 1, '2024-01-23 11:13:28', 1, 2, '000000');
INSERT INTO `sys_station_operator_link` VALUES (2, '11010810002', 'MA01D1QR8', 0, 1, '', '2024-01-05 16:00:35', 1, '2024-01-05 16:00:35', 1, 0, '000000');
INSERT INTO `sys_station_operator_link` VALUES (3, '11010810003', 'MA01D1QR8', 0, 1, '', '2024-01-05 16:00:50', 1, NULL, NULL, 0, '000000');
INSERT INTO `sys_station_operator_link` VALUES (4, '11010810001', 'MA01D1QR8', 0, 1, '', '2024-01-23 11:13:35', 1, '2024-01-23 11:13:35', 1, 0, '000000');
COMMIT;

-- ----------------------------
-- Table structure for sys_station_price
-- ----------------------------
DROP TABLE IF EXISTS `sys_station_price`;
CREATE TABLE `sys_station_price` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电站id(运营商自定义的唯一编码)',
  `price_code` bigint unsigned NOT NULL DEFAULT '0' COMMENT '价格id',
  `price_type` int unsigned NOT NULL DEFAULT '0' COMMENT '0 默认类型 1 仅限快充 2仅限慢充 ',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `is_use` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否正在启用0 否 1是',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `create_by_id` bigint DEFAULT '0' COMMENT '创建人ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by_id` bigint DEFAULT '0' COMMENT '更新人ID',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `station_id` (`station_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点-价格模型关联表';

-- ----------------------------
-- Records of sys_station_price
-- ----------------------------
BEGIN;
INSERT INTO `sys_station_price` VALUES (1, '11010810001', 0, 0, '', 0, 0, 0, '2024-01-05 16:41:35', 0, '2024-01-05 16:41:35', '000000');
INSERT INTO `sys_station_price` VALUES (2, '11010810002', 0, 0, '', 0, 0, 0, '2024-01-05 16:41:49', 0, '2024-01-05 16:41:49', '000000');
INSERT INTO `sys_station_price` VALUES (3, '11010810003', 0, 0, '', 0, 0, 0, '2024-01-05 16:41:57', 0, '2024-01-05 16:41:57', '000000');
INSERT INTO `sys_station_price` VALUES (4, '11010810002', 3, 0, '模板1', 0, 0, 1, '2024-01-22 18:04:58', 1, '2024-01-22 18:04:58', '000000');
INSERT INTO `sys_station_price` VALUES (5, '11010810001', 3, 0, '模板1', 0, 0, 1, '2024-01-22 18:18:20', 1, '2024-01-22 18:18:20', '000000');
INSERT INTO `sys_station_price` VALUES (6, '11010810001', 0, 0, '默认模版', 0, 0, 1, '2024-01-22 18:22:18', 1, '2024-01-22 18:22:18', '000000');
INSERT INTO `sys_station_price` VALUES (7, '11010810002', 0, 0, '默认模版', 1, 0, 1, '2024-01-22 18:25:10', 1, '2024-01-22 18:25:10', '000000');
INSERT INTO `sys_station_price` VALUES (8, '11010810003', 3, 0, '模板1', 0, 0, 1, '2024-01-22 18:39:28', 1, '2024-01-22 18:39:28', '000000');
INSERT INTO `sys_station_price` VALUES (9, '11010810003', 0, 0, '默认模版', 0, 0, 1, '2024-01-22 18:40:14', 1, '2024-01-22 18:40:14', '000000');
INSERT INTO `sys_station_price` VALUES (10, '11010810003', 3, 0, '模板1', 0, 0, 1, '2024-01-22 18:40:46', 1, '2024-01-22 18:40:46', '000000');
INSERT INTO `sys_station_price` VALUES (11, '11010810003', 0, 0, '默认模版', 0, 0, 1, '2024-01-22 18:40:59', 1, '2024-01-22 18:40:59', '000000');
INSERT INTO `sys_station_price` VALUES (12, '11010810003', 3, 0, '模板1', 0, 0, 1, '2024-01-22 18:55:39', 1, '2024-01-22 18:55:39', '000000');
INSERT INTO `sys_station_price` VALUES (13, '11010810003', 0, 0, '默认模版', 0, 0, 1, '2024-01-22 18:57:36', 1, '2024-01-22 18:57:36', '000000');
INSERT INTO `sys_station_price` VALUES (14, '11010810003', 3, 0, '模板1', 0, 0, 1, '2024-01-22 19:03:45', 1, '2024-01-22 19:03:45', '000000');
INSERT INTO `sys_station_price` VALUES (15, '11010810003', 0, 0, '默认模版', 0, 0, 1, '2024-01-22 19:03:57', 1, '2024-01-22 19:03:57', '000000');
INSERT INTO `sys_station_price` VALUES (16, '11010810003', 3, 0, '模板1', 0, 0, 1, '2024-01-22 19:04:18', 1, '2024-01-22 19:04:18', '000000');
INSERT INTO `sys_station_price` VALUES (17, '11010810003', 0, 0, '默认模版', 0, 0, 1, '2024-01-22 19:04:36', 1, '2024-01-22 19:04:36', '000000');
INSERT INTO `sys_station_price` VALUES (18, '11010810003', 3, 0, '模板1', 0, 0, 1, '2024-01-22 19:04:49', 1, '2024-01-22 19:04:49', '000000');
INSERT INTO `sys_station_price` VALUES (19, '11010810003', 0, 0, '默认模版', 0, 0, 1, '2024-01-22 19:05:57', 1, '2024-01-22 19:05:57', '000000');
INSERT INTO `sys_station_price` VALUES (20, '11010810003', 3, 0, '模板1', 0, 0, 1, '2024-01-22 20:44:16', 1, '2024-01-22 20:44:16', '000000');
INSERT INTO `sys_station_price` VALUES (21, '11010810003', 0, 0, '默认模版', 0, 0, 1, '2024-01-22 20:44:23', 1, '2024-01-22 20:44:23', '000000');
INSERT INTO `sys_station_price` VALUES (22, '11010810003', 3, 0, '模板1', 0, 0, 1, '2024-01-22 20:44:30', 1, '2024-01-22 20:44:30', '000000');
INSERT INTO `sys_station_price` VALUES (23, '11010810003', 0, 0, '默认模版', 1, 0, 1, '2024-01-22 20:44:34', 1, '2024-01-22 20:44:34', '000000');
INSERT INTO `sys_station_price` VALUES (24, '11010810001', 3, 0, '模板1', 0, 0, 1, '2024-01-22 21:04:10', 1, '2024-01-22 21:04:10', '000000');
INSERT INTO `sys_station_price` VALUES (25, '11010810001', 0, 0, '默认模版', 0, 0, 1, '2024-01-22 21:04:17', 1, '2024-01-22 21:04:17', '000000');
INSERT INTO `sys_station_price` VALUES (26, '11010810001', 3, 0, '模板1', 0, 0, 1, '2024-01-23 11:12:55', 1, '2024-01-23 11:12:55', '000000');
INSERT INTO `sys_station_price` VALUES (27, '11010810001', 0, 0, '默认模版', 0, 0, 1, '2024-01-23 11:12:59', 1, '2024-01-23 11:12:59', '000000');
INSERT INTO `sys_station_price` VALUES (28, '11010810001', 3, 0, '模板2', 0, 0, 1, '2024-01-24 18:14:44', 1, '2024-01-24 18:14:44', '000000');
INSERT INTO `sys_station_price` VALUES (29, '11010810001', 0, 0, '默认模版', 0, 0, 1, '2024-01-24 18:15:04', 1, '2024-01-24 18:15:04', '000000');
INSERT INTO `sys_station_price` VALUES (30, '11010810001', 3, 0, '模板2', 0, 0, 1, '2024-01-24 18:16:49', 1, '2024-01-24 18:16:49', '000000');
INSERT INTO `sys_station_price` VALUES (31, '11010810001', 4, 0, '模板3', 1, 0, 1, '2024-01-29 10:24:23', 1, '2024-01-29 10:24:23', '000000');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
