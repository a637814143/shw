package com.example.housekeeping.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 分页响应结果类
 */
@Data
public class PageResult<T> {
    
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean first;
    private boolean last;
    
    public PageResult() {}
    
    public PageResult(Page<T> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.currentPage = page.getNumber() + 1; // Spring Data页码从0开始，转换为从1开始
        this.pageSize = page.getSize();
        this.first = page.isFirst();
        this.last = page.isLast();
    }
    
    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(page);
    }
}
