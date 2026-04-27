-- 创建 notice 表
USE simple_car;

CREATE TABLE IF NOT EXISTS `notice` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `title` varchar(100) NOT NULL COMMENT '标题',
    `content` text COMMENT '内容',
    `type` tinyint DEFAULT '1' COMMENT '1 系统 2 维保 3 充电',
    `user_id` bigint DEFAULT NULL COMMENT '指定用户，为空则全员',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';
