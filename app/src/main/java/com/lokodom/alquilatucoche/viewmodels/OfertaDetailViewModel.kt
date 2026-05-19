package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Oferta
import com.lokodom.alquilatucoche.repository.OfertasRepository
import com.lokodom.alquilatucoche.repository.VehiculosRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class OfertaDetailViewModel : ViewModel() {

    private val ofertasRepo   = OfertasRepository()
    private val vehiculosRepo = VehiculosRepository()

    private val _state = MutableStateFlow<UiState<Oferta>>(UiState.Idle)
    val state = _state.asStateFlow()

    // Fechas ya reservadas para el vehículo — conjunto para O(1) lookup
    private val _fechasReservadas = MutableStateFlow<Set<String>>(emptySet())
    val fechasReservadas = _fechasReservadas.asStateFlow()

    private val _fechaInicio = MutableStateFlow("")
    val fechaInicio = _fechaInicio.asStateFlow()

    private val _fechaFin = MutableStateFlow("")
    val fechaFin = _fechaFin.asStateFlow()

    val diasSeleccionados: StateFlow<Int> = combine(_fechaInicio, _fechaFin) { inicio, fin ->
        calcularDias(inicio, fin)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun load(token: String, ofertaId: Long) {
        viewModelScope.launch {
            _state.value = UiState.Loading
            val result = ofertasRepo.getOferta(token, ofertaId)
            _state.value = result

            // Cuando tengamos la oferta, cargamos las fechas reservadas del vehículo
            if (result is UiState.Success) {
                cargarFechasReservadas(token, result.data.idVehiculo)
            }
        }
    }

    private suspend fun cargarFechasReservadas(token: String, vehiculoId: Long) {
        val result = vehiculosRepo.getFechasReservadas(token, vehiculoId)
        if (result is UiState.Success) {
            _fechasReservadas.value = result.data.toSet()
        }
        // Si falla, dejamos el set vacío — el usuario puede reservar
        // y el backend rechazará si hay conflicto
    }

    fun setFechaInicio(fecha: String) {
        _fechaInicio.value = fecha
        // Si la fecha fin ya elegida cae en rango bloqueado o pasa a ser inválida, la limpiamos
        if (_fechaFin.value.isNotBlank()) {
            val dias = calcularDias(fecha, _fechaFin.value)
            if (dias <= 0 || rangoContieneReservada(fecha, _fechaFin.value)) {
                _fechaFin.value = ""
            }
        }
    }

    fun setFechaFin(fecha: String) {
        _fechaFin.value = fecha
    }

    // Comprueba si alguna fecha del rango [inicio, fin) está reservada
    fun rangoContieneReservada(inicio: String, fin: String): Boolean {
        if (_fechasReservadas.value.isEmpty()) return false
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.isLenient = false
            val d1 = sdf.parse(inicio) ?: return false
            val d2 = sdf.parse(fin)   ?: return false
            val cal = Calendar.getInstance()
            cal.time = d1
            while (cal.time.before(d2)) {
                val iso = sdf.format(cal.time)
                if (iso in _fechasReservadas.value) return true
                cal.add(Calendar.DAY_OF_MONTH, 1)
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    private fun calcularDias(inicio: String, fin: String): Int {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.isLenient = false
            val d1 = sdf.parse(inicio) ?: return 0
            val d2 = sdf.parse(fin)   ?: return 0
            val diff = TimeUnit.MILLISECONDS.toDays(d2.time - d1.time).toInt()
            if (diff > 0) diff else 0
        } catch (e: Exception) { 0 }
    }
}