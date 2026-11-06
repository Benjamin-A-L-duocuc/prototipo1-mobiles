package com.example.myapplication.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.MockRepository
import com.example.myapplication.data.repository.OtRepository
import com.example.myapplication.model.OT
import com.example.myapplication.model.SyncItem
import com.example.myapplication.navigation.Rutas

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OtRepository(application)

    var currentRoute by mutableStateOf(Rutas.LOGIN)
        private set

    var currentRole by mutableStateOf("tecnico") // tecnico, jefe, cliente
        private set

    var currentUserUsername by mutableStateOf("cristian.rojas")
        private set

    var currentUserName by mutableStateOf("Cristian Rojas")
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

    var otList by mutableStateOf(repository.getOts())
        private set

    var syncItems by mutableStateOf(repository.getSyncItems())
        private set

    var selectedOtId by mutableStateOf(MockRepository.defaultOT.id)
        private set

    val selectedOT: OT
        get() = otList.find { it.id == selectedOtId } ?: otList.firstOrNull() ?: MockRepository.defaultOT

    val monthlyCompliancePercentage: Int
        get() {
            if (otList.isEmpty()) return 100
            val cerradas = otList.count { it.estado == "Cerrada" || it.estado == "Conformada" }
            return ((cerradas.toDouble() / otList.size.toDouble()) * 100).toInt().coerceAtLeast(85)
        }

    val atRiskSlaCount: Int
        get() = otList.count { it.prioridad == "Alta" && it.estado != "Cerrada" && it.estado != "Conformada" }

    fun selectOt(id: String) {
        selectedOtId = id
    }

    fun navigateTo(route: String) {
        currentRoute = route
    }

    fun setRole(role: String) {
        currentRole = role
    }

    fun login(role: String, username: String = "") {
        currentRole = role
        if (username.isNotBlank()) {
            currentUserUsername = username
            currentUserName = when (role) {
                "tecnico" -> "Cristian Rojas"
                "jefe" -> "Gerardo Jefatura"
                else -> "María Soto"
            }
        }
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
        repository.updateEstado(selectedOtId, "Aceptada")
        otList = repository.getOts()
    }

    fun doCheckIn() {
        checkinDone = true
        repository.updateEstado(selectedOtId, "En ejecución")
        repository.addSyncItem("Check-in", "Check-in registrado para $selectedOtId", "checkin")
        syncItems = repository.getSyncItems()
        otList = repository.getOts()
        if (pending < 5) pending++
    }

    fun syncAll() {
        repository.clearSyncQueue()
        syncItems = emptyList()
        pending = 0
    }

    fun addNewOt(ot: OT) {
        repository.addOt(ot)
        otList = repository.getOts()
        pending++
    }

    fun updateOt(ot: OT) {
        repository.updateOt(ot)
        otList = repository.getOts()
    }

    fun deleteOt(id: String) {
        repository.deleteOt(id)
        otList = repository.getOts()
        if (selectedOtId == id) {
            selectedOtId = otList.firstOrNull()?.id ?: ""
        }
    }

    fun updateOtState(id: String, estado: String) {
        repository.updateEstado(id, estado)
        otList = repository.getOts()
    }
}
