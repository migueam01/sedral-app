package com.mam.alicamp.db.repositorios;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mam.alicamp.api.RetrofitCliente;
import com.mam.alicamp.api.TuberiaApiService;
import com.mam.alicamp.db.dao.TuberiaDAO;
import com.mam.alicamp.db.database.AlicampDB;
import com.mam.alicamp.db.dto.TuberiaSpring;
import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Tuberia;
import com.mam.alicamp.db.relaciones.TuberiaConPozos;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TuberiaRepository {

    private final TuberiaDAO tuberiaDAO;
    private final TuberiaApiService apiService;
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public TuberiaRepository(Context context) {
        AlicampDB db = AlicampDB.getAppDb(context);
        tuberiaDAO = db.tuberiaDAO();
        apiService = RetrofitCliente.getCliente(context).create(TuberiaApiService.class);
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Tuberia>> obtenerTodos() {
        apiService.getTuberias().enqueue(new Callback<List<Tuberia>>() {
            @Override
            public void onResponse(@NonNull Call<List<Tuberia>> call, @NonNull Response<List<Tuberia>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Tuberia> remotos = response.body();
                    AlicampDB.dbExecutor.execute(() -> {
                        for (Tuberia remoto : remotos) {
                            Tuberia local = tuberiaDAO.findById(remoto.getIdTuberia());
                            if (local == null || !local.equals(remoto)) {
                                remoto.setSincronizado(true);
                                tuberiaDAO.insertOrUpdate(remoto);
                            }
                        }
                    });
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Tuberia>> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
        return tuberiaDAO.findAll();
    }

    public LiveData<List<Tuberia>> obtenerTuberiasPorPozo(String nombrePozo) {
        return tuberiaDAO.findTuberiasByPozo(nombrePozo);
    }

    public LiveData<TuberiaConPozos> obtenerTuberiasConPozos(Integer idTuberia) {
        return tuberiaDAO.findTuberiaWithPozos(idTuberia);
    }

    public void insertar(Tuberia tuberia, Pozo pozoInicio, Pozo pozoFin) {
        TuberiaSpring tuberiaSpring = getTuberiaSpring(tuberia, pozoInicio, pozoFin);
        apiService.createTuberia(tuberiaSpring).enqueue(new Callback<Tuberia>() {
            @Override
            public void onResponse(@NonNull Call<Tuberia> call, @NonNull Response<Tuberia> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tuberia.setIdTuberia(response.body().getIdTuberia());
                    AlicampDB.dbExecutor.execute(
                            () -> tuberiaDAO.insertOrUpdate(tuberia)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Tuberia> call, Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void actualizar(Tuberia tuberia, Pozo pozoInicio, Pozo pozoFin) {
        TuberiaSpring tuberiaSpring = getTuberiaSpring(tuberia, pozoInicio, pozoFin);
        tuberiaSpring.setIdTuberia(tuberia.getIdTuberia());
        apiService.updateTuberia(tuberiaSpring).enqueue(new Callback<Tuberia>() {
            @Override
            public void onResponse(Call<Tuberia> call, Response<Tuberia> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> tuberiaDAO.update(tuberia)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Tuberia> call, Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    private static @NonNull TuberiaSpring getTuberiaSpring(Tuberia tuberia, Pozo pozoInicio, Pozo pozoFin) {
        TuberiaSpring tuberiaSpring = new TuberiaSpring();
        tuberiaSpring.setOrientacion(tuberia.getOrientacion());
        tuberiaSpring.setBase(tuberia.getBase());
        tuberiaSpring.setCorona(tuberia.getCorona());
        tuberiaSpring.setDiametro(tuberia.getDiametro());
        tuberiaSpring.setMaterial(tuberia.getMaterial());
        tuberiaSpring.setFlujo(tuberia.getFlujo());
        tuberiaSpring.setFunciona(tuberia.getFunciona());
        tuberiaSpring.setPozoInicio(pozoInicio);
        tuberiaSpring.setPozoFin(pozoFin);
        return tuberiaSpring;
    }

    public void eliminarTuberia(Integer idTuberia) {
        AlicampDB.dbExecutor.execute(
                () -> tuberiaDAO.deleteTuberia(idTuberia)
        );
    }
}
