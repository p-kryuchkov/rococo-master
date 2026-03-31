package io.student.rococo.utils;

import io.student.rococo.grpc.PageableRequest;
import io.student.rococo.grpc.SortDirection;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GrpcUtilsTest {

    @Test
    void mapPageableRequest() {
        final PageableRequest request = PageableRequest.newBuilder()
                .setPage(2)
                .setSize(10)
                .build();

        final PageRequest result = GrpcUtils.grpcPageableRequestToSpringPageRequest(request);

        assertEquals(2, result.getPageNumber());
        assertEquals(10, result.getPageSize());
        assertFalse(result.getSort().isSorted());
    }

    @Test
    void mapPageableRequestWithDefaultSize() {
        final PageableRequest request = PageableRequest.newBuilder()
                .build();

        final PageRequest result = GrpcUtils.grpcPageableRequestToSpringPageRequest(request);

        assertEquals(0, result.getPageNumber());
        assertEquals(20, result.getPageSize());
        assertFalse(result.getSort().isSorted());
    }

    @Test
    void mapPageableRequestWithCustomDefaultSize() {
        final PageableRequest request = PageableRequest.newBuilder()
                .build();

        final PageRequest result = GrpcUtils.grpcPageableRequestToSpringPageRequest(request, 50);

        assertEquals(0, result.getPageNumber());
        assertEquals(50, result.getPageSize());
        assertFalse(result.getSort().isSorted());
    }

    @Test
    void mapPageableRequestWithAscSort() {
        final PageableRequest request = PageableRequest.newBuilder()
                .setPage(1)
                .setSize(5)
                .setSortBy("title")
                .setSortDir(SortDirection.ASC)
                .build();

        final PageRequest result = GrpcUtils.grpcPageableRequestToSpringPageRequest(request);

        assertEquals(1, result.getPageNumber());
        assertEquals(5, result.getPageSize());
        assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor("title").getDirection());
    }

    @Test
    void mapPageableRequestWithDescSort() {
        final PageableRequest request = PageableRequest.newBuilder()
                .setPage(1)
                .setSize(5)
                .setSortBy("title")
                .setSortDir(SortDirection.DESC)
                .build();

        final PageRequest result = GrpcUtils.grpcPageableRequestToSpringPageRequest(request);

        assertEquals(1, result.getPageNumber());
        assertEquals(5, result.getPageSize());
        assertEquals(Sort.Direction.DESC, result.getSort().getOrderFor("title").getDirection());
    }

    @Test
    void mapPageableRequestWithSortByOnly() {
        final PageableRequest request = PageableRequest.newBuilder()
                .setSortBy("title")
                .build();

        final PageRequest result = GrpcUtils.grpcPageableRequestToSpringPageRequest(request);

        assertEquals(0, result.getPageNumber());
        assertEquals(20, result.getPageSize());
        assertEquals(Sort.Direction.ASC, result.getSort().getOrderFor("title").getDirection());
    }

    @Test
    void mapPageableRequestWithBlankSortBy() {
        final PageableRequest request = PageableRequest.newBuilder()
                .setSortBy("   ")
                .build();

        final PageRequest result = GrpcUtils.grpcPageableRequestToSpringPageRequest(request);

        assertEquals(0, result.getPageNumber());
        assertEquals(20, result.getPageSize());
        assertFalse(result.getSort().isSorted());
    }
}