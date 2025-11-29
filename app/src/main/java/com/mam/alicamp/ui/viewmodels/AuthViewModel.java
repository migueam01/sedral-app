package com.mam.alicamp.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mam.alicamp.db.repositorios.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository repository;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
    }

    public void login(String username, String password) {
        repository.login(username, password);
    }

    public boolean isLoggedIn() {
        return repository.isLoggedIn();
    }

    public void logout() {
        repository.logout();
    }

    public String getUsername() {
        return repository.getUsername();
    }

    public String getToken() {
        return repository.getToken();
    }

    public void resetLoginState() {
        repository.resetLoginState();
    }

    public LiveData<Boolean> getLoginExitoso() {
        return repository.getLoginExitoso();
    }

    public LiveData<String> getMensajeError() {
        return repository.getMensajeError();
    }

    public LiveData<Boolean> getLoading() {
        return repository.getLoading();
    }
}