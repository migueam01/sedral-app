package com.mam.alicamp.ui.actividades;

import static com.mam.alicamp.constantes.ActividadesCompletadas.ACTIVIDAD_DIMENSIONES;
import static com.mam.alicamp.constantes.Constantes.GADM_ID;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_POZO;
import static com.mam.alicamp.constantes.Constantes.PROYECTO_ID;
import static com.mam.alicamp.constantes.Constantes.SECTOR_ID;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mam.alicamp.R;
import com.mam.alicamp.controlesUI.ISpinner;
import com.mam.alicamp.controlesUI.ValoresSpinner;
import com.mam.alicamp.databinding.ActivityDimensionesBinding;
import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.ui.viewmodels.PozoViewModel;

import static com.mam.alicamp.servicios.Validaciones.validarCamposVacios;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Dimensiones extends AppCompatActivity {

    private ActivityDimensionesBinding binding;

    private PozoViewModel pozoViewModel;

    private Pozo pozo;

    private ISpinner<String> iSpinner;

    private String nombrePozo;
    private int gadmId;
    private int proyectoId;
    private int sectorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimensiones);

        binding = ActivityDimensionesBinding.inflate(getLayoutInflater());
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

        inicializarFlechaRetroceso();

        inicializarViewModels();

        inicializarObjetos();

        clearFocusEdits();

        initListeners();
    }

    private void addToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbarDimensiones);
        toolbar.setTitle(R.string.tab_dimensiones);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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
        iSpinner = new ValoresSpinner();
        pozoViewModel.obtenerPozoPorNombre(nombrePozo).observe(this, pozoBuscado -> {
            if (pozoBuscado != null) {
                pozo = pozoBuscado;
                cargarValoresIniciales(pozoBuscado);
            }
        });
    }

    private void cargarValoresIniciales(Pozo pozoBuscado) {
        if (ACTIVIDAD_DIMENSIONES <= pozoBuscado.getActividadCompletada()) {
            seleccionarItemSpinnerFluido(pozoBuscado.getFluido());
            seleccionarItemSpinnerEstado(pozoBuscado.getEstado());
            binding.editTextTapa.setText(String.valueOf(pozoBuscado.getDimensionTapa()));
            binding.editTextAlto.setText(String.valueOf(pozoBuscado.getAltura()));
            binding.editTextAncho.setText(String.valueOf(pozoBuscado.getAncho()));
        }
    }

    private void seleccionarItemSpinnerFluido(String valorBuscado) {
        if (valorBuscado != null && !valorBuscado.isEmpty()) {
            List<String> listaFluido = Arrays.asList(getResources().getStringArray(R.array.spinner_sistema));
            int idSpinner = retornarIndiceSpinner(listaFluido, valorBuscado);
            binding.spinnerFluido.setSelection(idSpinner);
        }
    }

    private void seleccionarItemSpinnerEstado(String valorBuscado) {
        if (valorBuscado != null && !valorBuscado.isEmpty()) {
            List<String> listaFluido = Arrays.asList(getResources().getStringArray(R.array.spinner_estado));
            int idSpinner = retornarIndiceSpinner(listaFluido, valorBuscado);
            binding.spinnerEstadoPozo.setSelection(idSpinner);
        }
    }

    private int retornarIndiceSpinner(List<String> listaBusqueda, String valorBuscado) {
        return iSpinner.getIdSpinner(listaBusqueda, valorBuscado);
    }

    private void clearFocusEdits() {
        binding.editTextAncho.clearFocus();
        binding.editTextAlto.clearFocus();
        binding.editTextTapa.clearFocus();
    }

    private void initListeners() {
        binding.btnAvanzarTuberias.setOnClickListener(v -> avanzarTuberias());
    }

    private void avanzarTuberias() {
        if (validarCamposVacios(binding.editTextTapa, "Campo requerido") &&
                validarCamposVacios(binding.editTextAlto, "Campo requerido") &&
                validarCamposVacios(binding.editTextAncho, "Campo requerido")) {
            double tapa = Double.parseDouble(binding.editTextTapa.getText().toString());
            double altura = Double.parseDouble(binding.editTextAlto.getText().toString());
            double ancho = Double.parseDouble(binding.editTextAncho.getText().toString());
            String fluido = binding.spinnerFluido.getSelectedItem().toString();
            String estado = binding.spinnerEstadoPozo.getSelectedItem().toString();
            pozo.setDimensionTapa(tapa);
            pozo.setAltura(altura);
            pozo.setAncho(ancho);
            pozo.setFluido(fluido);
            pozo.setEstado(estado);
            if (pozo.getActividadCompletada() < ACTIVIDAD_DIMENSIONES) {
                pozo.setActividadCompletada(ACTIVIDAD_DIMENSIONES);
            }
            pozoViewModel.actualizarDimensiones(pozo);
            intentActivity();
        }
    }

    private void intentActivity() {
        Intent intent = new Intent(this, ListaTuberias.class);
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(NOMBRE_POZO, nombrePozo);
        startActivity(intent);
        finish();
    }

    private void regresar() {
        Intent intent = new Intent(this, CallesUbicacion.class);
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(NOMBRE_POZO, nombrePozo);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        regresar();
        return false;
    }
}