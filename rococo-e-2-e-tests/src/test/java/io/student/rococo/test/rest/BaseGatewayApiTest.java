package io.student.rococo.test.rest;

import io.student.rococo.jupiter.extension.ApiLoginExtension;
import io.student.rococo.service.api.GatewayApiClient;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public abstract class BaseGatewayApiTest {
    protected static final String IMAGE = "data:image/png;base64,"
            + "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAusB9Wn4nK0AAAAASUVORK5CYII=";
    protected final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    protected String bearer(String token) {
        return "Bearer " + token;
    }
    protected Pageable pageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    protected Pageable defaultPageable() {
        return PageRequest.of(0, 10);
    }
}
