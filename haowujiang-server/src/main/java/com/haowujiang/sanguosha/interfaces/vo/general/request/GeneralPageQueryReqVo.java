package com.haowujiang.sanguosha.interfaces.vo.general.request;

import com.haowujiang.sanguosha.infrastructure.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 武将分页查询请求对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GeneralPageQueryReqVo extends PageQuery {

    /**
     * 武将名称（模糊搜索）
     */
    private String name;

    /**
     * 所属阵营
     */
    private String camp;
}
