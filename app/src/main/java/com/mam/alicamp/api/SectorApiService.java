package com.mam.alicamp.api;

import com.mam.alicamp.db.entidades.Sector;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SectorApiService {

    @GET("sectores/android")
    Call<List<Sector>> getSectores();

    @POST("sectores/crear-android")
    Call<Sector> createSector(@Body Sector sector);

    @PUT("sectores/actualizar-android")
    Call<Sector> updateSector(@Body Sector sector);

    @DELETE("sectores/{id}")
    Call<Void> deleteSector(@Path("id") Integer id);
}
