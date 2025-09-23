package com.mam.alicamp.api;

import com.mam.alicamp.db.dto.TuberiaSpring;
import com.mam.alicamp.db.entidades.Tuberia;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TuberiaApiService {

    @GET("tuberias/android")
    Call<List<Tuberia>> getTuberias();

    @POST("tuberias")
    Call<Tuberia> createTuberia(@Body TuberiaSpring tuberia);

    @PUT("tuberias")
    Call<Tuberia> updateTuberia(@Body TuberiaSpring tuberia);

    @DELETE("tuberias/{id}")
    Call<Void> deleteTuberia(@Path("id") Integer id);
}
