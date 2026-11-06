package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.LocalDatabaseHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = LocalDatabaseHelper(application)

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun login(username: String, password: String, role: String): Boolean {
        val normalizedUsername = username.trim().lowercase()
        when {
            normalizedUsername.isEmpty() -> {
                _loginError.value = "Ingresa tu usuario."
                return false
            }
            password.isEmpty() -> {
                _loginError.value = "Ingresa tu contraseña."
                return false
            }
            password.length < 8 -> {
                _loginError.value = "La contraseña debe tener al menos 8 caracteres."
                return false
            }
        }

        return if (dbHelper.validateUser(normalizedUsername, role, password)) {
            _loginError.value = null
            true
        } else {
            _loginError.value = "Credenciales inválidas para el rol seleccionado."
            false
        }
    }

    fun clearLoginError() {
        _loginError.value = null
    }
}
