package com.haowujiang.sanguosha.infrastructure.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    /**
     * 总条数
     */
    private Long total;

    /**
     * 当前页
     */
    private Long curPage;

    /**
     * 每页大小
     */
    private Long size;

    /**
     * 总页数
     */
    private Long pages;

    /**
     * 数据列表
     */
    private List<T> records;

    public static <T> PageResult<T> success(IPage<?> page, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setCurPage(page.getCurrent());
        result.setSize(page.getSize());
        result.setPages(page.getPages());
        result.setRecords(records);
        return result;
    }
}
