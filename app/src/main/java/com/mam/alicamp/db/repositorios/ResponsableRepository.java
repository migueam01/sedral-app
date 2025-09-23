package com.mam.alicamp.db.repositorios;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mam.alicamp.api.ResponsableApiService;
import com.mam.alicamp.api.RetrofitCliente;
import com.mam.alicamp.db.dao.ResponsableDAO;
import com.mam.alicamp.db.database.AlicampDB;
import com.mam.alicamp.db.entidades.Responsable;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResponsableRepository {

    private final ResponsableDAO responsableDAO;
    private final ResponsableApiService apiService;
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public ResponsableRepository(Context context) {
        AlicampDB db = AlicampDB.getAppDb(context);
        responsableDAO = db.responsableDAO();
        apiService = RetrofitCliente.getCliente().create(ResponsableApiService.class);
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Responsable>> getAllResponsables() {
        apiService.getResponsables().enqueue(new Callback<List<Responsable>>() {
            @Override
            public void onResponse(@NonNull Call<List<Responsable>> call, @NonNull Response<List<Responsable>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Responsable> remotos = response.body();
                    AlicampDB.dbExecutor.execute(() -> {
                        for (Responsable remoto : remotos) {
                            Responsable local = responsableDAO.findById(remoto.getIdResponsable());
                            if (local == null || !local.equals(remoto)) {
                                remoto.setSincronizado(true);
                                responsableDAO.insertOrUpdate(remoto);
                            }
                        }
                    });
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Responsable>> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
        return responsableDAO.findAllResponsables();
    }

    public void insertar(Responsable responsable) {
        apiService.createResponsable(responsable).enqueue(new Callback<Responsable>() {
            @Override
            public void onResponse(@NonNull Call<Responsable> call, @NonNull Response<Responsable> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> responsableDAO.insertOrUpdate(response.body())
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Responsable> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void actualizar(Responsable responsable) {
        apiService.updateResponsable(responsable).enqueue(new Callback<Responsable>() {
            @Override
            public void onResponse(@NonNull Call<Responsable> call, @NonNull Response<Responsable> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> responsableDAO.update(response.body())
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Responsable> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void eliminarResponsable(Integer idResponsable) {
        apiService.deleteResponsable(idResponsable).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    AlicampDB.dbExecutor.execute(
                            () -> responsableDAO.deleteById(idResponsable)
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
