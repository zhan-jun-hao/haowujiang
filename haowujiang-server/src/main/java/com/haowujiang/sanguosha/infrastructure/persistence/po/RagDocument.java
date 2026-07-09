package com.haowujiang.sanguosha.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 武将 RAG 知识库文档持久化对象。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ragDocument")
public class RagDocument extends BaseEntity {

    /**
     * 武将编码。
     */
    @TableField("generalCode")
    private String generalCode;

    /**
     * 知识条目标题。
     */
    private String title;

    /**
     * 知识条目内容，用于武将专属 RAG 检索。
     */
    private String content;
}
