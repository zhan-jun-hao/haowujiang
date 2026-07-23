package com.haowujiang.sanguosha.infrastructure.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class PageQuery {

    /**
     * 当前页
     */
    @Min(value = 1, message = "当前页不能小于1")
    private Long current = 1L;

    /**
     * 每页数量
     */
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Long size = 10L;

}
