USE simple_car;

-- 1. Users (password is 123456)
INSERT INTO `user` (`id`, `username`, `password`, `nick_name`, `phone`, `status`)
VALUES
    (1, '13800000000', '{bcrypt}$2b$10$wcLZeUvgpf6vj4sw5eQ3nOd5Y13Rj8pZ7KNgy9c1DVMCuRapFdhMa', '张三 (测试车主)', '13800000000', 1),
    (2, '13912345678', '{bcrypt}$2b$10$wcLZeUvgpf6vj4sw5eQ3nOd5Y13Rj8pZ7KNgy9c1DVMCuRapFdhMa', '李四', '13912345678', 1)
ON DUPLICATE KEY UPDATE
    `username` = VALUES(`username`),
    `password` = VALUES(`password`),
    `nick_name` = VALUES(`nick_name`),
    `phone` = VALUES(`phone`),
    `status` = VALUES(`status`);

-- 2. Cars
INSERT INTO `car` (`id`, `car_name`, `car_models`, `license_tag`, `frame_number`, `endurance_mileage`, `remaining_power`, `temperature`, `car_state`)
VALUES
    (1, '比亚迪', '海豹 700km 性能版', '苏A12345', 'VIN20230001SEAL', 520, 85, 24.5, 2),
    (2, '特斯拉', 'Model 3 Performance', '苏A88888', 'VIN20230002M3P', 310, 42, 22.0, 3),
    (3, '蔚来', 'ET5 100kWh', '苏A66666', 'VIN20230003ET5', 450, 65, 25.0, 2)
ON DUPLICATE KEY UPDATE
    `car_name` = VALUES(`car_name`),
    `car_models` = VALUES(`car_models`),
    `license_tag` = VALUES(`license_tag`),
    `frame_number` = VALUES(`frame_number`),
    `endurance_mileage` = VALUES(`endurance_mileage`),
    `remaining_power` = VALUES(`remaining_power`),
    `temperature` = VALUES(`temperature`),
    `car_state` = VALUES(`car_state`);

-- 3. User-Car relationship
INSERT INTO `user_car` (`id`, `user_id`, `car_id`, `is_default`)
VALUES
    (1, 1, 1, 1),
    (2, 1, 2, 0),
    (3, 2, 3, 1)
ON DUPLICATE KEY UPDATE
    `user_id` = VALUES(`user_id`),
    `car_id` = VALUES(`car_id`),
    `is_default` = VALUES(`is_default`);

-- 4. Vehicle mileage (Car 1)
INSERT INTO `vehicle_mileage` (`id`, `car_id`, `car_mileage`, `create_time`)
VALUES
    (1, 1, 1000, '2023-01-01 10:00:00'),
    (2, 1, 1250, '2023-02-01 10:00:00'),
    (3, 1, 1800, '2023-03-01 10:00:00'),
    (4, 1, 2400, '2023-04-01 10:00:00'),
    (5, 1, 3100, '2023-05-01 10:00:00'),
    (6, 1, 3950, '2023-06-01 10:00:00'),
    (7, 1, 5200, '2023-12-31 23:59:59'),
    (8, 1, 6800, '2024-03-01 10:00:00')
ON DUPLICATE KEY UPDATE
    `car_id` = VALUES(`car_id`),
    `car_mileage` = VALUES(`car_mileage`),
    `create_time` = VALUES(`create_time`);

-- 5. Charging orders (Car 1)
INSERT INTO `charging_order` (`id`, `car_id`, `charged_quantity`, `actual_payment_amount`, `create_time`)
VALUES
    (1, 1, 45.0, 58.5, '2023-01-15 14:00:00'),
    (2, 1, 38.2, 49.6, '2023-02-12 18:30:00'),
    (3, 1, 52.5, 68.2, '2023-03-20 09:00:00'),
    (4, 1, 44.8, 55.1, '2023-04-05 21:00:00'),
    (5, 1, 40.0, 52.0, '2023-05-22 11:15:00'),
    (6, 1, 60.1, 84.0, '2023-06-15 16:45:00'),
    (7, 1, 55.0, 71.5, '2024-01-10 10:00:00'),
    (8, 1, 42.0, 54.6, '2024-02-25 15:30:00')
ON DUPLICATE KEY UPDATE
    `car_id` = VALUES(`car_id`),
    `charged_quantity` = VALUES(`charged_quantity`),
    `actual_payment_amount` = VALUES(`actual_payment_amount`),
    `create_time` = VALUES(`create_time`);

-- 6. Maintenance Service Stations
INSERT INTO `maintenance_service_station` (`id`, `service_station_name`, `city_id`, `address`, `phone`, `status`)
VALUES
    (1, '南京雨花旗舰维保中心', '320100', '南京市雨花台区宁双路19号', '025-88880001', 1),
    (2, '南京江宁智行服务站', '320100', '南京市江宁区天元西路123号', '025-88880002', 1),
    (3, '南京栖霞仙林特约店', '320100', '南京市栖霞区学海路45号', '025-88880003', 1),
    (4, '南京浦口高新服务点', '320100', '南京市浦口区星火路9号', '025-88880004', 1),
    (5, '南京建邺奥体维护中心', '320100', '南京市建邺区江东中路222号', '025-88880005', 1)
