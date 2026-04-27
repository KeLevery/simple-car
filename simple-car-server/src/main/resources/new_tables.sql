-- =====================================================
-- 新功能所需数据库表
-- 在 simple_car 数据库中执行此脚本
-- =====================================================

USE simple_car;

-- 1. 救援请求表
CREATE TABLE IF NOT EXISTS `rescue_request` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `car_id` bigint DEFAULT NULL COMMENT '车辆ID',
    `contact_name` varchar(50) DEFAULT NULL COMMENT '联系人姓名',
    `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `location` varchar(255) DEFAULT NULL COMMENT '救援地点',
    `description` text COMMENT '问题描述',
    `status` tinyint DEFAULT '0' COMMENT '0:待处理 1:救援中 2:已完成 3:已取消',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='救援请求表';

-- 2. 车辆违章记录表
CREATE TABLE IF NOT EXISTS `vehicle_violation` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `car_id` bigint NOT NULL COMMENT '车辆ID',
    `violation_type` varchar(100) DEFAULT NULL COMMENT '违章类型',
    `location` varchar(255) DEFAULT NULL COMMENT '违章地点',
    `fine_amount` decimal(10,2) DEFAULT '0.00' COMMENT '罚款金额',
    `deduct_points` int DEFAULT '0' COMMENT '扣分',
    `status` tinyint DEFAULT '0' COMMENT '0:未处理 1:已处理',
    `violation_time` datetime DEFAULT NULL COMMENT '违章时间',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_car_id` (`car_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆违章记录表';

-- =====================================================
-- 示例数据（可选）
-- =====================================================

-- 违章示例数据（假设 car_id = 1）
INSERT INTO `vehicle_violation` (`car_id`, `violation_type`, `location`, `fine_amount`, `deduct_points`, `status`, `violation_time`) VALUES
(1, '闯红灯', '南京市玄武区中山东路与太平北路交叉口', 200.00, 6, 0, '2026-03-15 08:30:00'),
(1, '违章停车', '南京市鼓楼区中央路98号', 100.00, 0, 1, '2026-02-20 14:15:00'),
(1, '超速20%以上', '南京市江宁区宁杭高速K120', 200.00, 6, 0, '2026-03-28 10:45:00'),
(1, '未按规定车道行驶', '南京市建邺区河西大街', 100.00, 3, 1, '2026-01-10 17:20:00');
