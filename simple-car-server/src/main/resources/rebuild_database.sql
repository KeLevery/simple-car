-- Simple Car canonical rebuild script.
-- WARNING: This script drops and recreates the simple_car database.
-- Use it when you want a clean local database that matches the current backend entities.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `simple_car`;
CREATE DATABASE `simple_car`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `simple_car`;

-- =====================================================
-- Account and permission domain
-- =====================================================

CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '登录账号',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0禁用 1正常',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  KEY `idx_user_phone` (`phone`),
  KEY `idx_user_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车主账号';

CREATE TABLE `admin_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(64) NOT NULL COMMENT '后台账号',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `display_name` varchar(64) DEFAULT NULL COMMENT '显示名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0禁用 1正常',
  `last_login_at` datetime DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_user_username` (`username`),
  KEY `idx_admin_user_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台账号';

CREATE TABLE `admin_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_code` varchar(64) NOT NULL COMMENT '角色编码',
  `role_name` varchar(64) NOT NULL COMMENT '角色名称',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台角色';

CREATE TABLE `admin_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_user_id` bigint NOT NULL COMMENT '后台账号ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_user_role` (`admin_user_id`, `role_id`),
  KEY `idx_admin_user_role_role_id` (`role_id`),
  CONSTRAINT `fk_admin_user_role_user` FOREIGN KEY (`admin_user_id`) REFERENCES `admin_user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_admin_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `admin_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台账号角色关系';

CREATE TABLE `admin_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_user_id` bigint DEFAULT NULL COMMENT '后台账号ID',
  `action` varchar(128) NOT NULL COMMENT '操作动作',
  `target_type` varchar(64) DEFAULT NULL COMMENT '目标类型',
  `target_id` bigint DEFAULT NULL COMMENT '目标ID',
  `request_ip` varchar(64) DEFAULT NULL COMMENT '请求IP',
  `detail` json DEFAULT NULL COMMENT '操作详情',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_admin_operation_user_time` (`admin_user_id`, `created_at`),
  KEY `idx_admin_operation_action_time` (`action`, `created_at`),
  CONSTRAINT `fk_admin_operation_user` FOREIGN KEY (`admin_user_id`) REFERENCES `admin_user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台操作日志';

