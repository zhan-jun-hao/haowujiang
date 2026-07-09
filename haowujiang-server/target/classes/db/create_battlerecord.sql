CREATE TABLE IF NOT EXISTS `battleRecord` (
    `id` BIGINT NOT NULL COMMENT '涓婚敭 ID',
    `battleNo` VARCHAR(64) NOT NULL COMMENT '瀵规垬缂栧彿',
    `userId` BIGINT NOT NULL COMMENT '鐢ㄦ埛 ID',
    `playerGeneralCode` VARCHAR(64) NOT NULL COMMENT '鐜╁姝﹀皢缂栫爜',
    `enemyGeneralCode` VARCHAR(64) NOT NULL COMMENT '鏁屾柟姝﹀皢缂栫爜',
    `round` INT NOT NULL DEFAULT 1 COMMENT '褰撳墠鍥炲悎鏁?,
    `currentSide` VARCHAR(16) NOT NULL COMMENT '褰撳墠琛屽姩鏂癸細player 鐜╁锛宔nemy 鏁屾柟',
    `playerHp` INT NOT NULL COMMENT '鐜╁褰撳墠浣撳姏',
    `enemyHp` INT NOT NULL COMMENT '鏁屾柟褰撳墠浣撳姏',
    `winner` VARCHAR(16) DEFAULT NULL COMMENT '鑳滃埄鏂癸細player 鐜╁锛宔nemy 鏁屾柟',
    `stateJson` LONGTEXT NOT NULL COMMENT '瀵规垬蹇収 JSON锛屼繚瀛樼墝鍫嗐€佹墜鐗屻€佸純鐗屽爢銆佹棩蹇楀拰 AI 鎬濊€?,
    `status` INT NOT NULL COMMENT '瀵规垬鐘舵€侊細0 杩涜涓紝1 宸茬粨鏉?,
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '閫昏緫鍒犻櫎锛?鏈垹闄?1宸插垹闄?,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ukBattleNo` (`battleNo`),
    KEY `idxUserId` (`userId`),
    KEY `idxCreateTime` (`createTime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='姝﹀皢瀵规垬璁板綍琛?;

