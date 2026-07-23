package com.haowujiang.sanguosha.domain.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import com.haowujiang.sanguosha.interfaces.vo.general.request.GeneralPageQueryReqVo;
import java.util.List;
import java.util.Set;

public interface GeneralDomainService {

    IPage<General> pageQuery(GeneralPageQueryReqVo query);

    General getGeneralById(Long id);

    General getGeneralByCode(String code);

    List<General> listOwnedGeneralsByCode(Set<String> code);
}
