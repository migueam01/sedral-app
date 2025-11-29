package com.mam.alicamp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;
    private static final String TAG = "AuthInterceptor";

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        if (originalRequest.url().toString().contains("/auth/login")) {
            return chain.proceed(originalRequest);
        }

        //Obtener el token de SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token != null && !token.isEmpty()) {
            Request newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();

            Log.d(TAG, "Agregando token a: " + originalRequest.url());
            return chain.proceed(newRequest);
        }
        return chain.proceed(originalRequest);
    }
}