package com.mam.alicamp.db.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Sector;

public class PozosConSector {
    @Embedded
    public Pozo pozo;

    @Relation(
            entity = Sector.class,
            parentColumn = "fk_id_sector",
            entityColumn = "id_sector"
    )
    public Sector sector;
}
