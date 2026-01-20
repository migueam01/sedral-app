package com.mam.alicamp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mam.alicamp.db.dto.GadmSectorProyecto;
import com.mam.alicamp.db.entidades.Sector;
import com.mam.alicamp.db.repositorios.SectorRepository;

import java.util.List;

public class SectorViewModel extends AndroidViewModel {

    private final SectorRepository repository;
    private final LiveData<List<Sector>> allSectores;
    private final LiveData<String> mensajeError;

    public SectorViewModel(@NonNull Application application) {
        super(application);
        repository = new SectorRepository(application);
        allSectores = repository.obtenerTodos();
        mensajeError = repository.getMensajeError();
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Sector>> getAllSectores() {
        return allSectores;
    }

    public LiveData<List<Sector>> obtenerSectoresPorProyecto(Integer idProyecto) {
        return repository.obtenerSectoresPorProyecto(idProyecto);
    }

    public LiveData<List<GadmSectorProyecto>> obtenerGadmsProyectosSectores() {
        return repository.obtenerGadmsSectoresProyectos();
    }

    public void insertar(Sector sector) {
        repository.insertar(sector);
    }

    public void actualizar(Sector sector) {
        repository.actualizar(sector);
    }

    /*public void eliminarSectorConPozos(Integer idSector) {
        repository.eliminarSectorConPozos(idSector);
    }*/
}
