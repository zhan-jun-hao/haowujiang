package com.haowujiang.sanguosha.interfaces.vo.rag.request;

import com.haowujiang.sanguosha.infrastructure.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RAG 文档分页查询请求对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RagDocumentPageQueryReqVo extends PageQuery {

    // 仅分页，generalCode 从请求路径获取
}
