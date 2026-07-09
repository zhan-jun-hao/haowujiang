CREATE DATABASE IF NOT EXISTS `haowujiang`
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE `haowujiang`;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `phone` VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '手机号',
    `nickname` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '昵称',
    `password` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '密码密文',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0普通用户 1管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1正常',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE IF NOT EXISTS `general` (
    `id` BIGINT NOT NULL COMMENT '涓婚敭 ID',
    `code` VARCHAR(64) NOT NULL COMMENT '姝﹀皢鍞竴缂栫爜锛屼緥濡?zhao-yun',
    `name` VARCHAR(64) NOT NULL COMMENT '姝﹀皢鍚嶇О',
    `title` VARCHAR(128) NOT NULL COMMENT '姝﹀皢绉板彿',
    `camp` VARCHAR(32) NOT NULL COMMENT '鎵€灞為樀钀?,
    `hp` INT NOT NULL COMMENT '浣撳姏涓婇檺',
    `rarity` VARCHAR(64) NOT NULL COMMENT '绋€鏈夊害鎴栬幏鍙栫被鍨嬪睍绀烘枃妗?,
    `skillName` VARCHAR(64) NOT NULL COMMENT '鎶€鑳藉悕绉?,
    `skillSummary` VARCHAR(255) NOT NULL COMMENT '鎶€鑳芥憳瑕?,
    `unlockSource` INT NOT NULL COMMENT '鑾峰彇鏉ユ簮锛? 榛樿鎷ユ湁锛? 绉掓潃鑾峰彇',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '閫昏緫鍒犻櫎锛?鏈垹闄?1宸插垹闄?,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ukGeneralCode` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='姝﹀皢鍩虹淇℃伅琛?;

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

CREATE TABLE IF NOT EXISTS `ragDocument` (
    `id` BIGINT NOT NULL COMMENT '涓婚敭 ID',
    `generalCode` VARCHAR(64) NOT NULL COMMENT '姝﹀皢缂栫爜',
    `title` VARCHAR(128) NOT NULL COMMENT '鐭ヨ瘑鏉＄洰鏍囬',
    `content` TEXT NOT NULL COMMENT '鐭ヨ瘑鏉＄洰鍐呭锛岀敤浜庢灏嗕笓灞?RAG 妫€绱?,
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '閫昏緫鍒犻櫎锛?鏈垹闄?1宸插垹闄?,
    PRIMARY KEY (`id`),
    KEY `idxGeneralCode` (`generalCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='姝﹀皢 RAG 鐭ヨ瘑搴撴枃妗ｈ〃';

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

CREATE TABLE IF NOT EXISTS `seckillStock` (
    `id` BIGINT NOT NULL COMMENT '涓婚敭 ID',
    `generalCode` VARCHAR(64) NOT NULL COMMENT '姝﹀皢缂栫爜',
    `totalStock` INT NOT NULL COMMENT '鎬诲簱瀛?,
    `availableStock` INT NOT NULL COMMENT '鍙敤搴撳瓨',
    `createTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
    `updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '閫昏緫鍒犻櫎锛?鏈垹闄?1宸插垹闄?,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ukGeneralCode` (`generalCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='姝﹀皢绉掓潃搴撳瓨琛?;

INSERT INTO `general` (`id`, `code`, `name`, `title`, `camp`, `hp`, `rarity`, `skillName`, `skillSummary`, `unlockSource`)
VALUES
    (1, 'zhao-yun', '璧典簯', '甯稿北璧靛瓙榫?, '铚€', 4, '绉掓潃闄愬畾', '榫欒儐', '鏉€褰撻棯锛岄棯褰撴潃銆?, 1),
    (2, 'zhang-fei', '寮犻', '鐕曚汉寮犵考寰?, '铚€', 4, '榛樿鎷ユ湁', '鍜嗗摦', '鍑烘潃娆℃暟涓嶅彈姣忓洖鍚堜竴娆￠檺鍒躲€?, 0)
ON DUPLICATE KEY UPDATE
    `name` = VALUES(`name`),
    `title` = VALUES(`title`),
    `camp` = VALUES(`camp`),
    `hp` = VALUES(`hp`),
    `rarity` = VALUES(`rarity`),
    `skillName` = VALUES(`skillName`),
    `skillSummary` = VALUES(`skillSummary`),
    `unlockSource` = VALUES(`unlockSource`),
    `deleted` = 0;

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

INSERT INTO `userGeneral` (`id`, `userId`, `generalCode`)
VALUES
    (1001, 1, 'zhang-fei'),
    (1002, 2, 'zhang-fei')
ON DUPLICATE KEY UPDATE
    `updateTime` = CURRENT_TIMESTAMP,
    `deleted` = 0;

INSERT INTO `ragDocument` (`id`, `generalCode`, `title`, `content`)
VALUES
    (2001, 'zhao-yun', '榫欒儐鍩虹绛栫暐', '璧典簯鍙互鎶婃潃褰撻棯銆侀棯褰撴潃銆傞槻瀹堟椂浼樺厛淇濈暀鏉€锛岃繘鏀绘椂鍙互鎶婇棯杞寲涓烘潃銆?),
    (2002, 'zhao-yun', '閰掓潃绛栫暐', '鑻ユ墜涓悓鏃舵湁閰掑拰鏉€锛屼笖鏁屾柟浣撳姏杈冮珮锛屽彲浠ュ厛浣跨敤閰掓彁楂樹笅涓€寮犳潃鐨勪激瀹炽€?)
ON DUPLICATE KEY UPDATE
    `title` = VALUES(`title`),
    `content` = VALUES(`content`),
    `deleted` = 0;

INSERT INTO `seckillStock` (`id`, `generalCode`, `totalStock`, `availableStock`)
VALUES (3001, 'zhao-yun', 12, 12)
ON DUPLICATE KEY UPDATE
    `totalStock` = VALUES(`totalStock`),
    `availableStock` = VALUES(`availableStock`),
    `deleted` = 0;

