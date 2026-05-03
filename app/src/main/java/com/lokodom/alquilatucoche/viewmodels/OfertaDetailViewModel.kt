package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.repository.OfertasRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OfertaDetailViewModel : ViewModel() {

    private val repo = OfertasRepository()

    private val _state = MutableStateFlow<UiState<Oferta>>(UiState.Idle)
    val state = _state.asStateFlow()

    private val _diasSeleccionados = MutableStateFlow(1)
    val diasSeleccionados = _diasSeleccionados.asStateFlow()

    fun load(token: String, ofertaId: Long) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repo.getOferta(token, ofertaId)
        }
    }

    fun setDias(dias: Int) {
        _diasSeleccionados.value = dias.coerceAtLeast(1)
    }
}
