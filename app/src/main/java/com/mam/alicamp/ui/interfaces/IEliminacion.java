package com.mam.alicamp.ui.interfaces;

import java.io.File;

public interface IEliminacion {
    void onEliminar(File directorio, String tipo);

    void onCancelar();
}
