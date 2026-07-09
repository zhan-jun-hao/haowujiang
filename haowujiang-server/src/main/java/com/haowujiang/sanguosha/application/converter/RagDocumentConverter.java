package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.application.vo.rag.response.RagDocumentBasicVo;
import com.haowujiang.sanguosha.infrastructure.persistence.po.RagDocument;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RagDocumentConverter {

    RagDocumentConverter INSTANCE = Mappers.getMapper(RagDocumentConverter.class);

    RagDocumentBasicVo poToVo(RagDocument po);

    List<RagDocumentBasicVo> poToVo(List<RagDocument> po);
}
