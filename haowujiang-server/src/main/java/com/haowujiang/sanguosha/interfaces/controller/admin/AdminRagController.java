package com.haowujiang.sanguosha.interfaces.controller.admin;

import com.haowujiang.sanguosha.application.service.RagApplicationService;
import com.haowujiang.sanguosha.infrastructure.common.PageResult;
import com.haowujiang.sanguosha.interfaces.vo.rag.request.CreateRagDocumentReqVo;
import com.haowujiang.sanguosha.interfaces.vo.rag.request.RagDocumentPageQueryReqVo;
import com.haowujiang.sanguosha.interfaces.vo.rag.response.RagDocumentBasicVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * admin端-RAG知识管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/rag/generals/{generalCode}/documents")
public class AdminRagController {

    private final RagApplicationService ragApplicationService;

    /**
     * 分页查询documents文档
     * @param generalCode
     * @param query
     * @return
     */
    @GetMapping
    public Result<PageResult<RagDocumentBasicVo>> listDocuments(@PathVariable("generalCode") String generalCode,
            @Validated RagDocumentPageQueryReqVo query) {
        return Result.success(ragApplicationService.pageQueryDocuments(generalCode, query));
    }

    /**
     * 增加文档
     * @param generalCode
     * @param request
     * @return
     */
    @PostMapping
    public Result<RagDocumentBasicVo> addDocument(@PathVariable("generalCode") String generalCode,
            @Valid @RequestBody CreateRagDocumentReqVo request) {
        return Result.success(ragApplicationService.addDocument(generalCode, request.getTitle(), request.getContent()));
    }
}
