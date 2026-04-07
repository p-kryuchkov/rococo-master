package io.student.rococo.api.core;

import io.student.rococo.jupiter.extension.ApiLoginExtension;
import okhttp3.Interceptor;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

public class CodeInterceptor implements Interceptor {
    @Nonnull
    @Override
    public Response intercept(@Nonnull Chain chain) throws IOException {
       final Response response = chain.proceed(chain.request());
       if (response.isRedirect()){
           String location = Objects.requireNonNull(response.header("Location"));
           if (location.contains("code=")){
              ApiLoginExtension.setCode(StringUtils.substringAfter(location, "code="));
           }
       }
       return response;
    }
}
