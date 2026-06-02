package com.financialapp.notifications.domain.model.response;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PageResultTest {

    @Test
    void compactConstructor_derivesTotalPages() {
        // Given 5 elements at page size 2 / When built via the 4-arg ctor
        PageResult<String> page = new PageResult<>(List.of("a", "b"), 0, 2, 5);

        // Then total pages is ceil(5/2) = 3
        assertThat(page.totalPages()).isEqualTo(3);
        assertThat(page.content()).containsExactly("a", "b");
        assertThat(page.pageNumber()).isZero();
        assertThat(page.pageSize()).isEqualTo(2);
        assertThat(page.totalElements()).isEqualTo(5);
    }

    @Test
    void hasNext_trueWhenMorePagesRemain_falseOnLast() {
        // Given a first page with 3 total pages
        PageResult<String> first = new PageResult<>(List.of("a"), 0, 1, 3);
        // And a last page
        PageResult<String> last = new PageResult<>(List.of("c"), 2, 1, 3);

        // Then hasNext reflects remaining pages
        assertThat(first.hasNext()).isTrue();
        assertThat(last.hasNext()).isFalse();
    }

    @Test
    void hasPrevious_trueAfterFirstPage_falseOnFirst() {
        // Given the first and a later page
        PageResult<String> first = new PageResult<>(List.of("a"), 0, 1, 3);
        PageResult<String> second = new PageResult<>(List.of("b"), 1, 1, 3);

        // Then hasPrevious reflects the page index
        assertThat(first.hasPrevious()).isFalse();
        assertThat(second.hasPrevious()).isTrue();
    }

    @Test
    void equalsHashCodeToString_distinguishInstances() {
        // Given two equal and one different page
        PageResult<String> a = new PageResult<>(List.of("a"), 0, 1, 1, 1);
        PageResult<String> b = new PageResult<>(List.of("a"), 0, 1, 1, 1);
        PageResult<String> c = new PageResult<>(List.of("b"), 0, 1, 1, 1);

        // Then it behaves as a record value
        assertThat(a).isEqualTo(b).hasSameHashCodeAs(b).isNotEqualTo(c);
        assertThat(a.toString()).contains("PageResult");
    }
}
