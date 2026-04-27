-- 社区动态表
CREATE TABLE IF NOT EXISTS `community_post` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL COMMENT '作者ID',
    `content` text COMMENT '内容',
    `images` varchar(1000) DEFAULT NULL COMMENT '图片地址(逗号分隔)',
    `like_count` int DEFAULT '0' COMMENT '点赞数',
    `comment_count` int DEFAULT '0' COMMENT '评论数',
    `share_count` int DEFAULT '0' COMMENT '分享数',
    `is_hot` tinyint DEFAULT '0' COMMENT '是否热门',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='社区动态表';

-- 社区点赞记录
CREATE TABLE IF NOT EXISTS `community_post_like` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `post_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_user` (`post_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

-- 社区评论记录
CREATE TABLE IF NOT EXISTS `community_post_comment` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `post_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `content` text NOT NULL,
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_post_time` (`post_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论记录表';

-- 充电站表
CREATE TABLE IF NOT EXISTS `charging_station` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `station_name` varchar(100) NOT NULL,
    `address` varchar(255) DEFAULT NULL,
    `city_id` varchar(20) DEFAULT NULL,
    `total_piles` int DEFAULT '10' COMMENT '总电桩数',
    `available_piles` int DEFAULT '0' COMMENT '空闲电桩数',
    `status` tinyint DEFAULT '1' COMMENT '1可用 0维护',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电站表';

-- 初始数据
INSERT INTO `community_post` (user_id, content, images, like_count, comment_count, share_count, is_hot)
VALUES (1, '极氪001续航挑战成功！今天一口气跑了500公里，电量还剩15%，底盘质感太赞了！', 'https://img01.yzcdn.cn/vant/apple-1.jpg,https://img01.yzcdn.cn/vant/apple-2.jpg', 128, 45, 12, 1);
INSERT INTO `community_post` (user_id, content, images, like_count, comment_count, share_count, is_hot)
VALUES (2, '分享一下春日自驾的路线：南京->扬州->镇江。这一路充电站很多，完全没有续航焦虑。', 'https://img01.yzcdn.cn/vant/apple-3.jpg', 56, 22, 5, 0);

INSERT INTO `charging_station` (station_name, address, city_id, total_piles, available_piles)
VALUES ('极氪南京大明路超充站', '南京市秦淮区大明路1号', '320100', 12, 8);
INSERT INTO `charging_station` (station_name, address, city_id, total_piles, available_piles)
VALUES ('极氪南京雨花旗舰充电中心', '南京市雨花台区宁双路19号', '320100', 20, 5);
INSERT INTO `charging_station` (station_name, address, city_id, total_piles, available_piles)
VALUES ('极氪南京奥体中心服务站', '南京市建邺区江东中路222号', '320100', 10, 0);

