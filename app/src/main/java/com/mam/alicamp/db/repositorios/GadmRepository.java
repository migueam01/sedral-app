package com.mam.alicamp.db.repositorios;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mam.alicamp.api.GadmApiService;
import com.mam.alicamp.api.RetrofitCliente;
import com.mam.alicamp.db.dao.GadmDAO;
import com.mam.alicamp.db.database.AlicampDB;
import com.mam.alicamp.db.entidades.Gadm;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GadmRepository {

    private final GadmDAO gadmDAO;
    private final GadmApiService apiService;
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public GadmRepository(Context context) {
        AlicampDB db = AlicampDB.getAppDb(context);
        gadmDAO = db.gadmDAO();
        apiService = RetrofitCliente.getCliente().create(GadmApiService.class);
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Gadm>> getAllGadms() {
        apiService.getGadms().enqueue(new Callback<List<Gadm>>() {
            @Override
            public void onResponse(@NonNull Call<List<Gadm>> call, @NonNull Response<List<Gadm>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Gadm> remotos = response.body();
                    AlicampDB.dbExecutor.execute(() -> {
                        for (Gadm remoto : remotos) {
                            Gadm local = gadmDAO.findById(remoto.getIdGadm());
                            if (local == null || !local.equals(remoto)) {
                                remoto.setSincronizado(true);
                                gadmDAO.insertOrUpdate(remoto);
                            }
                        }
                    });
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Gadm>> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
        return gadmDAO.findAllGadm();
    }

    public LiveData<Gadm> obtenerGadmPorId(Integer idGadm) {
        return gadmDAO.findGadmById(idGadm);
    }

    public void insertar(Gadm gadm) {
        apiService.createGadm(gadm).enqueue(new Callback<Gadm>() {
            @Override
            public void onResponse(@NonNull Call<Gadm> call, @NonNull Response<Gadm> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> gadmDAO.insertOrUpdate(response.body())
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Gadm> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void actualizar(Gadm gadm) {
        apiService.updateGadm(gadm).enqueue(new Callback<Gadm>() {
            @Override
            public void onResponse(@NonNull Call<Gadm> call, @NonNull Response<Gadm> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> gadmDAO.updateGadm(response.body())
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Gadm> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void eliminarGadm(Integer idGadm) {
        apiService.deleteGadm(idGadm).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    AlicampDB.dbExecutor.execute(
                            () -> gadmDAO.deleteById(idGadm)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }
}
