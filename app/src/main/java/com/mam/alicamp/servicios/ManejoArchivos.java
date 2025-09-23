package com.mam.alicamp.servicios;

import java.io.File;

public class ManejoArchivos {

    public static boolean eliminarArchivos(String directorio, String nombre) {
        boolean archivoEliminado = false;
        File[] listaArchivos = new File(directorio).listFiles(archivo -> {
            if (archivo.isFile()) {
                return archivo.getName().startsWith(nombre);
            }
            return false;
        });
        if (listaArchivos != null) {
            for (File file : listaArchivos) {
                archivoEliminado = file.delete();
            }
        }
        return archivoEliminado;
    }
}
