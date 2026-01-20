package com.mam.alicamp.ui.actividades;

import static com.mam.alicamp.constantes.Constantes.DESCRIPCION_FOTO;
import static com.mam.alicamp.constantes.Constantes.DIRECTORIO_POZOS;
import static com.mam.alicamp.constantes.Constantes.EDITANDO;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_CARPETA_BASE;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_CARPETA_MEDIA;
import static com.mam.alicamp.constantes.Constantes.PATH_FOTO;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_POZO;
import static com.mam.alicamp.constantes.Constantes.REGRESANDO;
import static com.mam.alicamp.constantes.Constantes.SECTOR;
import static com.mam.alicamp.constantes.Constantes.SECTOR_NOMBRE;
import static com.mam.alicamp.servicios.Validaciones.validarCamposVacios;
import static com.mam.alicamp.constantes.Constantes.DIRECTORIO_PROYECTO;
import static com.mam.alicamp.constantes.Constantes.GADM_ID;
import static com.mam.alicamp.constantes.Constantes.PROYECTO_ID;
import static com.mam.alicamp.constantes.Constantes.SECTOR_ID;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.mam.alicamp.R;
import com.mam.alicamp.controlesUI.ISpinner;
import com.mam.alicamp.controlesUI.SectorSpinner;
import com.mam.alicamp.databinding.ActivityListaPozosBinding;
import com.mam.alicamp.db.dto.GadmSectorProyecto;
import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Sector;
import com.mam.alicamp.servicios.ManejoArchivos;
import com.mam.alicamp.servicios.ManejoDialogos;
import com.mam.alicamp.servicios.FolderHandling;
import com.mam.alicamp.servicios.SweetAlertOpciones;
import com.mam.alicamp.ui.adapters.PozoAdapter;
import com.mam.alicamp.ui.interfaces.IEliminacion;
import com.mam.alicamp.ui.viewmodels.DescargaViewModel;
import com.mam.alicamp.ui.viewmodels.GadmViewModel;
import com.mam.alicamp.ui.viewmodels.PozoViewModel;
import com.mam.alicamp.ui.viewmodels.ResponsableViewModel;
import com.mam.alicamp.ui.viewmodels.SectorViewModel;

