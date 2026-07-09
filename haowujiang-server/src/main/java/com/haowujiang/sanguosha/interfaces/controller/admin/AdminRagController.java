package com.haowujiang.sanguosha.interfaces.controller.admin;

import com.haowujiang.sanguosha.application.service.RagApplicationService;
import com.haowujiang.sanguosha.application.vo.rag.request.CreateRagDocumentReqVo;
import com.haowujiang.sanguosha.application.vo.rag.response.RagDocumentBasicVo;
import com.haowujiang.sanguosha.infrastructure.common.Result;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/rag/generals/{generalCode}/documents")
public class AdminRagController {

    private final RagApplicationService ragApplicationService;

    @GetMapping
    public Result<List<RagDocumentBasicVo>> listDocuments(@PathVariable("generalCode") String generalCode) {
        return Result.success(ragApplicationService.listDocuments(generalCode));
    }

    @PostMapping
    public Result<RagDocumentBasicVo> addDocument(@PathVariable("generalCode") String generalCode,
            @Valid @RequestBody CreateRagDocumentReqVo request) {
        return Result.success(ragApplicationService.addDocument(generalCode, request.getTitle(), request.getContent()));
    }
}
