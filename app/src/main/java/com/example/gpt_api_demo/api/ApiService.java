package com.example.gpt_api_demo.api;



import com.example.gpt_api_demo.request.ChatGPTRespond;
import com.example.gpt_api_demo.request.GPTImageRequest;
import com.example.gpt_api_demo.request.GPTRequest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("v1/chat/completions")
    Observable<Response<ChatGPTRespond>> getChatGPTRespond(@Body GPTRequest gptRequest);
    @POST("v1/chat/completions")
    Observable<Response<ChatGPTRespond>> getChatGPTImageRespond(@Body GPTImageRequest gptImageRequest);
}
