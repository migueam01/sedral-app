package com.mam.alicamp.api;

import android.content.Context;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitCliente {

    public static Retrofit retrofit;
    private static final String BASE_URL = "http://192.168.100.12:8080/v1/";

    public static Retrofit getCliente(Context context) {
        if (retrofit == null) {
            HttpLoggingInterceptor.Logger logger = s -> Log.d("HTTP_LOGS", s);

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(logger);
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            if (context != null) {
                httpClient.addInterceptor(new AuthInterceptor(context));
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}