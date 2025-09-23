package com.mam.alicamp.db.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Sector;

import java.util.List;

public class SectorConPozos {
    @Embedded
    public Sector sector;
    @Relation(
            entity = Pozo.class,
            parentColumn = "id_sector",
            entityColumn = "fk_id_sector"
    )
    public List<Pozo> pozos;
}
