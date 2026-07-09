package com.haowujiang.sanguosha.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户武将持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("userGeneral")
public class UserGeneral extends BaseEntity {

    /**
     * 用户 ID。
     */
    @TableField("userId")
    private Long userId;

    /**
     * 武将编码。
     */
    @TableField("generalCode")
    private String generalCode;

    /**
     * 获得时间。
     */
    @TableField("obtainedAt")
    private LocalDateTime obtainedAt;
}
