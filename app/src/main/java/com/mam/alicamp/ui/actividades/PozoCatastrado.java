package com.mam.alicamp.ui.actividades;

import static com.mam.alicamp.constantes.ActividadesCompletadas.ACTIVIDAD_POZO_CATASTRADO;
import static com.mam.alicamp.constantes.Constantes.DESCARGA;
import static com.mam.alicamp.constantes.Constantes.DESCRIPCION_FOTO;
import static com.mam.alicamp.constantes.Constantes.DIRECTORIO_POZOS;
import static com.mam.alicamp.constantes.Constantes.EDITANDO;
import static com.mam.alicamp.constantes.Constantes.GADM_ID;
import static com.mam.alicamp.constantes.Constantes.PATH_FOTO;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_POZO;
import static com.mam.alicamp.constantes.Constantes.PROYECTO_ID;
import static com.mam.alicamp.constantes.Constantes.REGRESANDO;
import static com.mam.alicamp.constantes.Constantes.RESPONSABLE;
import static com.mam.alicamp.constantes.Constantes.SECTOR_ID;
import static com.mam.alicamp.constantes.Constantes.SECTOR_NOMBRE;
import static com.mam.alicamp.servicios.Validaciones.emptyTextInputLayout;
import static com.mam.alicamp.servicios.Validaciones.isRepeated;
import static com.mam.alicamp.servicios.Validaciones.validarCamposVacios;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.mam.alicamp.R;
import com.mam.alicamp.controlesUI.DescargaSpinner;
import com.mam.alicamp.controlesUI.ISpinner;
import com.mam.alicamp.controlesUI.ResponsableSpinner;
import com.mam.alicamp.controlesUI.ValoresSpinner;
import com.mam.alicamp.databinding.ActivityPozoCatastradoBinding;
import com.mam.alicamp.db.entidades.Descarga;
import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Responsable;
import com.mam.alicamp.servicios.ManejoDialogos;
import com.mam.alicamp.servicios.ManejoFechas;
import com.mam.alicamp.servicios.SweetAlertOpciones;
import com.mam.alicamp.servicios.Utilitarios;
import com.mam.alicamp.ui.interfaces.IEliminacion;
import com.mam.alicamp.ui.viewmodels.DescargaViewModel;
import com.mam.alicamp.ui.viewmodels.PozoViewModel;
import com.mam.alicamp.ui.viewmodels.ResponsableViewModel;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class PozoCatastrado extends PermisosActivity implements ManejoDialogos.IDialogHandling,
        IEliminacion {

    private ActivityPozoCatastradoBinding binding;

    private ActivityResultLauncher<Intent> lanzaCamara;

    private PozoViewModel pozoViewModel;
    private ResponsableViewModel responsableViewModel;
    private DescargaViewModel descargaViewModel;

    private SweetAlertOpciones sweetAlertOpciones;
    private ManejoDialogos dialogo;

    private List<Pozo> pozos;
    private Pozo pozo;

    private String oldName;
    private String directorioPozo;
    private String nombreSector;
    private String opcionFoto;
    private String descripcionFoto;
    private String nombrePozo;
    private boolean editando;
    private boolean regresando;
    private int opcionEditarEliminar;
    private int gadmId;
    private int proyectoId;
    private int sectorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pozo_catastrado);

        binding = ActivityPozoCatastradoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        addToolBar();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            gadmId = b.getInt(GADM_ID);
            proyectoId = b.getInt(PROYECTO_ID);
            nombrePozo = b.getString(NOMBRE_POZO);
            sectorId = b.getInt(SECTOR_ID);
            nombreSector = b.getString(SECTOR_NOMBRE);
            editando = b.getBoolean(EDITANDO);
            regresando = b.getBoolean(REGRESANDO);
            directorioPozo = b.getString(DIRECTORIO_POZOS);
        }

        requestAdditionalPermissions();

        inicializarFlechaRetroceso();

        inicializarViews();

        inicializarLauncher();

        inicializarViewModels();

        inicializarObjetos();

        buscarPozosSector();

        clearFocusEdits();

        cargarSpinnerResponsable();

        cargarSpinnerDescarga();

        cargarMensajesError();

        initListeners();
    }

    @Override
    protected void onStoragePermissionGranted() {

    }

    @Override
    protected void onAdditionalPermissionsGranted() {
        Log.i("PERMISOS", "Permisos concedidos");
    }

    private void cargarMensajesError() {
        responsableViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null) {
                sweetAlertOpciones.setMensaje(mensaje);
                sweetAlertOpciones.mostrarDialogoError();
            }
        });

        descargaViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null) {
                sweetAlertOpciones.setMensaje(mensaje);
                sweetAlertOpciones.mostrarDialogoError();
            }
        });
    }

    private void inicializarViews() {
        if (editando || regresando) {
            binding.imgBtnCrearPozo.setEnabled(false);
            binding.imgBtnFotoUbicacion.setEnabled(true);
            binding.imgBtnFotoInterior.setEnabled(true);
            binding.imgBtnFotoUbicacion.setImageResource(R.drawable.ic_camara_enabled);
            binding.imgBtnFotoInterior.setImageResource(R.drawable.ic_camara_enabled);
            binding.btnSiguientePozo.setEnabled(true);
        } else {
            binding.imgBtnCrearPozo.setEnabled(true);
            binding.imgBtnFotoUbicacion.setEnabled(false);
            binding.imgBtnFotoInterior.setEnabled(false);
            binding.btnSiguientePozo.setEnabled(false);
        }
    }

    private void inicializarViewModels() {
        ViewModelProvider.AndroidViewModelFactory factory =
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        pozoViewModel = new ViewModelProvider(this, factory).get(PozoViewModel.class);
        responsableViewModel = new ViewModelProvider(this, factory).get(ResponsableViewModel.class);
        descargaViewModel = new ViewModelProvider(this, factory).get(DescargaViewModel.class);
    }

    private void addToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbarPozo);
        toolbar.setTitle(R.string.activity_pozo_catastrado);
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

    private void clearFocusEdits() {
        Objects.requireNonNull(binding.inputLayoutPozo.getEditText()).clearFocus();
    }

    private void inicializarObjetos() {
        binding.txtSectorDescripcion.setText(nombreSector);
        pozoViewModel.obtenerPozoPorNombre(nombrePozo).observe(this, pozoBuscado -> {
            if (pozoBuscado != null) {
                pozo = pozoBuscado;
                cargarValoresIniciales(pozoBuscado);
                cargarImagenFoto();
                seleccionarSpinnerSistema(pozoBuscado);
            }
        });
        sweetAlertOpciones = new SweetAlertOpciones(this);
        dialogo = new ManejoDialogos();
    }

    private void inicializarLauncher() {
        lanzaCamara = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultado -> {
                    if (resultado.getResultCode() == RESULT_OK) {
                        cargarImagenFoto();
                        Toast.makeText(this,
                                        "Foto tomada correctamente",
                                        Toast.LENGTH_SHORT)
                                .show();
                    } else {
                        Toast.makeText(this,
                                        "Foto cancelada",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                }
        );
    }

    private void initListeners() {
        binding.imgBtnAgregarDescarga.setOnClickListener(v -> crearDescarga());
        binding.imgBtnEditarDescarga.setOnClickListener(v -> editarDescarga());
        binding.imgBtnEliminarDescarga.setOnClickListener(v -> eliminarDescarga());
        binding.imgBtnCrearResponsable.setOnClickListener(v -> crearResponsable());
        binding.imgBtnEditarResponsable.setOnClickListener(v -> editarResponsable());
        binding.imgBtnEliminarResponsable.setOnClickListener(v -> eliminarResponsable());
        binding.imgViewFotoU.setOnClickListener(v -> clicFotoUbicacion());
        binding.imgViewFotoI.setOnClickListener(v -> clicFotoInterior());
        binding.imgBtnFotoUbicacion.setOnClickListener(v -> cambiarValorFotoU());
        binding.imgBtnFotoInterior.setOnClickListener(v -> cambiarValorFotoI());
        binding.imgBtnCrearPozo.setOnClickListener(v -> validarNombrePozo());
        binding.btnSiguientePozo.setOnClickListener(v -> avanzarCalle());
    }

    private void verificarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            tomarFoto();
        } else {
            Toast.makeText(this, "Permiso de cámara no concedido", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarSpinnerResponsable() {
        responsableViewModel.getListaResponsables().observe(this, listaResponsables -> {
            if (listaResponsables != null) {
                ArrayAdapter<Responsable> responsableAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, listaResponsables);
                responsableAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerResponsable.setAdapter(responsableAdapter);
            }
            seleccionarItemSpinnerResponsable(listaResponsables);
        });
    }

    private void cargarSpinnerDescarga() {
        descargaViewModel.getDescargas().observe(this, listaDescargas -> {
            if (listaDescargas != null) {
                ArrayAdapter<Descarga> descargaAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, listaDescargas);
                descargaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.spinnerDescarga.setAdapter(descargaAdapter);
            }
            seleccionarItemSpinnerDescarga(listaDescargas);
        });
    }

    private void seleccionarItemSpinnerResponsable(List<Responsable> listaResponsables) {
        if (editando || regresando) {
            pozoViewModel.obtenerPozoPorNombre(nombrePozo).observe(this, pozoObjeto -> {
                if (pozoObjeto != null) {
                    ISpinner<Responsable> iSpinner = new ResponsableSpinner();
                    int idSpinner = iSpinner.getIdSpinner(listaResponsables, pozoObjeto.getIdResponsable());
                    if (idSpinner != -1) {
                        binding.spinnerResponsable.setSelection(idSpinner);
                    }
                }
            });
        }
    }

    private void seleccionarItemSpinnerDescarga(List<Descarga> listaDescargas) {
        if (editando || regresando) {
            pozoViewModel.obtenerPozoPorNombre(nombrePozo).observe(this, pozoObjeto -> {
                if (pozoObjeto != null) {
                    ISpinner<Descarga> iSpinner = new DescargaSpinner();
                    int idSpinner = iSpinner.getIdSpinner(listaDescargas, pozoObjeto.getIdDescarga());
                    if (idSpinner != -1) {
                        binding.spinnerDescarga.setSelection(idSpinner);
                    }
                }
            });
        }
    }

    private void seleccionarSpinnerSistema(Pozo pozoBuscado) {
        if (pozoBuscado.getSistema() != null && !pozoBuscado.getSistema().isEmpty()) {
            List<String> listaSistema = Arrays.asList(getResources()
                    .getStringArray(R.array.spinner_sistema));
            ISpinner<String> iSpinner = new ValoresSpinner();
            int idSpinner = iSpinner.getIdSpinner(listaSistema, pozoBuscado.getSistema());
            if (idSpinner != 0) {
                binding.spinnerTipoSistema.setSelection(idSpinner);
            }
        }
    }

    private void seleccionarCheckTapado(Pozo pozoBuscado) {
        boolean pozoTapado;
        pozoTapado = pozoBuscado.getTapado().equalsIgnoreCase("Si");
        binding.checkTapado.setChecked(pozoTapado);
    }

    private void cargarValoresIniciales(Pozo pozoBuscado) {
        if (ACTIVIDAD_POZO_CATASTRADO <= pozoBuscado.getActividadCompletada()) {
            directorioPozo = pozoBuscado.getPathMedia();
            Objects.requireNonNull(binding.inputLayoutPozo.getEditText()).setText(pozoBuscado.getNombre());
            binding.inputLayoutPozo.setEnabled(false);
            binding.spinnerResponsable.setEnabled(false);
            seleccionarCheckTapado(pozoBuscado);
        }
    }

    private void cargarImagenFoto() {
        String nombrePozoCargar = Objects.requireNonNull(binding.inputLayoutPozo.getEditText())
                .getText().toString();
        File fotoUbicacion = new File(directorioPozo, nombrePozoCargar + "-U.jpg");
        File fotoInterior = new File(directorioPozo, nombrePozoCargar + "-I.jpg");
        if (fotoUbicacion.exists()) {
            binding.imgViewFotoU.setImageResource(R.drawable.view_photo);
        }
        if (fotoInterior.exists()) {
            binding.imgViewFotoI.setImageResource(R.drawable.view_photo);
        }
    }

    private void clicFotoUbicacion() {
        opcionFoto = "-U.jpg";
        descripcionFoto = getString(R.string.ubicacion_pozo);
        mostrarFoto();
    }

    private void clicFotoInterior() {
        opcionFoto = "-I.jpg";
        descripcionFoto = getString(R.string.interior_pozo);
        mostrarFoto();
    }

    private void mostrarFoto() {
        String pathFoto;
        descripcionFoto += " " + nombrePozo;
        if (!nombrePozo.isEmpty()) {
            pathFoto = directorioPozo + nombrePozo + opcionFoto;
            Intent intent = new Intent(this, VisualizaFoto.class);
            intent.putExtra(PATH_FOTO, pathFoto);
            intent.putExtra(DESCRIPCION_FOTO, descripcionFoto);
            startActivity(intent);
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, EditText editText, String typeFolder) {
        if (validarCamposVacios(editText, "El nombre es obligatorio")) {
            String nombre = editText.getText().toString();
            operarBD(typeFolder, nombre);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }

    private void operarBD(String tipoObjeto, String nombreNuevo) {
        try {
            switch (tipoObjeto) {
                case RESPONSABLE:
                    try {
                        String[] nombreCompleto;
                        String nombre, apellido;
                        if (oldName == null) {
                            nombreCompleto = Utilitarios.separarString(nombreNuevo);
                            nombre = nombreCompleto[0];
                            apellido = nombreCompleto[1];
                            responsableViewModel.insertar(new Responsable(nombre, apellido, "",
                                    "", false));
                            sweetAlertOpciones.setMensaje("Responsable ingresado correctamente " +
                                    "a la base de datos");
                            sweetAlertOpciones.mostrarDialogoSuccess();
                        } else {
                            Responsable responsableSeleccion = (Responsable) binding.spinnerResponsable
                                    .getSelectedItem();
                            if (responsableSeleccion != null) {
                                if (opcionEditarEliminar == 1) {
                                    responsableViewModel.eliminarResponsable(responsableSeleccion.getIdResponsable());
                                    sweetAlertOpciones.setMensaje("Responsable eliminado de la base de datos");
                                    sweetAlertOpciones.mostrarDialogoSuccess();
                                } else if (opcionEditarEliminar == 0) {
                                    nombreCompleto = Utilitarios.separarString(nombreNuevo);
                                    String nombreActualizar = nombreCompleto[0];
                                    String apellidoActualizar = nombreCompleto[1];
                                    responsableSeleccion.setNombre(nombreActualizar);
                                    responsableSeleccion.setApellido(apellidoActualizar);
                                    responsableSeleccion.setSincronizado(false);
                                    responsableViewModel.actualizar(responsableSeleccion);
                                    sweetAlertOpciones.setMensaje("Responsable actualizado correctamente " +
                                            "en la base de datos");
                                    sweetAlertOpciones.mostrarDialogoSuccess();
                                }
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException aiobe) {
                        sweetAlertOpciones.setMensaje("Ingrese nombre y apellido");
                        sweetAlertOpciones.mostrarDialogoError();
                    }
                    break;
                case DESCARGA:
                    if (oldName == null) {
                        descargaViewModel.insertar(new Descarga(nombreNuevo, "", false));
                        sweetAlertOpciones.setMensaje("Descarga ingresada correctamente a la base de datos");
                        sweetAlertOpciones.mostrarDialogoSuccess();
                    } else {
                        Descarga descargaSeleccionada = (Descarga) binding.spinnerDescarga
                                .getSelectedItem();
                        if (descargaSeleccionada != null) {
                            if (opcionEditarEliminar == 1) {
                                descargaViewModel.eliminar(descargaSeleccionada.getIdDescarga());
                                sweetAlertOpciones.setMensaje("Descarga eliminada correctamente " +
                                        "de la base de datos");
                                sweetAlertOpciones.mostrarDialogoSuccess();
                            } else if (opcionEditarEliminar == 0) {
                                descargaSeleccionada.setNombre(nombreNuevo);
                                descargaSeleccionada.setSincronizado(false);
                                sweetAlertOpciones.setMensaje("Descarga actualizada correctamente " +
                                        "en la base de datos");
                                sweetAlertOpciones.mostrarDialogoSuccess();
                            }
                        } else {
                            sweetAlertOpciones.setMensaje("Seleccione una Descarga");
                            sweetAlertOpciones.mostrarDialogoError();
                        }
                        break;
                    }
            }
        } catch (Exception ex) {
            sweetAlertOpciones.setMensaje("Error en la base de datos");
            sweetAlertOpciones.mostrarDialogoError();
            Log.e("ErrorBD", Objects.requireNonNull(ex.getMessage()));
        }
    }

    private void crearResponsable() {
        opcionEditarEliminar = 2;
        oldName = null;
        cargarDialogo(RESPONSABLE, null);
    }

    private void editarResponsable() {
        opcionEditarEliminar = 0;
        oldName = binding.spinnerResponsable.getSelectedItem().toString();
        cargarDialogo(RESPONSABLE, oldName);
    }

    private void eliminarResponsable() {
        opcionEditarEliminar = 1;
        oldName = binding.spinnerResponsable.getSelectedItem().toString();
        sweetAlertOpciones.mostrarDialogoEliminar(this, RESPONSABLE,
                null);
    }

    private void crearDescarga() {
        opcionEditarEliminar = 2;
        oldName = null;
        cargarDialogo(DESCARGA, null);
    }

    private void editarDescarga() {
        opcionEditarEliminar = 0;
        oldName = binding.spinnerDescarga.getSelectedItem().toString();
        cargarDialogo(DESCARGA, oldName);
    }

    private void eliminarDescarga() {
        opcionEditarEliminar = 1;
        oldName = binding.spinnerDescarga.getSelectedItem().toString();
        sweetAlertOpciones.mostrarDialogoEliminar(this, DESCARGA,
                null);
    }

    private void cargarDialogo(String tituloDialogo, String nombre) {
        dialogo.setTitleDialog(tituloDialogo);
        dialogo.setOldName(nombre);
        dialogo.show(getSupportFragmentManager(), "dialogo");
    }

    private void cambiarValorFotoU() {
        opcionFoto = "-U.jpg";
        verificarPermisos();
    }

    private void cambiarValorFotoI() {
        opcionFoto = "-I.jpg";
        verificarPermisos();
    }

    private void validarNombrePozo() {
        nombrePozo = Objects.requireNonNull(binding.inputLayoutPozo.getEditText())
                .getText().toString();
        if (emptyTextInputLayout(binding.inputLayoutPozo) &&
                isRepeated(nombrePozo, pozos, binding.inputLayoutPozo)) {
            crearPozo();
        }
    }

    private void buscarPozosSector() {
        pozoViewModel.obtenerPozosPorSector(sectorId).observe(this,
                listaPozos -> pozos = listaPozos);
    }

    private void tomarFoto() {
        File foto;
        try {
            foto = crearArchivoFoto();
        } catch (IOException io) {
            Log.e("ERROR", io.toString());
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Uri uri = FileProvider.getUriForFile(this,
                "com.mam.alicamp.fileprovider",
                foto);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        lanzaCamara.launch(intent);
    }

    private File crearArchivoFoto() throws IOException {
        String nombreFotoPozo = Objects.requireNonNull(binding.inputLayoutPozo.getEditText())
                .getText().toString();
        nombreFotoPozo += opcionFoto;
        return new File(directorioPozo, nombreFotoPozo);
    }

    private void crearPozo() {
        String tapado = binding.checkTapado.isChecked() ? "Si" : "No";
        Responsable responsableSeleccionado = (Responsable) binding.spinnerResponsable.getSelectedItem();
        Descarga descargaSeleccionada = (Descarga) binding.spinnerDescarga.getSelectedItem();
        String sistema = binding.spinnerTipoSistema.getSelectedItem().toString();
        if (sistema.equals("Seleccione")) {
            sweetAlertOpciones.setMensaje("Seleccione sistema");
            sweetAlertOpciones.mostrarDialogoError();
            return;
        }
        try {
            Integer idResponsable = responsableSeleccionado.getIdResponsable();
            Integer idDescarga = descargaSeleccionada.getIdDescarga();
            pozoViewModel.insertar(new Pozo(nombrePozo, false, ManejoFechas.obtenerFechaActual(),
                    ManejoFechas.obtenerFechaActual(), tapado, sistema, directorioPozo, ACTIVIDAD_POZO_CATASTRADO,
                    sectorId, idResponsable, idDescarga));
            binding.imgBtnCrearPozo.setEnabled(false);
            binding.imgBtnFotoUbicacion.setEnabled(true);
            binding.imgBtnFotoInterior.setEnabled(true);
            binding.imgBtnFotoUbicacion.setImageResource(R.drawable.ic_camara_enabled);
            binding.imgBtnFotoInterior.setImageResource(R.drawable.ic_camara_enabled);
            binding.btnSiguientePozo.setEnabled(true);
            binding.inputLayoutPozo.setEnabled(false);
            binding.spinnerTipoSistema.setEnabled(false);
            binding.spinnerDescarga.setEnabled(false);
            binding.spinnerResponsable.setEnabled(false);
            sweetAlertOpciones.setMensaje("Pozo creado correctamente");
            sweetAlertOpciones.mostrarDialogoSuccess();
        } catch (NullPointerException np) {
            sweetAlertOpciones.setMensaje("Ingrese un Responsable o una Descarga");
            sweetAlertOpciones.mostrarDialogoError();
        }
    }

    @Override
    public void onEliminar(File directorio, String tipo) {
        operarBD(tipo, "");
    }

    @Override
    public void onCancelar() {

    }

    private void avanzarCalle() {
        try {
            if (regresando || editando) {
                String tipoSistema = binding.spinnerTipoSistema.getSelectedItem().toString();
                Responsable responsableSeleccionado = (Responsable) binding.spinnerResponsable
                        .getSelectedItem();
                Descarga descargaSeleccionada = (Descarga) binding.spinnerDescarga.getSelectedItem();
                Integer idResponsable = responsableSeleccionado.getIdResponsable();
                Integer idDescarga = descargaSeleccionada.getIdDescarga();
                pozo.setIdSector(sectorId);
                pozo.setSistema(tipoSistema);
                pozo.setIdResponsable(idResponsable);
                pozo.setIdDescarga(idDescarga);
                pozo.setSincronizado(false);
                pozoViewModel.actualizar(pozo);
            }
            intentActivity();
        } catch (Exception ex) {
            sweetAlertOpciones.setMensaje("Ingrese un Responsable o una Descarga");
            sweetAlertOpciones.mostrarDialogoError();
        }
    }

    private void intentActivity() {
        Intent intent = new Intent(this, CallesUbicacion.class);
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(NOMBRE_POZO, nombrePozo);
        startActivity(intent);
        finish();
    }

    private void regresar() {
        Intent intent = new Intent(this, ListaPozos.class);
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(DIRECTORIO_POZOS, directorioPozo);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        regresar();
        return false;
    }
}