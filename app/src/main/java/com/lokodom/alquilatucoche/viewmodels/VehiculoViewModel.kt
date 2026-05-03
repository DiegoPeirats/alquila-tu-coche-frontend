package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import com.lokodom.alquilatucoche.repository.VehiculosRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VehiculoViewModel : ViewModel() {

    private val repo = VehiculosRepository()

    private val _state = MutableStateFlow<UiState<Vehiculo>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun load(token: String, vehiculoId: Long) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repo.getVehiculo(token, vehiculoId)
        }
    }
}