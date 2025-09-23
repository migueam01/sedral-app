package com.mam.alicamp.servicios;

import static com.mam.alicamp.constantes.Constantes.NOMBRE_DB;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Objects;

public class DatabaseExporter {

    public static void exportRoomDatabase(Context context, File archivoExportacion) {
        SweetAlertOpciones sweetAlertOpciones = new SweetAlertOpciones(context);
        try {
            String dbName = NOMBRE_DB;
            File dbFile = context.getDatabasePath(dbName);
            File exportFile = new File(archivoExportacion, dbName + ".db");
            try (
                    FileInputStream fis = new FileInputStream(dbFile);
                    FileOutputStream fos = new FileOutputStream(exportFile);
                    FileChannel src = fis.getChannel();
                    FileChannel dst = fos.getChannel()
            ) {
                dst.transferFrom(src, 0, src.size());
                sweetAlertOpciones.setMensaje("Base de datos exportada correctamente");
                sweetAlertOpciones.mostrarDialogoSuccess();
            }

        } catch (IOException io) {
            Log.e("ERROR_BD", Objects.requireNonNull(io.getMessage()));
            sweetAlertOpciones.setMensaje("Error al exportar la base de datos");
            sweetAlertOpciones.mostrarDialogoError();
        }
    }
}
