package com.mam.alicamp.ui.actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mam.alicamp.R;
import com.mam.alicamp.databinding.ActivityMainBinding;
import com.mam.alicamp.db.database.AlicampDB;
import com.mam.alicamp.db.entidades.Gadm;
import com.mam.alicamp.db.entidades.Proyecto;
import com.mam.alicamp.servicios.DatabaseExporter;
import com.mam.alicamp.servicios.ManejoDialogos;
import com.mam.alicamp.servicios.FolderHandling;
import com.mam.alicamp.servicios.SweetAlertOpciones;
import com.mam.alicamp.ui.interfaces.IEliminacion;
import com.mam.alicamp.ui.viewmodels.AuthViewModel;
import com.mam.alicamp.ui.viewmodels.GadmViewModel;
import com.mam.alicamp.ui.viewmodels.ProyectoViewModel;
import com.mam.alicamp.ui.viewmodels.SectorViewModel;

import static com.mam.alicamp.constantes.Constantes.DIRECTORIO_PROYECTO;
import static com.mam.alicamp.servicios.Validaciones.validarCamposVacios;

import java.io.File;
import java.util.Comparator;
import java.util.Objects;

import static com.mam.alicamp.constantes.Constantes.GADM;
import static com.mam.alicamp.constantes.Constantes.PROYECTO;
import static com.mam.alicamp.constantes.Constantes.GADM_ID;
import static com.mam.alicamp.constantes.Constantes.PROYECTO_ID;
import static com.mam.alicamp.constantes.Constantes.SECTOR_ID;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_CARPETA_BASE;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_CARPETA_ARCHIVOS;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_CARPETA_MEDIA;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends PermisosActivity implements AdapterView.OnItemSelectedListener,
        ManejoDialogos.IDialogHandling, IEliminacion {

    private GadmViewModel gadmViewModel;
    private ProyectoViewModel proyectoViewModel;
    private SectorViewModel sectorViewModel;
    private AuthViewModel authViewModel;

    private ActivityMainBinding binding;

    private String rootPath;
    private SweetAlertOpciones sweetAlertOpciones;
    private ManejoDialogos dialogo;
    private FolderHandling folderHandling;

    private File directorioBase;
    private File filePathMedia;
    private File filePathArchivos;

    private String oldName;
    private String pathBase;
    private int opcionEditarEliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //AlicampDB.resetDatabase(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        addToolbar();

        inicializarViewModel();

        verificarSesion();

        requestStoragePermission();

        inicializarObjetos();

        cargarSpinnerGadm();

        cargarTodosProyectos();

        cargarTodosSectores();

        cargarMensajesError();

        initListeners();

        mostrarInformacionUsuario();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (authViewModel != null && !authViewModel.isLoggedIn()) {
            irLogin();
        }
    }

    private void mostrarInformacionUsuario() {
        String username = authViewModel.getUsername();
        if (!username.isEmpty()) {
            System.out.println("Usuario " + username);
        }
    }

    private void verificarSesion() {
        if (authViewModel == null) {
            irLogin();
            return;
        }

        if (!authViewModel.isLoggedIn()) {
            irLogin();
        }
    }

    private void inicializarViewModel() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        gadmViewModel = new ViewModelProvider(this, factory).get(GadmViewModel.class);
        proyectoViewModel = new ViewModelProvider(this, factory).get(ProyectoViewModel.class);
        sectorViewModel = new ViewModelProvider(this, factory).get(SectorViewModel.class);
        authViewModel = new ViewModelProvider(this, factory).get(AuthViewModel.class);
    }

    @Override
    protected void onStoragePermissionGranted() {
        crearCarpetaBase();
    }

    @Override
    protected void onAdditionalPermissionsGranted() {
    }

    private void crearCarpetaBase() {
        File pathDocuments = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        directorioBase = new File(pathDocuments, NOMBRE_CARPETA_BASE);
        filePathArchivos = new File(directorioBase, NOMBRE_CARPETA_ARCHIVOS);
        filePathMedia = new File(directorioBase, NOMBRE_CARPETA_MEDIA);
        if (!directorioBase.exists()) {
            if (directorioBase.mkdirs()) {
                if (filePathArchivos.mkdir() && filePathMedia.mkdir()) {
                    mostrarToast("Carpeta base creada");
                }
            }
        }
    }

    private void addToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarDatos);
        toolbar.setTitle(R.string.activity_datos_proyecto);
        setSupportActionBar(toolbar);
    }

    private void inicializarObjetos() {
        sweetAlertOpciones = new SweetAlertOpciones(this);
        dialogo = new ManejoDialogos();
        folderHandling = new FolderHandling(this);
    }

    private void initListeners() {
        binding.spinnerGadm.setOnItemSelectedListener(this);
        binding.btnFinalizar.setOnClickListener(v -> confirmarSalida());
        binding.imgBtnAgregarGadm.setOnClickListener(v -> crearGadm());
        binding.imgBtnEditarGadm.setOnClickListener(v -> editarGadm());
        binding.imgBtnEliminarGadm.setOnClickListener(v -> eliminarGadm());
        binding.imgBtnAgregarProyecto.setOnClickListener(v -> crearProyecto());
        binding.imgBtnEditarProyecto.setOnClickListener(v -> editarProyecto());
        binding.imgBtnEliminarProyecto.setOnClickListener(v -> eliminarProyecto());
        binding.btnCatastrar.setOnClickListener(v -> avanzarListaPozos());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_backup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.upload_item) {
            DatabaseExporter.exportRoomDatabase(this, filePathArchivos);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cargarSpinnerGadm() {
        filePathMedia = new File(directorioBase, NOMBRE_CARPETA_MEDIA);
        pathBase = filePathMedia.getAbsolutePath();
        gadmViewModel.getAllGadms().observe(this, listaGadms -> {
            if (listaGadms != null) {
                for (Gadm g : listaGadms) {
                    crearCarpetasBD(g.getAlias());
                }
                listaGadms.sort(Comparator.comparing(Gadm::getAlias));
                ArrayAdapter<Gadm> gadmAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, listaGadms);
                gadmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerGadm.setAdapter(gadmAdapter);
            }
        });
    }

    private void cargarTodosProyectos() {
        filePathMedia = new File(directorioBase, NOMBRE_CARPETA_MEDIA);
        proyectoViewModel.getAllProyectos().observe(this, listaProyectos -> {
            if (listaProyectos != null) {
                for (Proyecto p : listaProyectos) {
                    gadmViewModel.obtenerGadmPorId(p.getIdGadm()).observe(this, gadm -> {
                        pathBase = filePathMedia.getAbsolutePath() + "/" + gadm.getAlias() + "/";
                        crearCarpetasBD(p.getAlias());
                    });
                }
            }
        });
    }

    private void cargarTodosSectores() {
        sectorViewModel.getAllSectores().observe(this, listaSectores -> {
            if (listaSectores != null) {
                Log.i("SECTORES", "Cantidad de sectores " + listaSectores.size());
            }
        });
    }

    private void crearCarpetasBD(String nombreCarpeta) {
        File carpetaNueva = new File(pathBase, nombreCarpeta);
        folderHandling.crearCarpetasBD(carpetaNueva);
    }

    private void cargarMensajesError() {
        gadmViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null) {
                sweetAlertOpciones.setMensaje(mensaje);
                sweetAlertOpciones.mostrarDialogoError();
            }
        });
    }

    private void cargarSpinnerProyectos(Gadm gadm) {
        proyectoViewModel.obtenerProyectosPorGadm(gadm.getIdGadm()).observe(this, listaProyectos -> {
            if (listaProyectos != null) {
                listaProyectos.sort(Comparator.comparing(Proyecto::getAlias));
                ArrayAdapter<Proyecto> proyectoAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, listaProyectos);
                proyectoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerProyecto.setAdapter(proyectoAdapter);
            } else {
                Snackbar.make(findViewById(R.id.constraintMain),
                        "No existen proyectos",
                        BaseTransientBottomBar.LENGTH_SHORT
                ).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        Gadm gadmSeleccionado = (Gadm) binding.spinnerGadm.getSelectedItem();
        cargarSpinnerProyectos(gadmSeleccionado);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, EditText editText, String typeFolder) {
        if (validarCamposVacios(editText, "El nombre es obligatorio")) {
            String nombreCarpeta = editText.getText().toString();
            folderHandling.setNombreCarpeta(nombreCarpeta);
            folderHandling.setTipoCarpeta(typeFolder);
            if (folderHandling.crearEditarCarpeta(oldName, pathBase)) {
                operarBD(typeFolder, nombreCarpeta);
            }
        }
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
    }

    /*
        Valor para la variable opcionEditarEliminar, variable que controla las acciones
        sobre carpetas
            - Editar nombre: 0
            - Eliminar: 1
            - Crear: 2
    */
    private void crearGadm() {
        opcionEditarEliminar = 2;
        rootPath = null;
        oldName = null;
        pathBase = filePathMedia.getAbsolutePath();
        //El nombre para la carpeta es vacío cuando se va a crear
        cargarDialogo(GADM, null);
    }

    private void crearProyecto() {
        opcionEditarEliminar = 2;
        oldName = null;
        rootPath = binding.spinnerGadm.getSelectedItem().toString();
        pathBase = filePathMedia.getAbsolutePath() + "/" + rootPath + "/";
        cargarDialogo(PROYECTO, null);
    }

    private void editarGadm() {
        opcionEditarEliminar = 0;
        oldName = binding.spinnerGadm.getSelectedItem().toString();
        rootPath = null;
        pathBase = filePathMedia.getAbsolutePath();
        cargarDialogo(GADM, oldName);
    }

    private void editarProyecto() {
        opcionEditarEliminar = 0;
        oldName = binding.spinnerProyecto.getSelectedItem().toString();
        rootPath = binding.spinnerGadm.getSelectedItem().toString();
        pathBase = filePathMedia.getAbsolutePath() + "/" + rootPath + "/";
        cargarDialogo(PROYECTO, oldName);
    }

    private void eliminarGadm() {
        opcionEditarEliminar = 1;
        String directorio = directorioBase.getAbsolutePath() + "/Media/";
        oldName = binding.spinnerGadm.getSelectedItem().toString();
        File carpetaEliminar = new File(directorio, oldName);
        sweetAlertOpciones.mostrarDialogoEliminar(this, GADM,
                carpetaEliminar);
    }

    private void eliminarProyecto() {
        opcionEditarEliminar = 1;
        oldName = binding.spinnerProyecto.getSelectedItem().toString();
        String nombreGadm = binding.spinnerGadm.getSelectedItem().toString();
        String directorio = directorioBase.getAbsolutePath() + "/Media/" + nombreGadm + "/";
        File carpetaEliminar = new File(directorio, oldName);
        sweetAlertOpciones.mostrarDialogoEliminar(this, PROYECTO,
                carpetaEliminar);
    }

    private void cargarDialogo(String tituloDialogo, String nombre) {
        dialogo.setTitleDialog(tituloDialogo);
        dialogo.setOldName(nombre);
        dialogo.show(getSupportFragmentManager(), "dialogo");
    }

    @Override
    public void onEliminar(File directorio, String tipo) {
        eliminarRegistro(directorio, tipo);
    }

    @Override
    public void onCancelar() {
        Toast.makeText(this, "Acción cancelada", Toast.LENGTH_SHORT).show();
    }

    private void eliminarRegistro(File directorio, String tipo) {
        if (folderHandling.eliminarCarpetaDisco(directorio)) {
            operarBD(tipo, "");
        }
    }

    private void operarBD(String tipoObjeto, String nombre) {
        Gadm gadmSeleccionado = (Gadm) binding.spinnerGadm.getSelectedItem();
        try {
            switch (tipoObjeto) {
                case GADM:
                    if (oldName == null) {
                        gadmViewModel.insertarGadm(new Gadm(nombre, nombre,
                                false));
                        sweetAlertOpciones.setMensaje("Gadm ingresado correctamente a la base de datos");
                        sweetAlertOpciones.mostrarDialogoSuccess();
                    } else {
                        if (gadmSeleccionado != null) {
                            if (opcionEditarEliminar == 1) {
                                gadmViewModel.eliminarGadm(gadmSeleccionado.getIdGadm());
                                sweetAlertOpciones.setMensaje("Gadm eliminado de la base de datos");
                                sweetAlertOpciones.mostrarDialogoSuccess();
                            } else if (opcionEditarEliminar == 0) {
                                gadmSeleccionado.setNombre(nombre);
                                gadmSeleccionado.setAlias(nombre);
                                gadmSeleccionado.setSincronizado(false);
                                gadmViewModel.actualizarGadm(gadmSeleccionado);
                                sweetAlertOpciones.setMensaje("Gadm actualizado correctamente " +
                                        "en la base de datos");
                                sweetAlertOpciones.mostrarDialogoSuccess();
                            }
                        } else {
                            sweetAlertOpciones.setMensaje("Seleccione un Gadm");
                            sweetAlertOpciones.mostrarDialogoError();
                        }
                    }
                    break;
                case PROYECTO:
                    if (oldName == null) {
                        if (gadmSeleccionado != null) {
                            proyectoViewModel.insertarProyecto(new Proyecto(nombre, nombre,
                                    gadmSeleccionado.getIdGadm(), false));
                            sweetAlertOpciones.setMensaje("Proyecto ingresado correctamente a " +
                                    "la base de datos");
                            sweetAlertOpciones.mostrarDialogoSuccess();
                        } else {
                            sweetAlertOpciones.setMensaje("Seleccione un Gadm para crear el Proyecto");
                            sweetAlertOpciones.mostrarDialogoError();
                        }
                    } else {
                        Proyecto proyectoSeleccionado = (Proyecto) binding.spinnerProyecto.getSelectedItem();
                        if (proyectoSeleccionado != null) {
                            if (opcionEditarEliminar == 1) {
                                proyectoViewModel.eliminarProyecto(proyectoSeleccionado.getIdProyecto());
                                sweetAlertOpciones.setMensaje("Proyecto eliminado de la base de datos");
                                sweetAlertOpciones.mostrarDialogoSuccess();
                            } else if (opcionEditarEliminar == 0) {
                                proyectoSeleccionado.setNombre(nombre);
                                proyectoSeleccionado.setAlias(nombre);
                                proyectoSeleccionado.setSincronizado(false);
                                proyectoViewModel.updateProyecto(proyectoSeleccionado);
                                sweetAlertOpciones.setMensaje("Proyecto actualizado correctamente " +
                                        "en la base de datos");
                                sweetAlertOpciones.mostrarDialogoSuccess();
                            }
                        } else {
                            sweetAlertOpciones.setMensaje("Seleccione un proyecto");
                            sweetAlertOpciones.mostrarDialogoError();
                        }
                    }
                    break;
            }
        } catch (Exception ex) {
            sweetAlertOpciones.setMensaje("Error en la base de datos");
            sweetAlertOpciones.mostrarDialogoError();
            Log.e("ErrorBD", Objects.requireNonNull(ex.getMessage()));
        }
    }

    private void avanzarListaPozos() {
        try {
            Gadm gadmSeleccion = (Gadm) binding.spinnerGadm.getSelectedItem();
            Proyecto proyectoSeleccion = (Proyecto) binding.spinnerProyecto.getSelectedItem();
            String directorioProyecto = directorioBase.getAbsolutePath() + "/" + NOMBRE_CARPETA_MEDIA
                    + "/" + gadmSeleccion.getNombre() + "/" + proyectoSeleccion.getNombre() + "/";
            Intent intent = new Intent(this, ListaPozos.class);
            intent.putExtra(GADM_ID, gadmSeleccion.getIdGadm());
            intent.putExtra(PROYECTO_ID, proyectoSeleccion.getIdProyecto());
            intent.putExtra(SECTOR_ID, 0);
            intent.putExtra(DIRECTORIO_PROYECTO, directorioProyecto);
            startActivity(intent);
            finish();
        } catch (NullPointerException np) {
            sweetAlertOpciones.setMensaje("No existen proyectos para continuar");
            sweetAlertOpciones.mostrarDialogoError();
        }
    }

    private void confirmarSalida() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Cerrar Sesión")
                .setContentText("¿Está seguro que desea cerrar su sesión?")
                .setConfirmText("Sí, cerrar sesión")
                .setCancelText("Cancelar")
                .showCancelButton(true)
                .setCancelClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.cancel();
                    Toast.makeText(MainActivity.this, "Operación cancelada", Toast.LENGTH_SHORT).show();
                })
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.setTitleText("Procesando...")
                            .setContentText("Cerrando su sesión")
                            .setConfirmText("")
                            .showCancelButton(false)
                            .changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

                    new android.os.Handler().postDelayed(this::cerrarSesion, 800);
                })
                .show();
    }

    private void cerrarSesion() {
        authViewModel.logout();

        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sesión cerrada")
                .setContentText("Ha cerrado su sesión exitosamente")
                .setConfirmText("Continuar")
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    finishAffinity();
                }).show();
    }

    private void irLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}