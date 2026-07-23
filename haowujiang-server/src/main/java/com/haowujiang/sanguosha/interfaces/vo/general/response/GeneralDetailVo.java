package com.haowujiang.sanguosha.interfaces.vo.general.response;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * 武将详情视图对象。
 */
@Data
public class GeneralDetailVo {

    /**
     * 武将基础信息。
     */
    private GeneralBasicVo basicVo;

    /**
     * 创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 更新时间。
     */
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标识。
     */
    private Integer deleted;
}
