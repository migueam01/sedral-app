package com.mam.alicamp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mam.alicamp.db.entidades.Descarga;
import com.mam.alicamp.db.repositorios.DescargaRepository;

import java.util.List;

public class DescargaViewModel extends AndroidViewModel {

    private final DescargaRepository repository;
    private final LiveData<List<Descarga>> descargas;
    private final LiveData<String> mensajeError;

    public DescargaViewModel(@NonNull Application application) {
        super(application);
        repository = new DescargaRepository(application);
        descargas = repository.getDescargas();
        mensajeError = repository.getMensajeError();
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Descarga>> getDescargas() {
        return descargas;
    }

    public void insertar(Descarga descarga) {
        repository.insertar(descarga);
    }

    public void actualizar(Descarga descarga) {
        repository.actualizar(descarga);
    }

    public void eliminar(Integer idDescarga) {
        repository.eliminarDescarga(idDescarga);
    }

    /*public void eliminarDescarga(Descarga descarga) {
        repository.eliminarDescarga(descarga);
    }*/

    /*public void eliminar(Integer idDescarga) {
        repository.eliminar(idDescarga);
    }*/
}
