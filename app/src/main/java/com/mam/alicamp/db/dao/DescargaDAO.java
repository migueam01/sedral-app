package com.mam.alicamp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mam.alicamp.db.entidades.Descarga;

import java.util.List;

@Dao
public interface DescargaDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Descarga> descargas);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Descarga descarga);

    @Query("SELECT * FROM descargas WHERE id_descarga =:idDescarga")
    Descarga findById(Integer idDescarga); //Para sincronización

    @Query("SELECT * FROM descargas ORDER BY nombre")
    LiveData<List<Descarga>> findAllDescargas();

    @Update(entity = Descarga.class)
    void update(Descarga descarga);

    @Query("DELETE FROM descargas")
    void deleteAll();

    @Query("DELETE FROM descargas WHERE id_descarga =:idDescarga")
    void deleteById(Integer idDescarga);

    @Delete(entity = Descarga.class)
    void deleteDescarga(Descarga descarga);

    @Query("UPDATE descargas SET eliminado = 1, sincronizado = 0 WHERE id_descarga =:idDescarga")
    void delete(Integer idDescarga);
}
