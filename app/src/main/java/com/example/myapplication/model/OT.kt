package com.example.myapplication.model

data class OT(
    val id: String,
    val cliente: String,
    val local: String,
    val direccion: String,
    val equipo: String,
    val equipoId: String,
    val tipo: String,
    val prioridad: String,
    val estado: String,
    val sla: String,
    val distancia: String,
    val descripcion: String,
    val exterior: Boolean,
    val clienteId: Long? = null,
    val localId: Long? = null,
    val tecnicoId: Long? = null,
    val tecnicoNombre: String? = null,
    val programadaPara: Long? = null,
    val creadaEn: Long = 0L,
    val aceptadaEn: Long? = null,
    val checkInEn: Long? = null,
    val checkOutEn: Long? = null,
    val cerradaEn: Long? = null,
    val conformadaEn: Long? = null,
    val diagnostico: String = ""
)

data class Material(
    val nombre: String,
    val cantidad: Int,
    val unidad: String,
    val id: Long = 0L,
    val otId: String = ""
)

data class SyncItem(
    val tipo: String,
    val detalle: String,
    val icono: String
)

data class Evidence(
    val id: Long,
    val otId: String,
    val path: String,
    val createdAt: Long,
    val comentario: String = ""
)

data class CheckIn(
    val otId: String,
    val latitude: Double?,
    val longitude: Double?,
    val accuracy: Float?,
    val createdAt: Long,
    val source: String = "gps"
)

data class TimelineItem(
    val hito: String,
    val fecha: String,
    val hecho: Boolean
)

data class ClienteInfo(
    val nombre: String,
    val cargo: String,
    val local: String,
    val email: String
)

data class TecnicoInfo(
    val nombre: String,
    val rol: String,
    val iniciales: String,
    val telefono: String
)

data class Cliente(
    val id: Long,
    val nombre: String,
    val contacto: String,
    val email: String,
    val telefono: String,
    val activo: Boolean = true
)

data class LocalComercial(
    val id: Long,
    val clienteId: Long,
    val nombre: String,
    val direccion: String,
    val comuna: String,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val activo: Boolean = true
)

data class Equipo(
    val id: String,
    val localId: Long,
    val nombre: String,
    val tipo: String,
    val exterior: Boolean,
    val ultimaMantencion: String,
    val activo: Boolean = true
)

data class Tecnico(
    val id: Long,
    val username: String,
    val nombre: String,
    val telefono: String,
    val disponible: Boolean,
    val activo: Boolean = true
)

data class OtEvento(
    val id: Long,
    val otId: String,
    val tipo: String,
    val actor: String,
    val detalle: String,
    val createdAt: Long
)

data class ActividadOT(
    val id: Long,
    val otId: String,
    val descripcion: String,
    val completada: Boolean,
    val createdAt: Long
)

data class Conformidad(
    val otId: String,
    val firmante: String,
    val observacion: String,
    val conforme: Boolean,
    val createdAt: Long
)

data class UserSession(
    val id: Long,
    val username: String,
    val nombre: String,
    val role: String,
    val tecnicoId: Long?,
    val clienteId: Long?
)
