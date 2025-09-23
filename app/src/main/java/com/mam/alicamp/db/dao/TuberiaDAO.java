package com.mam.alicamp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mam.alicamp.db.entidades.Tuberia;
import com.mam.alicamp.db.relaciones.TuberiaConPozos;

import java.util.List;

@Dao
public interface TuberiaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Tuberia tuberia);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Tuberia> tuberias);

    @Query("SELECT * FROM tuberias WHERE id_tuberia =:idTuberia")
    Tuberia findById(Integer idTuberia); //Para sincronización

    @Query("SELECT * FROM tuberias")
    LiveData<List<Tuberia>> findAll();

    @Query("SELECT t.* FROM tuberias t, pozos p " +
            "WHERE t.fk_pozo_inicio = p.id_pozo " +
            "AND p.nombre =:nombrePozo")
    LiveData<List<Tuberia>> findTuberiasByPozo(String nombrePozo);

    @Transaction
    @Query("SELECT * FROM tuberias WHERE id_tuberia =:idTuberia")
    LiveData<TuberiaConPozos> findTuberiaWithPozos(Integer idTuberia);

    @Update(entity = Tuberia.class)
    void update(Tuberia tuberia);

    @Query("DELETE FROM tuberias")
    void deleteAll();

    @Query("UPDATE tuberias SET eliminado = 1, sincronizado = 0 WHERE id_tuberia =:idTuberia")
    void deleteTuberia(Integer idTuberia);

    @Query("UPDATE tuberias SET eliminado = 1, sincronizado = 0 WHERE fk_pozo_inicio =:nombrePozo " +
            " OR fk_pozo_fin =:nombrePozo")
    void deleteTuberiasByPozo(String nombrePozo);

    @Query("UPDATE tuberias SET eliminado = 1, sincronizado = 0 WHERE fk_pozo_inicio IN (:nombrePozo)" +
            " OR fk_pozo_fin IN (:nombrePozo)")
    void deleteTuberiasByPozoBySector(List<String> nombrePozo);
}
