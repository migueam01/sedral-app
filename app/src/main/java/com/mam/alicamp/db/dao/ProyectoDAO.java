package com.mam.alicamp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mam.alicamp.db.entidades.Proyecto;

import java.util.List;

@Dao
public interface ProyectoDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Proyecto proyecto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Proyecto> proyectos);

    @Query("SELECT * FROM proyectos WHERE id_proyecto =:idProyecto")
    Proyecto findById(Integer idProyecto); //Para sincronización

    @Query("SELECT * FROM proyectos WHERE id_proyecto =:idProyecto")
    LiveData<Proyecto> findProyectoById(Integer idProyecto);

    @Query("SELECT * FROM proyectos")
    LiveData<List<Proyecto>> findAll();

    @Query("SELECT * FROM proyectos WHERE fk_id_gadm =:idGadm ORDER BY nombre")
    LiveData<List<Proyecto>> findProyectosByGadm(Integer idGadm);

    @Update(entity = Proyecto.class)
    void updateProyecto(Proyecto proyecto);

    @Query("DELETE FROM proyectos WHERE id_proyecto =:idProyecto")
    void deleteById(Integer idProyecto);

    @Query("DELETE FROM proyectos")
    void deleteAll();

    /*@Query("UPDATE proyectos SET eliminado = 1, sincronizado = 0 WHERE id_proyecto =:idProyecto")
    void deleteProyecto(Integer idProyecto);*/
}
