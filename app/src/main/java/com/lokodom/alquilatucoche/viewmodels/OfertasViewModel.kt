package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.repository.OfertasRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OfertasViewModel : ViewModel() {

    private val repository = OfertasRepository()

    private val _ofertasState = MutableStateFlow<UiState<List<Oferta>>>(UiState.Idle)
    val ofertasState: StateFlow<UiState<List<Oferta>>> = _ofertasState

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val deleteState: StateFlow<UiState<Unit>> = _deleteState

    fun loadOfertas(token: String) {
        viewModelScope.launch {
            _ofertasState.value = UiState.Loading
            _ofertasState.value = repository.getOfertas(token)
        }
    }

    fun deleteOferta(token: String, id: Long) {
        viewModelScope.launch {
            _deleteState.value = UiState.Loading
            _deleteState.value = repository.deleteOferta(token, id)
        }
    }

    fun resetDeleteState() {
        _deleteState.value = UiState.Idle
    }
}