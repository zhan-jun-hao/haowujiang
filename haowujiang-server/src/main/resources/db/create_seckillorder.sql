CREATE TABLE IF NOT EXISTS `seckillOrder` (
    `id` BIGINT NOT NULL COMMENT '涓婚敭 ID',
    `orderNo` VARCHAR(64) NOT NULL COMMENT '绉掓潃璁㈠崟鍙?,
    `userId` BIGINT NOT NULL COMMENT '鐢ㄦ埛 ID',
    `generalCode` VARCHAR(64) NOT NULL COMMENT '姝﹀皢缂栫爜',
    `status` INT NOT NULL COMMENT '璁㈠崟鐘舵€侊細0 宸插垱寤?1 闃熷垪涓?2 澶辫触',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '閫昏緫鍒犻櫎锛?鏈垹闄?1宸插垹闄?,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ukOrderNo` (`orderNo`),
    UNIQUE KEY `ukUserGeneralOrder` (`userId`, `generalCode`),
    KEY `idxCreateTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='姝﹀皢绉掓潃璁㈠崟琛?;

