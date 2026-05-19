package com.lokodom.alquilatucoche.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lokodom.alquilatucoche.model.entidad.Propietario
import com.lokodom.alquilatucoche.model.entidad.Usuario
import com.lokodom.alquilatucoche.model.peticion.auth.AltaUsuarioRequest
import com.lokodom.alquilatucoche.repository.AuthRepository
import com.lokodom.alquilatucoche.utils.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Estado del formulario de registro
data class RegistroFormState(
    val nombre: String = "",
    val apellidos: String = "",
    val genero: String = "",
    val direccion: String = "",
    val provincia: String = "",
    val email: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val numeroTelefono: String = "",
    val imagenPerfil: ByteArray? = null
) {
    val passwordsCoinciden get() = password == confirmarPassword
    val formularioValido get() =
        nombre.isNotBlank() && apellidos.isNotBlank() && email.isNotBlank() &&
                password.isNotBlank() && passwordsCoinciden && numeroTelefono.isNotBlank()
}

class RegistroViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _formState = MutableStateFlow(RegistroFormState())
    val formState = _formState.asStateFlow()

    private val _state = MutableStateFlow<UiState<Usuario>>(UiState.Idle)
    val state = _state.asStateFlow()

    fun updateForm(update: RegistroFormState.() -> RegistroFormState) {
        _formState.value = _formState.value.update()
    }

    fun registrar() {
        val form = _formState.value
        if (!form.formularioValido) return

        viewModelScope.launch {
            _state.value = UiState.Loading
            _state.value = repo.registro(
                AltaUsuarioRequest(
                    nombre = form.nombre,
                    apellidos = form.apellidos,
                    genero = form.genero.ifBlank { "no especificado" },
                    direccion = form.direccion.ifBlank { "-" },
                    provincia = form.provincia.ifBlank { "-" },
                    email = form.email,
                    password = form.password,
                    numeroTelefono = form.numeroTelefono,
                    imagenPerfil = form.imagenPerfil
                )
            )
        }
    }

    fun resetState() { _state.value = UiState.Idle }
}