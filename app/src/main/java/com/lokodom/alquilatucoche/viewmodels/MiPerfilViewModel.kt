package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.model.entidad.Vehiculo
import com.lokodom.alquilatucoche.model.peticion.ofertas.CreacionOfertaRequest
import com.lokodom.alquilatucoche.model.peticion.vehiculos.CrearVehiculoRequest
import com.lokodom.alquilatucoche.repository.PropietarioRepository
import com.lokodom.alquilatucoche.session.SessionManager
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MiPerfilViewModel : ViewModel() {

    private val repo = PropietarioRepository()

    private val _state = MutableStateFlow<UiState<Propietario>>(UiState.Idle)
    val state = _state.asStateFlow()

    // Estados de acciones secundarias
    private val _registroState = MutableStateFlow<UiState<Propietario>>(UiState.Idle)
    val registroState = _registroState.asStateFlow()

    private val _crearVehiculoState = MutableStateFlow<UiState<Vehiculo>>(UiState.Idle)
    val crearVehiculoState = _crearVehiculoState.asStateFlow()

    private val _crearOfertaState = MutableStateFlow<UiState<Oferta>>(UiState.Idle)
    val crearOfertaState = _crearOfertaState.asStateFlow()

    fun loadMiPerfil(token: String) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = repo.getMiPerfil(token)
            _state.value = result
            // Actualizar rol en sesión
            if (result is UiState.Success) {
                SessionManager.actualizarRol(result.data.vehiculos.isNotEmpty())
            }
        }
    }

    fun registrarComoPropietario(token: String, imagenContrato: ByteArray) {
        viewModelScope.launch {
            _registroState.value = UiState.Loading
            val result = repo.registrarComoPropietario(token, imagenContrato)
            _registroState.value = result
            if (result is UiState.Success) loadMiPerfil(token)
        }
    }

    fun crearVehiculo(token: String, request: CrearVehiculoRequest) {
        viewModelScope.launch {
            _crearVehiculoState.value = UiState.Loading
            val result = repo.crearVehiculo(token, request)
            _crearVehiculoState.value = result
            if (result is UiState.Success) loadMiPerfil(token)
        }
    }

    fun crearOferta(token: String, request: CreacionOfertaRequest) {
        viewModelScope.launch {
            _crearOfertaState.value = UiState.Loading
            val result = repo.crearOferta(token, request)
            _crearOfertaState.value = result
            if (result is UiState.Success) loadMiPerfil(token)
        }
    }

    fun resetRegistroState() { _registroState.value = UiState.Idle }
    fun resetVehiculoState() { _crearVehiculoState.value = UiState.Idle }
    fun resetOfertaState() { _crearOfertaState.value = UiState.Idle }
}