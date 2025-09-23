package com.mam.alicamp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mam.alicamp.db.entidades.Responsable;

import java.util.List;

@Dao
public interface ResponsableDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Responsable> responsables);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Responsable responsable);

    @Query("SELECT * FROM responsables WHERE id_responsable =:idResponsable")
    Responsable findById(Integer idResponsable); //Para sincronización

    @Query("SELECT * FROM responsables ORDER BY nombre")
    LiveData<List<Responsable>> findAllResponsables();

    @Update(entity = Responsable.class)
    void update(Responsable responsable);

    @Query("DELETE FROM responsables WHERE id_responsable =:idResponsable")
    void deleteById(Integer idResponsable);

    @Query("DELETE FROM responsables")
    void deleteAll();

    @Query("UPDATE responsables SET eliminado = 1, sincronizado = 0 WHERE id_responsable =:idResponsable")
    void deleteResponsable(Integer idResponsable);
}
