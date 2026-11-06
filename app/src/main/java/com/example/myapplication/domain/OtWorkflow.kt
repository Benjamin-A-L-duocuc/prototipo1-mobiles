package com.example.myapplication.domain

import com.example.myapplication.model.OT

object OtWorkflow {
    const val PENDIENTE = "Pendiente"
    const val ASIGNADA = "Asignada"
    const val ACEPTADA = "Aceptada"
    const val EN_EJECUCION = "En ejecución"
    const val CERRADA = "Cerrada"
    const val CONFORMADA = "Conformada"
    const val RECHAZADA_CLIENTE = "Rechazada por cliente"
    const val CANCELADA = "Cancelada"

    val estados = setOf(
        PENDIENTE,
        ASIGNADA,
        ACEPTADA,
        EN_EJECUCION,
        CERRADA,
        CONFORMADA,
        RECHAZADA_CLIENTE,
        CANCELADA
    )

    fun esFinal(estado: String): Boolean =
        estado == CONFORMADA || estado == RECHAZADA_CLIENTE || estado == CANCELADA

    fun estaCerrada(estado: String): Boolean =
        estado == CERRADA || estado == CONFORMADA || estado == RECHAZADA_CLIENTE

    fun puedeAsignar(ot: OT): Boolean =
        ot.estado == PENDIENTE || ot.estado == ASIGNADA

    fun puedeReasignar(ot: OT): Boolean =
        !estaCerrada(ot.estado) && ot.estado != CANCELADA

    fun puedeAceptar(ot: OT, tecnicoId: Long?): Boolean =
        tecnicoId != null && ot.tecnicoId == tecnicoId && ot.estado == ASIGNADA

    fun puedeRechazar(ot: OT, tecnicoId: Long?): Boolean =
        tecnicoId != null && ot.tecnicoId == tecnicoId && ot.estado == ASIGNADA

    fun puedeCheckIn(ot: OT, tecnicoId: Long?): Boolean =
        tecnicoId != null && ot.tecnicoId == tecnicoId && ot.estado == ACEPTADA

    fun puedeEjecutar(ot: OT, tecnicoId: Long?): Boolean =
        tecnicoId != null && ot.tecnicoId == tecnicoId && ot.estado == EN_EJECUCION

    fun puedeCerrar(ot: OT, tecnicoId: Long?): Boolean =
        puedeEjecutar(ot, tecnicoId)

    fun puedeConformar(ot: OT): Boolean =
        ot.estado == CERRADA

    fun puedeCancelar(ot: OT): Boolean =
        ot.estado != CERRADA && ot.estado != CONFORMADA &&
            ot.estado != RECHAZADA_CLIENTE && ot.estado != CANCELADA

    fun siguientePaso(ot: OT): String = when (ot.estado) {
        PENDIENTE -> "Asignar técnico"
        ASIGNADA -> "Aceptar OT"
        ACEPTADA -> "Registrar check-in"
        EN_EJECUCION -> if (ot.diagnostico.isBlank()) "Registrar diagnóstico" else "Continuar ejecución"
        CERRADA -> "Esperando conformidad del cliente"
        CONFORMADA -> "OT conformada"
        RECHAZADA_CLIENTE -> "OT rechazada por el cliente"
        CANCELADA -> "OT cancelada"
        else -> "Revisar OT"
    }
}
