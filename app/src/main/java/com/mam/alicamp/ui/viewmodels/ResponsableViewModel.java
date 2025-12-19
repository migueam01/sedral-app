package com.mam.alicamp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mam.alicamp.db.entidades.Responsable;
import com.mam.alicamp.db.repositorios.ResponsableRepository;

import java.util.List;

public class ResponsableViewModel extends AndroidViewModel {

    private final ResponsableRepository repository;
    private final LiveData<List<Responsable>> listaResponsables;
    private final LiveData<String> mensajeError;

    public ResponsableViewModel(@NonNull Application application) {
        super(application);
        repository = new ResponsableRepository(application);
        listaResponsables = repository.getAllResponsables();
        mensajeError = repository.getMensajeError();
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Responsable>> getListaResponsables() {
        return listaResponsables;
    }

    public LiveData<Responsable> obtenerResponsablePorUsername(String username) {
        return repository.obtenerResponsablePorUsername(username);
    }

    public void insertar(Responsable responsable) {
        repository.insertar(responsable);
    }

    public void actualizar(Responsable responsable) {
        repository.actualizar(responsable);
    }

    public void eliminarResponsable(Integer idResponsable) {
        repository.eliminarResponsable(idResponsable);
    }
}
