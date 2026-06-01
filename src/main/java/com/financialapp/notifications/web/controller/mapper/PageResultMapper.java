package com.financialapp.notifications.web.controller.mapper;

import com.financialapp.notifications.domain.model.response.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PageResultMapper {

    public static <T> Page<T> toPage(PageResult<T> pageResult, Pageable pageable) {
        return new PageImpl<>(
                pageResult.content(),
                pageable,
                pageResult.totalElements()
        );
    }
}