ON DUPLICATE KEY UPDATE
    `service_station_name` = VALUES(`service_station_name`),
    `city_id` = VALUES(`city_id`),
    `address` = VALUES(`address`),
    `phone` = VALUES(`phone`),
    `status` = VALUES(`status`);

-- 7. Maintenance Plans
INSERT INTO `maintenance_plan` (`id`, `category`, `replacement_part`, `unit_price`, `total_price`, `duration`)
VALUES
    (1, '常规保养', '空调滤芯', 150.00, 150.00, 1),
    (2, '常规保养', '雨刮片', 98.00, 196.00, 1),
    (3, '底盘系统', '刹车油更换', 280.00, 280.00, 2),
    (4, '驱动系统', '减速器油', 450.00, 450.00, 3),
    (5, '制动系统', '前轮刹车片', 320.00, 640.00, 2),
    (6, '电池系统', '健康度深度检测', 199.00, 199.00, 1),
    (7, '轮胎系统', '四轮定位', 260.00, 260.00, 2)
ON DUPLICATE KEY UPDATE
    `category` = VALUES(`category`),
    `replacement_part` = VALUES(`replacement_part`),
    `unit_price` = VALUES(`unit_price`),
    `total_price` = VALUES(`total_price`),
    `duration` = VALUES(`duration`);

-- 8. Notices
INSERT INTO `notice` (`id`, `title`, `content`, `type`, `user_id`, `create_time`)
VALUES
    (1, '欢迎加入 Simple Car 社区', '尊贵的车主，欢迎开启您的智能出行之旅。您可以在此管理车辆、预约维保、查看行程报告。', 1, NULL, '2024-01-01 09:00:00'),
    (2, '冬季用车贴心提醒', '南京气温骤降，建议您在行车前开启电池预热功能，以获得更佳的续航表现和驾驶体验。', 1, NULL, '2024-01-15 10:30:00'),
    (3, '维保到期提醒', '您的苏A12345行驶已接近 5000 公里，建议预约首次保养。', 2, 1, '2023-06-20 14:00:00'),
    (4, '充电完成通知', '您的车辆苏A88888已充电至 90%，请及时挪车，避免占位费。', 3, 1, '2024-03-20 16:45:00'),
    (5, '优惠券到账通知', '一张 50 元维保抵用券已发至您的账户，可在下次预约时使用。', 1, 1, '2024-04-01 08:00:00')
ON DUPLICATE KEY UPDATE
    `title` = VALUES(`title`),
    `content` = VALUES(`content`),
    `type` = VALUES(`type`),
    `user_id` = VALUES(`user_id`),
    `create_time` = VALUES(`create_time`);

-- 9. Historical Maintenance Appointments
INSERT INTO `maintenance_appointment` (`id`, `work_no`, `type`, `user_id`, `car_id`, `maintenance_service_station_id`, `customer_name`, `customer_phone`, `appoint_date`, `appoint_time`, `total_amount`, `status`, `created_at`)
VALUES
    (1, 'WB202306250001', 0, 1, 1, 1, '张三', '13800000000', '2023-06-25', '10:00-11:00', 349.00, 2, '2023-06-20 14:00:00'),
    (2, 'WB202404050001', 1, 1, 2, 2, '张三', '13800000000', '2024-04-05', '14:00-15:00', 640.00, 0, '2024-04-01 09:00:00')
ON DUPLICATE KEY UPDATE
    `work_no` = VALUES(`work_no`),
    `type` = VALUES(`type`),
    `user_id` = VALUES(`user_id`),
    `car_id` = VALUES(`car_id`),
    `maintenance_service_station_id` = VALUES(`maintenance_service_station_id`),
    `customer_name` = VALUES(`customer_name`),
    `customer_phone` = VALUES(`customer_phone`),
    `appoint_date` = VALUES(`appoint_date`),
    `appoint_time` = VALUES(`appoint_time`),
    `total_amount` = VALUES(`total_amount`),
    `status` = VALUES(`status`),
    `created_at` = VALUES(`created_at`);

INSERT INTO `maintenance_pay` (`id`, `maintenance_appointment_id`, `price`, `status`, `paid_at`, `created_at`)
VALUES
    (1, 1, 349.00, 1, '2023-06-25 11:30:00', '2023-06-25 10:00:00'),
    (2, 2, 640.00, 0, NULL, '2024-04-05 14:00:00')
ON DUPLICATE KEY UPDATE
    `maintenance_appointment_id` = VALUES(`maintenance_appointment_id`),
    `price` = VALUES(`price`),
    `status` = VALUES(`status`),
    `paid_at` = VALUES(`paid_at`),
    `created_at` = VALUES(`created_at`);
