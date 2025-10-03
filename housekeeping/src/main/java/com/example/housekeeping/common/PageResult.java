package com.example.housekeeping.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页结果类
 */
@Data
public class PageResult<T> {
    
    private List<T> records;
    private Long total;
    private Integer current;
    private Integer size;
    private Integer pages;

    public PageResult() {}

    public PageResult(List<T> records, Long total, Integer current, Integer size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
    }

    /**
     * 从Spring Data Page对象创建PageResult
     */
    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
            page.getContent(),
            page.getTotalElements(),
            page.getNumber() + 1, // Spring Data页码从0开始，转换为从1开始
            page.getSize()
        );
    }
}
