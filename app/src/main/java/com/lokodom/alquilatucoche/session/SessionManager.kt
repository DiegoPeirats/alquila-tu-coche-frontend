package com.lokodom.alquilatucoche.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object SessionManager {
    var token: String by mutableStateOf("")
        private set
    var usuarioId: Long by mutableStateOf(0L)
        private set
    var esPropietario: Boolean by mutableStateOf(false)
        private set

    fun iniciarSesion(token: String, usuarioId: Long) {
        this.token = token
        this.usuarioId = usuarioId
    }

    fun actualizarRol(esPropietario: Boolean) {
        this.esPropietario = esPropietario
    }

    fun cerrarSesion() {
        token = ""
        usuarioId = 0L
        esPropietario = false
    }

    fun isLoggedIn() = token.isNotBlank()
}