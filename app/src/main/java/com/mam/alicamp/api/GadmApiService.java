package com.mam.alicamp.api;

import com.mam.alicamp.db.entidades.Gadm;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface GadmApiService {

    @GET("gadms")
    Call<List<Gadm>> getGadms();

    @GET("gadms/{id}")
    Call<Gadm> getGadmById(@Path("id") Integer id);

    @POST("gadms")
    Call<Gadm> createGadm(@Body Gadm gadm);

    @PUT("gadms")
    Call<Gadm> updateGadm(@Body Gadm gadm);

    @DELETE("gadms/{id}")
    Call<Void> deleteGadm(@Path("id") Integer id);
}
