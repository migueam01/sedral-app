package com.mam.alicamp.ui.actividades;

import static com.mam.alicamp.constantes.Constantes.ALTURA_POZO;
import static com.mam.alicamp.constantes.Constantes.EDITANDO;
import static com.mam.alicamp.constantes.Constantes.GADM_ID;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_POZO;
import static com.mam.alicamp.constantes.Constantes.PROYECTO_ID;
import static com.mam.alicamp.constantes.Constantes.SECTOR_ID;
import static com.mam.alicamp.constantes.Constantes.TUBERIA_ID;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mam.alicamp.R;
import com.mam.alicamp.databinding.ActivityListaTuberiasBinding;
import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Tuberia;
import com.mam.alicamp.ui.adapters.TuberiaAdapter;
import com.mam.alicamp.ui.viewmodels.PozoViewModel;
import com.mam.alicamp.ui.viewmodels.TuberiaViewModel;

import java.text.MessageFormat;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ListaTuberias extends AppCompatActivity {

    private ActivityListaTuberiasBinding binding;

    private PozoViewModel pozoViewModel;
    private TuberiaViewModel tuberiaViewModel;

    private TuberiaAdapter tuberiaAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Pozo pozo;

    private String nombrePozo;
    private int gadmId;
    private int proyectoId;
    private int sectorId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_tuberias);

        binding = ActivityListaTuberiasBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            gadmId = b.getInt(GADM_ID);
            proyectoId = b.getInt(PROYECTO_ID);
            sectorId = b.getInt(SECTOR_ID);
            nombrePozo = b.getString(NOMBRE_POZO);
        }

        initViews();

        inicializarViewModel();

        addToolbar();

        cargarTuberias();

        inicializarObjetos();

        configurarRecycler();

        cargarTuberiasPorPozo();

        initListeners();
    }

    private void cargarTuberias() {
        tuberiaViewModel.getAllTuberias().observe(this, listaTuberias -> {
            if (listaTuberias != null) {
                Log.i("TUBERIAS", "Cantidad de tuberías " + listaTuberias.size());
            }
        });
    }

    private void initViews() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                regresar();
            }
        });
        binding.recyclerTuberias.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        binding.recyclerTuberias.setHasFixedSize(true);
        binding.recyclerTuberias.setItemAnimator(new DefaultItemAnimator());
    }

    private void inicializarViewModel() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        pozoViewModel = new ViewModelProvider(this, factory).get(PozoViewModel.class);
        tuberiaViewModel = new ViewModelProvider(this, factory).get(TuberiaViewModel.class);
    }

    private void addToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarListaTuberias);
        toolbar.setTitle(R.string.activity_lista_tuberias);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void inicializarObjetos() {
        pozoViewModel.obtenerPozoPorNombre(nombrePozo).observe(this, pozoBuscado -> {
            if (pozoBuscado != null) {
                pozo = pozoBuscado;
                binding.txtTituloTuberias.setText(MessageFormat.format("{0} {1}",
                        getString(R.string.label_titulo_tub1), pozoBuscado.getNombre()));
            }
        });
        layoutManager = new LinearLayoutManager(this);
    }

    private void configurarRecycler() {
        tuberiaAdapter = new TuberiaAdapter(new TuberiaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Tuberia tuberia) {
                editarTuberia(tuberia.getIdTuberia());
            }

            @Override
            public void onEliminarClick(Tuberia tuberia) {
                new SweetAlertDialog(ListaTuberias.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("¿Eliminar tubería?")
                        .setContentText("Esta acción no se puede deshacer")
                        .setConfirmText("Si")
                        .setCancelText("No")
                        .showCancelButton(true)
                        .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                        .setConfirmClickListener(sweetAlertDialog -> {
                            eliminarTuberia(tuberia.getIdTuberia());
                            sweetAlertDialog.setTitleText("Eliminado!")
                                    .setContentText("El registro ha sido eliminado")
                                    .setConfirmText("Aceptar")
                                    .showCancelButton(false)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }).show();
            }
        });
        binding.recyclerTuberias.setLayoutManager(layoutManager);
        binding.recyclerTuberias.setAdapter(tuberiaAdapter);
    }

    private void cargarTuberiasPorPozo() {
        tuberiaViewModel.obtenerTuberiasPorPozo(nombrePozo).observe(this, tuberias ->
                tuberiaAdapter.setTuberias(tuberias));
    }

    private void initListeners() {
        binding.fabCrearTuberia.setOnClickListener(v -> crearTuberia());
        binding.btnAvanzarFin.setOnClickListener(v -> avanzarFin());
    }

    private void eliminarTuberia(Integer idTuberia) {
        //tuberiaViewModel.eliminarTuberia(idTuberia);
    }

    private void crearTuberia() {
        intentTuberia(-1, false);
    }

    private void editarTuberia(Integer idTuberia) {
        intentTuberia(idTuberia, true);
    }

    private void intentTuberia(Integer idTuberia, boolean editando) {
        Intent intent = new Intent(this, TuberiaCatastrada.class);
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(NOMBRE_POZO, nombrePozo);
        intent.putExtra(ALTURA_POZO, pozo.getAltura());
        intent.putExtra(TUBERIA_ID, idTuberia);
        intent.putExtra(EDITANDO, editando);
        startActivity(intent);
        finish();
    }

    private void avanzarFin() {
        Intent intent = new Intent(this, FinCatastro.class);
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(NOMBRE_POZO, nombrePozo);
        startActivity(intent);
        finish();
    }

    private void regresar() {
        Intent intent = new Intent(this, Dimensiones.class);
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