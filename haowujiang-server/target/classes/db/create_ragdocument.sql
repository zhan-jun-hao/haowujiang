CREATE TABLE IF NOT EXISTS `ragDocument` (
    `id` BIGINT NOT NULL COMMENT '主键 ID',
    `generalCode` VARCHAR(64) NOT NULL COMMENT '武将编码',
    `title` VARCHAR(128) NOT NULL COMMENT '知识条目标题',
    `content` TEXT NOT NULL COMMENT '知识条目内容，用于武将专属 RAG 检索',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    KEY `idxGeneralCode` (`generalCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='武将 RAG 知识库文档表';
