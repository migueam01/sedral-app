package com.mam.alicamp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.relaciones.PozoConDescarga;

import java.util.List;

@Dao
public interface PozoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Pozo> pozos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Pozo pozo);

    @Query("SELECT * FROM pozos WHERE id_pozo =:idPozo")
    Pozo findById(Integer idPozo); //Para sincronización

    @Query("SELECT * FROM pozos")
    LiveData<List<Pozo>> findAll();

    @Query("SELECT * FROM pozos WHERE fk_id_sector = :idSector ORDER BY nombre")
    LiveData<List<Pozo>> findPozosBySector(Integer idSector);

    //Para la búsqueda mediante Search View
    @Query("SELECT * FROM pozos WHERE nombre LIKE '%' || :nombre || '%'")
    LiveData<List<Pozo>> findPozoByNombreSearch(String nombre);

    @Query("SELECT * FROM pozos WHERE nombre =:nombrePozo")
    LiveData<Pozo> findPozoByNombre(String nombrePozo);

    @Query("SELECT * FROM pozos WHERE id_pozo =:idPozo")
    LiveData<Pozo> findPozoById(Integer idPozo);

    @Transaction
    @Query("SELECT * FROM pozos WHERE nombre =:nombrePozo")
    LiveData<PozoConDescarga> findPozoWithDescarga(String nombrePozo);

    @Update(entity = Pozo.class)
    void updatePozo(Pozo pozo);

    @Query("DELETE FROM pozos")
    void deleteAll();

    @Query("DELETE FROM pozos WHERE id_pozo =:idPozo")
    void deleteById(Integer idPozo);

    @Query("UPDATE pozos SET eliminado = 1, sincronizado = 0 WHERE nombre =:nombrePozo")
    void deletePozo(String nombrePozo);

    //Consulta para realizar la eliminación lógica de las tuberías
    @Query("SELECT nombre FROM pozos WHERE fk_id_sector =:idSector")
    List<String> findPozosActivosBySector(Integer idSector);

    @Query("UPDATE pozos SET eliminado = 1, sincronizado = 0 WHERE fk_id_sector =:idSector")
    void deletePozosBySector(Integer idSector);
}
