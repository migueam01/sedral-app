package com.mam.alicamp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mam.alicamp.db.entidades.Gadm;
import com.mam.alicamp.db.repositorios.GadmRepository;

import java.util.List;

public class GadmViewModel extends AndroidViewModel {
    private final GadmRepository repository;
    private final LiveData<List<Gadm>> allGadms;
    private final LiveData<String> mensajeError;

    public GadmViewModel(@NonNull Application application) {
        super(application);
        repository = new GadmRepository(application);
        allGadms = repository.getAllGadms();
        mensajeError = repository.getMensajeError();
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Gadm>> getAllGadms() {
        return allGadms;
    }

    public LiveData<Gadm> obtenerGadmPorId(Integer idGadm) {
        return repository.obtenerGadmPorId(idGadm);
    }

    public void insertarGadm(Gadm gadm) {
        repository.insertar(gadm);
    }

    public void actualizarGadm(Gadm gadm) {
        repository.actualizar(gadm);
    }

    public void eliminarGadm(Integer idGadm) {
        repository.eliminarGadm(idGadm);
    }
}
