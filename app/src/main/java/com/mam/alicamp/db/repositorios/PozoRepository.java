package com.mam.alicamp.db.repositorios;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mam.alicamp.api.PozoApiService;
import com.mam.alicamp.api.RetrofitCliente;
import com.mam.alicamp.db.dao.PozoDAO;
import com.mam.alicamp.db.dao.TuberiaDAO;
import com.mam.alicamp.db.database.AlicampDB;
import com.mam.alicamp.db.dto.PozoCreacion;
import com.mam.alicamp.db.dto.PozoDimensiones;
import com.mam.alicamp.db.dto.PozoFin;
import com.mam.alicamp.db.dto.PozoUbicacion;
import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.relaciones.PozoConDescarga;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mam.alicamp.servicios.ManejoFechas.*;

public class PozoRepository {

    private final PozoDAO pozoDAO;
    private final TuberiaDAO tuberiaDAO;
    private final PozoApiService apiService;
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();

    public PozoRepository(Context context) {
        AlicampDB db = AlicampDB.getAppDb(context);
        pozoDAO = db.pozoDAO();
        tuberiaDAO = db.tuberiaDAO();
        apiService = RetrofitCliente.getCliente().create(PozoApiService.class);
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<List<Pozo>> obtenerTodos() {
        apiService.getPozos().enqueue(new Callback<List<Pozo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Pozo>> call, @NonNull Response<List<Pozo>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Pozo> remotos = response.body();
                    AlicampDB.dbExecutor.execute(() -> {
                        for (Pozo remoto : remotos) {
                            Pozo local = pozoDAO.findById(remoto.getIdPozo());
                            if (local == null || convertirFechaADateTime(remoto.getFechaActualizacion())
                                    .isAfter(convertirFechaADateTime(local.getFechaActualizacion()))) {
                                remoto.setSincronizado(true);
                                pozoDAO.insertOrUpdate(remoto);
                            }
                        }
                    });
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Pozo>> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
        return pozoDAO.findAll();
    }

    public LiveData<List<Pozo>> obtenerPozosPorSector(Integer idSector) {
        return pozoDAO.findPozosBySector(idSector);
    }

    //Para la búsqueda mediante Search View
    public LiveData<List<Pozo>> obtenerPozosPorNombreSearch(String nombre) {
        return pozoDAO.findPozoByNombreSearch(nombre);
    }

    public LiveData<Pozo> obtenerPozoPorNombre(String nombrePozo) {
        return pozoDAO.findPozoByNombre(nombrePozo);
    }

    public LiveData<PozoConDescarga> obtenerPozoConDescarga(String nombrePozo) {
        return pozoDAO.findPozoWithDescarga(nombrePozo);
    }

    public void insertar(Pozo pozo) {
        PozoCreacion pozoCreacion = getPozoCreacion(pozo);
        apiService.createPozo(pozoCreacion).enqueue(new Callback<Pozo>() {
            @Override
            public void onResponse(@NonNull Call<Pozo> call, @NonNull Response<Pozo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    pozo.setIdPozo(response.body().getIdPozo());
                    AlicampDB.dbExecutor.execute(
                            () -> pozoDAO.insertOrUpdate(pozo));
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Pozo> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    public void actualizar(Pozo pozo) {
        PozoCreacion pozoCreacion = getPozoCreacion(pozo);
        apiService.updatePozo(pozo.getIdPozo(), pozoCreacion).enqueue(new Callback<Pozo>() {
            @Override
            public void onResponse(@NonNull Call<Pozo> call, @NonNull Response<Pozo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> pozoDAO.updatePozo(pozo)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Pozo> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    private static @NonNull PozoCreacion getPozoCreacion(Pozo pozo) {
        PozoCreacion pozoCreacion = new PozoCreacion();
        pozoCreacion.setNombre(pozo.getNombre());
        pozoCreacion.setFechaCatastro(pozo.getFechaCatastro());
        pozoCreacion.setFechaActualizacion(pozo.getFechaActualizacion());
        pozoCreacion.setSincronizado(pozo.isSincronizado());
        pozoCreacion.setTapado(pozo.getTapado());
        pozoCreacion.setSistema(pozo.getSistema());
        pozoCreacion.setPathMedia(pozo.getPathMedia());
        pozoCreacion.setActividadCompletada(pozo.getActividadCompletada());
        pozoCreacion.setIdSector(pozo.getIdSector());
        pozoCreacion.setIdResponsable(pozo.getIdResponsable());
        pozoCreacion.setIdDescarga(pozo.getIdDescarga());
        return pozoCreacion;
    }

    public void actualizarUbicacion(Pozo pozo) {
        PozoUbicacion pozoUbicacion = getPozoUbicacion(pozo);
        apiService.updateUbicacion(pozo.getIdPozo(), pozoUbicacion).enqueue(new Callback<Pozo>() {
            @Override
            public void onResponse(@NonNull Call<Pozo> call, @NonNull Response<Pozo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> pozoDAO.updatePozo(pozo)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Pozo> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    private static @NonNull PozoUbicacion getPozoUbicacion(Pozo pozo) {
        PozoUbicacion pozoUbicacion = new PozoUbicacion();
        pozoUbicacion.setCalleNS(pozo.getCalleNS());
        pozoUbicacion.setCalleOE(pozo.getCalleOE());
        pozoUbicacion.setCoordEste(pozo.getCoordEste());
        pozoUbicacion.setCoordNorte(pozo.getCoordNorte());
        pozoUbicacion.setCota(pozo.getCota());
        pozoUbicacion.setZona(pozo.getZona());
        pozoUbicacion.setAproximacion(pozo.getAproximacion());
        pozoUbicacion.setCalzada(pozo.getCalzada());
        pozoUbicacion.setSrid(pozo.getSrid());
        pozoUbicacion.setActividadCompletada(pozo.getActividadCompletada());
        return pozoUbicacion;
    }

    public void actualizarDimensiones(Pozo pozo) {
        PozoDimensiones pozoDimensiones = getPozoDimensiones(pozo);
        apiService.updateDimensiones(pozo.getIdPozo(), pozoDimensiones).enqueue(new Callback<Pozo>() {
            @Override
            public void onResponse(@NonNull Call<Pozo> call, @NonNull Response<Pozo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> pozoDAO.updatePozo(pozo)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Pozo> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    private static @NonNull PozoDimensiones getPozoDimensiones(Pozo pozo) {
        PozoDimensiones pozoDimensiones = new PozoDimensiones();
        pozoDimensiones.setDimensionTapa(pozo.getDimensionTapa());
        pozoDimensiones.setAlturaPozo(pozo.getAltura());
        pozoDimensiones.setAncho(pozo.getAncho());
        pozoDimensiones.setFluido(pozo.getFluido());
        pozoDimensiones.setEstadoPozo(pozo.getEstado());
        pozoDimensiones.setActividadCompletada(pozo.getActividadCompletada());
        return pozoDimensiones;
    }

    public void actualizarFin(Pozo pozo) {
        PozoFin pozoFin = getPozoFin(pozo);
        apiService.updateFin(pozo.getIdPozo(), pozoFin).enqueue(new Callback<Pozo>() {
            @Override
            public void onResponse(@NonNull Call<Pozo> call, @NonNull Response<Pozo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AlicampDB.dbExecutor.execute(
                            () -> pozoDAO.updatePozo(pozo)
                    );
                } else {
                    mensajeError.postValue("Error en la respuesta del servidor " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Pozo> call, @NonNull Throwable t) {
                mensajeError.postValue("Error en la conexión " + t.getMessage());
            }
        });
    }

    private static @NonNull PozoFin getPozoFin(Pozo pozo) {
        PozoFin pozoFin = new PozoFin();
        pozoFin.setObservacion(pozo.getObservacion());
        pozoFin.setActividadCompletada(pozo.getActividadCompletada());
        return pozoFin;
    }

    public void eliminarPozoConTuberias(String nombrePozo) {
        AlicampDB.dbExecutor.execute(() -> {
                    pozoDAO.deletePozo(nombrePozo);
                    tuberiaDAO.deleteTuberiasByPozo(nombrePozo);
                }
        );
    }
}
