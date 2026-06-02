package com.financialapp.notifications.web.controller.mapper;

import com.financialapp.notifications.domain.model.pagination.PageResult;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class PageResultMapperTest {

    @Test
    void toPage_wrapsPageResultContentAndTotal() {
        // Given a domain page result and a pageable (utility class is instantiable)
        new PageResultMapper();
        PageResult<String> pageResult = new PageResult<>(List.of("a", "b"), 0, 2, 5);
        Pageable pageable = PageRequest.of(0, 2);

        // When converting to a Spring Page
        Page<String> page = PageResultMapper.toPage(pageResult, pageable, Function.identity());

        // Then content and total elements are carried over
        assertThat(page.getContent()).containsExactly("a", "b");
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isZero();
    }
}
