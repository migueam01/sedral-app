package com.mam.alicamp.servicios;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class FolderHandling {

    private SweetAlertOpciones sweetAlertOpciones;

    private String nombreCarpeta;
    private String tipoCarpeta;

    public FolderHandling() {
    }

    public FolderHandling(Context context) {
        sweetAlertOpciones = new SweetAlertOpciones(context);
    }

    public boolean crearEditarCarpeta(String nombreAnterior, String pathBase) {
        boolean accionCarpeta;
        File carpetaNueva;
        File carpetaAntigua;

        carpetaNueva = new File(pathBase, nombreCarpeta);
        if (nombreAnterior == null) {
            accionCarpeta = crearCarpetaDisco(carpetaNueva);
        } else {
            carpetaAntigua = new File(pathBase, nombreAnterior);
            accionCarpeta = editarCarpetaDisco(carpetaAntigua, carpetaNueva);
        }
        return accionCarpeta;
    }

    public void crearCarpetasBD(File folder) {
        if (!folder.exists()) {
            if (!folder.mkdir()) {
                Log.i("CARPETAS", "No se pudo crear la carpeta");
            } else {
                Log.i("CARPETAS", "Carpeta creada exitosamente");
            }
        } else {
            Log.i("CARPETAS", "Ya existe la carpeta");
        }
    }

    private boolean crearCarpetaDisco(File folder) {
        boolean created = false;
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                sweetAlertOpciones.setMensaje("No se pudo crear la carpeta " + folder.getAbsolutePath());
                sweetAlertOpciones.mostrarDialogoError();
            } else {
                created = true;
                sweetAlertOpciones.setMensaje("Carpeta creada exitosamente");
                sweetAlertOpciones.mostrarDialogoSuccess();
            }
        } else {
            sweetAlertOpciones.setMensaje("Ya existe la carpeta");
            sweetAlertOpciones.mostrarDialogoError();
        }
        return created;
    }

    private boolean editarCarpetaDisco(File oldFolder, File newFolder) {
        if (oldFolder.renameTo(newFolder)) {
            sweetAlertOpciones.setMensaje("Carpeta actualizada exitosamente");
            sweetAlertOpciones.mostrarDialogoSuccess();
            return true;
        } else {
            sweetAlertOpciones.setMensaje("No se pudo actualizar la carpeta");
            sweetAlertOpciones.mostrarDialogoError();
            return false;
        }
    }

    public boolean eliminarCarpetaDisco(File carpeta) {
        File[] carpetasHijas = carpeta.listFiles();
        if (carpetasHijas != null) {
            for (File hija : carpetasHijas) {
                eliminarCarpetaDisco(hija);
            }
        }
        return carpeta.delete();
    }

    public void setNombreCarpeta(String nombreCarpeta) {
        this.nombreCarpeta = nombreCarpeta;
    }

    public String getTipoCarpeta() {
        return tipoCarpeta;
    }

    public void setTipoCarpeta(String tipoCarpeta) {
        this.tipoCarpeta = tipoCarpeta;
    }
}
