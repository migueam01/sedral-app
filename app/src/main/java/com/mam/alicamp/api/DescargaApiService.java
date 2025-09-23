package com.mam.alicamp.api;

import com.mam.alicamp.db.entidades.Descarga;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface DescargaApiService {

    @GET("descargas")
    Call<List<Descarga>> getDescargas();

    @POST("descargas")
    Call<Descarga> createDescarga(@Body Descarga descarga);

    @PUT("descargas")
    Call<Descarga> updateDescarga(@Body Descarga descarga);

    @DELETE("descargas/{id}")
    Call<Void> deleteDescarga(@Path("id") Integer id);
}
