package com.mam.alicamp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mam.alicamp.db.entidades.Proyecto;
import com.mam.alicamp.db.repositorios.ProyectoRepository;

import java.util.List;

public class ProyectoViewModel extends AndroidViewModel {

    private final ProyectoRepository repository;
    private final LiveData<List<Proyecto>> allProyectos;
    private final LiveData<String> mensajeError;

    public ProyectoViewModel(@NonNull Application application) {
        super(application);
        repository = new ProyectoRepository(application);
        allProyectos = repository.obtenerTodos();
        mensajeError = repository.getMensajeError();
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Proyecto>> getAllProyectos() {
        return allProyectos;
    }

    public LiveData<Proyecto> obtenerProyectoPorId(Integer idProyecto) {
        return repository.obtenerProyectoPorId(idProyecto);
    }

    public LiveData<List<Proyecto>> obtenerProyectosPorGadm(Integer idGadm) {
        return repository.obtenerProyectosPorGadm(idGadm);
    }

    public void insertarProyecto(Proyecto proyecto) {
        repository.insertar(proyecto);
    }

    public void updateProyecto(Proyecto proyecto) {
        repository.actualizar(proyecto);
    }

    public void eliminarProyecto(Integer idProyecto) {
        repository.eliminarProyecto(idProyecto);
    }
}
