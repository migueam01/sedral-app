package com.mam.alicamp.ui.actividades;

import static com.mam.alicamp.constantes.Constantes.DESCRIPCION_FOTO;
import static com.mam.alicamp.constantes.Constantes.PATH_FOTO;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mam.alicamp.R;
import com.mam.alicamp.servicios.TouchImageView;

public class VisualizaFoto extends AppCompatActivity {

    private TouchImageView touchImageView;
    private String descriptionPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photo);

        String pathImage;

        Bundle b = getIntent().getExtras();
        if (b != null) {
            pathImage = b.getString(PATH_FOTO);
            descriptionPhoto = b.getString(DESCRIPCION_FOTO);
        } else {
            pathImage = "";
        }

        addToolbar();

        touchImageView = findViewById(R.id.esquemaImagen);

        insertarImagen(pathImage);
    }

    private void insertarImagen(String img) {
        Bitmap foto = BitmapFactory.decodeFile(img);
        touchImageView.setImageBitmap(foto);
    }

    private void addToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarPhoto);
        toolbar.setTitle(descriptionPhoto);
        setSupportActionBar(toolbar);
    }
}