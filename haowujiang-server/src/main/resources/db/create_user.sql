CREATE TABLE `user` (
  `id` bigint unsigned NOT NULL COMMENT '用户ID',
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '昵称',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '密码密文',
  `role` tinyint NOT NULL DEFAULT '0' COMMENT '角色：0普通用户 1管理员',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0禁用 1正常',
  `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_admin_list` (`deleted`,`status`,`createTime`),
  KEY `idx_nickname` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

INSERT INTO `user` (`id`, `phone`, `nickname`, `password`, `role`, `status`, `deleted`)
VALUES
  (1, '13800000000', '演示管理员', '$2a$10$NYDPuZFNGt2tOCgBGtIwDe3.RJqw1Av/nAgp9/wBQkAR6FKCey0Dq', 1, 1, 0),
  (2, '13900000000', '演示玩家', '$2a$10$NYDPuZFNGt2tOCgBGtIwDe3.RJqw1Av/nAgp9/wBQkAR6FKCey0Dq', 0, 1, 0)
ON DUPLICATE KEY UPDATE
  `nickname` = VALUES(`nickname`),
  `password` = VALUES(`password`),
  `role` = VALUES(`role`),
  `status` = VALUES(`status`),
  `deleted` = 0;
