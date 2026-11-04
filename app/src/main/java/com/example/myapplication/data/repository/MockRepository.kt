package com.example.myapplication.data.repository

import com.example.myapplication.model.*

object MockRepository {
    val defaultOT = OT(
        id = "OT-1042",
        cliente = "Supermercado Ñuñoa",
        local = "Local Ñuñoa",
        direccion = "Av. Irarrázaval 3400, Ñuñoa",
        equipo = "Cámara frigorífica 4 puertas",
        equipoId = "EQ-221",
        tipo = "Correctivo",
        prioridad = "Alta",
        estado = "Asignada",
        sla = "1h 20m",
        distancia = "3,2 km",
        descripcion = "Cliente reporta alza de temperatura en cámara de lácteos. Revisar condensadora, circuito de refrigerante y estado del evaporador.",
        exterior = false
    )

    val otList = listOf(
        defaultOT,
        OT(
            id = "OT-1043",
            cliente = "Retail Macul",
            local = "Local Macul",
            direccion = "Av. Macul 4500, Macul",
            equipo = "Condensadora en azotea",
            equipoId = "EQ-118",
            tipo = "Preventivo",
            prioridad = "Media",
            estado = "Asignada",
            sla = "4h 10m",
            distancia = "5,8 km",
            descripcion = "Mantención preventiva de condensadora ubicada en azotea. Revisar soportes y limpieza de serpentín.",
            exterior = true
        ),
        OT(
            id = "OT-1044",
            cliente = "Mall Plaza Oeste",
            local = "Patio de comidas",
            direccion = "Av. Vespucio 1200, Pudahuel",
            equipo = "Chiller de agua helada",
            equipoId = "EQ-305",
            tipo = "Correctivo",
            prioridad = "Alta",
            estado = "En ejecución",
            sla = "0h 45m",
            distancia = "8,1 km",
            descripcion = "Baja presión en circuito de agua helada. Revisar bomba y niveles.",
            exterior = false
        ),
        OT(
            id = "OT-1045",
            cliente = "Supermercado La Reina",
            local = "Local La Reina",
            direccion = "Av. Ossa 900, La Reina",
            equipo = "Unidad exterior split",
            equipoId = "EQ-402",
            tipo = "Preventivo",
            prioridad = "Baja",
            estado = "Aceptada",
            sla = "6h",
            distancia = "6,4 km",
            descripcion = "Limpieza de unidad exterior y revisión de soportes.",
            exterior = true
        )
    )

    val agendaHoy = listOf(
        AgendaItem("09:00", "Cristian Rojas", "OT-1042", "Supermercado Ñuñoa", false, "Alta"),
        AgendaItem("11:30", "Paula Díaz", "OT-1043", "Retail Macul", true, "Media"),
        AgendaItem("14:00", "Diego Soto", "OT-1044", "Mall Plaza Oeste", false, "Alta"),
        AgendaItem("16:30", "Cristian Rojas", "OT-1045", "Supermercado La Reina", true, "Baja")
    )

    val actividades = listOf(
        "Revisar presión del circuito de refrigerante",
        "Limpiar serpentín del condensador",
        "Verificar funcionamiento del evaporador",
        "Registrar temperatura de salida"
    )

    val materiales = listOf(
        Material("Gas refrigerante R410A", 2, "kg"),
        Material("Filtro secador", 1, "un"),
        Material("Cinta aislante térmica", 2, "rollo")
    )

    val syncItems = listOf(
        SyncItem("Check-in", "09:41 · GPS capturado", "checkin"),
        SyncItem("Diagnóstico", "09:52 · Fuga en circuito", "diagnostico"),
        SyncItem("Actividades", "10:05 · 3 actividades", "actividades"),
        SyncItem("Evidencias", "10:20 · 2 fotografías", "evidencias")
    )

    val otPrioritarias = listOf(
        mapOf("id" to "OT-1042", "cliente" to "Supermercado Ñuñoa", "prioridad" to "Alta", "estado" to "Asignada", "sla" to "1h 20m"),
        mapOf("id" to "OT-1044", "cliente" to "Mall Plaza Oeste", "prioridad" to "Alta", "estado" to "En ejecución", "sla" to "0h 45m"),
        mapOf("id" to "OT-1043", "cliente" to "Retail Macul", "prioridad" to "Media", "estado" to "Asignada", "sla" to "4h 10m")
    )

    val cliente = ClienteInfo(
        nombre = "María Soto",
        cargo = "Administradora de local",
        local = "Supermercado Ñuñoa",
        email = "maria.soto@supernunoa.cl"
    )

    val tecnico = TecnicoInfo(
        nombre = "Cristian Rojas",
        rol = "Técnico de terreno",
        iniciales = "CR",
        telefono = "+56 9 8765 4321"
    )

    val timeline = listOf(
        TimelineItem("Creada", "27/10 · 08:10", true),
        TimelineItem("Asignada", "27/10 · 08:25", true),
        TimelineItem("Aceptada", "27/10 · 08:40", true),
        TimelineItem("En ejecución", "27/10 · 09:41", true),
        TimelineItem("Cerrada", "27/10 · 10:35", true),
        TimelineItem("Conformada", "Pendiente", false)
    )
}
