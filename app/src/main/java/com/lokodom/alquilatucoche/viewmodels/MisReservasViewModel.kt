package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Reserva
import com.lokodom.alquilatucoche.model.peticion.reservas.ModificarReservaRequest
import com.lokodom.alquilatucoche.repository.ReservasRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MisReservasViewModel : ViewModel() {

    private val repo = ReservasRepository()

    private val _state = MutableStateFlow<UiState<List<Reserva>>>(UiState.Idle)
    val state = _state.asStateFlow()

    private val _accionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val accionState = _accionState.asStateFlow()

    fun load(token: String, usuarioId: Long) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repo.getReservasUsuario(token)
        }
    }

    fun cancelarReserva(token: String, usuarioId: Long, reservaId: Long) {
        viewModelScope.launch {
            _accionState.value = UiState.Loading
            val result = repo.cancelarReserva(token, reservaId)
            _accionState.value = result
            if (result is UiState.Success) load(token, usuarioId)
        }
    }

    fun modificarReserva(
        token: String,
        usuarioId: Long,
        reservaId: Long,
        fechaInicio: String,
        fechaFin: String
    ) {
        viewModelScope.launch {
            _accionState.value = UiState.Loading
            val result = repo.modificarReserva(
                token, reservaId,
                ModificarReservaRequest(fechaInicio, fechaFin)
            )
            // Mapear Reserva → Unit para el estado de acción
            _accionState.value = when (result) {
                is UiState.Success -> UiState.Success(Unit)
                is UiState.Error -> result
                else -> UiState.Idle
            }
            if (result is UiState.Success) load(token, usuarioId)
        }
    }

    fun resetAccionState() { _accionState.value = UiState.Idle }
}