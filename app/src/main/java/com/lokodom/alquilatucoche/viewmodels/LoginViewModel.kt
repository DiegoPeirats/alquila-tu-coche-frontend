package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.respuesta.LoginResponse
import com.lokodom.alquilatucoche.repository.AuthRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val loginState: StateFlow<UiState<LoginResponse>> = _loginState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Email y contraseña requeridos")
            return
        }

        viewModelScope.launch {
            _loginState.value = UiState.Loading
            _loginState.value = repository.login(email, password)
        }
    }

    fun resetState() {
        _loginState.value = UiState.Idle
    }
}