CREATE TABLE `user_settings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `setting_type` varchar(50) NOT NULL COMMENT '设置类型',
  `setting_key` varchar(50) NOT NULL COMMENT '设置项',
  `setting_value` varchar(255) NOT NULL DEFAULT '0' COMMENT '设置值',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_settings_type_key` (`user_id`, `setting_type`, `setting_key`),
  CONSTRAINT `fk_user_settings_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户设置';

-- =====================================================
-- Vehicle domain
-- =====================================================

CREATE TABLE `car` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `car_name` varchar(50) DEFAULT NULL COMMENT '品牌/名称',
  `car_models` varchar(100) DEFAULT NULL COMMENT '车型',
  `license_tag` varchar(20) DEFAULT NULL COMMENT '车牌号',
  `frame_number` varchar(64) DEFAULT NULL COMMENT '车架号/VIN',
  `endurance_mileage` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '剩余续航(km)',
  `remaining_power` int NOT NULL DEFAULT 0 COMMENT '剩余电量百分比',
  `temperature` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '车内温度',
  `car_state` tinyint NOT NULL DEFAULT 1 COMMENT '0离线 1在线 2静止 3启动',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_car_license_tag` (`license_tag`),
  UNIQUE KEY `uk_car_frame_number` (`frame_number`),
  KEY `idx_car_state` (`car_state`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆';

CREATE TABLE `user_car` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `car_id` bigint NOT NULL COMMENT '车辆ID',
  `is_default` tinyint NOT NULL DEFAULT 0 COMMENT '0否 1是',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_car` (`user_id`, `car_id`),
  KEY `idx_user_car_car_id` (`car_id`),
  KEY `idx_user_car_user_default` (`user_id`, `is_default`),
  CONSTRAINT `fk_user_car_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_car_car` FOREIGN KEY (`car_id`) REFERENCES `car` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户车辆关系';

CREATE TABLE `vehicle_mileage` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `car_id` bigint NOT NULL COMMENT '车辆ID',
  `car_mileage` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '行驶里程(km)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_vehicle_mileage_car_time` (`car_id`, `create_time`),
  CONSTRAINT `fk_vehicle_mileage_car` FOREIGN KEY (`car_id`) REFERENCES `car` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆里程';

CREATE TABLE `vehicle_violation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `car_id` bigint NOT NULL COMMENT '车辆ID',
  `violation_type` varchar(100) DEFAULT NULL COMMENT '违章类型',
  `location` varchar(255) DEFAULT NULL COMMENT '违章地点',
  `fine_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '罚款金额',
  `deduct_points` int NOT NULL DEFAULT 0 COMMENT '扣分',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0未处理 1已处理',
  `violation_time` datetime DEFAULT NULL COMMENT '违章时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_vehicle_violation_car_time` (`car_id`, `violation_time`),
  KEY `idx_vehicle_violation_status` (`status`),
  CONSTRAINT `fk_vehicle_violation_car` FOREIGN KEY (`car_id`) REFERENCES `car` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车辆违章';

-- =====================================================
-- Charging domain
-- =====================================================

CREATE TABLE `charging_station` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `station_name` varchar(100) NOT NULL COMMENT '充电站名称',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `city_id` varchar(20) DEFAULT NULL COMMENT '城市编码',
  `total_piles` int NOT NULL DEFAULT 0 COMMENT '总电桩数',
  `available_piles` int NOT NULL DEFAULT 0 COMMENT '可用电桩数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0停用/维护 1运营',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_charging_station_name` (`station_name`),
  KEY `idx_charging_station_city_status` (`city_id`, `status`),
  KEY `idx_charging_station_available` (`available_piles`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电站';

CREATE TABLE `charging_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `car_id` bigint NOT NULL COMMENT '车辆ID',
  `charged_quantity` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '充电量(kWh)',
  `actual_payment_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_charging_order_car_time` (`car_id`, `create_time`),
  CONSTRAINT `fk_charging_order_car` FOREIGN KEY (`car_id`) REFERENCES `car` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='充电订单';

-- =====================================================
-- Maintenance domain
-- =====================================================

CREATE TABLE `maintenance_service_station` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `service_station_name` varchar(100) DEFAULT NULL COMMENT '服务站名称',
  `city_id` varchar(20) DEFAULT NULL COMMENT '城市编码',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '0停用 1营业',
  PRIMARY KEY (`id`),
  KEY `idx_service_station_city_status` (`city_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维保服务站';

CREATE TABLE `maintenance_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(100) DEFAULT NULL COMMENT '分类',
  `replacement_part` varchar(100) DEFAULT NULL COMMENT '配件/项目',
  `unit_price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `total_price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '总价',
  `duration` int NOT NULL DEFAULT 1 COMMENT '预计工时',
  PRIMARY KEY (`id`),
  KEY `idx_maintenance_plan_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维保方案';

CREATE TABLE `maintenance_appointment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `work_no` varchar(50) NOT NULL COMMENT '工单号',
  `type` tinyint NOT NULL DEFAULT 0 COMMENT '0保养 1维修',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `car_id` bigint DEFAULT NULL COMMENT '车辆ID',
  `maintenance_service_station_id` bigint DEFAULT NULL COMMENT '维保服务站ID',
  `customer_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `customer_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `appoint_date` date DEFAULT NULL COMMENT '预约日期',
  `appoint_time` varchar(20) DEFAULT NULL COMMENT '预约时段',
  `delivery_time` datetime DEFAULT NULL COMMENT '交车时间',
  `maintenance_time` datetime DEFAULT NULL COMMENT '维保时间',
  `customer_signature` varchar(255) DEFAULT NULL COMMENT '客户签名',
  `total_amount` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0待确认/待支付 1处理中/维保中 2已完成 3已取消',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_maintenance_work_no` (`work_no`),
  KEY `idx_maintenance_user_status` (`user_id`, `status`),
  KEY `idx_maintenance_car_time` (`car_id`, `created_at`),
  KEY `idx_maintenance_station_date` (`maintenance_service_station_id`, `appoint_date`),
  CONSTRAINT `fk_maintenance_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_maintenance_car` FOREIGN KEY (`car_id`) REFERENCES `car` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_maintenance_station` FOREIGN KEY (`maintenance_service_station_id`) REFERENCES `maintenance_service_station` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维保预约';

CREATE TABLE `maintenance_appointment_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `appointment_id` bigint NOT NULL COMMENT '预约ID',
  `category` varchar(100) DEFAULT NULL COMMENT '分类',
  `replacement_part` varchar(100) DEFAULT NULL COMMENT '配件/项目',
  `unit_price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '单价',
  `total_price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '总价',
  `duration` int NOT NULL DEFAULT 1 COMMENT '预计工时',
  PRIMARY KEY (`id`),
  KEY `idx_appointment_plan_appointment` (`appointment_id`),
  CONSTRAINT `fk_appointment_plan_appointment` FOREIGN KEY (`appointment_id`) REFERENCES `maintenance_appointment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预约维保项目';

CREATE TABLE `maintenance_pay` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `maintenance_appointment_id` bigint NOT NULL COMMENT '预约ID',
  `price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT '支付金额',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0待支付 1已支付 2已取消',
  `paid_at` datetime DEFAULT NULL COMMENT '支付时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_maintenance_pay_appointment` (`maintenance_appointment_id`),
  KEY `idx_maintenance_pay_status` (`status`),
  CONSTRAINT `fk_maintenance_pay_appointment` FOREIGN KEY (`maintenance_appointment_id`) REFERENCES `maintenance_appointment` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维保支付';

-- =====================================================
-- Rescue and content domain
-- =====================================================

CREATE TABLE `rescue_request` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `car_id` bigint DEFAULT NULL COMMENT '车辆ID',
  `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `location` varchar(255) DEFAULT NULL COMMENT '救援地点',
  `description` text COMMENT '问题描述',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '0待处理 1救援中 2已完成 3已取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_rescue_user_time` (`user_id`, `create_time`),
  KEY `idx_rescue_status_time` (`status`, `create_time`),
  KEY `idx_rescue_car_time` (`car_id`, `create_time`),
  CONSTRAINT `fk_rescue_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_rescue_car` FOREIGN KEY (`car_id`) REFERENCES `car` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='道路救援请求';

CREATE TABLE `notice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '1系统 2维保 3充电',
  `user_id` bigint DEFAULT NULL COMMENT '指定用户, 空表示全员',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notice_user_time` (`user_id`, `create_time`),
  KEY `idx_notice_type_time` (`type`, `create_time`),
  CONSTRAINT `fk_notice_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知公告';

CREATE TABLE `community_post` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text NOT NULL COMMENT '内容',
  `images` text COMMENT '图片URL, 逗号分隔',
  `like_count` int NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NOT NULL DEFAULT 0 COMMENT '评论数',
  `share_count` int NOT NULL DEFAULT 0 COMMENT '分享数',
  `is_hot` tinyint NOT NULL DEFAULT 0 COMMENT '0普通 1热门',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_post_user_time` (`user_id`, `create_time`),
  KEY `idx_post_hot_time` (`is_hot`, `create_time`),
  CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区动态';

CREATE TABLE `community_post_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '动态ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_post_like_user` (`post_id`, `user_id`),
  KEY `idx_post_like_user_time` (`user_id`, `create_time`),
  CONSTRAINT `fk_post_like_post` FOREIGN KEY (`post_id`) REFERENCES `community_post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_post_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区动态点赞';

CREATE TABLE `community_post_comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '动态ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `content` text NOT NULL COMMENT '评论内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_post_comment_post_time` (`post_id`, `create_time`),
  KEY `idx_post_comment_user_time` (`user_id`, `create_time`),
  CONSTRAINT `fk_post_comment_post` FOREIGN KEY (`post_id`) REFERENCES `community_post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_post_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='社区动态评论';

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- Seed data
-- =====================================================

INSERT INTO `user` (`id`, `username`, `password`, `nick_name`, `phone`, `status`, `created_at`, `updated_at`) VALUES
  (1, '13800000000', '{bcrypt}$2b$10$wcLZeUvgpf6vj4sw5eQ3nOd5Y13Rj8pZ7KNgy9c1DVMCuRapFdhMa', '张三', '13800000000', 1, '2024-01-01 09:00:00', '2024-01-01 09:00:00'),
  (2, '13912345678', '{bcrypt}$2b$10$wcLZeUvgpf6vj4sw5eQ3nOd5Y13Rj8pZ7KNgy9c1DVMCuRapFdhMa', '李四', '13912345678', 1, '2024-01-02 09:00:00', '2024-01-02 09:00:00');

INSERT INTO `admin_user` (`id`, `username`, `password`, `display_name`, `status`, `created_at`, `updated_at`) VALUES
  (1, 'admin', '{bcrypt}$2b$10$wcLZeUvgpf6vj4sw5eQ3nOd5Y13Rj8pZ7KNgy9c1DVMCuRapFdhMa', '系统管理员', 1, '2024-01-01 09:00:00', '2024-01-01 09:00:00');

INSERT INTO `admin_role` (`id`, `role_code`, `role_name`, `created_at`) VALUES
  (1, 'ROLE_ADMIN', '超级管理员', '2024-01-01 09:00:00');

INSERT INTO `admin_user_role` (`id`, `admin_user_id`, `role_id`) VALUES
  (1, 1, 1);

INSERT INTO `user_settings` (`id`, `user_id`, `setting_type`, `setting_key`, `setting_value`) VALUES
  (1, 1, 'notification', 'maintenance_reminder', '1'),
  (2, 1, 'notification', 'charging_reminder', '1'),
  (3, 1, 'privacy', 'community_profile_visible', '1'),
  (4, 2, 'notification', 'maintenance_reminder', '1');

INSERT INTO `car` (`id`, `car_name`, `car_models`, `license_tag`, `frame_number`, `endurance_mileage`, `remaining_power`, `temperature`, `car_state`, `updated_at`) VALUES
  (1, '比亚迪', '海豹 700km 性能版', '苏A12345', 'VIN20230001SEAL', 520.00, 85, 24.50, 2, '2024-04-01 10:00:00'),
  (2, '特斯拉', 'Model 3 Performance', '苏A88888', 'VIN20230002M3P', 310.00, 42, 22.00, 3, '2024-04-01 10:00:00'),
  (3, '蔚来', 'ET5 100kWh', '苏A66666', 'VIN20230003ET5', 450.00, 65, 25.00, 2, '2024-04-01 10:00:00');

INSERT INTO `user_car` (`id`, `user_id`, `car_id`, `is_default`, `created_at`) VALUES
  (1, 1, 1, 1, '2024-01-01 09:10:00'),
  (2, 1, 2, 0, '2024-01-01 09:15:00'),
  (3, 2, 3, 1, '2024-01-02 09:10:00');

INSERT INTO `vehicle_mileage` (`id`, `car_id`, `car_mileage`, `create_time`) VALUES
  (1, 1, 1000.00, '2023-01-01 10:00:00'),
  (2, 1, 1250.00, '2023-02-01 10:00:00'),
  (3, 1, 1800.00, '2023-03-01 10:00:00'),
  (4, 1, 2400.00, '2023-04-01 10:00:00'),
  (5, 1, 3100.00, '2023-05-01 10:00:00'),
  (6, 1, 3950.00, '2023-06-01 10:00:00'),
  (7, 1, 5200.00, '2023-12-31 23:59:59'),
  (8, 1, 6800.00, '2024-03-01 10:00:00');

INSERT INTO `vehicle_violation` (`id`, `car_id`, `violation_type`, `location`, `fine_amount`, `deduct_points`, `status`, `violation_time`, `create_time`) VALUES
  (1, 1, '闯红灯', '南京市玄武区中山东路与太平北路交叉口', 200.00, 6, 0, '2026-03-15 08:30:00', '2026-03-15 09:00:00'),
  (2, 1, '违章停车', '南京市鼓楼区中央路88号', 100.00, 0, 1, '2026-02-20 14:15:00', '2026-02-20 15:00:00'),
  (3, 1, '超速20%以上', '南京市江宁区宁杭高速G120', 200.00, 6, 0, '2026-03-28 10:45:00', '2026-03-28 11:30:00'),
  (4, 1, '未按规定车道行驶', '南京市建邺区河西大街', 100.00, 3, 1, '2026-01-10 17:20:00', '2026-01-10 18:00:00');

INSERT INTO `charging_station` (`id`, `station_name`, `address`, `city_id`, `total_piles`, `available_piles`, `status`, `create_time`) VALUES
  (1, 'Simple Car 南京大明路超充站', '南京市秦淮区大明路8号', '320100', 12, 8, 1, '2024-01-01 09:00:00'),
  (2, 'Simple Car 南京雨花旗舰充电中心', '南京市雨花台区宁双路19号', '320100', 20, 5, 1, '2024-01-01 09:00:00'),
  (3, 'Simple Car 南京奥体中心服务站', '南京市建邺区江东中路222号', '320100', 10, 0, 1, '2024-01-01 09:00:00');

INSERT INTO `charging_order` (`id`, `car_id`, `charged_quantity`, `actual_payment_amount`, `create_time`) VALUES
  (1, 1, 45.00, 58.50, '2023-01-15 14:00:00'),
  (2, 1, 38.20, 49.60, '2023-02-12 18:30:00'),
  (3, 1, 52.50, 68.20, '2023-03-20 09:00:00'),
  (4, 1, 44.80, 55.10, '2023-04-05 21:00:00'),
  (5, 1, 40.00, 52.00, '2023-05-22 11:15:00'),
  (6, 1, 60.10, 84.00, '2023-06-15 16:45:00'),
  (7, 1, 55.00, 71.50, '2024-01-10 10:00:00'),
  (8, 1, 42.00, 54.60, '2024-02-25 15:30:00');

INSERT INTO `maintenance_service_station` (`id`, `service_station_name`, `city_id`, `address`, `phone`, `status`) VALUES
  (1, '南京雨花旗舰维保中心', '320100', '南京市雨花台区宁双路19号', '025-88880001', 1),
  (2, '南京江宁智行服务站', '320100', '南京市江宁区天元西路123号', '025-88880002', 1),
  (3, '南京栖霞仙林特约店', '320100', '南京市栖霞区学海路15号', '025-88880003', 1),
  (4, '南京浦口高新服务点', '320100', '南京市浦口区星火路9号', '025-88880004', 1),
  (5, '南京建邺奥体维护中心', '320100', '南京市建邺区江东中路222号', '025-88880005', 1);

INSERT INTO `maintenance_plan` (`id`, `category`, `replacement_part`, `unit_price`, `total_price`, `duration`) VALUES
  (1, '常规保养', '空调滤芯', 150.00, 150.00, 1),
  (2, '常规保养', '雨刮片', 98.00, 196.00, 1),
  (3, '底盘系统', '刹车油更换', 280.00, 280.00, 2),
  (4, '驱动系统', '减速器油', 450.00, 450.00, 3),
  (5, '制动系统', '前轮刹车片', 320.00, 640.00, 2),
  (6, '电池系统', '健康度深度检测', 199.00, 199.00, 1),
  (7, '轮胎系统', '四轮定位', 260.00, 260.00, 2);

INSERT INTO `maintenance_appointment` (`id`, `work_no`, `type`, `user_id`, `car_id`, `maintenance_service_station_id`, `customer_name`, `customer_phone`, `appoint_date`, `appoint_time`, `delivery_time`, `maintenance_time`, `customer_signature`, `total_amount`, `status`, `created_at`, `updated_at`) VALUES
  (1, 'WB202306250001', 0, 1, 1, 1, '张三', '13800000000', '2023-06-25', '10:00-11:00', '2023-06-25 10:00:00', '2023-06-25 11:30:00', NULL, 349.00, 2, '2023-06-20 14:00:00', '2023-06-25 11:30:00'),
  (2, 'WB202404050001', 1, 1, 2, 2, '张三', '13800000000', '2024-04-05', '14:00-15:00', NULL, NULL, NULL, 640.00, 0, '2024-04-01 09:00:00', '2024-04-01 09:00:00');

INSERT INTO `maintenance_appointment_plan` (`id`, `appointment_id`, `category`, `replacement_part`, `unit_price`, `total_price`, `duration`) VALUES
  (1, 1, '常规保养', '空调滤芯', 150.00, 150.00, 1),
  (2, 1, '电池系统', '健康度深度检测', 199.00, 199.00, 1),
  (3, 2, '制动系统', '前轮刹车片', 320.00, 640.00, 2);

INSERT INTO `maintenance_pay` (`id`, `maintenance_appointment_id`, `price`, `status`, `paid_at`, `created_at`, `updated_at`) VALUES
  (1, 1, 349.00, 1, '2023-06-25 11:30:00', '2023-06-25 10:00:00', '2023-06-25 11:30:00'),
  (2, 2, 640.00, 0, NULL, '2024-04-05 14:00:00', '2024-04-05 14:00:00');

INSERT INTO `rescue_request` (`id`, `user_id`, `car_id`, `contact_name`, `contact_phone`, `location`, `description`, `status`, `create_time`, `update_time`) VALUES
  (1, 1, 1, '张三', '13800000000', '南京市玄武区中山陵停车场', '车辆无法启动，需要道路救援。', 0, '2024-04-03 08:30:00', '2024-04-03 08:30:00'),
  (2, 2, 3, '李四', '13912345678', '南京市江宁区天元西路', '右后轮胎压异常，需要协助。', 2, '2024-03-18 16:20:00', '2024-03-18 18:00:00');

INSERT INTO `notice` (`id`, `title`, `content`, `type`, `user_id`, `create_time`) VALUES
  (1, '欢迎加入 Simple Car 社区', '欢迎开启智能出行服务，你可以在这里管理车辆、预约维保、查看行程和参与社区。', 1, NULL, '2024-01-01 09:00:00'),
  (2, '冬季用车贴心提醒', '气温下降时建议提前开启电池预热，以获得更稳定的续航表现。', 1, NULL, '2024-01-15 10:30:00'),
  (3, '维保到期提醒', '你的苏A12345行驶已超过5000公里，建议预约首次保养。', 2, 1, '2023-06-20 14:00:00'),
  (4, '充电完成通知', '你的苏A88888已充电至90%，请及时挪车。', 3, 1, '2024-03-20 16:45:00');

INSERT INTO `community_post` (`id`, `user_id`, `content`, `images`, `like_count`, `comment_count`, `share_count`, `is_hot`, `create_time`) VALUES
  (1, 1, '今天完成了一次长途续航挑战，电量管理比预期更稳。', 'https://img01.yzcdn.cn/vant/apple-1.jpg,https://img01.yzcdn.cn/vant/apple-2.jpg', 128, 1, 12, 1, '2024-03-10 12:00:00'),
  (2, 2, '分享一条南京到扬州的自驾路线，沿途充电站很多。', 'https://img01.yzcdn.cn/vant/apple-3.jpg', 56, 1, 5, 0, '2024-03-11 18:30:00');

INSERT INTO `community_post_like` (`id`, `post_id`, `user_id`, `create_time`) VALUES
  (1, 1, 2, '2024-03-10 12:30:00'),
  (2, 2, 1, '2024-03-11 19:00:00');

INSERT INTO `community_post_comment` (`id`, `post_id`, `user_id`, `content`, `create_time`) VALUES
  (1, 1, 2, '这个续航表现很不错，路线可以分享一下吗？', '2024-03-10 13:00:00'),
  (2, 2, 1, '这条路线我也收藏了，周末试试。', '2024-03-11 19:30:00');
