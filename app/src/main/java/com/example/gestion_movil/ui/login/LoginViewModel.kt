package com.example.gestion_movil.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gestion_movil.data.model.AuthRepository
import com.example.gestion_movil.data.model.Credential

class LoginViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    // Estado de la pantalla de inicio de sesión
    var uiState by mutableStateOf(LoginUiState())
        private set

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value, error = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, error = null)
    }

    // Valida las credenciales; si son correctas entrega la cuenta (usuario + rol) para iniciar la sesión
    fun submit(onSuccess: (Credential) -> Unit) {
        uiState = uiState.copy(isLoading = true, error = null)

        val cuenta = repo.login(uiState.username.trim(), uiState.password)

        uiState = uiState.copy(isLoading = false)

        if (cuenta != null) onSuccess(cuenta)
        else uiState = uiState.copy(error = "Usuario o contraseña incorrectos")
    }
}
