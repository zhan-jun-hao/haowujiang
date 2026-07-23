package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.interfaces.vo.rag.response.RagDocumentBasicVo;
import com.haowujiang.sanguosha.infrastructure.persistence.po.RagDocument;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RagDocumentConverter {

    RagDocumentConverter INSTANCE = Mappers.getMapper(RagDocumentConverter.class);

    RagDocumentBasicVo poToVo(RagDocument po);

    List<RagDocumentBasicVo> poToVo(List<RagDocument> po);
}
