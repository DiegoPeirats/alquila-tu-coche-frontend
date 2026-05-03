package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.respuesta.LoginResponse
import com.lokodom.alquilatucoche.repository.AuthRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _state = MutableStateFlow<UiState<LoginResponse>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repo.login(email, password)
        }
    }
}