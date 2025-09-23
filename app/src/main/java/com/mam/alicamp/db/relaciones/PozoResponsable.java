package com.mam.alicamp.db.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Responsable;

import java.util.List;

public class PozoResponsable {
    @Embedded
    public Responsable responsable;
    @Relation(
            parentColumn = "id_responsable",
            entityColumn = "fk_id_responsable"
    )
    public List<Pozo> responsablePozos;
}
