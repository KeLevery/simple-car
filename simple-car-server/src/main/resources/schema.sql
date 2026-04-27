-- Create database if not exists
CREATE DATABASE IF NOT EXISTS simple_car DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE simple_car;

-- User table
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL COMMENT '登录账号',
    `password` varchar(255) NOT NULL COMMENT '密码',
    `nick_name` varchar(50) DEFAULT NULL COMMENT '昵称',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `status` tinyint DEFAULT '1' COMMENT '状态',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- Car table
CREATE TABLE IF NOT EXISTS `car` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `car_name` varchar(50) DEFAULT NULL COMMENT '品牌/名称',
    `car_models` varchar(100) DEFAULT NULL COMMENT '车型',
    `license_tag` varchar(20) DEFAULT NULL COMMENT '车牌号',
    `frame_number` varchar(64) DEFAULT NULL COMMENT '车架号/VIN',
    `endurance_mileage` decimal(10,2) DEFAULT '0.00' COMMENT '可续航',
    `remaining_power` int DEFAULT '0' COMMENT '剩余电量百分比',
    `temperature` decimal(10,2) DEFAULT '0.00' COMMENT '温度',
    `car_state` tinyint DEFAULT '1' COMMENT '1离线 2关机 3开机',
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

-- User-Car relationship
CREATE TABLE IF NOT EXISTS `user_car` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `car_id` bigint NOT NULL,
    `is_default` tinyint DEFAULT '0',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户车辆关联表';

-- Charging order table
CREATE TABLE IF NOT EXISTS `charging_order` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `car_id` bigint NOT NULL,
    `charged_quantity` decimal(10,2) DEFAULT '0.00',
    `actual_payment_amount` decimal(10,2) DEFAULT '0.00',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电订单表';

-- Vehicle mileage table
CREATE TABLE IF NOT EXISTS `vehicle_mileage` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `car_id` bigint NOT NULL,
    `car_mileage` decimal(10,2) DEFAULT '0.00',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆里程表';

-- Maintenance Service Station table
CREATE TABLE IF NOT EXISTS `maintenance_service_station` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `service_station_name` varchar(100) DEFAULT NULL,
    `city_id` varchar(20) DEFAULT NULL,
    `address` varchar(255) DEFAULT NULL,
    `phone` varchar(20) DEFAULT NULL,
    `status` tinyint DEFAULT '1',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='服务站表';

-- Maintenance Plan table
CREATE TABLE IF NOT EXISTS `maintenance_plan` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `category` varchar(100) DEFAULT NULL,
    `replacement_part` varchar(100) DEFAULT NULL,
    `unit_price` decimal(10,2) DEFAULT '0.00',
    `total_price` decimal(10,2) DEFAULT '0.00',
    `duration` int DEFAULT '1',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修方案表';

-- Maintenance Appointment table
CREATE TABLE IF NOT EXISTS `maintenance_appointment` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `work_no` varchar(50) NOT NULL,
    `type` tinyint DEFAULT '0' COMMENT '0保养 1维修',
    `user_id` bigint DEFAULT NULL,
    `car_id` bigint DEFAULT NULL,
    `maintenance_service_station_id` bigint DEFAULT NULL,
    `customer_name` varchar(50) DEFAULT NULL,
    `customer_phone` varchar(20) DEFAULT NULL,
    `appoint_date` date DEFAULT NULL,
    `appoint_time` varchar(20) DEFAULT NULL,
    `delivery_time` datetime DEFAULT NULL,
    `maintenance_time` datetime DEFAULT NULL,
    `customer_signature` varchar(255) DEFAULT NULL,
    `total_amount` decimal(10,2) DEFAULT '0.00',
    `status` tinyint DEFAULT '0' COMMENT '0待支付 1维保中 2已完成',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_work_no` (`work_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维保预约表';

-- Maintenance Appointment Plan details
CREATE TABLE IF NOT EXISTS `maintenance_appointment_plan` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `appointment_id` bigint NOT NULL,
    `category` varchar(100) DEFAULT NULL,
    `replacement_part` varchar(100) DEFAULT NULL,
    `unit_price` decimal(10,2) DEFAULT '0.00',
    `total_price` decimal(10,2) DEFAULT '0.00',
    `duration` int DEFAULT '1',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约方案明细表';

-- Maintenance Payment table
CREATE TABLE IF NOT EXISTS `maintenance_pay` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `maintenance_appointment_id` bigint NOT NULL,
    `price` decimal(10,2) DEFAULT '0.00',
    `status` tinyint DEFAULT '0' COMMENT '0待支付 1已支付 2已取消',
    `paid_at` datetime DEFAULT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付表';

-- Notice table
CREATE TABLE IF NOT EXISTS `notice` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `title` varchar(100) NOT NULL COMMENT '标题',
    `content` text COMMENT '内容',
    `type` tinyint DEFAULT '1' COMMENT '1系统 2维保 3充电',
    `user_id` bigint DEFAULT NULL COMMENT '指定用户, 为空则全员',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- Community post table
CREATE TABLE IF NOT EXISTS `community_post` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `content` text NOT NULL,
    `images` text,
    `like_count` int DEFAULT '0',
    `comment_count` int DEFAULT '0',
    `share_count` int DEFAULT '0',
    `is_hot` tinyint DEFAULT '0',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区动态表';

-- Community post like table
CREATE TABLE IF NOT EXISTS `community_post_like` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `post_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user` (`post_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区动态点赞表';

-- Community post comment table
CREATE TABLE IF NOT EXISTS `community_post_comment` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `post_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `content` text NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_post_time` (`post_id`,`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区动态评论表';

