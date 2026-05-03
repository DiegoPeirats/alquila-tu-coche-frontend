package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.model.entidad.Usuario
import com.lokodom.alquilatucoche.repository.PropietarioRepository
import com.lokodom.alquilatucoche.repository.UsuariosRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PropietarioViewModel : ViewModel() {

    private val repo = PropietarioRepository()

    private val _state = MutableStateFlow<UiState<Propietario>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun load(token: String, usuarioId: Long) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repo.getPropietario(token, usuarioId)
        }
    }
}