USE simple_car;

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
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_charging_station_name` (`station_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充电站表';

-- 社区演示数据
INSERT INTO `community_post` (`id`, `user_id`, `content`, `images`, `like_count`, `comment_count`, `share_count`, `is_hot`)
VALUES
    (1, 1, '极氪001续航挑战成功！今天一口气跑了500公里，电量还剩15%，底盘质感太赞了！', 'https://img01.yzcdn.cn/vant/apple-1.jpg,https://img01.yzcdn.cn/vant/apple-2.jpg', 128, 45, 12, 1),
    (2, 2, '分享一下春日自驾的路线：南京->扬州->镇江。这一路充电站很多，完全没有续航焦虑。', 'https://img01.yzcdn.cn/vant/apple-3.jpg', 56, 22, 5, 0)
ON DUPLICATE KEY UPDATE
    `user_id` = VALUES(`user_id`),
    `content` = VALUES(`content`),
    `images` = VALUES(`images`),
    `like_count` = VALUES(`like_count`),
    `comment_count` = VALUES(`comment_count`),
    `share_count` = VALUES(`share_count`),
    `is_hot` = VALUES(`is_hot`);

INSERT INTO `charging_station` (`id`, `station_name`, `address`, `city_id`, `total_piles`, `available_piles`, `status`)
VALUES
    (1, '极氪南京大明路超充站', '南京市秦淮区大明路1号', '320100', 12, 8, 1),
    (2, '极氪南京雨花旗舰充电中心', '南京市雨花台区宁双路19号', '320100', 20, 5, 1),
    (3, '极氪南京奥体中心服务站', '南京市建邺区江东中路222号', '320100', 10, 0, 1)
ON DUPLICATE KEY UPDATE
    `station_name` = VALUES(`station_name`),
    `address` = VALUES(`address`),
    `city_id` = VALUES(`city_id`),
    `total_piles` = VALUES(`total_piles`),
    `available_piles` = VALUES(`available_piles`),
    `status` = VALUES(`status`);
