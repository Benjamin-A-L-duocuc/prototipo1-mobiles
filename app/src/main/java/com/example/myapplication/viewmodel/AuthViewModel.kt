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

    fun login(username: String, role: String): Boolean {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE username = ? AND role = ?", arrayOf(username.trim().lowercase(), role))
        val found = cursor.moveToFirst()
        cursor.close()

        if (found) {
            _loginError.value = null
            return true
        } else {
            _loginError.value = "Credenciales inválidas para el rol seleccionado."
            return false
        }
    }
}
