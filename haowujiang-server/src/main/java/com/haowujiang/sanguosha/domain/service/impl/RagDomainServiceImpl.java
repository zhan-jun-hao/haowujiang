package com.haowujiang.sanguosha.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haowujiang.sanguosha.domain.service.RagDomainService;
import com.haowujiang.sanguosha.infrastructure.persistence.mapper.RagDocumentMapper;
import com.haowujiang.sanguosha.infrastructure.persistence.po.RagDocument;
import com.haowujiang.sanguosha.interfaces.vo.rag.request.RagDocumentPageQueryReqVo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RagDomainServiceImpl implements RagDomainService {

    private final RagDocumentMapper ragDocumentMapper;

    @Override
    public List<RagDocument> listDocuments(String generalCode) {
        LambdaQueryWrapper<RagDocument> wrapper = Wrappers.lambdaQuery(RagDocument.class)
                .eq(RagDocument::getDeleted, 0)
                .eq(RagDocument::getGeneralCode, generalCode)
                .orderByDesc(RagDocument::getUpdateTime);
        return ragDocumentMapper.selectList(wrapper);
    }

    @Override
    public IPage<RagDocument> pageQuery(String generalCode, RagDocumentPageQueryReqVo query) {
        LambdaQueryWrapper<RagDocument> wrapper = Wrappers.lambdaQuery(RagDocument.class)
                .eq(RagDocument::getDeleted, 0)
                .eq(RagDocument::getGeneralCode, generalCode)
                .orderByDesc(RagDocument::getUpdateTime);
        return ragDocumentMapper.selectPage(new Page<>(query.getCurrent(), query.getSize()), wrapper);
    }

    @Override
    public RagDocument addDocument(String generalCode, String title, String content) {
        RagDocument document = new RagDocument();
        document.setGeneralCode(generalCode);
        document.setTitle(title);
        document.setContent(content);
        ragDocumentMapper.insert(document);
        return document;
    }
}
