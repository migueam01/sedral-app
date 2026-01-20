package com.mam.alicamp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.relaciones.PozoConDescarga;
import com.mam.alicamp.db.repositorios.PozoRepository;

import java.util.List;

public class PozoViewModel extends AndroidViewModel {

    private final PozoRepository repository;
    private final MutableLiveData<String> filtroNombre = new MutableLiveData<>("");
    private final MutableLiveData<Integer> idSectorSeleccionado = new MutableLiveData<>(1);
    private final LiveData<List<Pozo>> pozosFiltrados;
    private final LiveData<List<Pozo>> allPozos;
    private final LiveData<String> mensajeError;

    public PozoViewModel(@NonNull Application application) {
        super(application);
        repository = new PozoRepository(application);
        allPozos = repository.obtenerTodos();
        mensajeError = repository.getMensajeError();
        pozosFiltrados = Transformations.switchMap(filtroNombre, nombre -> {
            if (nombre == null || nombre.trim().isEmpty()) {
                return Transformations.switchMap(idSectorSeleccionado, repository::obtenerPozosPorSector);
            } else {
                return repository.obtenerPozosPorNombreSearch(nombre);
            }
        });
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Pozo>> getAllPozos() {
        return allPozos;
    }

    public LiveData<List<Pozo>> obtenerPozosPorSector(Integer idSector) {
        return repository.obtenerPozosPorSector(idSector);
    }

    public LiveData<List<Pozo>> filtrarPozosPorNombre() {
        return pozosFiltrados;
    }

    //Método para actualizar el Recycler cuando busca por nombre
    public void setFiltroNombre(String nombre) {
        filtroNombre.setValue(nombre);
    }

    //Método para actualizar el Recycler si el Search view está vacío, busca por el id del spinner
    public void setIdSectorSeleccionado(Integer idSector) {
        idSectorSeleccionado.setValue(idSector);
    }

    public LiveData<Pozo> obtenerPozoPorNombre(String nombrePozo) {
        return repository.obtenerPozoPorNombre(nombrePozo);
    }

    public LiveData<PozoConDescarga> obtenerPozoConDescarga(String nombrePozo) {
        return repository.obtenerPozoConDescarga(nombrePozo);
    }

    public void insertar(Pozo pozo) {
        repository.insertar(pozo);
    }

    public void actualizar(Pozo pozo) {
        repository.actualizar(pozo);
    }

    public void actualizarUbicacion(Pozo pozo) {
        repository.actualizarUbicacion(pozo);
    }

    public void actualizarDimensiones(Pozo pozo) {
        repository.actualizarDimensiones(pozo);
    }

    public void actualizarFin(Pozo pozo) {
        repository.actualizarFin(pozo);
    }

    /*public void eliminarPozoConTuberias(String nombrePozo) {
        repository.eliminarPozoConTuberias(nombrePozo);
    }*/
}
