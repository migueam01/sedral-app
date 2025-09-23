package com.mam.alicamp.controlesUI;

import com.mam.alicamp.db.entidades.Sector;

import java.util.List;

public class SectorSpinner implements ISpinner<Sector> {
    @Override
    public int getIdSpinner(List<Sector> listaBusqueda, Integer idBuscado) {
        int indiceSpinner = -1;
        for (int i = 0; i < listaBusqueda.size(); i++) {
            if (!listaBusqueda.get(i).getIdSector().equals(idBuscado)) {
                indiceSpinner = i++;
            } else {
                break;
            }
        }
        return indiceSpinner;
    }

    @Override
    public int getIdSpinner(List<Sector> listaBusqueda, String valorBusqueda) {
        return 0;
    }
}
