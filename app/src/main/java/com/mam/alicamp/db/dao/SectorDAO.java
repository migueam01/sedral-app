package com.mam.alicamp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mam.alicamp.db.dto.GadmSectorProyecto;
import com.mam.alicamp.db.entidades.Sector;

import java.util.List;

@Dao
public interface SectorDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Sector sector);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Sector> sectores);

    @Query("SELECT * FROM sectores WHERE id_sector =:idSector")
    Sector findById(Integer idSector); //Para sincronización

    @Query("SELECT * FROM sectores")
    LiveData<List<Sector>> findAll();

    @Query("SELECT * FROM sectores WHERE fk_id_proyecto =:idProyecto")
    LiveData<List<Sector>> findSectoresByProyecto(Integer idProyecto);

    @Query("SELECT g.nombre AS gadm, " +
            "p.nombre AS proyecto, " +
            "s.nombre AS sector " +
            "FROM gadms g, proyectos p, sectores s " +
            "WHERE p.fk_id_gadm = g.id_gadm AND " +
            "s.fk_id_proyecto = p.id_proyecto")
    LiveData<List<GadmSectorProyecto>> getGadmsSectoresProyectos();  //Para crear las carpetas de los sectores

    @Update(entity = Sector.class)
    void updateSector(Sector sector);

    @Query("DELETE FROM sectores WHERE id_sector =:idSector")
    void deleteById(Integer idSector);

    @Query("DELETE FROM sectores")
    void deleteAll();

    @Query("UPDATE sectores SET eliminado = 1, sincronizado = 0 WHERE id_sector =:idSector")
    void deleteSector(Integer idSector);
}
