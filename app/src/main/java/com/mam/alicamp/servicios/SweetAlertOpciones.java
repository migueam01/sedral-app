package com.mam.alicamp.servicios;

import android.content.Context;

import com.mam.alicamp.ui.interfaces.IEliminacion;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetAlertOpciones {

    private Context context;
    private String mensaje;

    public SweetAlertOpciones(Context context) {
        this.context = context;
    }

    public void mostrarDialogoSuccess() {
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Éxito")
                .setContentText(mensaje)
                .show();
    }

    public void mostrarDialogoError() {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText(mensaje)
                .show();
    }

    public void mostrarDialogoEliminar(IEliminacion elimina, String tipo,
                                       File directorio) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Eliminar " + tipo + "?")
                .setContentText("Esta acción no se puede deshacer")
                .setConfirmText("Si")
                .setCancelText("No")
                .showCancelButton(true)
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    if (elimina != null) {
                        elimina.onCancelar();
                    }
                })
                .setConfirmClickListener(sweetAlertDialog -> {
                    if (elimina != null) {
                        elimina.onEliminar(directorio, tipo);
                    }
                    sweetAlertDialog.dismissWithAnimation();
                })
                .show();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
