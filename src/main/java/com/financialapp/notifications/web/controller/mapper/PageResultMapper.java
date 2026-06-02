package com.financialapp.notifications.web.controller.mapper;

import com.financialapp.notifications.domain.model.pagination.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.function.Function;

public class PageResultMapper {

    public static <T, R> Page<R> toPage(PageResult<T> pageResult, Pageable pageable, Function<T, R> mapper) {
        return new PageImpl<>(
                pageResult.content().stream().map(mapper).toList(),
                pageable,
                pageResult.totalElements());
    }
}
