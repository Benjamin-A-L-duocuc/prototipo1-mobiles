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
    val exterior: Boolean
)

data class AgendaItem(
    val hora: String,
    val tecnico: String,
    val ot: String,
    val cliente: String,
    val exterior: Boolean,
    val prioridad: String
)

data class Material(
    val nombre: String,
    val cantidad: Int,
    val unidad: String
)

data class SyncItem(
    val tipo: String,
    val detalle: String,
    val icono: String
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
