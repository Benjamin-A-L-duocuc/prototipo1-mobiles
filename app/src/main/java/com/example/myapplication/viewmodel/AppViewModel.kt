package com.example.myapplication.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.repository.MockRepository
import com.example.myapplication.model.OT
import com.example.myapplication.navigation.Rutas

class AppViewModel : ViewModel() {
    var currentRoute by mutableStateOf(Rutas.LOGIN)
        private set

    var currentRole by mutableStateOf("tecnico") // tecnico, jefe, cliente
        private set

    var scenario by mutableStateOf("normal") // normal, lluvia
        private set

    var connection by mutableStateOf("online") // online, offline
        private set

    var pending by mutableStateOf(2)
        private set

    var otAccepted by mutableStateOf(false)
        private set

    var checkinDone by mutableStateOf(false)
        private set

    var otList by mutableStateOf(MockRepository.otList)
        private set

    fun navigateTo(route: String) {
        currentRoute = route
    }

    fun setRole(role: String) {
        currentRole = role
    }

    fun login(role: String) {
        currentRole = role
        when (role) {
            "tecnico" -> currentRoute = Rutas.JORNADA
            "jefe" -> currentRoute = Rutas.DASHBOARD
            "cliente" -> currentRoute = Rutas.CLIENTE
        }
    }

    fun changeScenario(newScenario: String) {
        scenario = newScenario
    }

    fun changeConnection(newConnection: String) {
        connection = newConnection
    }

    fun acceptOt() {
        otAccepted = true
    }

    fun doCheckIn() {
        checkinDone = true
        if (pending < 5) pending++
    }

    fun syncAll() {
        pending = 0
    }

    fun addNewOt(ot: OT) {
        otList = listOf(ot) + otList
        pending++
    }
}
