/*
 Navicat Premium Data Transfer

 Source Server         : 抢占式-preemptively.trytowish.cn
 Source Server Type    : MySQL
 Source Server Version : 80031
 Source Host           : preemptively.trytowish.cn:3306
 Source Schema         : omind-user-plat

 Target Server Type    : MySQL
 Target Server Version : 80031
 File Encoding         : 65001

 Date: 23/02/2024 11:21:01
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for omind_app
-- ----------------------------
DROP TABLE IF EXISTS `omind_app`;
CREATE TABLE `omind_app` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `app_type` int unsigned NOT NULL DEFAULT '0' COMMENT '0 openAPI 1 奥升小程序',
  `app_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用名',
  `app_key` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用key',
  `secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '应用密钥',
  `valid_time` bigint unsigned NOT NULL DEFAULT '253402271999' COMMENT '有效期',
  `state` tinyint NOT NULL DEFAULT '0' COMMENT '启用标记0未启用 1启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of omind_app
-- ----------------------------
BEGIN;
INSERT INTO `omind_app` VALUES (1, 1, '奥陌小程序', 'dbkUZnQjnIsUW0n7', 'k1E4e6ssJhk5ZIUkD1yn65NEX8ZwFCTF', 253402271999, 1, '2023-12-18 10:04:48', '2024-01-18 10:05:59', 0);
COMMIT;

-- ----------------------------
-- Table structure for omind_bill
-- ----------------------------
DROP TABLE IF EXISTS `omind_bill`;
CREATE TABLE `omind_bill` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电站id(运营商自定义的唯一编码)',
  `base_operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '基础平台运营商id(组织机构代码)',
  `start_charge_seq` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '格式"运营商ID+唯一编号",27字符',
  `connector_id` varchar(26) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电设备接口编码(充电设备接口编码，同一运营商内唯一)',
  `start_charge_seq_stat` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '充电订单状态:1、启动中;2、充电中;3、停止中;4、已结束;5、未知;8、异常订单;20、已处理异常订单',
  `user_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '充电者用户id',
  `start_time` datetime DEFAULT NULL COMMENT '开始充电时间(格式"yyyy-MM-dd HH:mm:ss")',
  `end_time` datetime DEFAULT NULL COMMENT '结束充电时间(格式"yyyy-MM-dd HH:mm:ss")',
  `total_power` decimal(6,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '累计充电量(单位:度,小数点后2位)',
  `total_elec_money` decimal(6,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '总电费(单位:元,小数点后2位)',
  `total_service_money` decimal(6,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '总服务费(单位:元,小数点后2位)',
  `total_money` decimal(6,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '累计总金额(单位:元,小数点后2位)',
  `real_pay_money` decimal(6,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '实际支付总金额(单位:元,小数点后2位)',
  `stop_reason` int unsigned NOT NULL DEFAULT '0' COMMENT '充电结束原因:0、用户手动停止充电;1、客户归属地运营商平台停止充电;2、BMS停止充电;3、充电机设备故障;4、连接器断开;5-99、自定义',
  `stop_fail_reason` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '停止失败原因:0、无;1、此设备不存在;2、此设备离线;3、设备已停止充电;4~99、自定义',
  `sum_period` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '时段数N，范围：0～32',
  `charge_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '交易信息 json',
  `car_vin` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '车辆识别码',
  `plate_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '车牌号',
  `succ_stat` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '成功标识:0、成功;1、失败;',
  `bill_type` tinyint unsigned DEFAULT NULL COMMENT '订单类型 0扫码充电 1刷卡充电',
  `soc` decimal(6,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '电池剩余电量(默认:0)',
  `pay_state` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否支付:0、未支付;1、已支付;',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `price_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '价格信息 json',
  `pay_plat` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电平台',
  `transaction_id` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付交易号(如微信)',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  `currentA` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT 'A相电流(单位:A,默认:0 含直流(输出))',
  `voltageA` decimal(6,2) DEFAULT '0.00' COMMENT 'A相电压(单位:V,默认:0 含直流(输出))',
  `charge_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '充电类型 1、充满;2、按金额充电',
  `charge_money` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '用户预计充电金额',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `start_charge_seq` (`start_charge_seq`),
  KEY `connector_id` (`connector_id`),
  KEY `create_time` (`create_time`),
  KEY `station_id` (`station_id`),
  KEY `start_time` (`start_time`),
  KEY `end_time` (`end_time`)
) ENGINE=InnoDB AUTO_INCREMENT=11438 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电订单信息表';

-- ----------------------------
-- Records of omind_bill
-- ----------------------------
BEGIN;
INSERT INTO `omind_bill` VALUES (11408, '11010810001', 'MA80GFQM8', 'MA01D1QR8946841013052518400', '2411010810000101', 4, 2, '2024-01-22 16:09:15', '2024-01-22 16:09:42', 0.54, 0.11, 0.33, 0.44, 0.44, 65, 0, 1, NULL, 'TRLTOZLGSYQFN0FSD', '', 0, NULL, 32.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"SevicePrice\":\"1.0000\",\"PriceType\":2}]', '', '', 0, '2024-01-22 16:09:44', '2024-01-22 16:09:16', '000000', 0.00, 0.00, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11409, '11010810001', 'MA80GFQM8', 'MA01D1QR8946841334579474432', '2411010810000101', 4, 1, '2024-01-22 16:10:32', '2024-01-22 16:10:49', 0.44, 0.09, 0.27, 0.36, 0.36, 65, 0, 1, NULL, 'P0VHYL9SK7OV7CWP6', '', 0, NULL, 22.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"SevicePrice\":\"1.0000\",\"PriceType\":2}]', '', '', 0, '2024-01-22 16:10:49', '2024-01-22 16:10:32', '000000', 0.00, 0.00, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11410, '11010810001', 'MA80GFQM8', 'MA01D1QR8946842207078592512', '2411010810000101', 4, 1, '2024-01-22 16:14:00', '2024-01-22 16:14:03', 0.39, 0.08, 0.24, 0.32, 0.32, 65, 0, 1, NULL, 'MYMJBMNSRDTQH6XEL', '', 0, NULL, 15.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"SevicePrice\":\"1.0000\",\"PriceType\":2}]', '', '', 0, '2024-01-22 16:14:04', '2024-01-22 16:14:00', '000000', 0.00, 0.00, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11411, '11010810001', 'MA80GFQM8', 'MA01D1QR8946843452543610880', '2411010810000101', 4, 1, '2024-01-22 16:18:57', '2024-01-22 16:19:00', 0.33, 0.07, 0.20, 0.27, 0.27, 65, 0, 1, NULL, 'ET4CJDNCERSQQ0CDR', '', 0, NULL, 34.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"SevicePrice\":\"1.0000\",\"PriceType\":2}]', '', '', 0, '2024-01-22 16:19:01', '2024-01-22 16:18:57', '000000', 0.00, 0.00, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11412, '11010810001', 'MA80GFQM8', 'MA01D1QR8946843832212008960', '2411010810000101', 4, 1, '2024-01-22 16:20:27', '2024-01-22 16:20:56', 0.88, 0.18, 0.53, 0.71, 0.71, 65, 0, 1, NULL, 'PV6SPTVF3QCB33SVM', '', 0, NULL, 37.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"SevicePrice\":\"1.0000\",\"PriceType\":2}]', '', '', 0, '2024-01-22 16:20:57', '2024-01-22 16:20:28', '000000', 0.00, 0.00, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11413, '11010810001', 'MA80GFQM8', 'MA01D1QR8946844054275239936', '2411010810000101', 4, 1, '2024-01-22 16:21:20', '2024-01-22 16:31:10', 14.36, 2.88, 8.62, 11.50, 11.50, 65, 0, 1, NULL, 'JHETYPH7RTC6VMR8R', '', 0, NULL, 28.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"SevicePrice\":\"1.0000\",\"PriceType\":2}]', '', '', 0, '2024-01-22 16:31:11', '2024-01-22 16:21:21', '000000', 0.00, 0.00, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11414, '11010810001', 'MA80GFQM8', 'MA01D1QR8947188467878866944', '2411010810000301', 4, 3, '2024-01-23 15:09:56', '2024-01-23 16:22:57', 103.72, 20.76, 62.24, 83.00, 83.00, 65, 0, 2, NULL, 'VGM75EF9GAPXCQNLW', '', 0, NULL, 36.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 16:22:57', '2024-01-23 15:09:56', '000000', 249.10, 56.50, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11415, '11010810001', 'MA80GFQM8', 'MA01D1QR8947189336695386112', '2411010810000101', 4, 1, '2024-01-23 15:13:22', '2024-01-23 15:17:47', 6.09, 1.22, 3.66, 4.88, 4.88, 65, 0, 1, NULL, 'B6QI6A93QV47WCKC5', '', 0, NULL, 25.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 15:17:48', '2024-01-23 15:13:23', '000000', 0.00, 0.00, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11416, '11010810001', 'MA80GFQM8', 'MA01D1QR8947192333093580800', '2411010810000101', 4, 1, '2024-01-23 15:25:16', '2024-01-23 16:11:14', 65.00, 13.00, 39.00, 52.00, 52.00, 65, 0, 2, NULL, 'RRVGT36FBMZEPJYZG', '', 0, NULL, 41.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 16:11:15', '2024-01-23 15:25:17', '000000', 222.10, 40.40, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11417, '11010810001', 'MA80GFQM8', 'MA01D1QR8947203928091799552', '2411010810000101', 4, 1, '2024-01-23 16:11:21', '2024-01-23 16:12:29', 1.82, 0.37, 1.10, 1.47, 1.47, 65, 0, 1, NULL, 'OG8IRJUMQVVV4UJU9', '', 0, NULL, 34.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 16:12:30', '2024-01-23 16:11:21', '000000', 137.50, 181.80, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11418, '11010810001', 'MA80GFQM8', 'MA01D1QR8947204568423608320', '2411010810000101', 4, 1, '2024-01-23 16:13:54', '2024-01-23 16:15:51', 2.98, 0.60, 1.79, 2.39, 2.39, 65, 0, 1, NULL, '62PRASO3EQRSZUDW5', '', 0, NULL, 4.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 16:15:51', '2024-01-23 16:13:54', '000000', 210.60, 108.50, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11419, '11010810001', 'MA80GFQM8', 'MA01D1QR8947205667801018368', '2411010810000101', 4, 1, '2024-01-23 16:18:16', '2024-01-23 18:02:19', 147.71, 29.55, 88.63, 118.18, 118.18, 65, 0, 3, NULL, 'DAWM1BZPAFLTFO0M1', '', 0, NULL, 45.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 18:02:20', '2024-01-23 16:18:16', '000000', 182.30, 194.80, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11420, '11010810001', 'MA80GFQM8', 'MA01D1QR8947208916234940416', '2411010810000301', 4, 4, '2024-01-23 16:31:10', '2024-01-23 16:42:27', 16.41, 3.29, 9.85, 13.14, 13.14, 65, 0, 1, NULL, 'Y5B0X9H44FFFCAMBH', '', 0, NULL, 51.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 16:42:28', '2024-01-23 16:31:11', '000000', 338.70, 188.80, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11421, '11010810001', 'MA80GFQM8', 'MA01D1QR8947212789846650880', '2411010810000301', 4, 4, '2024-01-23 16:46:34', '2024-01-23 16:47:45', 1.75, 0.35, 1.05, 1.40, 1.40, 65, 0, 1, NULL, '0BUKVDUUDDBDFBTRJ', '', 0, NULL, 46.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 16:47:45', '2024-01-23 16:46:34', '000000', 322.80, 48.70, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11422, '11010810001', 'MA80GFQM8', 'MA01D1QR8947216729963245568', '2411010810000301', 4, 4, '2024-01-23 17:02:13', '2024-01-23 17:17:02', 20.92, 4.19, 12.56, 16.75, 16.75, 65, 0, 1, NULL, 'RO1LU3EJOLR2O70MK', '', 0, NULL, 42.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 17:17:04', '2024-01-23 17:02:14', '000000', 177.30, 61.40, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11423, '11010810001', 'MA80GFQM8', 'MA01D1QR8947223384251707392', '2411010810000301', 4, 4, '2024-01-23 17:28:40', '2024-01-23 17:28:50', 0.50, 0.10, 0.30, 0.40, 0.40, 65, 0, 1, NULL, '35DTQJWYHOVJDBNYF', '', 0, NULL, 19.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 17:28:51', '2024-01-23 17:28:40', '000000', 4.50, 24.20, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11424, '11010810001', 'MA80GFQM8', 'MA01D1QR8947227111180218368', '2411010810000301', 4, 4, '2024-01-23 17:43:28', '2024-01-23 17:43:36', 0.50, 0.10, 0.30, 0.40, 0.40, 65, 0, 1, NULL, 'IOVHAVC2MH14WYBVX', '', 0, NULL, 41.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 17:43:37', '2024-01-23 17:43:29', '000000', 238.30, 27.20, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11425, '11010810001', 'MA80GFQM8', 'MA01D1QR8947230968924876800', '2411010810000301', 4, 4, '2024-01-23 17:58:48', '2024-01-23 17:59:23', 1.11, 0.23, 0.67, 0.90, 0.90, 65, 0, 1, NULL, 'KEYJMGLPANIGHSDZN', '', 0, NULL, 45.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 17:59:24', '2024-01-23 17:58:48', '000000', 228.10, 75.30, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11426, '11010810001', 'MA80GFQM8', 'MA01D1QR8947234996954083328', '2411010810000301', 4, 4, '2024-01-23 18:14:48', '2024-01-23 18:14:58', 0.29, 0.06, 0.18, 0.24, 0.24, 65, 0, 1, NULL, 'BIVOBJNZOFMTVQHU9', '', 0, NULL, 27.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"1.0000\",\"ServicePrice\":\"1.0000\",\"PriceType\":0}]', '', '', 0, '2024-01-23 18:14:59', '2024-01-23 18:14:49', '000000', 72.80, 20.20, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11427, '11010810001', 'MA80GFQM8', 'MA01D1QR8948313966218850304', '2411010810000101', 4, 2, '2024-01-26 17:42:14', '2024-01-26 17:42:57', 0.76, 1.53, 0.00, 1.53, 1.53, 0, 0, 0, NULL, '', '', 0, NULL, 15.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"ServicePrice\":\"0.6000\",\"PriceType\":0},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"ServicePrice\":\"0.5000\",\"PriceType\":0},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.5000\",\"PriceType\":0},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"ServicePrice\":\"0.5000\",\"PriceType\":0}]', '', '', 0, '2024-01-26 17:42:57', '2024-01-26 17:42:15', '000000', 159.00, 242.60, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11428, '11010810001', 'MA80GFQM8', 'MA01D1QR8949288193096953856', '2411010810000101', 4, 4, '2024-01-29 10:13:28', '2024-01-29 10:17:09', 5.32, 10.65, 0.00, 10.65, 10.65, 0, 0, 0, NULL, '', '', 0, NULL, 3.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.2000\",\"ServicePrice\":\"0.6000\",\"PriceType\":0},{\"StartTime\":\"060000\",\"ElecPrice\":\"0.5000\",\"ServicePrice\":\"0.5000\",\"PriceType\":0},{\"StartTime\":\"100000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.5000\",\"PriceType\":0},{\"StartTime\":\"180000\",\"ElecPrice\":\"0.8000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0},{\"StartTime\":\"220000\",\"ElecPrice\":\"0.5000\",\"ServicePrice\":\"0.5000\",\"PriceType\":0}]', '', '', 0, '2024-01-29 10:17:09', '2024-01-29 10:13:29', '000000', 195.70, 67.90, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11429, '11010810001', 'MA80GFQM8', 'MA01D1QR8949307066349268992', '2411010810000101', 4, 4, '2024-01-29 11:28:27', '2024-01-29 11:33:04', 6.62, 9.26, 0.00, 9.26, 9.26, 0, 0, 0, NULL, '', '', 0, NULL, 13.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"ServicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.8000\",\"PriceType\":0},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0}]', '', '', 0, '2024-01-29 11:33:04', '2024-01-29 11:28:28', '000000', 96.30, 157.30, 2, 5.00);
INSERT INTO `omind_bill` VALUES (11430, '11010810001', 'MA80GFQM8', 'MA01D1QR8957653938163953664', '2411010810000101', 4, 1, '2024-02-21 12:15:56', '2024-02-21 12:16:13', 0.20, 0.09, 0.07, 0.16, 0.16, 0, 0, 0, NULL, '', '', 0, NULL, 56.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"ServicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.8000\",\"PriceType\":0},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0}]', '', '', 0, '2024-02-21 12:16:13', '2024-02-21 12:15:57', '000000', 184.80, 157.70, 1, 0.00);
INSERT INTO `omind_bill` VALUES (11431, '11010810001', 'MA80GFQM8', 'MA01D1QR8957681099843317760', '2411010810000101', 4, 4, '2024-02-21 14:03:52', '2024-02-21 14:04:07', 0.39, 0.16, 0.12, 0.28, 0.28, 0, 0, 0, NULL, '', '', 0, NULL, 8.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"ServicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.8000\",\"PriceType\":0},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0}]', '', '', 0, '2024-02-21 14:04:08', '2024-02-21 14:03:53', '000000', 123.90, 358.60, 1, -1.00);
INSERT INTO `omind_bill` VALUES (11432, '11010810001', 'MA80GFQM8', 'MA01D1QR8957681292630306816', '2411010810000101', 4, 1, '2024-02-21 14:04:38', '2024-02-21 14:05:04', 0.52, 0.22, 0.16, 0.38, 0.38, 0, 0, 0, NULL, '', '', 0, NULL, 22.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"ServicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.8000\",\"PriceType\":0},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0}]', '', '', 0, '2024-02-21 14:05:04', '2024-02-21 14:04:39', '000000', 54.30, 213.10, 1, -1.00);
INSERT INTO `omind_bill` VALUES (11433, '11010810001', 'MA80GFQM8', 'MA01D1QR8958077727880261632', '2411010810000101', 4, 3, '2024-02-22 16:19:56', '2024-02-22 16:48:24', 39.27, 15.86, 11.90, 27.76, 27.76, 0, 0, 0, NULL, '', '', 0, NULL, 69.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"ServicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.8000\",\"PriceType\":0},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0}]', '', '', 0, '2024-02-22 16:48:24', '2024-02-22 16:19:57', '000000', 146.90, 229.90, 1, -1.00);
INSERT INTO `omind_bill` VALUES (11434, '11010810001', 'MA80GFQM8', 'MA01D1QR8958086206674186240', '2411010810000101', 4, 3, '2024-02-22 16:53:37', '2024-02-22 16:59:17', 8.10, 3.25, 2.44, 5.69, 5.69, 0, 0, 0, NULL, '', '', 0, NULL, 16.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"ServicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.8000\",\"PriceType\":0},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0}]', '', '', 0, '2024-02-22 16:59:17', '2024-02-22 16:53:38', '000000', 52.10, 38.50, 1, -1.00);
INSERT INTO `omind_bill` VALUES (11435, '11010810001', 'MA80GFQM8', 'MA01D1QR8958087712089583616', '2411010810000101', 4, 3, '2024-02-22 16:59:36', '2024-02-22 17:11:44', 17.17, 7.05, 5.29, 12.34, 12.34, 0, 0, 0, NULL, '', '', 0, NULL, 62.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"ServicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.8000\",\"PriceType\":0},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0}]', '', '', 0, '2024-02-22 17:11:44', '2024-02-22 16:59:37', '000000', 137.20, 381.60, 1, -1.00);
INSERT INTO `omind_bill` VALUES (11436, '11010810001', 'MA80GFQM8', 'MA01D1QR8958091711073234944', '2411010810000101', 4, 3, '2024-02-22 17:15:30', '2024-02-22 17:15:39', 0.38, 0.16, 0.12, 0.28, 0.28, 0, 0, 0, NULL, '', '', 0, NULL, 60.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"ServicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.8000\",\"PriceType\":0},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0}]', '', '', 0, '2024-02-22 17:15:39', '2024-02-22 17:15:30', '000000', 71.00, 395.40, 1, -1.00);
INSERT INTO `omind_bill` VALUES (11437, '11010810001', 'MA80GFQM8', 'MA01D1QR8958345544768696320', '2411010810000301', 4, 2, '2024-02-23 10:04:08', '2024-02-23 10:04:24', 0.47, 0.20, 0.15, 0.35, 0.35, 0, 0, 0, NULL, '', '', 0, NULL, 48.00, 1, '', '[{\"StartTime\":\"000000\",\"ElecPrice\":\"0.4000\",\"ServicePrice\":\"0.3000\",\"PriceType\":0},{\"StartTime\":\"090000\",\"ElecPrice\":\"0.6000\",\"ServicePrice\":\"0.8000\",\"PriceType\":0},{\"StartTime\":\"130000\",\"ElecPrice\":\"0.7000\",\"ServicePrice\":\"0.4000\",\"PriceType\":0}]', '', '', 0, '2024-02-23 10:04:24', '2024-02-23 10:04:09', '000000', 182.40, 293.50, 1, -1.00);
COMMIT;

-- ----------------------------
-- Table structure for omind_connector
-- ----------------------------
DROP TABLE IF EXISTS `omind_connector`;
CREATE TABLE `omind_connector` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电站id(运营商自定义的唯一编码)',
  `operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '基础运营商id(组织机构代码)',
  `user_operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户平台运营商id(组织机构代码)',
  `base_operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '基础运营商id(组织机构代码)',
  `equipment_id` varchar(23) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '设备编码(设备唯一编码，对同一运营商，保证唯一)',
  `connector_id` varchar(26) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电设备接口编码(充电设备接口编码，同一运营商内唯一)',
  `connector_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电设备接口名称',
  `connector_type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '充电设备接口类型:1、家用插座(模式2);2、交流接口插座(模式3，连接方式B);3、交流接口插头(带枪线，模式3，连接方式C);4、直流接口枪头(带枪线，模式4);5、无线充电座;6、其他',
  `voltage_upper_limits` smallint unsigned NOT NULL DEFAULT '0' COMMENT '额定电压上限(单位:V)',
  `voltage_lower_limits` smallint unsigned NOT NULL DEFAULT '0' COMMENT '额定电压下限(单位:V)',
  `current_value` smallint unsigned NOT NULL COMMENT '额定电流(单位:A)',
  `power` decimal(6,2) unsigned NOT NULL DEFAULT '0.00' COMMENT '额定功率(单位:kW)',
  `park_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '车位号(停车场车位编号)',
  `national_standard` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '国家标准:1、2011;2、2015',
  `status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '充电设备接口状态:0、离网;1、空闲;2、占用(未充电);3、占用(充电中);4、占用(预约锁定);255、故障',
  `park_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '车位状态:0:未知;10:空闲;50:已上锁',
  `lock_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '地锁状态:0:未知;10:已解锁;50:已上锁',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `equipment_id` (`equipment_id`),
  KEY `connector_id` (`connector_id`),
  KEY `station_id` (`station_id`),
  KEY `operator_id` (`operator_id`),
  KEY `base_operator_id` (`base_operator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4532 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电设备接口信息表';

-- ----------------------------
-- Records of omind_connector
-- ----------------------------
BEGIN;
INSERT INTO `omind_connector` VALUES (4526, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100001', '2411010810000101', '快充01', 4, 960, 180, 120, 120.00, '', 2, 2, 0, 0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_connector` VALUES (4527, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100002', '2411010810000201', '快充02', 4, 180, 960, 120, 120.00, '', 2, 2, 0, 0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_connector` VALUES (4528, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100003', '2411010810000301', '快充03', 4, 180, 960, 120, 120.00, '', 2, 0, 0, 0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_connector` VALUES (4529, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100004', '2411010810000401', '快充04', 4, 180, 960, 120, 120.00, '', 2, 0, 0, 0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_connector` VALUES (4530, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100005', '2411010810000501', '快充05', 4, 180, 960, 120, 120.00, '', 2, 0, 0, 0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_connector` VALUES (4531, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100006', '2411010810000601', '快充06', 4, 180, 960, 120, 120.00, '', 2, 0, 0, 0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
COMMIT;

-- ----------------------------
-- Table structure for omind_equipment
-- ----------------------------
DROP TABLE IF EXISTS `omind_equipment`;
CREATE TABLE `omind_equipment` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电站id(运营商自定义的唯一编码)',
  `operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营商id(组织机构代码)',
  `user_operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户平台运营商id(组织机构代码)',
  `base_operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '基础运营商id(组织机构代码)',
  `equipment_id` varchar(23) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '设备编码(设备唯一编码，对同一运营商，保证唯一)',
  `equipment_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电设备名称',
  `manufacturer_id` varchar(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '设备生产商组织机构代码',
  `manufacturer_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '设备生产商名称',
  `equipment_model` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '设备型号(由设备生产商定义的设备型号)',
  `production_date` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '设备生产日期(YYYY-MM-DD)',
  `equipment_type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '设备类型:1、直流设备;2、交流设备;3、交直流一体设备;4、无线设备;5、其他',
  `equipment_lng` decimal(6,6) unsigned NOT NULL DEFAULT '0.000000' COMMENT '站点经度(GCJ-02坐标系,保留小数点后6位)',
  `equipment_lat` decimal(6,6) unsigned NOT NULL DEFAULT '0.000000' COMMENT '站点纬度(GCJ-02坐标系,保留小数点后6位)',
  `power` decimal(6,1) unsigned NOT NULL DEFAULT '0.0' COMMENT '充电设备总功率(单位:kW,保留小数点后1位)',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `operator_id` (`operator_id`),
  KEY `station_id` (`station_id`),
  KEY `equipment_id` (`equipment_id`),
  KEY `manufacturer_id` (`manufacturer_id`),
  KEY `base_operator_id` (`base_operator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3572 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电设备信息表';

-- ----------------------------
-- Records of omind_equipment
-- ----------------------------
BEGIN;
INSERT INTO `omind_equipment` VALUES (3566, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100001', 'A1直流桩', 'MA01D1QR8', '', 'A1', '2024-01-05', 1, 0.000000, 0.000000, 120.0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_equipment` VALUES (3567, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100002', 'A2直流桩', 'MA01D1QR8', '', 'A1', '', 1, 0.000000, 0.000000, 120.0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_equipment` VALUES (3568, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100003', 'A3直流桩', 'MA01D1QR8', '', 'A1', '', 1, 0.000000, 0.000000, 120.0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_equipment` VALUES (3569, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100004', 'A4直流桩', 'MA01D1QR8', '', 'A1', '', 1, 0.000000, 0.000000, 120.0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_equipment` VALUES (3570, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100005', 'A5直流桩', 'MA01D1QR8', '', 'A1', '', 1, 0.000000, 0.000000, 120.0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_equipment` VALUES (3571, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', '24110108100006', 'A6直流桩', 'MA01D1QR8', '', 'A1', '', 1, 0.000000, 0.000000, 120.0, 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
COMMIT;

-- ----------------------------
-- Table structure for omind_feedback
-- ----------------------------
DROP TABLE IF EXISTS `omind_feedback`;
CREATE TABLE `omind_feedback` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `connector_id` varchar(26) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '桩ID',
  `feedback_type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '反馈类型:1、充电站;2、充电桩;3、充电枪;50、其他',
  `feedback_content` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '反馈内容',
  `imgs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片',
  `reply_content` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '回复内容',
  `reply_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否回复:0、未回复;1、已回复',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户反馈表';

-- ----------------------------
-- Records of omind_feedback
-- ----------------------------
BEGIN;
INSERT INTO `omind_feedback` VALUES (1, 4, '', 3, 'qwert', '[]', 'aaa', 1, '', 0, '2024-01-23 11:28:41', '2024-01-22 16:16:58', '000000');
INSERT INTO `omind_feedback` VALUES (2, 1, '11', 3, '11', '[]', '123', 1, '', 0, '2024-01-23 18:57:28', '2024-01-23 12:09:03', '000000');
INSERT INTO `omind_feedback` VALUES (3, 1, '123', 3, 'qq', '[]', '123', 1, '', 0, '2024-01-23 18:57:20', '2024-01-23 18:54:46', '000000');
COMMIT;

-- ----------------------------
-- Table structure for omind_operator
-- ----------------------------
DROP TABLE IF EXISTS `omind_operator`;
CREATE TABLE `omind_operator` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营商id(组织机构代码)',
  `operator_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营商名称',
  `operator_tel1` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营商客服电话1',
  `operator_tel2` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营商客服电话2',
  `operator_reg_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营商注册地址',
  `operator_note` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注信息',
  `user_operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '客户归属运营商id(组织机构代码)',
  `operator_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营商密钥',
  `data_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息密钥',
  `data_secret_iv` char(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '消息密钥初始化向量',
  `sig_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '签名密钥',
  `base_operator_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '基础运营商密钥',
  `base_data_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '基础消息密钥',
  `base_data_secret_iv` char(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '基础消息密钥初始化向量',
  `base_sig_secret` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '基础签名密钥',
  `api_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '接口地址',
  `plat_type` tinyint unsigned NOT NULL DEFAULT '1' COMMENT '平台类型:1、OMIND',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `operator_id` (`operator_id`),
  KEY `user_operator_id` (`user_operator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='基础设施运营商信息表';

-- ----------------------------
-- Records of omind_operator
-- ----------------------------
BEGIN;
INSERT INTO `omind_operator` VALUES (28, 'MA80GFQM8', '北京飞天科技邮箱公司', '13800000000', '13800000000', '北京市海淀区中关村大街', '', 'MA01D1QR8', 'o6gpvB9uo87UBHGy', '7sEQg8caT0kpAUNO', 'l9JC1bDWFLidvdv8', 'RnKZnW2RxxuXPOIz', 'e3bb15a6f95f8bb7', '37ruITzn5kL0hFZG', 'idPU4cFo4MAn2JEL', '57fd20184e19ca9a', 'http://172.25.0.101:9801/evcs/v1/', 1, 0, '2024-01-11 15:52:32', '2024-01-11 15:52:32', '000000');
COMMIT;

-- ----------------------------
-- Table structure for omind_price
-- ----------------------------
DROP TABLE IF EXISTS `omind_price`;
CREATE TABLE `omind_price` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电站ID',
  `price_code` bigint NOT NULL DEFAULT '0' COMMENT '价格模版ID，0为默认价格',
  `start_time` datetime NOT NULL COMMENT '时段起始时间点 6位 HHmmss',
  `price_type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '价格类型:0、尖;1、峰;2、平;3、谷;',
  `elec_price` decimal(10,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '电价:XXXX.XXXX',
  `service_price` decimal(10,4) unsigned NOT NULL DEFAULT '0.0000' COMMENT '服务费单价:XXXX.XXXX',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电价格表';

-- ----------------------------
-- Records of omind_price
-- ----------------------------
BEGIN;
INSERT INTO `omind_price` VALUES (1, '11010810001', 0, '2024-01-22 00:00:00', 2, 1.0000, 1.0000, '2024-01-22 13:03:09', '', 2, '000000');
INSERT INTO `omind_price` VALUES (2, '11010810002', 0, '2024-01-22 00:00:00', 2, 1.0000, 1.0000, '2024-01-22 13:03:34', '', 2, '000000');
INSERT INTO `omind_price` VALUES (3, '11010810003', 0, '2024-01-22 00:00:00', 2, 1.0000, 1.0000, '2024-01-22 13:03:52', '', 2, '000000');
INSERT INTO `omind_price` VALUES (4, '11010810001', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 18:22:19', '', 2, '000000');
INSERT INTO `omind_price` VALUES (5, '11010810002', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 18:25:10', '', 0, '000000');
INSERT INTO `omind_price` VALUES (6, '11010810003', 0, '2024-01-22 00:00:00', 0, 0.2000, 0.6000, '2024-01-22 18:39:28', '', 2, '000000');
INSERT INTO `omind_price` VALUES (7, '11010810003', 0, '2024-01-22 06:00:00', 0, 0.5000, 0.5000, '2024-01-22 18:39:28', '', 2, '000000');
INSERT INTO `omind_price` VALUES (8, '11010810003', 0, '2024-01-22 10:00:00', 0, 0.6000, 0.5000, '2024-01-22 18:39:28', '', 2, '000000');
INSERT INTO `omind_price` VALUES (9, '11010810003', 0, '2024-01-22 18:00:00', 0, 0.8000, 0.4000, '2024-01-22 18:39:28', '', 2, '000000');
INSERT INTO `omind_price` VALUES (10, '11010810003', 0, '2024-01-22 22:00:00', 0, 0.5000, 0.5000, '2024-01-22 18:39:28', '', 2, '000000');
INSERT INTO `omind_price` VALUES (11, '11010810003', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 18:40:14', '', 2, '000000');
INSERT INTO `omind_price` VALUES (12, '11010810003', 0, '2024-01-22 00:00:00', 0, 0.2000, 0.6000, '2024-01-22 18:40:46', '', 2, '000000');
INSERT INTO `omind_price` VALUES (13, '11010810003', 0, '2024-01-22 06:00:00', 0, 0.5000, 0.5000, '2024-01-22 18:40:46', '', 2, '000000');
INSERT INTO `omind_price` VALUES (14, '11010810003', 0, '2024-01-22 10:00:00', 0, 0.6000, 0.5000, '2024-01-22 18:40:46', '', 2, '000000');
INSERT INTO `omind_price` VALUES (15, '11010810003', 0, '2024-01-22 18:00:00', 0, 0.8000, 0.4000, '2024-01-22 18:40:46', '', 2, '000000');
INSERT INTO `omind_price` VALUES (16, '11010810003', 0, '2024-01-22 22:00:00', 0, 0.5000, 0.5000, '2024-01-22 18:40:46', '', 2, '000000');
INSERT INTO `omind_price` VALUES (17, '11010810003', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 18:40:59', '', 2, '000000');
INSERT INTO `omind_price` VALUES (18, '11010810003', 0, '2024-01-22 00:00:00', 0, 0.2000, 0.6000, '2024-01-22 18:55:39', '', 2, '000000');
INSERT INTO `omind_price` VALUES (19, '11010810003', 0, '2024-01-22 06:00:00', 0, 0.5000, 0.5000, '2024-01-22 18:55:39', '', 2, '000000');
INSERT INTO `omind_price` VALUES (20, '11010810003', 0, '2024-01-22 10:00:00', 0, 0.6000, 0.5000, '2024-01-22 18:55:39', '', 2, '000000');
INSERT INTO `omind_price` VALUES (21, '11010810003', 0, '2024-01-22 18:00:00', 0, 0.8000, 0.4000, '2024-01-22 18:55:39', '', 2, '000000');
INSERT INTO `omind_price` VALUES (22, '11010810003', 0, '2024-01-22 22:00:00', 0, 0.5000, 0.5000, '2024-01-22 18:55:39', '', 2, '000000');
INSERT INTO `omind_price` VALUES (23, '11010810003', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 18:57:36', '', 2, '000000');
INSERT INTO `omind_price` VALUES (24, '11010810003', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 19:03:46', '', 2, '000000');
INSERT INTO `omind_price` VALUES (25, '11010810003', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 19:03:57', '', 2, '000000');
INSERT INTO `omind_price` VALUES (26, '11010810003', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 19:04:18', '', 2, '000000');
INSERT INTO `omind_price` VALUES (27, '11010810003', 0, '2024-01-22 00:00:00', 0, 0.2000, 0.6000, '2024-01-22 19:04:36', '', 2, '000000');
INSERT INTO `omind_price` VALUES (28, '11010810003', 0, '2024-01-22 06:00:00', 0, 0.5000, 0.5000, '2024-01-22 19:04:36', '', 2, '000000');
INSERT INTO `omind_price` VALUES (29, '11010810003', 0, '2024-01-22 10:00:00', 0, 0.6000, 0.5000, '2024-01-22 19:04:36', '', 2, '000000');
INSERT INTO `omind_price` VALUES (30, '11010810003', 0, '2024-01-22 18:00:00', 0, 0.8000, 0.4000, '2024-01-22 19:04:36', '', 2, '000000');
INSERT INTO `omind_price` VALUES (31, '11010810003', 0, '2024-01-22 22:00:00', 0, 0.5000, 0.5000, '2024-01-22 19:04:36', '', 2, '000000');
INSERT INTO `omind_price` VALUES (32, '11010810003', 0, '2024-01-22 00:00:00', 0, 0.2000, 0.6000, '2024-01-22 19:04:49', '', 2, '000000');
INSERT INTO `omind_price` VALUES (33, '11010810003', 0, '2024-01-22 06:00:00', 0, 0.5000, 0.5000, '2024-01-22 19:04:49', '', 2, '000000');
INSERT INTO `omind_price` VALUES (34, '11010810003', 0, '2024-01-22 10:00:00', 0, 0.6000, 0.5000, '2024-01-22 19:04:49', '', 2, '000000');
INSERT INTO `omind_price` VALUES (35, '11010810003', 0, '2024-01-22 18:00:00', 0, 0.8000, 0.4000, '2024-01-22 19:04:49', '', 2, '000000');
INSERT INTO `omind_price` VALUES (36, '11010810003', 0, '2024-01-22 22:00:00', 0, 0.5000, 0.5000, '2024-01-22 19:04:49', '', 2, '000000');
INSERT INTO `omind_price` VALUES (37, '11010810003', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 19:05:57', '', 2, '000000');
INSERT INTO `omind_price` VALUES (38, '11010810003', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 20:44:17', '', 2, '000000');
INSERT INTO `omind_price` VALUES (39, '11010810003', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 20:44:23', '', 2, '000000');
INSERT INTO `omind_price` VALUES (40, '11010810003', 0, '2024-01-22 00:00:00', 0, 0.2000, 0.6000, '2024-01-22 20:44:30', '', 2, '000000');
INSERT INTO `omind_price` VALUES (41, '11010810003', 0, '2024-01-22 06:00:00', 0, 0.5000, 0.5000, '2024-01-22 20:44:30', '', 2, '000000');
INSERT INTO `omind_price` VALUES (42, '11010810003', 0, '2024-01-22 10:00:00', 0, 0.6000, 0.5000, '2024-01-22 20:44:30', '', 2, '000000');
INSERT INTO `omind_price` VALUES (43, '11010810003', 0, '2024-01-22 18:00:00', 0, 0.8000, 0.4000, '2024-01-22 20:44:30', '', 2, '000000');
INSERT INTO `omind_price` VALUES (44, '11010810003', 0, '2024-01-22 22:00:00', 0, 0.5000, 0.5000, '2024-01-22 20:44:30', '', 2, '000000');
INSERT INTO `omind_price` VALUES (45, '11010810003', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 20:44:34', '', 0, '000000');
INSERT INTO `omind_price` VALUES (46, '11010810001', 0, '2024-01-22 00:00:00', 0, 1.0000, 1.0000, '2024-01-22 21:04:10', '', 2, '000000');
INSERT INTO `omind_price` VALUES (47, '11010810001', 0, '2024-01-22 00:00:00', 0, 0.2000, 0.6000, '2024-01-22 21:04:17', '', 2, '000000');
INSERT INTO `omind_price` VALUES (48, '11010810001', 0, '2024-01-22 06:00:00', 0, 0.5000, 0.5000, '2024-01-22 21:04:17', '', 2, '000000');
INSERT INTO `omind_price` VALUES (49, '11010810001', 0, '2024-01-22 10:00:00', 0, 0.6000, 0.5000, '2024-01-22 21:04:17', '', 2, '000000');
INSERT INTO `omind_price` VALUES (50, '11010810001', 0, '2024-01-22 18:00:00', 0, 0.8000, 0.4000, '2024-01-22 21:04:17', '', 2, '000000');
INSERT INTO `omind_price` VALUES (51, '11010810001', 0, '2024-01-22 22:00:00', 0, 0.5000, 0.5000, '2024-01-22 21:04:17', '', 2, '000000');
INSERT INTO `omind_price` VALUES (52, '11010810001', 0, '2024-01-23 00:00:00', 0, 1.0000, 1.0000, '2024-01-23 11:12:57', '', 2, '000000');
INSERT INTO `omind_price` VALUES (53, '11010810001', 0, '2024-01-23 00:00:00', 0, 1.0000, 1.0000, '2024-01-23 11:12:59', '', 2, '000000');
INSERT INTO `omind_price` VALUES (54, '11010810001', 0, '2024-01-24 00:00:00', 0, 1.0000, 1.0000, '2024-01-24 18:14:44', '', 2, '000000');
INSERT INTO `omind_price` VALUES (55, '11010810001', 0, '2024-01-24 00:00:00', 0, 1.0000, 1.0000, '2024-01-24 18:15:04', '', 2, '000000');
INSERT INTO `omind_price` VALUES (56, '11010810001', 0, '2024-01-24 00:00:00', 0, 0.2000, 0.6000, '2024-01-24 18:16:49', '', 2, '000000');
INSERT INTO `omind_price` VALUES (57, '11010810001', 0, '2024-01-24 06:00:00', 0, 0.5000, 0.5000, '2024-01-24 18:16:49', '', 2, '000000');
INSERT INTO `omind_price` VALUES (58, '11010810001', 0, '2024-01-24 10:00:00', 0, 0.6000, 0.5000, '2024-01-24 18:16:49', '', 2, '000000');
INSERT INTO `omind_price` VALUES (59, '11010810001', 0, '2024-01-24 18:00:00', 0, 0.8000, 0.4000, '2024-01-24 18:16:49', '', 2, '000000');
INSERT INTO `omind_price` VALUES (60, '11010810001', 0, '2024-01-24 22:00:00', 0, 0.5000, 0.5000, '2024-01-24 18:16:49', '', 2, '000000');
INSERT INTO `omind_price` VALUES (61, '11010810001', 0, '2024-01-29 00:00:00', 0, 0.4000, 0.3000, '2024-01-29 10:24:23', '', 0, '000000');
INSERT INTO `omind_price` VALUES (62, '11010810001', 0, '2024-01-29 09:00:00', 0, 0.6000, 0.8000, '2024-01-29 10:24:23', '', 0, '000000');
INSERT INTO `omind_price` VALUES (63, '11010810001', 0, '2024-01-29 13:00:00', 0, 0.7000, 0.4000, '2024-01-29 10:24:23', '', 0, '000000');
COMMIT;

-- ----------------------------
-- Table structure for omind_station
-- ----------------------------
DROP TABLE IF EXISTS `omind_station`;
CREATE TABLE `omind_station` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电站id(运营商自定义的唯一编码)',
  `operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '运营商id(组织机构代码)',
  `user_operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户平台运营商id(组织机构代码)',
  `base_operator_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '基础运营商id(组织机构代码)',
  `equipment_owner_id` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '设备所属商id(组织机构代码)',
  `station_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电站名称',
  `country_code` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电站国家代码,比如CN',
  `area_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电站省市辖区编码',
  `address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '详细地址',
  `station_tel` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '站点电话(能够联系场站工作人员进行协助的联系电话)',
  `service_tel` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '服务电话(平台服务电话，例如400的电话)',
  `station_type` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '站点类型:1、公共;50、个人;100、公交(专用);101、环卫(专用);102、物流(专用);103、出租车(专用);255、其他',
  `station_status` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '站点状态:0、未知;1、建设中;5、关闭下线;6、维护中;50、正常使用',
  `park_nums` int unsigned NOT NULL DEFAULT '0' COMMENT '车位数量(可停放进行充电的车位总数，默认：0 未知)',
  `station_lng` decimal(10,6) unsigned NOT NULL DEFAULT '0.000000' COMMENT '站点经度(GCJ-02坐标系,保留小数点后6位)',
  `station_lat` decimal(10,6) unsigned NOT NULL DEFAULT '0.000000' COMMENT '站点纬度(GCJ-02坐标系,保留小数点后6位)',
  `site_guide` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '站点引导:描述性文字，用于引导车主找到充电车位',
  `construction` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '建设场所类型:1、居民区;2、公共机构;3、企事业单位;4、写字楼;5、工业园区;6、交通枢纽;7、大型文体设施;8、城市绿地;9、大型建筑配建停车场;10、路边停车位;11、城际高速服务区;255、其他',
  `pictures` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '站点照片(充电设备照片、充电车位照片、停车场入口照片)JSON串',
  `match_cars` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '使用车型描述(描述该站点接受的车大小以及类型，如大巴、物流车、私家乘用车、出租车等)',
  `park_info` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '车位楼层及数量描述(车位楼层以及数量信息)',
  `busine_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '营业时间描述',
  `electricity_fee` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电电费描述',
  `service_fee` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '服务费描述',
  `park_fee` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '停车费描述',
  `payment` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付方式::刷卡、线上、现金。其中电子钱包类卡为刷卡，身份鉴权卡、微信/支付宝、APP为线上',
  `support_order` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否支持预约(充电设备是否需要提前预约后才能使用。0为不支持预约;1为支持预约。不填默认为0)',
  `plat_type` tinyint unsigned NOT NULL COMMENT '平台类型',
  `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注信息',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `station_id` (`station_id`),
  KEY `operator_id` (`operator_id`),
  KEY `equipment_owner_id` (`equipment_owner_id`),
  KEY `area_code` (`area_code`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电站信息表';

-- ----------------------------
-- Records of omind_station
-- ----------------------------
BEGIN;
INSERT INTO `omind_station` VALUES (128, '11010810001', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', 'MA01D1QR8', '海淀区001充电站', '86', '110108', '中关村大街', '13900000000', '13900000000', 0, 50, 0, 116.315792, 39.984553, '地下一层', 1, '[\"http://172.25.0.15:9000/ruoyi/2024/01/26/c855d51293b8453baa8c00019b891fe7.jpeg\"]', '小轿车', '收费15元/小时', '1', '1', '1', '', '微信', 0, 1, '', 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_station` VALUES (129, '11010810002', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', 'MA01D1QR8', '海淀区002充电站', '86', '110108', '中关村大街', '13900000000', '13900000000', 0, 50, 0, 116.416792, 39.987553, '地下一层', 0, '[]', '小轿车', '收费15元/小时', '', '', '', '', '微信', 0, 1, '', 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
INSERT INTO `omind_station` VALUES (130, '11010810003', 'MA80GFQM8', 'MA01D1QR8', 'MA80GFQM8', 'MA01D1QR8', '海淀区003充电站', '86', '110108', '中关村大街', '13900000000', '13900000000', 0, 50, 0, 116.516792, 39.987553, '地下一层', 0, '[]', '小轿车', '收费15元/小时', '', '', '', '', '微信', 0, 1, '', 0, '2024-02-23 11:20:00', '2024-02-23 11:20:00', '000000');
COMMIT;

-- ----------------------------
-- Table structure for omind_station_images
-- ----------------------------
DROP TABLE IF EXISTS `omind_station_images`;
CREATE TABLE `omind_station_images` (
  `id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `station_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '充电站id(运营商自定义的唯一编码)',
  `image_type` tinyint(1) NOT NULL DEFAULT '1' COMMENT '图片类型:1、充电站照片;2、充电桩照片;3、充电车位照片;4、停车场入口照片',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '站点图片',
  `image_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图片名称',
  `show_seq` int unsigned DEFAULT '0' COMMENT '显示顺序',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电站图片表';

-- ----------------------------
-- Table structure for omind_user
-- ----------------------------
DROP TABLE IF EXISTS `omind_user`;
CREATE TABLE `omind_user` (
  `uid` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '自增id',
  `phone_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '国家区号码',
  `mobile` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户手机号',
  `nick_name` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
  `wechat_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '微信昵称',
  `unionid_wx` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '当且仅当该移动应用已获得该用户的 userinfo 授权时，才会出现该字段',
  `openid_wx` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '小程序openID',
  `unionid_ali` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT 'ali统一id',
  `openid_ali` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '支付宝用户唯一标识',
  `credit_pay_wx` int NOT NULL DEFAULT '0' COMMENT '微信信用分授权-后支付',
  `credit_pay_ali` int NOT NULL DEFAULT '0' COMMENT '阿里信用分授权-后支付',
  `sex` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0、未知;1、男;2、女',
  `country` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '国家',
  `province` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '省',
  `city` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '城市',
  `birthday` bigint NOT NULL DEFAULT '0' COMMENT '生日',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '头像地址',
  `platform` int unsigned NOT NULL DEFAULT '0' COMMENT '注册来源的平台:关联平台id(0未知)',
  `disable_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否禁用用户:0、启用;1、禁用',
  `register_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_visit_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最近一次访问时间',
  `last_visit_ip` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '最近一次访问ip',
  `last_visit_area` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '最近一次访问ip对应区域',
  `org_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '用户是否从属组织机构:0、不从属;1、从属',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`uid`),
  KEY `mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ----------------------------
-- Records of omind_user
-- ----------------------------
BEGIN;
INSERT INTO `omind_user` VALUES (1, '86', '13124790000', '13124790000', '', 'ooEjV5vbOwzEVob09TQxxy2N64rc', 'onCyY5d9TRmkhnFaOM0c9PlAQVso', '', '', 0, 0, 0, '', '', '', 0, '', 0, 0, '2024-01-22 15:29:51', '2024-02-23 10:07:58', '219.142.137.156,172.25.0.3', '', 0, '', 0, '2024-02-23 10:07:58', '2024-01-22 15:29:51', '000000');
INSERT INTO `omind_user` VALUES (2, '86', '13400000000', '13400000000', '', 'ooEjV5ukdDN9ertqD-j-j8mHReAk', 'onCyY5ed6WUVCJrNDRtIEN4bTCV8', '', '', 0, 0, 0, '', '', '', 0, '', 0, 0, '2024-01-22 15:33:29', '2024-02-23 10:51:35', '219.142.137.156,172.25.0.3', '', 0, '', 0, '2024-02-23 10:51:35', '2024-01-22 15:33:30', '000000');
INSERT INTO `omind_user` VALUES (3, '86', '18600000000', '18600000000', '', 'ooEjV5hqzMrsHGPc8nI-ila6Wf_U', 'onCyY5TXaphwcjXBJiqsMSAnycaI', '', '', 0, 0, 0, '', '', '', 0, '', 0, 0, '2024-01-22 15:33:58', '2024-02-22 17:33:25', '219.142.137.156,172.25.0.3', '', 0, '', 0, '2024-02-22 17:33:25', '2024-01-22 15:33:59', '000000');
INSERT INTO `omind_user` VALUES (4, '86', '13800000000', '13800000000', '', 'ooEjV5sQcoZDsP9a-EI1Mmo-ZMrQ', 'onCyY5Uve8iwpq_3JTR5SgBTL_PM', '', '', 0, 0, 0, '', '', '', 0, '', 0, 0, '2024-01-22 15:49:46', '2024-02-22 09:59:34', '219.142.137.156,172.25.0.3', '', 0, '', 0, '2024-02-22 09:59:34', '2024-01-22 15:49:47', '000000');
COMMIT;

-- ----------------------------
-- Table structure for omind_user_car
-- ----------------------------
DROP TABLE IF EXISTS `omind_user_car`;
CREATE TABLE `omind_user_car` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  `user_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '用户ID',
  `plate_no` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '车牌号',
  `car_vin` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '车辆vin码',
  `engine_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '发动机号码',
  `vehicle_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '车辆类型',
  `car_model` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '品牌型号',
  `owner` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '车辆所有人',
  `address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '住址',
  `use_character` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '使用性质:运营、非运营',
  `register_date` date DEFAULT NULL COMMENT '注册日期(格式"yyyy-MM-dd")',
  `issue_date` date DEFAULT NULL COMMENT '发证日期(格式"yyyy-MM-dd")',
  `license_imgs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '行驶证图片json串',
  `check_state` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '审核状态:0、待审核;1、审核通过;2、审核不通过;3、不需审核',
  `auth_state` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '认证状态:0、不认证;1、待认证;2、认证通过;3、认证不通过',
  `remark` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '备注',
  `del_flag` tinyint unsigned NOT NULL DEFAULT '0' COMMENT '数据状态:0、正常;1、删除',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '000000' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `plate_no` (`plate_no`),
  KEY `car_vin` (`car_vin`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户的车辆';

-- ----------------------------
-- Records of omind_user_car
-- ----------------------------
BEGIN;
INSERT INTO `omind_user_car` VALUES (126, 4, '沪A12345', '', '', '', '', '', '', '', NULL, NULL, NULL, 0, 0, '', 0, '2024-01-23 11:30:20', '2024-01-23 11:30:20', '000000');
INSERT INTO `omind_user_car` VALUES (127, 1, '京AD99999', '', '', '', '', '', '', '', NULL, NULL, NULL, 0, 0, '', 0, '2024-01-23 12:09:16', '2024-01-23 12:09:16', '000000');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
