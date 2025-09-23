package com.mam.alicamp.api;

import com.mam.alicamp.db.dto.PozoCreacion;
import com.mam.alicamp.db.dto.PozoDimensiones;
import com.mam.alicamp.db.dto.PozoFin;
import com.mam.alicamp.db.dto.PozoUbicacion;
import com.mam.alicamp.db.entidades.Pozo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PozoApiService {

    @GET("pozos/obtiene-android")
    Call<List<Pozo>> getPozos();

    @POST("pozos/android")
    Call<Pozo> createPozo(@Body PozoCreacion pozo);

    @POST("pozos/android/tuberia")
    Call<Pozo> createPozoTuberia(@Body Pozo pozo);

    @PUT("pozos/android/{id}")
    Call<Pozo> updatePozo(@Path("id") Integer id, @Body PozoCreacion pozo);

    @PUT("pozos/ubicacion/{id}")
    Call<Pozo> updateUbicacion(@Path("id") Integer id, @Body PozoUbicacion pozo);

    @PUT("pozos/dimensiones/{id}")
    Call<Pozo> updateDimensiones(@Path("id") Integer id, @Body PozoDimensiones pozo);

    @PUT("pozos/fin/{id}")
    Call<Pozo> updateFin(@Path("id") Integer id, @Body PozoFin pozo);

    @DELETE("pozos/{id}")
    Call<Void> deletePozo(@Path("id") Integer idPozo);
}
