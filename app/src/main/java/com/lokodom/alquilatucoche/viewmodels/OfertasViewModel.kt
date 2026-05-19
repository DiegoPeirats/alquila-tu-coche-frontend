package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.model.peticion.ofertas.OfertasFiltro
import com.lokodom.alquilatucoche.repository.OfertasRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OfertasViewModel : ViewModel() {

    private val repo = OfertasRepository()

    private val _state = MutableStateFlow<UiState<List<Oferta>>>(UiState.Idle)
    val state = _state.asStateFlow()

    // Estado del formulario de filtros
    private val _filtro = MutableStateFlow(OfertasFiltro())
    val filtro = _filtro.asStateFlow()

    // Controla si el panel de filtros está visible
    private val _filtrosVisibles = MutableStateFlow(false)
    val filtrosVisibles = _filtrosVisibles.asStateFlow()

    // Carga inicial: solo ofertas disponibles
    fun loadDisponibles(token: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repo.getOfertasDisponibles(token)
        }
    }

    // Aplica el filtro actual y lanza la búsqueda
    fun buscarConFiltro(token: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            _filtrosVisibles.value = false
            _state.value = repo.getOfertasFiltradas(token, _filtro.value)
        }
    }

    // Resetea filtros y vuelve a cargar disponibles
    fun limpiarFiltros(token: String) {
        _filtro.value = OfertasFiltro()
        loadDisponibles(token)
    }

    fun toggleFiltros() {
        _filtrosVisibles.value = !_filtrosVisibles.value
    }

    fun updateFiltro(nuevo: OfertasFiltro) {
        _filtro.value = nuevo
    }
}