import java.io.File;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ListaPozos extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        ManejoDialogos.IDialogHandling, IEliminacion {

    private ActivityListaPozosBinding binding;

    private SweetAlertOpciones sweetAlertOpciones;
    private ManejoDialogos dialog;
    private FolderHandling folderHandling;

    private SectorViewModel sectorViewModel;
    private PozoViewModel pozoViewModel;
    private GadmViewModel gadmViewModel;
    private DescargaViewModel descargaViewModel;
    private ResponsableViewModel responsableViewModel;

    private PozoAdapter adapterPozo;
    private RecyclerView.LayoutManager layoutManager;

    private String oldName;
    private String directorioProyecto;
    private String pathBase;
    private int gadmId;
    private int proyectoId;
    private int sectorId;
    private int opcionEditarEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pozos);

        binding = ActivityListaPozosBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            gadmId = b.getInt(GADM_ID);
            proyectoId = b.getInt(PROYECTO_ID);
            sectorId = b.getInt(SECTOR_ID);
            directorioProyecto = b.getString(DIRECTORIO_PROYECTO);
        }

        inicializarViewModel();

        addToolbar();

        inicializarObjetos();

        initViews();

        //cargarTodosSectores();

        crearCarpetasSectores();

        cargarSpinnerSector();

        cargarResponsables();

        cargarDescargas();

        cargarPozos();

        configurarRecycler();

        buscarSearchView();

        pozoViewModel.filtrarPozosPorNombre().observe(this, pozos ->
                adapterPozo.setPozos(pozos));

        initListeners();
    }

    private void inicializarViewModel() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        gadmViewModel = new ViewModelProvider(this, factory).get(GadmViewModel.class);
        sectorViewModel = new ViewModelProvider(this, factory).get(SectorViewModel.class);
        descargaViewModel = new ViewModelProvider(this, factory).get(DescargaViewModel.class);
        responsableViewModel = new ViewModelProvider(this, factory).get(ResponsableViewModel.class);
        pozoViewModel = new ViewModelProvider(this, factory).get(PozoViewModel.class);
    }


    private void addToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarListaPozos);
        gadmViewModel.obtenerGadmPorId(gadmId).observe(this, gadm ->
                toolbar.setTitle(getString(R.string.activity_lista_pozos) + " - " + gadm.getNombre())
        );
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void inicializarObjetos() {
        layoutManager = new LinearLayoutManager(this);
        folderHandling = new FolderHandling(this);
        sweetAlertOpciones = new SweetAlertOpciones(this);
        dialog = new ManejoDialogos();
    }

    private void initViews() {
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        regresar();
                    }
                });
        binding.recyclerPozos.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        binding.recyclerPozos.setHasFixedSize(true);
        binding.recyclerPozos.setItemAnimator(new DefaultItemAnimator());
    }

    private void cargarSpinnerSector() {
        sectorViewModel.obtenerSectoresPorProyecto(proyectoId).observe(this, listaSectores -> {
            if (listaSectores != null) {
                ArrayAdapter<Sector> sectorAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, listaSectores);
                sectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerSector.setAdapter(sectorAdapter);
            }
            seleccionarItemSpinner(listaSectores);
        });
    }

    private void crearCarpetasSectores() {
        File pathDocuments = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File directorioBase = new File(pathDocuments, NOMBRE_CARPETA_BASE);
        File filePathMedia = new File(directorioBase, NOMBRE_CARPETA_MEDIA);
        sectorViewModel.obtenerGadmsProyectosSectores().observe(this, listaCarpetas -> {
            if (listaCarpetas != null) {
                for (GadmSectorProyecto g : listaCarpetas) {
                    pathBase = filePathMedia.getAbsolutePath() + "/"
                            + g.getGadm() + "/"
                            + g.getProyecto() + "/";
                    crearCarpetasBD(g.getSector());
                }
            }
        });
    }

    private void cargarDescargas() {
        descargaViewModel.getDescargas().observe(this, listaDescargas -> {
            if (listaDescargas != null) {
                Log.i("DESCARGAS", "Cantidad de descargas " + listaDescargas.size());
            }
        });
    }

    private void cargarResponsables() {
        responsableViewModel.getListaResponsables().observe(this, listaResponsables -> {
            if (listaResponsables != null) {
                Log.i("RESPONSABLES", "Cantidad de responsables " + listaResponsables.size());
            }
        });
    }

    private void cargarPozos() {
        pozoViewModel.getAllPozos().observe(this, listaPozos -> {
            if (listaPozos != null) {
                Log.i("POZOS", "Cantidad de pozos: " + listaPozos.size());
            }
        });
    }

    private void crearCarpetasBD(String nombreCarpeta) {
        File carpetaNueva = new File(pathBase, nombreCarpeta);
        folderHandling.crearCarpetasBD(carpetaNueva);
    }

    private void configurarRecycler() {
        adapterPozo = new PozoAdapter(new PozoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Pozo pozo) {
                editarPozo(pozo.getNombre());
            }

            @Override
            public void onItemLongClick(View view, Pozo pozo) {
                Sector sectorSeleccionado = (Sector) binding.spinnerSector.getSelectedItem();
                PopupMenu popupMenu = new PopupMenu(ListaPozos.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.foto_ubicacion_item) {
                        String descripcionFoto = getString(R.string.ubicacion_pozo) + " " + pozo.getNombre();
                        String pathFoto = directorioProyecto + sectorSeleccionado.getNombre()
                                + "/" + pozo.getNombre() + "-U.jpg";
                        mostrarFoto(pathFoto, descripcionFoto);
                        return true;
                    } else if (item.getItemId() == R.id.foto_interior_item) {
                        String descripcionFoto = getString(R.string.interior_pozo) + " " + pozo.getNombre();
                        String pathFoto = directorioProyecto + sectorSeleccionado.getNombre()
                                + "/" + pozo.getNombre() + "-I.jpg";
                        mostrarFoto(pathFoto, descripcionFoto);
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            }

            @Override
            public void onEliminarClick(Pozo pozo) {
                new SweetAlertDialog(ListaPozos.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("¿Eliminar pozo?")
                        .setContentText("Esta acción no se puede deshacer")
                        .setConfirmText("Si")
                        .setCancelText("No")
                        .showCancelButton(true)
                        .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                        .setConfirmClickListener(sweetAlertDialog -> {
                            eliminarPozo(pozo);
                            sweetAlertDialog.setTitleText("Eliminado!")
                                    .setContentText("El registro ha sido eliminado")
                                    .setConfirmText("Aceptar")
                                    .showCancelButton(false)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }).show();
            }
        });
        binding.recyclerPozos.setLayoutManager(layoutManager);
        binding.recyclerPozos.setAdapter(adapterPozo);
    }

    private void buscarSearchView() {
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pozoViewModel.setFiltroNombre(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pozoViewModel.setFiltroNombre(newText);
                return true;
            }
        });
    }

    private void seleccionarItemSpinner(List<Sector> listaSectores) {
        ISpinner<Sector> iSpinner = new SectorSpinner();
        int idSpinner = iSpinner.getIdSpinner(listaSectores, sectorId);
        if (idSpinner != -1) {
            binding.spinnerSector.setSelection(idSpinner);
        } else {
            pozoViewModel.setIdSectorSeleccionado(idSpinner);
        }
    }

    private void initListeners() {
        binding.spinnerSector.setOnItemSelectedListener(this);
        binding.imgBtnAgregarSector.setOnClickListener(v -> crearSector());
        binding.imgBtnEditarSector.setOnClickListener(v -> editarSector());
        binding.imgBtnEliminarSector.setOnClickListener(v -> eliminarSector());
        binding.fabNuevoPozo.setOnClickListener(v -> crearPozo());
    }

    private void regresar() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(DIRECTORIO_PROYECTO, directorioProyecto);
        startActivity(intent);
        finish();
    }

    private void mostrarFoto(String pathFoto, String descripcionFoto) {
        Intent intent = new Intent(this, VisualizaFoto.class);
        intent.putExtra(PATH_FOTO, pathFoto);
        intent.putExtra(DESCRIPCION_FOTO, descripcionFoto);
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Sector sectorSeleccionado = (Sector) binding.spinnerSector.getSelectedItem();
        pozoViewModel.setIdSectorSeleccionado(sectorSeleccionado.getIdSector());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, EditText editText, String typeFolder) {
        if (validarCamposVacios(editText, "El nombre es obligatorio")) {
            String nombreCarpeta = editText.getText().toString();
            folderHandling.setNombreCarpeta(nombreCarpeta);
            folderHandling.setTipoCarpeta(typeFolder);
            if (folderHandling.crearEditarCarpeta(oldName, directorioProyecto)) {
                operarBD(nombreCarpeta);
            }
        }
        dialog.dismiss();
    }

    private void crearSector() {
        opcionEditarEliminar = 2;
        oldName = null;
        cargarDialogo(null);
    }

    private void editarSector() {
        opcionEditarEliminar = 0;
        oldName = binding.spinnerSector.getSelectedItem().toString();
        cargarDialogo(oldName);
    }

    private void eliminarSector() {
        opcionEditarEliminar = 1;
        oldName = binding.spinnerSector.getSelectedItem().toString();
        File carpetaEliminar = new File(directorioProyecto, oldName);
        sweetAlertOpciones.mostrarDialogoEliminar(this, SECTOR,
                carpetaEliminar);
    }

    private void eliminarRegistro(File directorio, String tipo) {
        if (folderHandling.eliminarCarpetaDisco(directorio)) {
            operarBD(tipo);
        }
    }

    private void cargarDialogo(String nombre) {
        dialog.setTitleDialog(SECTOR);
        dialog.setOldName(nombre);
        dialog.show(getSupportFragmentManager(), "dialogo");
    }

    private void operarBD(String nombre) {
        Sector sectorSeleccionado = (Sector) binding.spinnerSector.getSelectedItem();
        try {
            if (oldName == null) {
                sectorViewModel.insertar(new Sector(nombre, proyectoId));
                sweetAlertOpciones.setMensaje("Sector ingresado correctamente a la base de datos");
                sweetAlertOpciones.mostrarDialogoSuccess();
            } else {
                if (sectorSeleccionado != null) {
                    if (opcionEditarEliminar == 1) {
                        //sectorViewModel.eliminarSectorConPozos(sectorSeleccionado.getIdSector());
                        sweetAlertOpciones.setMensaje("Sector eliminado de la base de datos");
                        sweetAlertOpciones.mostrarDialogoSuccess();
                    } else if (opcionEditarEliminar == 0) {
                        sectorSeleccionado.setNombre(nombre);
                        sectorViewModel.actualizar(sectorSeleccionado);
                        sweetAlertOpciones.setMensaje("Sector actualizado correctamente en la base de datos");
                        sweetAlertOpciones.mostrarDialogoSuccess();
                    }
                } else {
                    sweetAlertOpciones.setMensaje("Seleccione un Sector");
                    sweetAlertOpciones.mostrarDialogoError();
                }
            }
        } catch (Exception ex) {
            sweetAlertOpciones.setMensaje("Error en la base de datos");
            sweetAlertOpciones.mostrarDialogoError();
            Log.e("ErrorBD", Objects.requireNonNull(ex.getMessage()));
        }
    }

    private void eliminarPozo(@NonNull Pozo pozo) {
        if (ManejoArchivos.eliminarArchivos(pozo.getPathMedia(), pozo.getNombre())) {
            sweetAlertOpciones.setMensaje("Fotos eliminadas");
            sweetAlertOpciones.mostrarDialogoSuccess();
        } else {
            sweetAlertOpciones.setMensaje("No existen fotos del pozo");
            sweetAlertOpciones.mostrarDialogoError();
        }
        //pozoViewModel.eliminarPozoConTuberias(pozo.getNombre());
    }

    private void crearPozo() {
        iniciarActividad("", false);
    }

    private void editarPozo(String nombrePozo) {
        iniciarActividad(nombrePozo, true);
    }

    private void iniciarActividad(String nombrePozo, boolean editando) {
        Intent intent = new Intent(this, PozoCatastrado.class);
        Sector sectorSeleccionado = (Sector) binding.spinnerSector.getSelectedItem();
        if (sectorSeleccionado != null) {
            String directorioPozos = directorioProyecto + sectorSeleccionado.getNombre() + "/";
            intent.putExtra(GADM_ID, gadmId);
            intent.putExtra(PROYECTO_ID, proyectoId);
            intent.putExtra(NOMBRE_POZO, nombrePozo);
            intent.putExtra(SECTOR_ID, sectorSeleccionado.getIdSector());
            intent.putExtra(SECTOR_NOMBRE, sectorSeleccionado.getNombre());
            intent.putExtra(EDITANDO, editando);
            intent.putExtra(REGRESANDO, false);
            intent.putExtra(DIRECTORIO_POZOS, directorioPozos);
            startActivity(intent);
            finish();
        } else {
            sweetAlertOpciones.setMensaje("Debe crear un sector para continuar");
            sweetAlertOpciones.mostrarDialogoError();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    @Override
    public void onEliminar(File directorio, String tipo) {
        eliminarRegistro(directorio, tipo);
    }

    @Override
    public void onCancelar() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        regresar();
        return false;
    }
}