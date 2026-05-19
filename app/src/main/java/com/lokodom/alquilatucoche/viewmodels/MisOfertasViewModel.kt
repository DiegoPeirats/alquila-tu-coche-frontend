package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.model.peticion.ofertas.ModificarOfertaRequest
import com.lokodom.alquilatucoche.repository.PropietarioRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MisOfertasViewModel : ViewModel() {

    private val repo = PropietarioRepository()

    private val _state = MutableStateFlow<UiState<List<Oferta>>>(UiState.Idle)
    val state = _state.asStateFlow()

    private val _accionState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val accionState = _accionState.asStateFlow()

    fun load(token: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repo.getMisOfertas(token)
        }
    }

    fun eliminarOferta(token: String, id: Long) {
        viewModelScope.launch {
            _accionState.value = UiState.Loading
            val result = repo.eliminarOferta(token, id)
            _accionState.value = result
            if (result is UiState.Success) load(token)
        }
    }

    fun modificarOferta(token: String, id: Long, precioPorDia: Double, estado: String) {
        viewModelScope.launch {
            _accionState.value = UiState.Loading
            val result = repo.modificarOferta(
                token, id,
                ModificarOfertaRequest(precioPorDia, estado)
            )
            _accionState.value = when (result) {
                is UiState.Success -> UiState.Success(Unit)
                is UiState.Error -> result
                else -> UiState.Idle
            }
            if (result is UiState.Success) load(token)
        }
    }

    fun resetAccionState() { _accionState.value = UiState.Idle }
}
