CREATE TABLE IF NOT EXISTS `seckillStock` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `generalCode` VARCHAR(64) NOT NULL COMMENT '武将编码',
    `totalStock` INT NOT NULL COMMENT '总库存',
    `availableStock` INT NOT NULL COMMENT '可用库存',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `ukGeneralCode` (`generalCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='武将秒杀库存表';
