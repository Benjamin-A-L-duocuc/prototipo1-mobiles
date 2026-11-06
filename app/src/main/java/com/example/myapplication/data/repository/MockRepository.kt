package com.example.myapplication.data.repository

import com.example.myapplication.model.*
import java.util.Calendar

object MockRepository {
    private fun todayAt(hour: Int, minute: Int): Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private val todayStart: Long =
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    val clientes = listOf(
        Cliente(1L, "Supermercado Ñuñoa", "María Soto", "maria.soto@supernunoa.cl", "+56 9 8765 4321"),
        Cliente(2L, "Retail Macul", "Jorge Fuentes", "jorge.fuentes@retailmacul.cl", "+56 9 7654 3210"),
        Cliente(3L, "Mall Plaza Oeste", "Camila Ríos", "camila.rios@plaza.cl", "+56 9 6543 2109"),
        Cliente(4L, "Supermercado La Reina", "Andrés Silva", "andres.silva@lareina.cl", "+56 9 5432 1098")
    )

    val locales = listOf(
        LocalComercial(1L, 1L, "Local Ñuñoa", "Av. Irarrázaval 3400", "Ñuñoa", -33.4548, -70.6104),
        LocalComercial(2L, 2L, "Local Macul", "Av. Macul 4500", "Macul", -33.4829, -70.5994),
        LocalComercial(3L, 3L, "Patio de comidas", "Av. Vespucio 1200", "Pudahuel", -33.4458, -70.7467),
        LocalComercial(4L, 4L, "Local La Reina", "Av. Ossa 900", "La Reina", -33.4489, -70.5594)
    )

    val equipos = listOf(
        Equipo("EQ-221", 1L, "Cámara frigorífica 4 puertas", "Refrigeración", false, "15/09/2026"),
        Equipo("EQ-118", 2L, "Condensadora en azotea", "Climatización", true, "01/10/2026"),
        Equipo("EQ-305", 3L, "Chiller de agua helada", "Climatización", false, "22/09/2026"),
        Equipo("EQ-402", 4L, "Unidad exterior split", "Climatización", true, "29/09/2026")
    )

    val tecnicos = listOf(
        Tecnico(1L, "cristian.rojas", "Cristian Rojas", "+56 9 8765 4321", true),
        Tecnico(4L, "camila.vega", "Camila Vega", "+56 9 7654 3210", true),
        Tecnico(5L, "diego.herrera", "Diego Herrera", "+56 9 6543 2109", true)
    )

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
        exterior = false,
        clienteId = 1L,
        localId = 1L,
        tecnicoId = 1L,
        tecnicoNombre = "Cristian Rojas",
        programadaPara = todayAt(9, 0),
        creadaEn = todayAt(8, 10)
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
            exterior = true,
            clienteId = 2L,
            localId = 2L,
            tecnicoId = 1L,
            tecnicoNombre = "Cristian Rojas",
            programadaPara = todayAt(11, 30),
            creadaEn = todayAt(8, 20)
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
            estado = "Aceptada",
            sla = "0h 45m",
            distancia = "8,1 km",
            descripcion = "Baja presión en circuito de agua helada. Revisar bomba y niveles.",
            exterior = false,
            clienteId = 3L,
            localId = 3L,
            tecnicoId = 4L,
            tecnicoNombre = "Camila Vega",
            programadaPara = todayAt(14, 0),
            creadaEn = todayAt(8, 30),
            aceptadaEn = todayAt(8, 42)
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
            estado = "Pendiente",
            sla = "6h",
            distancia = "6,4 km",
            descripcion = "Limpieza de unidad exterior y revisión de soportes.",
            exterior = true,
            clienteId = 4L,
            localId = 4L,
            creadaEn = todayAt(8, 45)
        )
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

    val seedEvents = listOf(
        OtEvento(0L, "OT-1042", "Creación", "gerardo.jefe", "OT creada", todayStart + 8 * 60 * 60 * 1000 + 10 * 60 * 1000),
        OtEvento(0L, "OT-1042", "Asignación", "gerardo.jefe", "Asignada a Cristian Rojas", todayStart + 8 * 60 * 60 * 1000 + 25 * 60 * 1000),
        OtEvento(0L, "OT-1043", "Creación", "gerardo.jefe", "OT creada", todayStart + 8 * 60 * 60 * 1000 + 20 * 60 * 1000),
        OtEvento(0L, "OT-1043", "Asignación", "gerardo.jefe", "Asignada a Cristian Rojas", todayStart + 8 * 60 * 60 * 1000 + 35 * 60 * 1000),
        OtEvento(0L, "OT-1044", "Creación", "gerardo.jefe", "OT creada", todayStart + 8 * 60 * 60 * 1000 + 30 * 60 * 1000),
        OtEvento(0L, "OT-1044", "Asignación", "gerardo.jefe", "Asignada a Camila Vega", todayStart + 8 * 60 * 60 * 1000 + 40 * 60 * 1000),
        OtEvento(0L, "OT-1044", "Aceptación", "camila.vega", "OT aceptada", todayStart + 8 * 60 * 60 * 1000 + 42 * 60 * 1000),
        OtEvento(0L, "OT-1045", "Creación", "gerardo.jefe", "OT creada sin asignar", todayStart + 8 * 60 * 60 * 1000 + 45 * 60 * 1000)
    )
}
