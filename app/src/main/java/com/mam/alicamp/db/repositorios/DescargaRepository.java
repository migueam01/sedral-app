package com.mam.alicamp.db.repositorios;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mam.alicamp.api.DescargaApiService;
import com.mam.alicamp.api.RetrofitCliente;
import com.mam.alicamp.db.dao.DescargaDAO;
import com.mam.alicamp.db.database.AlicampDB;
import com.mam.alicamp.db.entidades.Descarga;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DescargaRepository {

    private final DescargaDAO descargaDAO;
    private final DescargaApiService apiService;
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public DescargaRepository(Context context) {
        AlicampDB db = AlicampDB.getAppDb(context);
        descargaDAO = db.descargaDAO();
        apiService = RetrofitCliente.getCliente(context).create(DescargaApiService.class);

    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Descarga>> getDescargas() {
        apiService.getDescargas().enqueue(new Callback<List<Descarga>>() {
            @Override
            public void onResponse(@NonNull Call<List<Descarga>> call, @NonNull Response<List<Descarga>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Descarga> remotos = response.body();
                    AlicampDB.dbExecutor.execute(() -> {
                        for (Descarga remoto : remotos) {
                            Descarga local = descargaDAO.findById(remoto.getIdDescarga());
                            if (local == null || !local.equals(remoto)) {
                                remoto.setSincronizado(true);
                                descargaDAO.insertOrUpdate(remoto);
                            }
                        }
                    });
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Descarga>> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
        return descargaDAO.findAllDescargas();
    }

    public void insertar(Descarga descarga) {
        apiService.createDescarga(descarga).enqueue(new Callback<Descarga>() {
            @Override
            public void onResponse(@NonNull Call<Descarga> call, @NonNull Response<Descarga> response) {
                if (response.isSuccessful() && response.body() != null) {
                    descarga.setIdDescarga(response.body().getIdDescarga());
                    AlicampDB.dbExecutor.execute(
                            () -> descargaDAO.insertOrUpdate(descarga)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Descarga> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void actualizar(Descarga descarga) {
        apiService.updateDescarga(descarga).enqueue(new Callback<Descarga>() {
            @Override
            public void onResponse(@NonNull Call<Descarga> call, @NonNull Response<Descarga> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> descargaDAO.update(descarga)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Descarga> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void eliminarDescarga(Integer idDescarga) {
        apiService.deleteDescarga(idDescarga).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    AlicampDB.dbExecutor.execute(
                            () -> descargaDAO.deleteById(idDescarga)
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

    public void eliminarTodos() {
        AlicampDB.dbExecutor.execute(
                descargaDAO::deleteAll
        );
    }

    /*public void eliminarDescarga(Descarga descarga) {
        AlicampDB.dbExecutor.execute(
                () -> descargaDAO.deleteDescarga(descarga)
        );
    }*/
}
