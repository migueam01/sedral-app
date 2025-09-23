package com.mam.alicamp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mam.alicamp.db.entidades.Gadm;

import java.util.List;

@Dao
public interface GadmDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Gadm> gadms);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Gadm gadm);

    @Query("SELECT * FROM gadms WHERE id_gadm =:idGadm")
    Gadm findById(Integer idGadm); //Para sincronización

    @Query("SELECT * FROM gadms ORDER BY nombre")
    LiveData<List<Gadm>> findAllGadm();

    @Query("SELECT * FROM gadms WHERE id_gadm =:idGadm")
    LiveData<Gadm> findGadmById(Integer idGadm);

    @Update(entity = Gadm.class)
    void updateGadm(Gadm gadm);

    @Query("DELETE FROM gadms WHERE id_gadm =:idGadm")
    void deleteById(Integer idGadm);

    @Query("UPDATE gadms SET eliminado = 1, sincronizado = 0 WHERE id_gadm =:idGadm")
    void deleteGadm(Integer idGadm);

    @Query("DELETE FROM gadms")
    void deleteAll();
}
