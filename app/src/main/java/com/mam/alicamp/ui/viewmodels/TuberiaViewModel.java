package com.mam.alicamp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Tuberia;
import com.mam.alicamp.db.relaciones.TuberiaConPozos;
import com.mam.alicamp.db.repositorios.TuberiaRepository;

import java.util.List;

public class TuberiaViewModel extends AndroidViewModel {

    private final TuberiaRepository repository;
    private final LiveData<List<Tuberia>> allTuberias;
    private final LiveData<String> mensajeError;

    public TuberiaViewModel(@NonNull Application application) {
        super(application);
        repository = new TuberiaRepository(application);
        allTuberias = repository.obtenerTodos();
        mensajeError = repository.getMensajeError();
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Tuberia>> getAllTuberias() {
        return allTuberias;
    }

    public LiveData<List<Tuberia>> obtenerTuberiasPorPozo(String nombrePozo) {
        return repository.obtenerTuberiasPorPozo(nombrePozo);
    }

    public LiveData<TuberiaConPozos> obtenerTuberiaConPozos(Integer idTuberia) {
        return repository.obtenerTuberiasConPozos(idTuberia);
    }

    public void insertar(Tuberia tuberia, Pozo pozoInicio, Pozo pozoFin) {
        repository.insertar(tuberia, pozoInicio, pozoFin);
    }

    public void actualizar(Tuberia tuberia, Pozo pozoInicio, Pozo pozoFin) {
        repository.actualizar(tuberia, pozoInicio, pozoFin);
    }

    public void eliminarTuberia(Integer idTuberia) {
        repository.eliminarTuberia(idTuberia);
    }
}
