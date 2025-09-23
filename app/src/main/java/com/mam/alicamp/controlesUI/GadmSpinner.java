package com.mam.alicamp.controlesUI;

import com.mam.alicamp.db.entidades.Gadm;

import java.util.List;

public class GadmSpinner implements ISpinner<Gadm> {

    @Override
    public int getIdSpinner(List<Gadm> listaBusqueda, Integer idBuscado) {
        int indiceSpinner = -1;
        for (int i = 0; i < listaBusqueda.size(); i++) {
            if (!listaBusqueda.get(i).getIdGadm().equals(idBuscado)) {
                indiceSpinner = i++;
            } else {
                break;
            }
        }
        return indiceSpinner;
    }

    @Override
    public int getIdSpinner(List<Gadm> listaBusqeuda, String valorBusqueda) {
        return 0;
    }
}
