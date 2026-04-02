package io.student.rococo.utils;

import io.student.rococo.grpc.PageableRequest;
import io.student.rococo.grpc.SortDirection;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GrpcUtilsTest {

    @Test
    void mapPageableWithoutSort() {
        final Pageable pageable = PageRequest.of(1, 20);

        final PageableRequest result = GrpcUtils.springPageableToGrpcPageableRequest(pageable);

        assertEquals(1, result.getPage());
        assertEquals(20, result.getSize());
        assertFalse(result.hasSortBy());
        assertFalse(result.hasSortDir());
    }

    @Test
    void mapPageableWithAscSort() {
        final Pageable pageable = PageRequest.of(0, 10, Sort.by("title").ascending());

        final PageableRequest result = GrpcUtils.springPageableToGrpcPageableRequest(pageable);

        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals("title", result.getSortBy());
        assertEquals(SortDirection.ASC, result.getSortDir());
    }

    @Test
    void mapPageableWithDescSort() {
        final Pageable pageable = PageRequest.of(2, 5, Sort.by("name").descending());

        final PageableRequest result = GrpcUtils.springPageableToGrpcPageableRequest(pageable);

        assertEquals(2, result.getPage());
        assertEquals(5, result.getSize());
        assertEquals("name", result.getSortBy());
        assertEquals(SortDirection.DESC, result.getSortDir());
    }

    @Test
    void mapPageableWithFirstSortOrder() {
        final Pageable pageable = PageRequest.of(
                0,
                15,
                Sort.by(Sort.Order.desc("name"), Sort.Order.asc("title"))
        );

        final PageableRequest result = GrpcUtils.springPageableToGrpcPageableRequest(pageable);

        assertEquals(0, result.getPage());
        assertEquals(15, result.getSize());
        assertEquals("name", result.getSortBy());
        assertEquals(SortDirection.DESC, result.getSortDir());
    }

    @Test
    void mapPageableWithNullSort() {
        final Pageable pageable = mock(Pageable.class);
        when(pageable.getPageNumber()).thenReturn(3);
        when(pageable.getPageSize()).thenReturn(7);
        when(pageable.getSort()).thenReturn(null);

        final PageableRequest result = GrpcUtils.springPageableToGrpcPageableRequest(pageable);

        assertEquals(3, result.getPage());
        assertEquals(7, result.getSize());
        assertFalse(result.hasSortBy());
        assertFalse(result.hasSortDir());
    }
}