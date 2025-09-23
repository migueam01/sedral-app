package com.mam.alicamp.controlesUI;

import java.util.List;

public interface ISpinner<T> {
    int getIdSpinner(List<T> listSearch, Integer idBuscado);
    int getIdSpinner(List<T> listaBusqueda, String valorBusqueda);
}