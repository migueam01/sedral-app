package com.mam.alicamp.ui.actividades;

import static com.mam.alicamp.constantes.ActividadesCompletadas.ACTIVIDAD_POZO_CATASTRADO;
import static com.mam.alicamp.constantes.ActividadesCompletadas.ACTIVIDAD_TUBERIA_CATASTRADA;
import static com.mam.alicamp.constantes.Constantes.ALTURA_POZO;
import static com.mam.alicamp.constantes.Constantes.EDITANDO;
import static com.mam.alicamp.constantes.Constantes.GADM_ID;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_POZO;
import static com.mam.alicamp.constantes.Constantes.PROYECTO_ID;
import static com.mam.alicamp.constantes.Constantes.SECTOR_ID;
import static com.mam.alicamp.constantes.Constantes.TUBERIA_ID;
import static com.mam.alicamp.servicios.Validaciones.checkHeights;
import static com.mam.alicamp.servicios.Validaciones.validarCamposVacios;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.mam.alicamp.R;
import com.mam.alicamp.controlesUI.ISpinner;
import com.mam.alicamp.controlesUI.ValoresSpinner;
import com.mam.alicamp.databinding.ActivityTuberiaCatastradaBinding;
import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.db.entidades.Tuberia;
import com.mam.alicamp.db.relaciones.TuberiaConPozos;
import com.mam.alicamp.servicios.ManejoDialogos;
import com.mam.alicamp.servicios.ManejoFechas;
import com.mam.alicamp.servicios.SweetAlertOpciones;
import com.mam.alicamp.ui.viewmodels.PozoViewModel;
import com.mam.alicamp.ui.viewmodels.TuberiaViewModel;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TuberiaCatastrada extends AppCompatActivity {

    private ActivityTuberiaCatastradaBinding binding;

    private TuberiaViewModel tuberiaViewModel;
    private PozoViewModel pozoViewModel;

    private SweetAlertOpciones sweetAlertOpciones;

    private Tuberia tuberia;
    private Pozo pozoFin;
    private Pozo pozoInicio;

    private ISpinner<String> iSpinner;
    private ManejoDialogos manejoDialogos;

    private String nombrePozoInicio;
    private String nombrePozoFin;
    private boolean editando;
    private double alturaPozo;
    private int gadmId;
    private int proyectoId;
    private int sectorId;
    private int tuberiaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuberia_catastrada);

        binding = ActivityTuberiaCatastradaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        addToolbar();

        Bundle b = getIntent().getExtras();
        if (b != null) {
            gadmId = b.getInt(GADM_ID);
            proyectoId = b.getInt(PROYECTO_ID);
            nombrePozoInicio = b.getString(NOMBRE_POZO);
            alturaPozo = b.getDouble(ALTURA_POZO);
            tuberiaId = b.getInt(TUBERIA_ID);
            sectorId = b.getInt(SECTOR_ID);
            editando = b.getBoolean(EDITANDO);
        }

        inicializarFlechaRetroceso();

        inicializarViewModels();

        inicializarObjetos();

        clearFocusEdits();

        initListeners();
    }

    private void addToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbarTuberia);
        toolbar.setTitle(R.string.activity_tuberia);
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
        tuberiaViewModel = new ViewModelProvider(this, factory).get(TuberiaViewModel.class);
    }

    private void inicializarObjetos() {
        iSpinner = new ValoresSpinner();
        binding.txtAlturaPozoDato.setText(MessageFormat.format(
                "{0} {1}",
                alturaPozo, " m"
        ));
        binding.txtAlturaPozo.setText(MessageFormat.format(
                "{0} {1}",
                "Altura pozo ", nombrePozoInicio + "= "
        ));
        tuberiaViewModel.obtenerTuberiaConPozos(tuberiaId).observe(this, tuberiaConPozos -> {
            if (tuberiaConPozos != null) {
                tuberia = tuberiaConPozos.tuberia;
                cargarValoresInicialesTuberias(tuberiaConPozos);
            }
        });
        pozoViewModel.obtenerPozoPorNombre(nombrePozoInicio).observe(this,
                pozoBuscado -> pozoInicio = pozoBuscado);
        manejoDialogos = new ManejoDialogos();
        sweetAlertOpciones = new SweetAlertOpciones(this);
    }

    private void cargarValoresInicialesTuberias(TuberiaConPozos tuberiaConPozos) {
        if (editando) {
            int diametro = tuberiaConPozos.tuberia.getDiametro();
            binding.editTextPozoConectado.setText(tuberiaConPozos.pozoFin.getNombre());
            binding.editInputBase.setText(String.valueOf(tuberiaConPozos.tuberia.getBase()));
            binding.editInputCorona.setText(String.valueOf(tuberiaConPozos.tuberia.getCorona()));
            binding.txtDiametroDato.setText(String.valueOf(tuberiaConPozos.tuberia.getDiametro()));
            pintarLabelDiametro(diametro);
            seleccionarItemSpinnerOrientacion(tuberiaConPozos.tuberia.getOrientacion());
            seleccionarItemSpinnerMaterial(tuberiaConPozos.tuberia.getMaterial());
            seleccionarItemSpinnerFlujo(tuberiaConPozos.tuberia.getFlujo());
            seleccionarRadioButtonFunciona(tuberiaConPozos.tuberia.getFunciona());
            binding.editInputLongitud.setText(String.valueOf(tuberiaConPozos.tuberia.getLongitud()));
            binding.editInputAreaAporte.setText(String.valueOf(tuberiaConPozos.tuberia.getAreaAporte()));
            binding.editInputCalado.setText(String.valueOf(tuberiaConPozos.tuberia.getCalado()));
            binding.btnAgregarTuberia.setText(R.string.button_guardar);
        }
    }

    private void initListeners() {
        binding.btnAgregarTuberia.setOnClickListener(v -> agregarTuberia());
        binding.btnCalcularDiametro.setOnClickListener(v -> calcularDiametro());
        binding.btnValidarPozo.setOnClickListener(v -> verificarPozo());
    }

    private void clearFocusEdits() {
        binding.editTextPozoConectado.clearFocus();
        binding.editInputBase.clearFocus();
        binding.editInputCorona.clearFocus();
    }

    private int retornarIndiceSpinner(List<String> listaBusqueda, String valorBuscado) {
        return iSpinner.getIdSpinner(listaBusqueda, valorBuscado);
    }

    private void seleccionarItemSpinnerOrientacion(String valorBuscado) {
        if (valorBuscado != null && !valorBuscado.isEmpty()) {
            List<String> listaBusqueda = Arrays.asList(getResources().getStringArray(
                    R.array.spinner_orientacion));
            int idSpinner = retornarIndiceSpinner(listaBusqueda, valorBuscado);
            binding.spinnerOrientacion.setSelection(idSpinner);
        }
    }

    private void seleccionarItemSpinnerMaterial(String valorBuscado) {
        if (valorBuscado != null && !valorBuscado.isEmpty()) {
            List<String> listaBusqueda = Arrays.asList(getResources().getStringArray(
                    R.array.spinner_material_tub));
            int idSpinner = retornarIndiceSpinner(listaBusqueda, valorBuscado);
            binding.spinnerMaterial.setSelection(idSpinner);
        }
    }

    private void seleccionarItemSpinnerFlujo(String valorBuscado) {
        if (valorBuscado != null && !valorBuscado.isEmpty()) {
            List<String> listaBusqueda = Arrays.asList(getResources().getStringArray(
                    R.array.spinner_flujo));
            int idSpinner = retornarIndiceSpinner(listaBusqueda, valorBuscado);
            binding.spinnerFlujo.setSelection(idSpinner);
        }
    }

    private void seleccionarRadioButtonFunciona(String funciona) {
        binding.radioGroupFunciona.clearCheck();
        if (funciona.equals("Si")) {
            binding.radioGroupFunciona.check(R.id.radioBtnFuncionaSi);
        } else {
            binding.radioGroupFunciona.check(R.id.radioBtnFuncionaNo);
        }
    }

    private void pintarLabelDiametro(int diametro) {
        if (diametro < 200) {
            binding.txtDiametroDato.setTextColor(Color.RED);
        } else {
            binding.txtDiametroDato.setTextColor(Color.BLACK);
        }
    }

    private void calcularDiametro() {
        double base, corona;
        int diametroCalculado;
        if (validarCamposVacios(binding.editInputBase, "Campo requerido") &&
                validarCamposVacios(binding.editInputCorona, "Campo requerido")) {
            base = Double.parseDouble(Objects.requireNonNull(binding.editInputBase.getText()).toString());
            corona = Double.parseDouble(Objects.requireNonNull(binding.editInputCorona.getText()).toString());
            diametroCalculado = (int) Math.round((base - corona) * 1000);
            binding.txtDiametroDato.setText(String.valueOf(diametroCalculado));
            pintarLabelDiametro(diametroCalculado);
        }
    }

    private void agregarTuberia() {
        if (validarCamposVacios(binding.editInputBase, "Campo requerido") &&
                validarCamposVacios(binding.editInputCorona, "Campo requerido")) {
            double alturaBase = Double.parseDouble(Objects.requireNonNull(binding.editInputBase
                    .getText()).toString());
            double alturaCorona = Double.parseDouble(Objects.requireNonNull(binding.editInputCorona
                    .getText()).toString());
            double areaAporte = Double.parseDouble(Objects.requireNonNull(binding.editInputAreaAporte
                    .getText()).toString());
            double calado = Double.parseDouble(Objects.requireNonNull(binding.editInputCalado
                    .getText()).toString());
            int diametro = Integer.parseInt(binding.txtDiametroDato.getText().toString());
            String flujo = binding.spinnerFlujo.getSelectedItem().toString();
            String material = binding.spinnerMaterial.getSelectedItem().toString();
            String orientacion = binding.spinnerOrientacion.getSelectedItem().toString();
            String funciona;
            int radioFuncionaId = binding.radioGroupFunciona.getCheckedRadioButtonId();
            if (radioFuncionaId == -1) {
                sweetAlertOpciones.setMensaje("Seleccione si la tubería funciona");
                sweetAlertOpciones.mostrarDialogoError();
                return;
            } else {
                RadioButton radioSeleccionado = findViewById(radioFuncionaId);
                funciona = radioSeleccionado.getText().toString();
            }
            if (flujo.equals("Seleccione")) {
                sweetAlertOpciones.setMensaje("Seleccione flujo");
                sweetAlertOpciones.mostrarDialogoError();
                return;
            }
            if (material.equals("Seleccione")) {
                sweetAlertOpciones.setMensaje("Seleccione material");
                sweetAlertOpciones.mostrarDialogoError();
                return;
            }
            if (orientacion.equals("Seleccione")) {
                sweetAlertOpciones.setMensaje("Seleccione orientación");
                sweetAlertOpciones.mostrarDialogoError();
                return;
            }
            if (!checkHeights(flujo, alturaBase, alturaPozo)) {
                manejoDialogos.crearDialogo("Verifique las alturas: \n" +
                        "- Si el flujo es de entrada, la altura de la base debe ser menor o " +
                        "igual a la altura del pozo\n" +
                        "- Si el flujo es de salida, la altura de la base debe tener una diferencia " +
                        "entre 1 y 2 cm con respecto a la altura del pozo", this);
                return;
            }
            if (tuberia != null) {
                tuberia.setBase(alturaBase);
                tuberia.setCorona(alturaCorona);
                tuberia.setDiametro(diametro);
                tuberia.setFlujo(flujo);
                tuberia.setMaterial(material);
                tuberia.setOrientacion(orientacion);
                tuberia.setFunciona(funciona);
                tuberia.setAreaAporte(areaAporte);
                tuberia.setCalado(calado);
                tuberia.setIdPozoInicio(pozoInicio.getIdPozo());
                tuberia.setIdPozoFin(pozoFin.getIdPozo());
                tuberiaViewModel.actualizar(tuberia, pozoInicio, pozoFin);
            } else {
                tuberia = new Tuberia(orientacion, alturaBase, alturaCorona, diametro, material,
                        flujo, funciona, areaAporte, calado, pozoInicio.getIdPozo(), pozoFin.getIdPozo());
                tuberiaViewModel.insertar(tuberia, pozoInicio, pozoFin);
            }
            if (pozoInicio.getActividadCompletada() < ACTIVIDAD_TUBERIA_CATASTRADA) {
                pozoInicio.setActividadCompletada(ACTIVIDAD_TUBERIA_CATASTRADA);
                pozoViewModel.actualizar(pozoInicio);
            }
            Snackbar.make(findViewById(R.id.constraintTuberia),
                    "Tubería agregada",
                    BaseTransientBottomBar.LENGTH_SHORT
            ).show();
            regresar();
        }
    }

    private void regresar() {
        Intent intent = new Intent(this, ListaTuberias.class);
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(NOMBRE_POZO, nombrePozoInicio);
        startActivity(intent);
        finish();
    }

    private void verificarPozo() {
        if (validarCamposVacios(binding.editTextPozoConectado, "Campo requerido")) {
            nombrePozoFin = binding.editTextPozoConectado.getText().toString().trim();
            pozoViewModel.obtenerPozoPorNombre(nombrePozoFin).observe(this, this::crearPozo);
        }
    }

    private void crearPozo(Pozo pozoBuscado) {
        pozoFin = pozoBuscado;
        if (pozoFin == null) {
            new SweetAlertDialog(TuberiaCatastrada.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("El pozo no existe")
                    .setContentText("¿Desea crear un pozo nuevo?")
                    .setConfirmText("Si")
                    .setCancelText("No")
                    .showCancelButton(true)
                    .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                    .setConfirmClickListener(sweetAlertDialog -> {
                        pozoFin = new Pozo(nombrePozoFin, ManejoFechas.obtenerFechaActual(),
                                ManejoFechas.obtenerFechaActual(),
                                "No", pozoInicio.getSistema(),
                                pozoInicio.getPathMedia(), ACTIVIDAD_POZO_CATASTRADO,
                                pozoInicio.getIdSector(), pozoInicio.getIdResponsable(),
                                pozoInicio.getIdDescarga());
                        pozoViewModel.insertar(pozoFin);
                        sweetAlertDialog.setTitleText("POZO NUEVO")
                                .setContentText("Se ha creado el pozo exitosamente")
                                .setConfirmText("Aceptar")
                                .showCancelButton(false)
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }).show();
        } else {
            Snackbar.make(findViewById(R.id.constraintTuberia),
                    "El pozo si existe",
                    BaseTransientBottomBar.LENGTH_LONG
            ).show();
        }
        binding.btnAgregarTuberia.setEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        regresar();
        return false;
    }
}