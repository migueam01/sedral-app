package com.mam.alicamp.db.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mam.alicamp.db.entidades.Descarga;
import com.mam.alicamp.db.entidades.Pozo;

public class PozoConDescarga {
    @Embedded
    public Pozo pozo;

    @Relation(
            entity = Descarga.class,
            parentColumn = "fk_id_descarga",
            entityColumn = "id_descarga"
    )
    public Descarga descarga;
}
