package com.example.myapplication.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.MockRepository
import com.example.myapplication.data.repository.OtRepository
import com.example.myapplication.model.Evidence
import com.example.myapplication.model.OT
import com.example.myapplication.model.SyncItem
import com.example.myapplication.navigation.Rutas
import com.example.myapplication.util.NetworkMonitor
import java.io.File
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OtRepository(application)
    private val networkMonitor = NetworkMonitor(application)

    var currentRole by mutableStateOf("tecnico")
        private set

    var currentUserUsername by mutableStateOf("cristian.rojas")
        private set

    var currentUserName by mutableStateOf("Cristian Rojas")
        private set

    var scenario by mutableStateOf("normal")
        private set

    var connection by mutableStateOf(if (networkMonitor.isOnline.value) "online" else "offline")
        private set

    var otList by mutableStateOf(repository.getOts())
        private set

    var syncItems by mutableStateOf(repository.getSyncItems())
        private set

    var evidenceList by mutableStateOf<List<Evidence>>(emptyList())
        private set

    val pending: Int
        get() = syncItems.size

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

    init {
        selectedOtId = otList.firstOrNull()?.id ?: MockRepository.defaultOT.id
        evidenceList = repository.getEvidence(selectedOtId)
        viewModelScope.launch {
            networkMonitor.isOnline.collect { isOnline ->
                connection = if (isOnline) "online" else "offline"
            }
        }
    }

    override fun onCleared() {
        networkMonitor.unregister()
        super.onCleared()
    }

    fun selectOt(id: String) {
        selectedOtId = id
        evidenceList = repository.getEvidence(id)
    }

    fun login(role: String, username: String = ""): String {
        currentRole = role
        if (username.isNotBlank()) {
            currentUserUsername = username
            currentUserName = when (username) {
                "gerardo.jefe" -> "Gerardo Jefatura"
                "maria.cliente" -> "María Soto"
                else -> "Cristian Rojas"
            }
        }
        return when (role) {
            "tecnico" -> Rutas.JORNADA
            "jefe" -> Rutas.DASHBOARD
            "cliente" -> Rutas.CLIENTE
            else -> Rutas.LOGIN
        }
    }

    fun logout() {
        currentRole = "tecnico"
        currentUserUsername = "cristian.rojas"
        currentUserName = "Cristian Rojas"
        selectedOtId = repository.getOts().firstOrNull()?.id ?: MockRepository.defaultOT.id
        evidenceList = repository.getEvidence(selectedOtId)
    }

    fun changeScenario(newScenario: String) {
        scenario = newScenario
    }

    fun acceptOt() {
        repository.updateEstado(selectedOtId, "Aceptada")
        otList = repository.getOts()
    }

    fun doCheckIn() {
        repository.updateEstado(selectedOtId, "En ejecución")
        repository.addSyncItem("Check-in", "Check-in registrado para $selectedOtId", "checkin")
        syncItems = repository.getSyncItems()
        otList = repository.getOts()
    }

    fun syncAll() {
        repository.clearSyncQueue()
        syncItems = emptyList()
    }

    fun nextOtId(): String {
        val lastNumber = otList.mapNotNull { it.id.removePrefix("OT-").toIntOrNull() }.maxOrNull() ?: 1041
        return "OT-${lastNumber + 1}"
    }

    fun addNewOt(ot: OT) {
        repository.addOt(ot)
        otList = repository.getOts()
    }

    fun updateOt(ot: OT) {
        repository.updateOt(ot)
        otList = repository.getOts()
    }

    fun deleteOt(id: String) {
        repository.getEvidence(id).forEach { File(it.path).delete() }
        repository.deleteOt(id)
        otList = repository.getOts()
        if (selectedOtId == id) {
            selectedOtId = otList.firstOrNull()?.id ?: ""
        }
        evidenceList = repository.getEvidence(selectedOtId)
    }

    fun updateOtState(id: String, estado: String) {
        repository.updateEstado(id, estado)
        otList = repository.getOts()
    }

    fun addEvidence(path: String) {
        repository.addEvidence(selectedOtId, path, System.currentTimeMillis())
        evidenceList = repository.getEvidence(selectedOtId)
        repository.addSyncItem("Evidencia", "Fotografía adjuntada a $selectedOtId", "camera")
        syncItems = repository.getSyncItems()
    }

    fun deleteEvidence(evidence: Evidence) {
        repository.deleteEvidence(evidence.id)
        File(evidence.path).delete()
        evidenceList = repository.getEvidence(selectedOtId)
    }
}
