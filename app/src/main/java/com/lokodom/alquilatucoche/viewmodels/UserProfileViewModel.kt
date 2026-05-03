package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Usuario
import com.lokodom.alquilatucoche.repository.UsuariosRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {

    private val repo = UsuariosRepository()

    private val _state = MutableStateFlow<UiState<Usuario>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun load(token: String, usuarioId: Long) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repo.getMe(token)
        }
    }
}