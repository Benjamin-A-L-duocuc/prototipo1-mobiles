package com.example.myapplication.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.repository.MockRepository
import com.example.myapplication.data.repository.OtRepository
import com.example.myapplication.domain.OtWorkflow
import com.example.myapplication.model.ActividadOT
import com.example.myapplication.model.CheckIn
import com.example.myapplication.model.Cliente
import com.example.myapplication.model.Conformidad
import com.example.myapplication.model.Equipo
import com.example.myapplication.model.Evidence
import com.example.myapplication.model.LocalComercial
import com.example.myapplication.model.Material
import com.example.myapplication.model.OT
import com.example.myapplication.model.OtEvento
import com.example.myapplication.model.SyncItem
import com.example.myapplication.model.Tecnico
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

    var currentUserId by mutableStateOf(0L)
        private set

    var currentTecnicoId by mutableStateOf<Long?>(null)
        private set

    var currentClienteId by mutableStateOf<Long?>(null)
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

    var selectedCheckIn by mutableStateOf<CheckIn?>(null)
        private set

    var selectedEvents by mutableStateOf<List<OtEvento>>(emptyList())
        private set

    var selectedActivities by mutableStateOf<List<ActividadOT>>(emptyList())
        private set

    var selectedMaterials by mutableStateOf<List<Material>>(emptyList())
        private set

    var selectedConformity by mutableStateOf<Conformidad?>(null)
        private set

    var technicians by mutableStateOf<List<Tecnico>>(emptyList())
        private set

    var customers by mutableStateOf<List<Cliente>>(emptyList())
        private set

    var locations by mutableStateOf<List<LocalComercial>>(emptyList())
        private set

    var equipment by mutableStateOf<List<Equipo>>(emptyList())
        private set

    val pending: Int
        get() = syncItems.size

    var selectedOtId by mutableStateOf("")
        private set

    val visibleOts: List<OT>
        get() = when (currentRole) {
            "tecnico" -> currentTecnicoId?.let { tecnicoId -> otList.filter { it.tecnicoId == tecnicoId } }.orEmpty()
            "cliente" -> currentClienteId?.let { clienteId -> otList.filter { it.clienteId == clienteId } }.orEmpty()
            else -> otList
        }

    val pendingAssignmentOts: List<OT>
        get() = otList.filter { it.estado == OtWorkflow.PENDIENTE }

    private fun hasOpenOts(predicate: (OT) -> Boolean): Boolean =
        otList.any { it.estado != OtWorkflow.CERRADA && it.estado != OtWorkflow.CONFORMADA && it.estado != OtWorkflow.RECHAZADA_CLIENTE && it.estado != OtWorkflow.CANCELADA && predicate(it) }

    val selectedOT: OT
        get() = visibleOts.find { it.id == selectedOtId }
            ?: visibleOts.firstOrNull()
            ?: otList.find { it.id == selectedOtId }
            ?: MockRepository.defaultOT

    val monthlyCompliancePercentage: Int
        get() {
            if (otList.isEmpty()) return 100
            val conformadas = otList.count { it.estado == OtWorkflow.CONFORMADA }
            return ((conformadas.toDouble() / otList.size.toDouble()) * 100).toInt()
        }

    val atRiskSlaCount: Int
        get() = otList.count {
            it.prioridad == "Alta" && !OtWorkflow.estaCerrada(it.estado) && it.estado != OtWorkflow.CANCELADA
        }

    init {
        refreshCatalogs()
        selectedOtId = visibleOts.firstOrNull()?.id ?: otList.firstOrNull()?.id.orEmpty()
        refreshSelectedOtData()
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
        refreshSelectedOtData()
    }

    fun selectFirstVisibleOt() {
        selectedOtId = visibleOts.firstOrNull()?.id.orEmpty()
        refreshSelectedOtData()
    }

    private fun refreshSelectedOtData() {
        evidenceList = repository.getEvidence(selectedOtId)
        selectedCheckIn = repository.getCheckIn(selectedOtId)
        selectedEvents = repository.getEvents(selectedOtId)
        selectedActivities = repository.getActivities(selectedOtId)
        selectedMaterials = repository.getMaterials(selectedOtId)
        selectedConformity = repository.getConformity(selectedOtId)
    }

    private fun refreshCatalogs() {
        technicians = repository.getTecnicos()
        customers = repository.getClientes()
        locations = repository.getLocales()
        equipment = repository.getEquipos()
    }

    private fun refreshOts() {
        otList = repository.getOts()
        if (selectedOtId.isBlank() || selectedOT.id != selectedOtId) {
            selectedOtId = visibleOts.firstOrNull()?.id ?: otList.firstOrNull()?.id.orEmpty()
        }
        refreshSelectedOtData()
    }

    fun refreshAll() {
        refreshCatalogs()
        refreshOts()
        syncItems = repository.getSyncItems()
    }

    fun login(role: String, username: String = ""): String {
        val normalizedUsername = username.trim().lowercase()
        val session = repository.getUserSession(normalizedUsername, role)
        if (session != null) {
            currentRole = session.role
            currentUserUsername = session.username
            currentUserName = session.nombre
            currentUserId = session.id
            currentTecnicoId = session.tecnicoId
            currentClienteId = session.clienteId
        } else {
            currentRole = role
            if (normalizedUsername.isNotBlank()) currentUserUsername = normalizedUsername
        }
        refreshAll()
        selectedOtId = visibleOts.firstOrNull()?.id ?: otList.firstOrNull()?.id.orEmpty()
        refreshSelectedOtData()
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
        currentUserId = 0L
        currentTecnicoId = null
        currentClienteId = null
        selectedOtId = ""
        refreshAll()
    }

    fun changeScenario(newScenario: String) {
        scenario = newScenario
    }

    fun nextOtId(): String {
        val lastNumber = otList.mapNotNull { it.id.removePrefix("OT-").toIntOrNull() }.maxOrNull() ?: 1045
        return "OT-${lastNumber + 1}"
    }

    fun addNewOt(ot: OT) {
        repository.addOt(ot)
        repository.addEvent(ot.id, "Creación", currentUserUsername, "OT creada")
        syncItems = repository.getSyncItems()
        refreshOts()
    }

    fun updateOt(ot: OT) {
        repository.updateOt(ot)
        refreshOts()
    }

    fun deleteOt(id: String) {
        repository.getEvidence(id).forEach { File(it.path).delete() }
        repository.deleteOt(id)
        refreshOts()
    }

    fun updateOtState(id: String, estado: String) {
        repository.updateEstado(id, estado)
        repository.addEvent(id, "Cambio de estado", currentUserUsername, "Estado: $estado")
        refreshOts()
    }

    fun assignOt(otId: String, tecnicoId: Long, programadaPara: Long?): String? {
        val ot = repository.getOtById(otId) ?: return "La OT no existe."
        if (!OtWorkflow.puedeReasignar(ot)) return "La OT ya no puede asignarse."
        val tecnico = technicians.find { it.id == tecnicoId } ?: return "Selecciona un técnico válido."
        if (!tecnico.activo || !tecnico.disponible) return "El técnico no está disponible."
        repository.assignOt(otId, tecnicoId, programadaPara)
        repository.addEvent(otId, if (ot.tecnicoId == null) "Asignación" else "Reasignación", currentUserUsername, "Asignada a ${tecnico.nombre}")
        repository.addSyncItem("Asignación", "$otId asignada a ${tecnico.nombre}", "asignacion")
        syncItems = repository.getSyncItems()
        refreshOts()
        return null
    }

    fun acceptOt(id: String = selectedOtId): String? {
        val ot = repository.getOtById(id) ?: return "La OT no existe."
        if (!OtWorkflow.puedeAceptar(ot, currentTecnicoId)) return "La OT no está asignada a tu usuario."
        val now = System.currentTimeMillis()
        repository.acceptOt(id, now)
        repository.addEvent(id, "Aceptación", currentUserUsername, "OT aceptada", now)
        repository.addSyncItem("Aceptación", "$id aceptada por $currentUserName", "aceptacion")
        syncItems = repository.getSyncItems()
        refreshOts()
        return null
    }

    fun rejectOt(id: String, motivo: String): String? {
        val ot = repository.getOtById(id) ?: return "La OT no existe."
        if (!OtWorkflow.puedeRechazar(ot, currentTecnicoId)) return "La OT no puede rechazarse en su estado actual."
        if (motivo.trim().length !in 10..300) return "El motivo debe tener entre 10 y 300 caracteres."
        repository.rejectOt(id)
        repository.addEvent(id, "Rechazo", currentUserUsername, motivo.trim())
        repository.addSyncItem("Rechazo", "$id devuelta a asignación", "rechazo")
        syncItems = repository.getSyncItems()
        refreshOts()
        return null
    }

    fun doCheckIn(
        id: String = selectedOtId,
        latitude: Double?,
        longitude: Double?,
        accuracy: Float?,
        timestamp: Long = System.currentTimeMillis(),
        source: String = "gps"
    ): String? {
        val ot = repository.getOtById(id) ?: return "La OT no existe."
        if (!OtWorkflow.puedeCheckIn(ot, currentTecnicoId)) return "La OT no está lista para check-in."
        if (repository.getCheckIn(id) != null) return null
        repository.addCheckIn(id, latitude, longitude, accuracy, timestamp, source)
        repository.checkIn(id, timestamp)
        repository.addEvent(id, "Check-in", currentUserUsername, "Check-in registrado con ubicación $source", timestamp)
        repository.addSyncItem("Check-in", "$id iniciado en terreno", "checkin")
        syncItems = repository.getSyncItems()
        refreshOts()
        return null
    }

    fun saveDiagnostico(texto: String) {
        val ot = selectedOT
        if (!OtWorkflow.puedeEjecutar(ot, currentTecnicoId)) return
        val trimmed = texto.trim()
        repository.saveDiagnostico(ot.id, trimmed)
        if (ot.diagnostico.isBlank() && trimmed.isNotBlank()) {
            repository.addEvent(ot.id, "Diagnóstico", currentUserUsername, "Diagnóstico registrado")
        }
        refreshOts()
    }

    fun addActivity(descripcion: String) {
        val ot = selectedOT
        if (!OtWorkflow.puedeEjecutar(ot, currentTecnicoId)) return
        if (descripcion.trim().isBlank()) return
        repository.addActivity(ot.id, descripcion.trim())
        repository.addEvent(ot.id, "Actividad", currentUserUsername, descripcion.trim())
        selectedActivities = repository.getActivities(ot.id)
    }

    fun toggleActivity(activity: ActividadOT) {
        repository.updateActivityCompleted(activity.id, !activity.completada)
        selectedActivities = repository.getActivities(activity.otId)
    }

    fun deleteActivity(activity: ActividadOT) {
        repository.deleteActivity(activity.id)
        selectedActivities = repository.getActivities(activity.otId)
    }

    fun saveMaterials(materiales: List<Material>) {
        val ot = selectedOT
        if (!OtWorkflow.puedeEjecutar(ot, currentTecnicoId)) return
        repository.saveMaterials(ot.id, materiales)
        repository.addEvent(ot.id, "Materiales", currentUserUsername, "${materiales.sumOf { it.cantidad }} unidades registradas")
        selectedMaterials = repository.getMaterials(ot.id)
    }

    fun addEvidence(path: String, comentario: String = "") {
        repository.addEvidence(selectedOtId, path, System.currentTimeMillis(), comentario)
        repository.addEvent(selectedOtId, "Evidencia", currentUserUsername, "Fotografía adjuntada")
        repository.addSyncItem("Evidencia", "Fotografía adjuntada a $selectedOtId", "camera")
        evidenceList = repository.getEvidence(selectedOtId)
        syncItems = repository.getSyncItems()
    }

    fun updateEvidenceComment(evidence: Evidence, comentario: String) {
        repository.updateEvidenceComment(evidence.id, comentario)
        evidenceList = repository.getEvidence(selectedOtId)
    }

    fun deleteEvidence(evidence: Evidence) {
        repository.deleteEvidence(evidence.id)
        File(evidence.path).delete()
        evidenceList = repository.getEvidence(selectedOtId)
    }

    fun closeOt(id: String = selectedOtId): String? {
        val ot = repository.getOtById(id) ?: return "La OT no existe."
        if (!OtWorkflow.puedeCerrar(ot, currentTecnicoId)) return "La OT no está en ejecución."
        if (ot.diagnostico.isBlank()) return "Registra el diagnóstico antes de cerrar."
        val activities = repository.getActivities(id)
        if (activities.isEmpty()) return "Registra al menos una actividad."
        val evidence = repository.getEvidence(id)
        if (evidence.isEmpty()) return "Agrega al menos una evidencia fotográfica."
        val now = System.currentTimeMillis()
        repository.closeOt(id, now)
        repository.addEvent(id, "Cierre", currentUserUsername, "Check-out y cierre registrados", now)
        repository.addSyncItem("Cierre", "$id cerrada y lista para conformidad", "cierre")
        syncItems = repository.getSyncItems()
        refreshOts()
        return null
    }

    fun conformOt(id: String, firmante: String, observacion: String, conforme: Boolean): String? {
        val ot = repository.getOtById(id) ?: return "La OT no existe."
        if (!OtWorkflow.puedeConformar(ot)) return "La OT debe estar cerrada para registrar conformidad."
        if (firmante.trim().length < 3) return "Ingresa el nombre de quien recibe."
        if (!conforme && observacion.trim().length < 10) return "Indica el motivo del rechazo."
        val now = System.currentTimeMillis()
        repository.saveConformity(Conformidad(id, firmante.trim(), observacion.trim(), conforme, now))
        val estado = if (conforme) OtWorkflow.CONFORMADA else OtWorkflow.RECHAZADA_CLIENTE
        repository.conformOt(id, estado, now)
        repository.addEvent(id, "Conformidad", currentUserUsername, if (conforme) "Trabajo conforme" else observacion.trim(), now)
        refreshOts()
        return null
    }

    fun cancelOt(id: String, motivo: String): String? {
        val ot = repository.getOtById(id) ?: return "La OT no existe."
        if (!OtWorkflow.puedeCancelar(ot)) return "La OT ya no puede cancelarse."
        if (motivo.trim().length !in 10..500) return "El motivo debe tener entre 10 y 500 caracteres."
        repository.cancelOt(id)
        repository.addEvent(id, "Cancelación", currentUserUsername, motivo.trim())
        repository.addSyncItem("Cancelación", "$id cancelada", "cancelacion")
        syncItems = repository.getSyncItems()
        refreshOts()
        return null
    }

    fun syncAll() {
        repository.clearSyncQueue()
        syncItems = emptyList()
    }

    fun refreshCatalogsOnly() {
        refreshCatalogs()
    }

    fun addCliente(nombre: String, contacto: String, email: String, telefono: String): String? {
        if (nombre.trim().length < 3) return "El nombre debe tener al menos 3 caracteres."
        repository.addCliente(Cliente(0L, nombre.trim(), contacto.trim(), email.trim(), telefono.trim()))
        refreshCatalogs()
        return null
    }

    fun updateCliente(cliente: Cliente): String? {
        if (cliente.nombre.trim().length < 3) return "El nombre debe tener al menos 3 caracteres."
        repository.updateCliente(cliente.copy(nombre = cliente.nombre.trim()))
        refreshCatalogs()
        return null
    }

    fun toggleClienteActivo(cliente: Cliente): String? {
        if (cliente.activo && hasOpenOts { it.clienteId == cliente.id }) {
            return "No puedes desactivar un cliente con OT abiertas."
        }
        repository.setClienteActivo(cliente.id, !cliente.activo)
        refreshCatalogs()
        return null
    }

    fun addLocal(clienteId: Long, nombre: String, direccion: String, comuna: String, latitud: Double?, longitud: Double?): String? {
        if (clienteId <= 0) return "Selecciona un cliente."
        if (nombre.trim().length < 3) return "El nombre del local es obligatorio."
        if (direccion.trim().length < 5) return "La dirección es obligatoria."
        repository.addLocal(LocalComercial(0L, clienteId, nombre.trim(), direccion.trim(), comuna.trim(), latitud, longitud))
        refreshCatalogs()
        return null
    }

    fun updateLocal(local: LocalComercial): String? {
        if (local.nombre.trim().length < 3) return "El nombre del local es obligatorio."
        repository.updateLocal(local.copy(nombre = local.nombre.trim(), direccion = local.direccion.trim()))
        refreshCatalogs()
        return null
    }

    fun toggleLocalActivo(local: LocalComercial): String? {
        if (local.activo && hasOpenOts { it.localId == local.id }) {
            return "No puedes desactivar un local con OT abiertas."
        }
        repository.setLocalActivo(local.id, !local.activo)
        refreshCatalogs()
        return null
    }

    fun addEquipo(id: String, localId: Long, nombre: String, tipo: String, exterior: Boolean, ultimaMantencion: String): String? {
        if (id.trim().length < 3) return "El ID del equipo es obligatorio."
        if (localId <= 0) return "Selecciona un local."
        if (nombre.trim().length < 3) return "El nombre del equipo es obligatorio."
        repository.addEquipo(Equipo(id.trim(), localId, nombre.trim(), tipo.trim(), exterior, ultimaMantencion.trim()))
        refreshCatalogs()
        return null
    }

    fun updateEquipo(equipo: Equipo): String? {
        if (equipo.nombre.trim().length < 3) return "El nombre del equipo es obligatorio."
        repository.updateEquipo(equipo.copy(nombre = equipo.nombre.trim()))
        refreshCatalogs()
        return null
    }

    fun toggleEquipoActivo(equipo: Equipo): String? {
        if (equipo.activo && hasOpenOts { ot -> ot.equipoId == equipo.id }) {
            return "No puedes desactivar un equipo con OT abiertas."
        }
        repository.setEquipoActivo(equipo.id, !equipo.activo)
        refreshCatalogs()
        return null
    }

    fun addTecnico(username: String, nombre: String, telefono: String, password: String, disponible: Boolean): String? {
        if (username.trim().length < 3) return "El usuario es obligatorio."
        if (nombre.trim().length < 3) return "El nombre es obligatorio."
        if (password.length < 8) return "La contraseña debe tener al menos 8 caracteres."
        repository.addTecnico(username.trim().lowercase(), nombre.trim(), telefono.trim(), password, disponible)
        refreshCatalogs()
        return null
    }

    fun updateTecnico(tecnico: Tecnico, password: String = ""): String? {
        if (tecnico.nombre.trim().length < 3) return "El nombre es obligatorio."
        if (password.isNotBlank() && password.length < 8) return "La contraseña debe tener al menos 8 caracteres."
        repository.updateTecnico(tecnico.copy(nombre = tecnico.nombre.trim(), username = tecnico.username.trim().lowercase()), password)
        refreshCatalogs()
        return null
    }

    fun toggleTecnicoActivo(tecnico: Tecnico) {
        repository.setTecnicoActivo(tecnico.id, !tecnico.activo)
        refreshCatalogs()
    }

    fun toggleTecnicoDisponible(tecnico: Tecnico) {
        repository.setTecnicoDisponible(tecnico.id, !tecnico.disponible)
        refreshCatalogs()
    }

    fun tecnicoName(id: Long?): String = technicians.find { it.id == id }?.nombre ?: "Sin asignar"

    fun clienteName(id: Long?): String = customers.find { it.id == id }?.nombre ?: "Sin cliente"

    fun localName(id: Long?): String = locations.find { it.id == id }?.nombre ?: "Sin local"

    fun equipoName(id: String): String = equipment.find { it.id == id }?.nombre ?: id
}
