package com.mam.alicamp.controlesUI;

import com.mam.alicamp.db.entidades.Proyecto;

import java.util.List;

public class ProyectoSpinner implements ISpinner<Proyecto> {
    @Override
    public int getIdSpinner(List<Proyecto> listaBusqueda, Integer idBuscado) {
        int indiceSpinner = -1;
        for (int i = 0; i < listaBusqueda.size(); i++) {
            if (!listaBusqueda.get(i).getIdProyecto().equals(idBuscado)) {
                indiceSpinner = i++;
            } else {
                break;
            }
        }
        return indiceSpinner;
    }

    @Override
    public int getIdSpinner(List<Proyecto> listaBusqueda, String valorBusqueda) {
        return 0;
    }
}
