CREATE TABLE IF NOT EXISTS `general` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `code` VARCHAR(64) NOT NULL COMMENT '武将唯一编码，例如 zhao-yun',
    `name` VARCHAR(64) NOT NULL COMMENT '武将名称',
    `title` VARCHAR(128) NOT NULL COMMENT '武将称号',
    `camp` VARCHAR(32) NOT NULL COMMENT '所属阵营',
    `hp` INT NOT NULL COMMENT '体力上限',
    `rarity` VARCHAR(64) NOT NULL COMMENT '稀有度或获取类型展示文案',
    `skillName` VARCHAR(64) NOT NULL COMMENT '技能名称',
    `skillSummary` VARCHAR(255) NOT NULL COMMENT '技能摘要',
    `unlockSource` INT NOT NULL COMMENT '获取来源：0 默认拥有，1 秒杀获取',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `ukGeneralCode` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='武将基础信息表';
