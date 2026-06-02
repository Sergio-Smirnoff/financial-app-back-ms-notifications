package com.financialapp.notifications.domain.model.pagination;

import java.util.List;

public record PageResult<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages
) {
    public PageResult(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this(content, pageNumber, pageSize, totalElements,
                (int) Math.ceil((double) totalElements / pageSize));
    }

    public boolean hasNext() {
        return pageNumber < totalPages - 1;
    }

    public boolean hasPrevious() {
        return pageNumber > 0;
    }
}