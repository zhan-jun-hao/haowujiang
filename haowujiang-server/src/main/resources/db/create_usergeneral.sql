CREATE TABLE IF NOT EXISTS `userGeneral` (
    `id` BIGINT NOT NULL COMMENT '涓婚敭 ID',
    `userId` BIGINT NOT NULL COMMENT '鐢ㄦ埛 ID',
    `generalCode` VARCHAR(64) NOT NULL COMMENT '姝﹀皢缂栫爜',
    `obtainedAt` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鑾峰緱鏃堕棿',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '閫昏緫鍒犻櫎锛?鏈垹闄?1宸插垹闄?,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ukUserGeneral` (`userId`, `generalCode`),
    KEY `idxUserId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐢ㄦ埛宸叉嫢鏈夋灏嗚〃';

INSERT INTO `userGeneral` (`id`, `userId`, `generalCode`)
VALUES
    (1001, 1, 'zhang-fei'),
    (1002, 2, 'zhang-fei')
ON DUPLICATE KEY UPDATE
    `updateTime` = CURRENT_TIMESTAMP,
    `deleted` = 0;

