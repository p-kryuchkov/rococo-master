package io.student.rococo.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

public class ErrorAttributes  extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> defaultMap = super.getErrorAttributes(webRequest, options);
        ApiError apiError = ApiError.fromAttributesMap(defaultMap);
        return apiError.toAttributesMap();
    }
}
