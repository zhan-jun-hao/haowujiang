package com.haowujiang.sanguosha.domain.service;

import com.haowujiang.sanguosha.infrastructure.persistence.po.General;
import java.util.List;
import java.util.Set;

public interface GeneralDomainService {

    List<General> listGenerals();

    General getGeneralById(Long id);

    General getGeneralByCode(String code);

    List<General> listOwnedGeneralsByCode(Set<String> code);
}
