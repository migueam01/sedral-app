package com.mam.alicamp.ui.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.mam.alicamp.R;
import com.mam.alicamp.databinding.ActivityLoginBinding;
import com.mam.alicamp.ui.viewmodels.AuthViewModel;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        inicializarViewModels();
        configurarObservadores();
        //configurarVisibilidadPass();

        //Verificar si ya está logueado
        if (authViewModel.isLoggedIn()) {
            irMainActivity();
            return;
        }

        initListeners();
    }

    /*private void configurarVisibilidadPass(){
        binding.tilPassword.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibilidad();
            }
        });
    }

    private void togglePasswordVisibilidad(){
        if(isPasswordVisible){
            binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.tilPassword.setEndIconDrawable(R.drawable.ic_visi);
        }
    }*/

    private void initListeners() {
        binding.btnLogin.setOnClickListener(v -> iniciarSesion());
    }

    private void inicializarViewModels() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void configurarObservadores() {
        authViewModel.getLoginExitoso().observe(this, exitoso -> {
            if (exitoso != null && exitoso) {
                Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();
                irMainActivity();
                authViewModel.resetLoginState();
            }
        });

        authViewModel.getMensajeError().observe(this, mensaje -> {
            if (mensaje != null && !mensaje.isEmpty()) {
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void iniciarSesion() {
        String username = Objects.requireNonNull(binding.txtInputUsuario.getEditText()).getText().toString().trim();
        String password = Objects.requireNonNull(binding.txtInputPassword.getEditText()).getText().toString().trim();
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        authViewModel.resetLoginState();

        authViewModel.login(username, password);
    }

    private void irMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}