package com.mam.alicamp.db.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.mam.alicamp.constantes.Constantes;
import com.mam.alicamp.db.dao.DescargaDAO;
import com.mam.alicamp.db.dao.GadmDAO;
import com.mam.alicamp.db.dao.PozoDAO;
import com.mam.alicamp.db.dao.ProyectoDAO;
import com.mam.alicamp.db.dao.ResponsableDAO;
import com.mam.alicamp.db.dao.SectorDAO;
import com.mam.alicamp.db.dao.TuberiaDAO;
import com.mam.alicamp.db.entidades.Descarga;
import com.mam.alicamp.db.entidades.Gadm;
import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Proyecto;
import com.mam.alicamp.db.entidades.Responsable;
import com.mam.alicamp.db.entidades.Sector;
import com.mam.alicamp.db.entidades.Tuberia;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Descarga.class, Gadm.class, Pozo.class, Proyecto.class, Responsable.class,
        Sector.class, Tuberia.class}, version = 1, exportSchema = false)
public abstract class AlicampDB extends RoomDatabase {
    private static volatile AlicampDB INSTANCIA;

    public abstract DescargaDAO descargaDAO();

    public abstract GadmDAO gadmDAO();

    public abstract PozoDAO pozoDAO();

    public abstract ProyectoDAO proyectoDAO();

    public abstract ResponsableDAO responsableDAO();

    public abstract SectorDAO sectorDAO();

    public abstract TuberiaDAO tuberiaDAO();

    private static final int THREADS = 4;

    public static final ExecutorService dbExecutor = Executors.newFixedThreadPool(THREADS);

    public static AlicampDB getAppDb(final Context context) {
        if (INSTANCIA == null) {
            synchronized (AlicampDB.class) {
                if (INSTANCIA == null) {
                    INSTANCIA = Room.databaseBuilder(
                                    context.getApplicationContext(), AlicampDB.class,
                                    Constantes.NOMBRE_DB)
                            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                            .build();
                }
            }
        }
        return INSTANCIA;
    }

    public static void resetDatabase(Context context) {
        if (INSTANCIA != null) {
            INSTANCIA.close();
            INSTANCIA = null;
        }
        context.deleteDatabase(Constantes.NOMBRE_DB); // Elimina físicamente el archivo
    }

    public static void destroyInstance() {
        INSTANCIA = null;
    }
}
