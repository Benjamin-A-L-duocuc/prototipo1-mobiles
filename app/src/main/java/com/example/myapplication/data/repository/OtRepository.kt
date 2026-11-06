package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.LocalDatabaseHelper
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
import com.example.myapplication.model.UserSession

class OtRepository(context: Context) {
    private val dbHelper = LocalDatabaseHelper(context)

    fun getUserSession(username: String, role: String): UserSession? =
        dbHelper.getUserSession(username, role)

    fun getOts(): List<OT> = dbHelper.getAllOts()

    fun getOtById(id: String): OT? = dbHelper.getOtById(id)

    fun addOt(ot: OT) {
        dbHelper.insertOt(ot)
    }

    fun updateOt(ot: OT) {
        dbHelper.updateOt(ot)
    }

    fun deleteOt(id: String) {
        dbHelper.deleteOt(id)
    }

    fun updateEstado(id: String, estado: String) {
        dbHelper.updateOtEstado(id, estado)
    }

    fun assignOt(id: String, tecnicoId: Long, programadaPara: Long?) {
        dbHelper.updateOtAssignment(id, tecnicoId, programadaPara, "Asignada")
    }

    fun acceptOt(id: String, timestamp: Long) {
        dbHelper.updateOtAcceptance(id, timestamp)
    }

    fun rejectOt(id: String) {
        dbHelper.updateOtRejection(id)
    }

    fun checkIn(id: String, timestamp: Long) {
        dbHelper.updateOtCheckIn(id, timestamp)
    }

    fun saveDiagnostico(id: String, diagnostico: String) {
        dbHelper.updateOtDiagnostico(id, diagnostico)
    }

    fun closeOt(id: String, timestamp: Long) {
        dbHelper.updateOtClosure(id, timestamp)
    }

    fun cancelOt(id: String) {
        dbHelper.updateOtCancellation(id)
    }

    fun conformOt(id: String, estado: String, timestamp: Long) {
        dbHelper.updateOtConformityState(id, estado, timestamp)
    }

    fun addSyncItem(tipo: String, detalle: String, icono: String) {
        dbHelper.addSyncItem(tipo, detalle, icono)
    }

    fun getSyncItems(): List<SyncItem> = dbHelper.getSyncItems()

    fun clearSyncQueue() {
        dbHelper.clearSyncItems()
    }

    fun getEvidence(otId: String): List<Evidence> = dbHelper.getEvidence(otId)

    fun addEvidence(otId: String, path: String, createdAt: Long, comentario: String): Long =
        dbHelper.addEvidence(otId, path, createdAt, comentario)

    fun updateEvidenceComment(id: Long, comentario: String) =
        dbHelper.updateEvidenceComment(id, comentario)

    fun deleteEvidence(id: Long) = dbHelper.deleteEvidence(id)

    fun getCheckIn(otId: String): CheckIn? = dbHelper.getCheckIn(otId)

    fun addCheckIn(otId: String, latitude: Double?, longitude: Double?, accuracy: Float?, createdAt: Long, source: String) =
        dbHelper.addCheckIn(otId, latitude, longitude, accuracy, createdAt, source)

    fun getEvents(otId: String): List<OtEvento> = dbHelper.getOtEvents(otId)

    fun addEvent(otId: String, tipo: String, actor: String, detalle: String, createdAt: Long = System.currentTimeMillis()) =
        dbHelper.addOtEvent(otId, tipo, actor, detalle, createdAt)

    fun getActivities(otId: String): List<ActividadOT> = dbHelper.getActivities(otId)

    fun addActivity(otId: String, descripcion: String): Long = dbHelper.addActivity(otId, descripcion)

    fun updateActivityCompleted(id: Long, completada: Boolean) = dbHelper.updateActivityCompleted(id, completada)

    fun deleteActivity(id: Long) = dbHelper.deleteActivity(id)

    fun getMaterials(otId: String): List<Material> = dbHelper.getMaterials(otId)

    fun saveMaterials(otId: String, materiales: List<Material>) = dbHelper.replaceMaterials(otId, materiales)

    fun getConformity(otId: String): Conformidad? = dbHelper.getConformity(otId)

    fun saveConformity(conformidad: Conformidad) = dbHelper.saveConformity(conformidad)

    fun getClientes(): List<Cliente> = dbHelper.getClientes()

    fun addCliente(cliente: Cliente): Long = dbHelper.insertCliente(cliente)

    fun updateCliente(cliente: Cliente) = dbHelper.updateCliente(cliente)

    fun setClienteActivo(id: Long, activo: Boolean) = dbHelper.setClienteActivo(id, activo)

    fun getLocales(): List<LocalComercial> = dbHelper.getLocales()

    fun addLocal(local: LocalComercial): Long = dbHelper.insertLocal(local)

    fun updateLocal(local: LocalComercial) = dbHelper.updateLocal(local)

    fun setLocalActivo(id: Long, activo: Boolean) = dbHelper.setLocalActivo(id, activo)

    fun getEquipos(): List<Equipo> = dbHelper.getEquipos()

    fun addEquipo(equipo: Equipo) = dbHelper.insertEquipo(equipo)

    fun updateEquipo(equipo: Equipo) = dbHelper.updateEquipo(equipo)

    fun setEquipoActivo(id: String, activo: Boolean) = dbHelper.setEquipoActivo(id, activo)

    fun getTecnicos(): List<Tecnico> = dbHelper.getTecnicos()

    fun addTecnico(username: String, nombre: String, telefono: String, password: String, disponible: Boolean): Long =
        dbHelper.insertTecnico(username, nombre, telefono, password, disponible)

    fun updateTecnico(tecnico: Tecnico, password: String = "") = dbHelper.updateTecnico(tecnico, password)

    fun setTecnicoActivo(id: Long, activo: Boolean) = dbHelper.setTecnicoActivo(id, activo)

    fun setTecnicoDisponible(id: Long, disponible: Boolean) = dbHelper.setTecnicoDisponible(id, disponible)
}
