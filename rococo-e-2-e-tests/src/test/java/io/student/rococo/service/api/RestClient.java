package io.student.rococo.service.api;


import io.qameta.allure.okhttp3.AllureOkHttp3;
import io.student.rococo.api.core.ThreadSafeCookieStore;
import io.student.rococo.config.Config;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.net.CookieManager;
import java.net.CookiePolicy;

@ParametersAreNonnullByDefault
public class RestClient {
    protected static final Config CFG = Config.getInstance();

    private final OkHttpClient okHttpClient;
    private final Retrofit retrofit;

    public RestClient(String baseUrl) {
        this(baseUrl, JacksonConverterFactory.create(), false, HttpLoggingInterceptor.Level.BASIC);
    }

    public RestClient(String baseUrl, boolean followRedirect) {
        this(baseUrl, JacksonConverterFactory.create(), followRedirect, HttpLoggingInterceptor.Level.BASIC);
    }

    public RestClient(String baseUrl, boolean followRedirect, @Nullable Interceptor... interceptors) {
        this(baseUrl, JacksonConverterFactory.create(), followRedirect , HttpLoggingInterceptor.Level.BASIC, interceptors);
    }
    public RestClient(String baseUrl, boolean followRedirect, HttpLoggingInterceptor.Level level, @Nullable Interceptor... interceptors) {
        this(baseUrl, JacksonConverterFactory.create(), followRedirect , level, interceptors);
    }

    @SafeVarargs
    public RestClient(String baseUrl, Converter.Factory converterFactory, boolean followRedirect, HttpLoggingInterceptor.Level level, @Nullable Interceptor... interceptors) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirect)
                .cookieJar(
                        new JavaNetCookieJar(
                                new CookieManager(
                                        ThreadSafeCookieStore.INSTANCE,
                                        CookiePolicy.ACCEPT_ALL
                                )
                        )
                );
        if (interceptors != null)
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level));
        builder.addNetworkInterceptor(new AllureOkHttp3());
        this.okHttpClient = builder.build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(converterFactory)
                .client(okHttpClient).build();
    }

    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
