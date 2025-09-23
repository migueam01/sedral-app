package com.mam.alicamp.api;

import com.mam.alicamp.db.entidades.Responsable;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ResponsableApiService {

    @GET("responsables")
    Call<List<Responsable>> getResponsables();

    @POST("responsables")
    Call<Responsable> createResponsable(@Body Responsable responsable);

    @PUT("responsables")
    Call<Responsable> updateResponsable(@Body Responsable responsable);

    @DELETE("responsables/{id}")
    Call<Void> deleteResponsable(@Path("id") Integer id);
}
