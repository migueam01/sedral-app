package com.mam.alicamp.api;

import com.mam.alicamp.servicios.LoginRequest;
import com.mam.alicamp.servicios.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
}