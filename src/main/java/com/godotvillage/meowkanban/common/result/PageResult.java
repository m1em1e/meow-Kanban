package com.godotvillage.meowkanban.common.result;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private List<T> records;

    private Long total;

    private Long pageIndex;

    private Long pageSize;

    private Long pages;

    public static <T> PageResult<T> of(List<T> records, Long total, Long pageIndex, Long pageSize, Long pages) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(records);
        result.setTotal(total);
        result.setPageIndex(pageIndex);
        result.setPageSize(pageSize);
        result.setPages(pages);
        return result;
    }
}
