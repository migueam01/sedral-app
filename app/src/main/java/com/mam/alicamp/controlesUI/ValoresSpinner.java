package com.mam.alicamp.controlesUI;

import java.util.List;

public class ValoresSpinner implements ISpinner<String> {
    @Override
    public int getIdSpinner(List<String> listSearch, Integer idBuscado) {
        return 0;
    }

    @Override
    public int getIdSpinner(List<String> listaBusqueda, String valorBusqueda) {
        int indiceSpinner = 0;
        for (int i = 0; i < listaBusqueda.size(); i++) {
            if (!listaBusqueda.get(i).equals(valorBusqueda)) {
                indiceSpinner++;
            } else {
                break;
            }
        }
        if (indiceSpinner == listaBusqueda.size()) {
            indiceSpinner = 0;
        }
        return indiceSpinner;
    }
}
