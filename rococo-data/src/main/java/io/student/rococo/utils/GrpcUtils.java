package io.student.rococo.utils;

import io.student.rococo.grpc.PageableRequest;
import io.student.rococo.grpc.SortDirection;
import jakarta.annotation.Nonnull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class GrpcUtils {
    @Nonnull
    public static PageRequest grpcPageableRequestToSpringPageRequest(@Nonnull PageableRequest request) {
        return grpcPageableRequestToSpringPageRequest(request, 20);
    }

    @Nonnull
    public static PageRequest grpcPageableRequestToSpringPageRequest(@Nonnull PageableRequest request, int sizeDefault) {
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
