package io.student.rococo.exception;

import java.util.List;
import java.util.Map;

public class ApiError {
    /*
    {
  "apiVersion": "2.0",
  "error": {
    "code": 404,
    "message": "File Not Found",
    "errors": [{
      "domain": "Calendar",
      "reason": "ResourceNotFoundException",
      "message": "File Not Found"
    }]
  }
}
     */

    private final Error error;

    public ApiError(Error error) {
        this.error = error;
    }

    public ApiError(String code,
                    String message,
                    String domain,
                    String reason) {
        this.error = new Error(code,
                message,
                List.of(new ErrorItem(domain, reason, message)));
    }

    public static ApiError fromAttributesMap(Map<String, Object> attributesMap) {
        return new ApiError(
                ((Integer) attributesMap.get("status")).toString(),
                ((String) attributesMap.getOrDefault("error", "No message found")),
                ((String) attributesMap.getOrDefault("path", "No path found")),
                ((String) attributesMap.getOrDefault("error", "No message found")));
    }

    public Map<String, Object> toAttributesMap(){
        return Map.of("error", error);
    }

    private record Error(String code,
                         String message,
                         List<ErrorItem> error) {
    }

    private record ErrorItem(String domain,
                             String reason,
                             String message) {
    }

    public Error getError() {
        return error;
    }
}
