package com.haowujiang.sanguosha.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 持久化对象基础字段。
 */
@Data
public class BaseEntity {

    /**
     * 主键 ID。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建时间。
     */
    @TableField("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    @TableField("updateTime")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0 未删除，1 已删除。
     */
    @TableLogic
    private Integer deleted;
}
