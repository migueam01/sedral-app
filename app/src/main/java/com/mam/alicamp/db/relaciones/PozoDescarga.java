package com.mam.alicamp.db.relaciones;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mam.alicamp.db.entidades.Descarga;
import com.mam.alicamp.db.entidades.Pozo;

import java.util.List;

public class PozoDescarga {
    @Embedded
    public Descarga descarga;
    @Relation(
            parentColumn = "id_descarga",
            entityColumn = "fk_id_descarga"
    )
    public List<Pozo> descargaPozos;
}
