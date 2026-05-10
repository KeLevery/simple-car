USE simple_car;

CREATE TABLE IF NOT EXISTS `admin_user` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(64) NOT NULL,
    `password` varchar(255) NOT NULL,
    `display_name` varchar(64) DEFAULT NULL,
    `status` tinyint DEFAULT '1' COMMENT '0禁用 1正常',
    `last_login_at` datetime DEFAULT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台账号表';

CREATE TABLE IF NOT EXISTS `admin_role` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `role_code` varchar(64) NOT NULL,
    `role_name` varchar(64) NOT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台角色表';

CREATE TABLE IF NOT EXISTS `admin_user_role` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `admin_user_id` bigint NOT NULL,
    `role_id` bigint NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_admin_user_role` (`admin_user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台账号角色关联表';

CREATE TABLE IF NOT EXISTS `admin_operation_log` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `admin_user_id` bigint DEFAULT NULL,
    `action` varchar(128) NOT NULL,
    `target_type` varchar(64) DEFAULT NULL,
    `target_id` bigint DEFAULT NULL,
    `request_ip` varchar(64) DEFAULT NULL,
    `detail` json DEFAULT NULL,
    `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_admin_action_time` (`admin_user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台操作日志表';

INSERT INTO `admin_role` (`id`, `role_code`, `role_name`)
VALUES (1, 'ROLE_ADMIN', '超级管理员')
ON DUPLICATE KEY UPDATE
    `role_code` = VALUES(`role_code`),
    `role_name` = VALUES(`role_name`);

INSERT INTO `admin_user` (`id`, `username`, `password`, `display_name`, `status`)
VALUES (1, 'admin', '{bcrypt}$2b$10$wcLZeUvgpf6vj4sw5eQ3nOd5Y13Rj8pZ7KNgy9c1DVMCuRapFdhMa', '系统管理员', 1)
ON DUPLICATE KEY UPDATE
    `password` = VALUES(`password`),
    `display_name` = VALUES(`display_name`),
    `status` = VALUES(`status`);

INSERT IGNORE INTO `admin_user_role` (`admin_user_id`, `role_id`)
SELECT `admin_user`.`id`, `admin_role`.`id`
FROM `admin_user`, `admin_role`
WHERE `admin_user`.`username` = 'admin'
  AND `admin_role`.`role_code` = 'ROLE_ADMIN';
