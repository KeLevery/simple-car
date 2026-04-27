USE simple_car;

CREATE TABLE IF NOT EXISTS `user_settings` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `setting_type` varchar(50) NOT NULL COMMENT '设置类型: notification/privacy',
    `setting_key` varchar(50) NOT NULL COMMENT '设置项',
    `setting_value` varchar(255) DEFAULT '0' COMMENT '设置值',
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_type_key` (`user_id`, `setting_type`, `setting_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户设置表';
