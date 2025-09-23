package com.mam.alicamp.ui.actividades;

import static com.mam.alicamp.constantes.ActividadesCompletadas.ACTIVIDAD_CALLES_UBICACION;
import static com.mam.alicamp.constantes.Constantes.DIRECTORIO_POZOS;
import static com.mam.alicamp.constantes.Constantes.GADM_ID;
import static com.mam.alicamp.constantes.Constantes.NOMBRE_POZO;
import static com.mam.alicamp.constantes.Constantes.PROYECTO_ID;
import static com.mam.alicamp.constantes.Constantes.REGRESANDO;
import static com.mam.alicamp.constantes.Constantes.SECTOR_ID;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.mam.alicamp.R;
import com.mam.alicamp.controlesUI.ISpinner;
import com.mam.alicamp.controlesUI.ValoresSpinner;
import com.mam.alicamp.databinding.ActivityCallesUbicacionBinding;
import com.mam.alicamp.db.entidades.Pozo;
import com.mam.alicamp.servicios.ManejoUTM;
import com.mam.alicamp.servicios.SweetAlertOpciones;
import com.mam.alicamp.servicios.Utilitarios;
import com.mam.alicamp.ui.viewmodels.PozoViewModel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CallesUbicacion extends PermisosActivity {

    private LocationManager locationManager;
    private LocationListener locationListener;

    private ActivityCallesUbicacionBinding binding;

    private PozoViewModel pozoViewModel;

    private SweetAlertOpciones sweetAlertOpciones;

    private Pozo pozo;

    private String nombrePozo;
    private int gadmId;
    private int proyectoId;
    private int sectorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calles_ubicacion);

        binding = ActivityCallesUbicacionBinding.inflate(getLayoutInflater());
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

        clearFocusEdits();

        initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerRecepcionGPS();
    }

    private void addToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbarCalles);
        toolbar.setTitle(R.string.activity_calles);
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

    private void clearFocusEdits() {
        binding.textInputOE.clearFocus();
        binding.textInputNS.clearFocus();
        binding.editTextNorte.clearFocus();
        binding.editTextEste.clearFocus();
        binding.editTextCota.clearFocus();
        binding.editTextSrid.clearFocus();
    }

    private void initListeners() {
        binding.toggleBtnGPS.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                verificarPermisos();
            } else {
                detenerRecepcionGPS();
            }
        }));
        binding.btnSiguienteActividad.setOnClickListener(v -> avanzarActivity());
    }

    private void inicializarObjetos() {
        binding.editTextNorte.setText("0.0");
        binding.editTextEste.setText("0.0");
        binding.editTextCota.setText("0.0");
        binding.editTextSrid.setText("0");
        binding.txtAproximacionDato.setText("0.0");
        pozoViewModel.obtenerPozoPorNombre(nombrePozo).observe(this, this::cargarValoresIniciales);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        sweetAlertOpciones = new SweetAlertOpciones(this);
    }

    private void cargarValoresIniciales(Pozo pozoBuscado) {
        pozo = pozoBuscado;
        if (ACTIVIDAD_CALLES_UBICACION <= pozoBuscado.getActividadCompletada()) {
            binding.textInputNS.setText(pozo.getCalleNS());
            binding.textInputOE.setText(pozo.getCalleOE());
            DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
            simbolos.setDecimalSeparator('.');
            DecimalFormat num = new DecimalFormat("####.00", simbolos);
            BigDecimal bigDecimal = new BigDecimal(num.format(pozo.getCoordNorte()));
            binding.editTextNorte.setText(String.format("%s", bigDecimal));
            bigDecimal = new BigDecimal(num.format(pozo.getCoordEste()));
            binding.editTextEste.setText(String.format("%s", bigDecimal));
            bigDecimal = new BigDecimal(num.format(pozo.getCota()));
            binding.editTextCota.setText(String.format("%s", bigDecimal));
            binding.editTextSrid.setText(String.valueOf(pozo.getSrid()));
            binding.txtZona.setText(pozo.getZona());
            binding.txtAproximacionDato.setText(String.valueOf(pozo.getAproximacion()));
            seleccionarSpinnerCalzada();
        }
    }

    private void seleccionarSpinnerCalzada() {
        Log.d("CALZADA", "Calzada "+pozo.getCalzada()+" pozo "+pozo.getNombre());
        List<String> listaCalzada = Arrays.asList(getResources().getStringArray(R.array.spinner_calzada));
        ISpinner<String> iSpinner = new ValoresSpinner();
        int idSpinner = iSpinner.getIdSpinner(listaCalzada, pozo.getCalzada());
        if (idSpinner != 0) {
            binding.spinnerCalzada.setSelection(idSpinner);
        }
    }

    private void verificarPermisos() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            iniciarRecepcionGPS();
        } else {
            showToast("Permisos de ubicación no concedido");
        }
    }

    private void iniciarRecepcionGPS() {
        locationListener = this::actualizarUI;
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000,
                    0, locationListener);
            showToast("Recibiendo actualizaciones de GPS...");
        } catch (SecurityException se) {
            showToast("Permisos insuficientes para GPS");
            binding.toggleBtnGPS.setChecked(false);
        }
    }

    private void actualizarUI(Location location) {
        double latitud, longitud, cota, aproximacion;
        latitud = location.getLatitude();
        longitud = location.getLongitude();
        cota = location.getAltitude();
        aproximacion = location.getAccuracy();
        binding.editTextNorte.setText(String.valueOf(latitud));
        binding.editTextEste.setText(String.valueOf(longitud));
        binding.editTextCota.setText(String.valueOf(cota));
        binding.txtAproximacionDato.setText(String.valueOf(Utilitarios.redondearDecimales(
                aproximacion, 2)));
    }

    private void detenerRecepcionGPS() {
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
            ManejoUTM convUTM = new ManejoUTM();
            double latitud = Double.parseDouble(binding.editTextNorte.getText().toString());
            double longitud = Double.parseDouble(binding.editTextEste.getText().toString());
            double cota = Double.parseDouble(binding.editTextCota.getText().toString());
            convUTM.calcularCoordUTM(longitud, latitud);
            binding.editTextNorte.setText(String.valueOf(Utilitarios
                    .redondearDecimales(convUTM.getCoordY(), 2)));
            binding.editTextEste.setText(String.valueOf(Utilitarios
                    .redondearDecimales(convUTM.getCoordX(), 2)));
            binding.editTextCota.setText(String.valueOf(Utilitarios
                    .redondearDecimales(cota, 2)));
            binding.txtZona.setText(convUTM.getZona());
        }
    }

    @Override
    protected void onStoragePermissionGranted() {

    }

    @Override
    protected void onAdditionalPermissionsGranted() {
        Log.i("PERMISOS", "Permisos concedidos");
    }

    private void avanzarActivity() {
        String tipoCalzada = binding.spinnerCalzada.getSelectedItem().toString();
        if (!tipoCalzada.equals("Seleccione")) {
            String calleOE = Objects.requireNonNull(binding.textInputOE.getText()).toString().trim();
            String calleNS = Objects.requireNonNull(binding.textInputNS.getText()).toString().trim();
            String zona = binding.txtZona.getText().toString();
            double coordNorte = Double.parseDouble(binding.editTextNorte.getText().toString());
            double coordEste = Double.parseDouble(binding.editTextEste.getText().toString());
            double cota = Double.parseDouble(binding.editTextCota.getText().toString());
            int srid = Integer.parseInt(binding.editTextSrid.getText().toString());
            double aproximacion = Double.parseDouble(binding.txtAproximacionDato.getText().toString());
            pozo.setCalleOE(calleOE);
            pozo.setCalleNS(calleNS);
            pozo.setCoordNorte(coordNorte);
            pozo.setCoordEste(coordEste);
            pozo.setCota(cota);
            pozo.setZona(zona);
            pozo.setAproximacion(aproximacion);
            pozo.setCalzada(tipoCalzada);
            pozo.setSrid(srid);
            if (pozo.getActividadCompletada() < ACTIVIDAD_CALLES_UBICACION) {
                pozo.setActividadCompletada(ACTIVIDAD_CALLES_UBICACION);
            }
            pozoViewModel.actualizarUbicacion(pozo);
            intentActivity();
        } else {
            sweetAlertOpciones.setMensaje("Seleccione tipo de calzada");
            sweetAlertOpciones.mostrarDialogoError();
        }
    }

    private void intentActivity() {
        Intent intent;
        boolean pozoTapado = pozo.getTapado().equalsIgnoreCase("Si");
        if (pozoTapado) {
            intent = new Intent(this, FinCatastro.class);
        } else {
            intent = new Intent(this, Dimensiones.class);
        }
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(NOMBRE_POZO, nombrePozo);
        startActivity(intent);
        finish();
    }

    private void regresar() {
        Intent intent = new Intent(this, PozoCatastrado.class);
        intent.putExtra(GADM_ID, gadmId);
        intent.putExtra(PROYECTO_ID, proyectoId);
        intent.putExtra(SECTOR_ID, sectorId);
        intent.putExtra(NOMBRE_POZO, nombrePozo);
        intent.putExtra(DIRECTORIO_POZOS, pozo.getPathMedia());
        intent.putExtra(REGRESANDO, true);
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