package io.student.rococo.utils;

import io.student.rococo.grpc.PageableRequest;
import io.student.rococo.grpc.SortDirection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


public class GrpcUtils {
    public static PageableRequest springPageableToGrpcPageableRequest(Pageable pageable) {
        PageableRequest.Builder builder = PageableRequest.newBuilder()
                .setPage(pageable.getPageNumber())
                .setSize(pageable.getPageSize());

        Sort sort = pageable.getSort();
        if (sort != null && sort.isSorted()) {
            Sort.Order order = sort.iterator().next(); // берём первый Order

            builder.setSortBy(order.getProperty());
            builder.setSortDir(order.isDescending()
                    ? SortDirection.DESC
                    : SortDirection.ASC);
        }

        return builder.build();
    }
}
