package com.mam.alicamp.db.repositorios;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mam.alicamp.api.AuthApiService;
import com.mam.alicamp.api.RetrofitCliente;
import com.mam.alicamp.servicios.LoginRequest;
import com.mam.alicamp.servicios.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApiService apiService;
    private final SharedPreferences sharedPreferences;

    private final MutableLiveData<Boolean> loginExitoso = new MutableLiveData<>();
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public AuthRepository(Context context) {
        apiService = RetrofitCliente.getCliente(context).create(AuthApiService.class);
        sharedPreferences = context.getSharedPreferences("session", Context.MODE_PRIVATE);
    }

    public void login(String username, String password) {
        loading.setValue(true);
        LoginRequest loginRequest = new LoginRequest(username, password);

        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                loading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", response.body().getToken());
                    editor.putString("username", username);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    loginExitoso.setValue(true);
                } else {
                    mensajeError.setValue("Credenciales incorrectas");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                loading.setValue(false);
                mensajeError.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean("isLoggedIn", false);
    }

    public void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        loginExitoso.setValue(false);
        mensajeError.setValue("");
        loading.setValue(false);
    }

    public String getUsername() {
        return sharedPreferences.getString("username", "");
    }

    public String getToken() {
        return sharedPreferences.getString("token", "");
    }

    public LiveData<Boolean> getLoginExitoso() {
        return loginExitoso;
    }

    public LiveData<String> getMensajeError() {
        return mensajeError;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public void resetLoginState() {
        loginExitoso.setValue(false);
        mensajeError.setValue("");
    }
}