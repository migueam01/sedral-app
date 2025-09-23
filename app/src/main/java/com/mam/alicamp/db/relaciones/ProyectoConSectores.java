package com.mam.alicamp.db.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mam.alicamp.db.entidades.Proyecto;
import com.mam.alicamp.db.entidades.Sector;

import java.util.List;

public class ProyectoConSectores {
    @Embedded
    public Proyecto proyecto;

    @Relation(
            entity = Sector.class,
            parentColumn = "id_proyecto",
            entityColumn = "fk_id_proyecto"
    )
    public List<Sector> sectores;
}
