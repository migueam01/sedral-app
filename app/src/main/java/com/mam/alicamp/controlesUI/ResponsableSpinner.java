package com.mam.alicamp.controlesUI;

import com.mam.alicamp.db.entidades.Responsable;

import java.util.List;

public class ResponsableSpinner implements ISpinner<Responsable> {
    @Override
    public int getIdSpinner(List<Responsable> listaBusqueda, Integer idBuscado) {
        int indiceSpinner = -1;
        for (int i = 0; i < listaBusqueda.size(); i++) {
            if (!listaBusqueda.get(i).getIdResponsable().equals(idBuscado)) {
                indiceSpinner = i++;
            } else {
                break;
            }
        }
        return indiceSpinner;
    }

    @Override
    public int getIdSpinner(List<Responsable> listaBusqueda, String valorBusqueda) {
        return 0;
    }
}
