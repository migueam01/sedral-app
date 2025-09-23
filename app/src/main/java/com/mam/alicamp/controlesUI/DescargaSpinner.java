package com.mam.alicamp.controlesUI;

import com.mam.alicamp.db.entidades.Descarga;

import java.util.List;

public class DescargaSpinner implements ISpinner<Descarga> {
    @Override
    public int getIdSpinner(List<Descarga> listaBusqueda, Integer idBuscado) {
        int indiceSpinner = -1;
        for (int i = 0; i < listaBusqueda.size(); i++) {
            if (!listaBusqueda.get(i).getIdDescarga().equals(idBuscado)) {
                indiceSpinner = i++;
            } else {
                break;
            }
        }
        return indiceSpinner;
    }

    @Override
    public int getIdSpinner(List<Descarga> listaBusqueda, String valorBusqueda) {
        return 0;
    }
}
