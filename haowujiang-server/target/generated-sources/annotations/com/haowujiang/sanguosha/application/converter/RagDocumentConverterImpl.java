package com.haowujiang.sanguosha.application.converter;

import com.haowujiang.sanguosha.application.vo.rag.response.RagDocumentBasicVo;
import com.haowujiang.sanguosha.infrastructure.persistence.po.RagDocument;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-07-10T01:47:01+0800",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.15 (Oracle Corporation)"
)
public class RagDocumentConverterImpl implements RagDocumentConverter {

    @Override
    public RagDocumentBasicVo poToVo(RagDocument po) {
        if ( po == null ) {
            return null;
        }

        RagDocumentBasicVo ragDocumentBasicVo = new RagDocumentBasicVo();

        ragDocumentBasicVo.setId( po.getId() );
        ragDocumentBasicVo.setGeneralCode( po.getGeneralCode() );
        ragDocumentBasicVo.setTitle( po.getTitle() );
        ragDocumentBasicVo.setContent( po.getContent() );
        if ( po.getUpdateTime() != null ) {
            ragDocumentBasicVo.setUpdateTime( DateTimeFormatter.ISO_LOCAL_DATE_TIME.format( po.getUpdateTime() ) );
        }

        return ragDocumentBasicVo;
    }

    @Override
    public List<RagDocumentBasicVo> poToVo(List<RagDocument> po) {
        if ( po == null ) {
            return null;
        }

        List<RagDocumentBasicVo> list = new ArrayList<RagDocumentBasicVo>( po.size() );
        for ( RagDocument ragDocument : po ) {
            list.add( poToVo( ragDocument ) );
        }

        return list;
    }
}
