package io.student.rococo.utils;

import io.student.rococo.grpc.PageableRequest;
import io.student.rococo.grpc.SortDirection;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class GrpcUtils {
    public static PageRequest grpcPageableRequestToSpringPageRequest(PageableRequest request) {
        return grpcPageableRequestToSpringPageRequest(request, 20);
    }

    public static PageRequest grpcPageableRequestToSpringPageRequest(PageableRequest request, int sizeDefault) {
        final int page = request.hasPage() ? request.getPage() : 0;
        final int size = request.hasSize() ? request.getSize() : sizeDefault;

        Sort sort = Sort.unsorted();
        if (request.hasSortBy() && !request.getSortBy().isBlank()) {
            sort = Sort.by(request.getSortBy()).ascending();
            if (request.hasSortDir() && request.getSortDir() == SortDirection.DESC)
                sort = Sort.by(request.getSortBy()).descending();
        }
        return PageRequest.of(page, size, sort);
    }
}
