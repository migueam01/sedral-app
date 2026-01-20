package com.mam.alicamp.db.repositorios;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mam.alicamp.api.ProyectoApiService;
import com.mam.alicamp.api.RetrofitCliente;
import com.mam.alicamp.db.dao.ProyectoDAO;
import com.mam.alicamp.db.database.AlicampDB;
import com.mam.alicamp.db.entidades.Proyecto;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProyectoRepository {

    private final ProyectoDAO proyectoDAO;
    private final ProyectoApiService apiService;
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public ProyectoRepository(Context context) {
        AlicampDB db = AlicampDB.getAppDb(context);
        proyectoDAO = db.proyectoDAO();
        apiService = RetrofitCliente.getCliente(context).create(ProyectoApiService.class);
    }

    public LiveData<Proyecto> obtenerProyectoPorId(Integer idProyecto) {
        return proyectoDAO.findProyectoById(idProyecto);
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Proyecto>> obtenerTodos() {
        apiService.getProyectos().enqueue(new Callback<List<Proyecto>>() {
            @Override
            public void onResponse(@NonNull Call<List<Proyecto>> call, @NonNull Response<List<Proyecto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Proyecto> remotos = response.body();
                    AlicampDB.dbExecutor.execute(() -> {
                        for (Proyecto remoto : remotos) {
                            Proyecto local = proyectoDAO.findById(remoto.getIdProyecto());
                            if (local == null || !local.equals(remoto)) {
                                proyectoDAO.insertOrUpdate(remoto);
                            }
                        }
                    });
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Proyecto>> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
        return proyectoDAO.findAll();
    }

    public LiveData<List<Proyecto>> obtenerProyectosPorGadm(Integer idGadm) {
        return proyectoDAO.findProyectosByGadm(idGadm);
    }

    public void insertar(Proyecto proyecto) {
        apiService.createProyecto(proyecto).enqueue(new Callback<Proyecto>() {
            @Override
            public void onResponse(@NonNull Call<Proyecto> call, @NonNull Response<Proyecto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    proyecto.setIdProyecto(response.body().getIdProyecto());
                    AlicampDB.dbExecutor.execute(
                            () -> proyectoDAO.insertOrUpdate(proyecto)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Proyecto> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void actualizar(Proyecto proyecto) {
        apiService.updateProyecto(proyecto).enqueue(new Callback<Proyecto>() {
            @Override
            public void onResponse(@NonNull Call<Proyecto> call, @NonNull Response<Proyecto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> proyectoDAO.updateProyecto(proyecto)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Proyecto> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void eliminarProyecto(Integer idProyecto) {
        apiService.deleteProyecto(idProyecto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    AlicampDB.dbExecutor.execute(
                            () -> proyectoDAO.deleteById(idProyecto)
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
