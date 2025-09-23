package com.mam.alicamp.api;

import com.mam.alicamp.db.entidades.Proyecto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProyectoApiService {

    @GET("proyectos/android")
    Call<List<Proyecto>> getProyectos();

    @POST("proyectos/crea-android")
    Call<Proyecto> createProyecto(@Body Proyecto proyecto);

    @PUT("proyectos/actualiza-android")
    Call<Proyecto> updateProyecto(@Body Proyecto proyecto);

    @DELETE("proyectos/{id}")
    Call<Void> deleteProyecto(@Path("id") Integer id);
}
