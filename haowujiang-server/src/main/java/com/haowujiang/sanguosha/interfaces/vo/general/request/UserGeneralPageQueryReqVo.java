package com.haowujiang.sanguosha.interfaces.vo.general.request;

import com.haowujiang.sanguosha.infrastructure.common.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户武将分页查询请求对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserGeneralPageQueryReqVo extends PageQuery {

    // 仅分页，userId 从请求路径或登录上下文获取
}
