package com.mam.alicamp.ui.actividades;

import static com.mam.alicamp.constantes.ActividadesCompletadas.ACTIVIDAD_FIN_CATASTRO;
import static com.mam.alicamp.constantes.Constantes.DIRECTORIO_POZOS;
import static com.mam.alicamp.constantes.Constantes.GADM_ID;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_POZO;
import static com.mam.alicamp.constantes.Constantes.PROYECTO_ID;
import static com.mam.alicamp.constantes.Constantes.SECTOR_ID;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mam.alicamp.R;
import com.mam.alicamp.databinding.ActivityFinCatastroBinding;
import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.relaciones.PozoConDescarga;
import com.mam.alicamp.servicios.SweetAlertOpciones;
import com.mam.alicamp.ui.viewmodels.PozoViewModel;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FinCatastro extends PermisosActivity {

    private ActivityFinCatastroBinding binding;

    private MediaRecorder recorder;

    private PozoViewModel pozoViewModel;

    private SweetAlertOpciones sweetAlertOpciones;

    private Pozo pozo;

    private String pathAudio;

    private String nombrePozo;
    private int gadmId;
    private int proyectoId;
    private int sectorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_catastro);

        binding = ActivityFinCatastroBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        addToolBar();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            gadmId = b.getInt(GADM_ID);
            proyectoId = b.getInt(PROYECTO_ID);
            nombrePozo = b.getString(NOMBRE_POZO);
            sectorId = b.getInt(SECTOR_ID);
        }

        requestAdditionalPermissions();
        inicializarFlechaRetroceso();
        inicializarViewModels();
        inicializarObjetos();
        initListeners();
    }

    @Override
    protected void onStoragePermissionGranted() {

    }

    @Override
    protected void onAdditionalPermissionsGranted() {
        Log.i("PERMISOS", "Permisos concedidos");
    }

    private void addToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbarFinal);
        toolbar.setTitle(R.string.activity_fin);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void initListeners() {
        binding.toggleButtonAudio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                verificarPermisos();
            } else {
                detenerGrabacion();
            }
        });
        binding.imgBtnPlay.setOnClickListener(v -> reproducirAudio());
        binding.btnInicio.setOnClickListener(v -> finalizarCatastro());
        binding.btnNuevoPozo.setOnClickListener(v -> catastrarNuevoPozo());
    }

    private void verificarPermisos() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {
            iniciarGrabacion();
        } else {
            showToast("Permisos de grabación no concedido");
        }
    }

    private void iniciarGrabacion() {
        recorder = new MediaRecorder();
        recorder.setOutputFile(pathAudio);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
            recorder.start();
            showToast("Grabando....");
        } catch (IllegalStateException | IOException ise) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(ise.getMessage())
                    .setTitle("Advertencia")
                    .setPositiveButton("OK",
                            (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void detenerGrabacion() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            showToast("Grabación detenida");
        }
    }

    private void reproducirAudio() {
        MediaPlayer player = new MediaPlayer();
        try {
            File archivoAudio = new File(pathAudio);
            if (archivoAudio.exists()) {
                player.setDataSource(pathAudio);
                player.prepare();
                player.start();
                showToast("Reproduciendo audio");
            } else {
                sweetAlertOpciones.setMensaje("No existe el archivo de audio");
                sweetAlertOpciones.mostrarDialogoError();
            }
        } catch (IOException ex) {
            Log.e("ERROR_AUDIO", Objects.requireNonNull(ex.getMessage()));
        }
    }

    private void inicializarFlechaRetroceso() {
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        regresar();
                    }
                });
    }

    private void inicializarViewModels() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        pozoViewModel = new ViewModelProvider(this, factory).get(PozoViewModel.class);
    }

    private void inicializarObjetos() {
        pozoViewModel.obtenerPozoConDescarga(nombrePozo).observe(this, pzDsc -> {
            pozo = pzDsc.pozo;
            pathAudio = pzDsc.pozo.getPathMedia() + "/" + pzDsc.pozo.getNombre() + ".mp3";
            cargarValoresIniciales(pzDsc);
            cargarFotoPozo(pzDsc);
        });
        sweetAlertOpciones = new SweetAlertOpciones(this);
    }

    private void cargarValoresIniciales(PozoConDescarga pozoConDescarga) {
        String tapado = pozoConDescarga.pozo.getTapado();
        //String fechaCatastro = pozoConDescarga.pozo.getFechaCatastro();
        binding.txtPozo.setText(String.format("Pozo catastrado: %s", pozoConDescarga.pozo.getNombre()));
        binding.txtHPozo.setText(String.format("H= %s%s", pozoConDescarga.pozo.getAltura(), "m"));
        if (tapado.equalsIgnoreCase("Si")) {
            binding.textViewEstado.setText(String.format("Estado: %s", getString(R.string.opcion_tapado)));
        } else {
            binding.textViewEstado.setText(String.format("Estado: %s", pozoConDescarga.pozo.getEstado()));
        }
        binding.textViewTapado.setText(String.format("Tapado: %s", tapado));
        binding.textViewDescarga.setText(String.format("Descarga: %s", pozoConDescarga.descarga.getNombre()));
        //binding.textViewFecha.setText(fechaCatastro);
        binding.editTextObservacion.setText(pozoConDescarga.pozo.getObservacion());
        if (pozo.getActividadCompletada() < ACTIVIDAD_FIN_CATASTRO) {
            binding.editTextObservacion.setText(pozoConDescarga.pozo.getObservacion());
        }
    }

    private void cargarFotoPozo(PozoConDescarga pozoConDescarga) {
        String pathFoto = pozoConDescarga.pozo.getPathMedia() + pozoConDescarga.pozo.getNombre() + "-U.jpg";
        Glide.with(this)
                .load(pathFoto)
                .into(binding.imagenFotoPozo);
    }

    private void finalizarCatastro() {
        Intent intent = new Intent(this, MainActivity.class);
        pozo.setObservacion(binding.editTextObservacion.getText().toString());
        pozo.setActividadCompletada(ACTIVIDAD_FIN_CATASTRO);
        pozoViewModel.actualizarFin(pozo);
        startActivity(intent);
        finish();
    }

    private void catastrarNuevoPozo() {
        Intent intent = new Intent(this, PozoCatastrado.class);
        String directorioPozos = pozo.getPathMedia();
        pozo.setObservacion(binding.editTextObservacion.getText().toString());
        pozo.setActividadCompletada(ACTIVIDAD_FIN_CATASTRO);
        pozoViewModel.actualizarFin(pozo);
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(DIRECTORIO_POZOS, directorioPozos);
        startActivity(intent);
        finish();
    }

    private void regresar() {
        Intent intent;
        if (pozo.getTapado().equalsIgnoreCase("Si")) {
            intent = new Intent(this, CallesUbicacion.class);
        } else {
            intent = new Intent(this, ListaTuberias.class);
        }
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(NOMBRE_POZO, nombrePozo);
        startActivity(intent);
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        regresar();
        return false;
    }
}