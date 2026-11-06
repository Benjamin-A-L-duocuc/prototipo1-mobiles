package com.example.myapplication.validation

object Validators {
    fun loginUsername(value: String): String? =
        if (value.trim().isEmpty()) "Ingresa tu usuario." else null

    fun loginPassword(value: String): String? = when {
        value.isEmpty() -> "Ingresa tu contraseña."
        value.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
        else -> null
    }

    fun evidenceComment(value: String): String? = when {
        value.trim().length < 10 -> "Escribe al menos 10 caracteres."
        value.trim().length > 300 -> "El comentario no puede superar los 300 caracteres."
        else -> null
    }

    fun otCliente(value: String): String? = when {
        value.trim().isEmpty() -> "Ingresa el cliente o empresa."
        value.trim().length < 3 -> "El cliente debe tener al menos 3 caracteres."
        else -> null
    }

    fun otEquipo(value: String): String? = when {
        value.trim().isEmpty() -> "Ingresa el equipo o activo."
        value.trim().length < 3 -> "El equipo debe tener al menos 3 caracteres."
        else -> null
    }

    fun otDescripcion(value: String): String? = when {
        value.trim().isEmpty() -> "Describe el problema o trabajo solicitado."
        value.trim().length < 10 -> "La descripción debe tener al menos 10 caracteres."
        value.trim().length > 300 -> "La descripción no puede superar los 300 caracteres."
        else -> null
    }
}
