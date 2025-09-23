package com.mam.alicamp.db.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Tuberia;

public class TuberiaConPozos {

    @Embedded
    public Tuberia tuberia;

    @Relation(
            parentColumn = "fk_pozo_inicio",
            entityColumn = "id_pozo"
    )
    public Pozo pozoInicio;

    @Relation(
            parentColumn = "fk_pozo_fin",
            entityColumn = "id_pozo"
    )
    public Pozo pozoFin;
}
