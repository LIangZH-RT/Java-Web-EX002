package com.liang.dto;

import java.util.List;

public class PageResult<T> {
    private final List<T> records;
    private final int page;
    private final int pageSize;
    private final long total;
    private final int totalPages;

    public PageResult(List<T> records, int page, int pageSize, long total) {
        this.records = records;
        this.page = page;
        this.pageSize = pageSize;
        this.total = total;
        this.totalPages = (int) ((total + pageSize - 1) / pageSize);
    }

    public List<T> getRecords() {
        return records;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotal() {
        return total;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
