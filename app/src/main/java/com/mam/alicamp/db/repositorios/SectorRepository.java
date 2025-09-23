package com.mam.alicamp.db.repositorios;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mam.alicamp.api.RetrofitCliente;
import com.mam.alicamp.api.SectorApiService;
import com.mam.alicamp.db.dao.PozoDAO;
import com.mam.alicamp.db.dao.SectorDAO;
import com.mam.alicamp.db.dao.TuberiaDAO;
import com.mam.alicamp.db.database.AlicampDB;
import com.mam.alicamp.db.dto.GadmSectorProyecto;
import com.mam.alicamp.db.entidades.Sector;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SectorRepository {

    private final SectorDAO sectorDAO;
    private final PozoDAO pozoDAO;
    private final TuberiaDAO tuberiaDAO;
    private final SectorApiService apiService;
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public SectorRepository(Context context) {
        AlicampDB db = AlicampDB.getAppDb(context);
        sectorDAO = db.sectorDAO();
        pozoDAO = db.pozoDAO();
        tuberiaDAO = db.tuberiaDAO();
        apiService = RetrofitCliente.getCliente().create(SectorApiService.class);
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Sector>> obtenerTodos() {
        apiService.getSectores().enqueue(new Callback<List<Sector>>() {
            @Override
            public void onResponse(@NonNull Call<List<Sector>> call, @NonNull Response<List<Sector>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Sector> remotos = response.body();
                    AlicampDB.dbExecutor.execute(() -> {
                        for (Sector remoto : remotos) {
                            Sector local = sectorDAO.findById(remoto.getIdSector());
                            if (local == null || !local.equals(remoto)) {
                                remoto.setSincronizado(true);
                                sectorDAO.insertOrUpdate(remoto);
                            }
                        }
                    });
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Sector>> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
        return sectorDAO.findAll();
    }

    public LiveData<List<Sector>> obtenerSectoresPorProyecto(Integer idProyecto) {
        return sectorDAO.findSectoresByProyecto(idProyecto);
    }

    public LiveData<List<GadmSectorProyecto>> obtenerGadmsSectoresProyectos() {
        return sectorDAO.getGadmsSectoresProyectos();
    }

    public void insertar(Sector sector) {
        apiService.createSector(sector).enqueue(new Callback<Sector>() {
            @Override
            public void onResponse(@NonNull Call<Sector> call, @NonNull Response<Sector> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sector.setIdSector(response.body().getIdSector());
                    AlicampDB.dbExecutor.execute(
                            () -> sectorDAO.insertOrUpdate(sector)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Sector> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void eliminarSectorConPozos(Integer idSector) {
        AlicampDB.dbExecutor.execute(() -> {
                    sectorDAO.deleteSector(idSector);
                    List<String> pozos = pozoDAO.findPozosActivosBySector(idSector);
                    pozoDAO.deletePozosBySector(idSector);
                    if (!pozos.isEmpty()) {
                        tuberiaDAO.deleteTuberiasByPozoBySector(pozos);
                    }
                }
        );
    }

    public void actualizar(Sector sector) {
        apiService.updateSector(sector).enqueue(new Callback<Sector>() {
            @Override
            public void onResponse(@NonNull Call<Sector> call, @NonNull Response<Sector> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> sectorDAO.updateSector(response.body())
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Sector> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void eliminarSector(Integer idSector) {
        apiService.deleteSector(idSector).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    AlicampDB.dbExecutor.execute(
                            () -> sectorDAO.deleteById(idSector)
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
