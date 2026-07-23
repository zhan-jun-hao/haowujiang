CREATE TABLE `seckillorder` (
                                `id` bigint NOT NULL COMMENT '主键 ID',
                                `orderNo` varchar(64) NOT NULL COMMENT '秒杀订单号',
                                `userId` bigint NOT NULL COMMENT '用户 ID',
                                `generalCode` varchar(64) NOT NULL COMMENT '武将编码',
                                `status` int NOT NULL COMMENT '订单状态：0 已创建 1 队列中 2 失败',
                                `createTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updateTime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0未删除 1已删除',
                                PRIMARY KEY (`id`),
                                UNIQUE KEY `ukOrderNo` (`orderNo`),
                                UNIQUE KEY `ukUserGeneralOrder` (`userId`,`generalCode`),
                                KEY `idxCreateTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='武将秒杀订单表';