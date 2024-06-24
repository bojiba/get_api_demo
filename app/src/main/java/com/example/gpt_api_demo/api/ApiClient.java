package com.example.gpt_api_demo.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String API_TOKEN = "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";
    private static final ApiClient GPTApiInstance = new ApiClient();

    private final ApiService mApiService;
    public ApiClient() {
        // 創建一個OkHttpClient實例，並設置一個Interceptor，用於在每個請求中添加Authorization header
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor(new Interceptor() {
                    @NotNull
                    @Override
                    public Response intercept(@NotNull Chain chain) throws IOException {
                        Request originalRequest = chain.request();
                        // 使用新的Request.Builder創建一個請求，將token添加到header中
                        Request newRequest = originalRequest.newBuilder()
                                .header("Authorization", "Bearer " + API_TOKEN)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        mApiService = retrofit.create(ApiService.class);
    }
    public static ApiClient getGPTApiInstance() {
        return GPTApiInstance;
    }

    public ApiService getApiService(){
        return mApiService;
    }
}
