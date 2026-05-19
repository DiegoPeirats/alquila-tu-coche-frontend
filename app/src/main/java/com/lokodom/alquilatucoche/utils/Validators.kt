package com.lokodom.alquilatucoche.utils

object Validators {

    // Matrícula española moderna: 1234ABC
    private val MATRICULA_REGEX = Regex("^[0-9]{4}[A-Z]{3}$")

    fun validarMatricula(matricula: String): ValidationResult {
        val limpia = matricula.trim().uppercase()
        return when {
            limpia.isBlank() -> ValidationResult.Error("La matrícula es obligatoria")
            limpia.length != 7 -> ValidationResult.Error("Formato: 4 números + 3 letras (ej: 1234ABC)")
            !MATRICULA_REGEX.matches(limpia) -> ValidationResult.Error("Formato inválido. Ejemplo: 1234ABC")
            else -> ValidationResult.Ok
        }
    }

    fun validarEmail(email: String): ValidationResult {
        return if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            ValidationResult.Ok
        else
            ValidationResult.Error("Email no válido")
    }

    fun validarPassword(password: String): ValidationResult {
        return when {
            password.length < 6 -> ValidationResult.Error("Mínimo 6 caracteres")
            else -> ValidationResult.Ok
        }
    }

    fun validarFechas(inicio: String, fin: String): ValidationResult {
        return try {
            val fmt = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
            val d1 = java.time.LocalDate.parse(inicio, fmt)
            val d2 = java.time.LocalDate.parse(fin, fmt)
            when {
                d1.isBefore(java.time.LocalDate.now()) ->
                    ValidationResult.Error("La fecha de inicio no puede ser en el pasado")
                !d2.isAfter(d1) ->
                    ValidationResult.Error("La fecha fin debe ser posterior a la de inicio")
                else -> ValidationResult.Ok
            }
        } catch (e: Exception) {
            ValidationResult.Error("Formato de fecha inválido (YYYY-MM-DD)")
        }
    }

    fun validarPrecio(precio: String): ValidationResult {
        val d = precio.toDoubleOrNull()
        return when {
            d == null -> ValidationResult.Error("Introduce un precio válido")
            d <= 0 -> ValidationResult.Error("El precio debe ser mayor que 0")
            d > 9999 -> ValidationResult.Error("Precio demasiado alto")
            else -> ValidationResult.Ok
        }
    }
}

sealed class ValidationResult {
    object Ok : ValidationResult()
    data class Error(val message: String) : ValidationResult()
    val isOk get() = this is Ok
    val errorMessage get() = (this as? Error)?.message
}
