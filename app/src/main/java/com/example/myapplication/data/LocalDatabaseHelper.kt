package com.example.myapplication.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.model.*

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "prototipo_acis.db"
        private const val DATABASE_VERSION = 2

        // Tables
        private const val TABLE_OT = "ots"
        private const val TABLE_CLIENT = "clients"
        private const val TABLE_EQUIPMENT = "equipment"
        private const val TABLE_MATERIAL = "materials"
        private const val TABLE_SYNC = "sync_items"
        private const val TABLE_USER = "users"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 1. OTs Table
        db.execSQL("""
            CREATE TABLE $TABLE_OT (
                id TEXT PRIMARY KEY,
                cliente TEXT,
                local TEXT,
                direccion TEXT,
                equipo TEXT,
                equipoId TEXT,
                tipo TEXT,
                prioridad TEXT,
                estado TEXT,
                sla TEXT,
                distancia TEXT,
                descripcion TEXT,
                exterior INTEGER
            )
        """)

        // 2. Clients Table (EP-05 Catastro)
        db.execSQL("""
            CREATE TABLE $TABLE_CLIENT (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                cargo TEXT,
                local TEXT,
                email TEXT
            )
        """)

        // 3. Equipment Table (EP-05 Catastro & Ubicación)
        db.execSQL("""
            CREATE TABLE $TABLE_EQUIPMENT (
                id TEXT PRIMARY KEY,
                nombre TEXT,
                local TEXT,
                ubicacion TEXT,
                exterior INTEGER,
                ultima_mantencion TEXT
            )
        """)

        // 4. Materials Table (EP-02)
        db.execSQL("""
            CREATE TABLE $TABLE_MATERIAL (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ot_id TEXT,
                nombre TEXT,
                cantidad INTEGER,
                unidad TEXT
            )
        """)

        // 5. Sync Queue Table (EP-06 Offline)
        db.execSQL("""
            CREATE TABLE $TABLE_SYNC (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT,
                detalle TEXT,
                icono TEXT
            )
        """)

        // 6. Users Table (EP-09 Seguridad)
        db.execSQL("""
            CREATE TABLE $TABLE_USER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT,
                role TEXT,
                name TEXT
            )
        """)

        // Seed initial data
        seedData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EQUIPMENT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MATERIAL")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SYNC")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    private fun seedData(db: SQLiteDatabase) {
        // Seed OTs
        for (ot in MockRepository.otList) {
            val values = ContentValues().apply {
                put("id", ot.id)
                put("cliente", ot.cliente)
                put("local", ot.local)
                put("direccion", ot.direccion)
                put("equipo", ot.equipo)
                put("equipoId", ot.equipoId)
                put("tipo", ot.tipo)
                put("prioridad", ot.prioridad)
                put("estado", ot.estado)
                put("sla", ot.sla)
                put("distancia", ot.distancia)
                put("descripcion", ot.descripcion)
                put("exterior", if (ot.exterior) 1 else 0)
            }
            db.insertWithOnConflict(TABLE_OT, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        }

        // Seed Client
        val clientVal = ContentValues().apply {
            put("nombre", MockRepository.cliente.nombre)
            put("cargo", MockRepository.cliente.cargo)
            put("local", MockRepository.cliente.local)
            put("email", MockRepository.cliente.email)
        }
        db.insert(TABLE_CLIENT, null, clientVal)

        // Seed Equipment
        val eqVal1 = ContentValues().apply {
            put("id", "EQ-221")
            put("nombre", "Cámara frigorífica 4 puertas")
            put("local", "Local Ñuñoa")
            put("ubicacion", "Interior - Sala de Frío")
            put("exterior", 0)
            put("ultima_mantencion", "15/09/2026")
        }
        db.insertWithOnConflict(TABLE_EQUIPMENT, null, eqVal1, SQLiteDatabase.CONFLICT_REPLACE)

        val eqVal2 = ContentValues().apply {
            put("id", "EQ-118")
            put("nombre", "Condensadora en azotea")
            put("local", "Local Macul")
            put("ubicacion", "Azotea sector norte")
            put("exterior", 1)
            put("ultima_mantencion", "01/10/2026")
        }
        db.insertWithOnConflict(TABLE_EQUIPMENT, null, eqVal2, SQLiteDatabase.CONFLICT_REPLACE)

        // Seed Materials
        for (mat in MockRepository.materiales) {
            val matVal = ContentValues().apply {
                put("ot_id", "OT-1042")
                put("nombre", mat.nombre)
                put("cantidad", mat.cantidad)
                put("unidad", mat.unidad)
            }
            db.insert(TABLE_MATERIAL, null, matVal)
        }

        // Seed Sync Items
        for (sync in MockRepository.syncItems) {
            val syncVal = ContentValues().apply {
                put("tipo", sync.tipo)
                put("detalle", sync.detalle)
                put("icono", sync.icono)
            }
            db.insert(TABLE_SYNC, null, syncVal)
        }

        // Seed Users
        val user1 = ContentValues().apply { put("username", "cristian.rojas"); put("role", "tecnico"); put("name", "Cristian Rojas") }
        val user2 = ContentValues().apply { put("username", "gerardo.jefe"); put("role", "jefe"); put("name", "Gerardo Jefatura") }
        val user3 = ContentValues().apply { put("username", "maria.cliente"); put("role", "cliente"); put("name", "María Soto") }
        db.insert(TABLE_USER, null, user1)
        db.insert(TABLE_USER, null, user2)
        db.insert(TABLE_USER, null, user3)
    }

    fun getAllOts(): List<OT> {
        val list = mutableListOf<OT>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_OT", null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        OT(
                            id = it.getString(it.getColumnIndexOrThrow("id")),
                            cliente = it.getString(it.getColumnIndexOrThrow("cliente")),
                            local = it.getString(it.getColumnIndexOrThrow("local")),
                            direccion = it.getString(it.getColumnIndexOrThrow("direccion")),
                            equipo = it.getString(it.getColumnIndexOrThrow("equipo")),
                            equipoId = it.getString(it.getColumnIndexOrThrow("equipoId")),
                            tipo = it.getString(it.getColumnIndexOrThrow("tipo")),
                            prioridad = it.getString(it.getColumnIndexOrThrow("prioridad")),
                            estado = it.getString(it.getColumnIndexOrThrow("estado")),
                            sla = it.getString(it.getColumnIndexOrThrow("sla")),
                            distancia = it.getString(it.getColumnIndexOrThrow("distancia")),
                            descripcion = it.getString(it.getColumnIndexOrThrow("descripcion")),
                            exterior = it.getInt(it.getColumnIndexOrThrow("exterior")) == 1
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun insertOt(ot: OT) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", ot.id)
            put("cliente", ot.cliente)
            put("local", ot.local)
            put("direccion", ot.direccion)
            put("equipo", ot.equipo)
            put("equipoId", ot.equipoId)
            put("tipo", ot.tipo)
            put("prioridad", ot.prioridad)
            put("estado", ot.estado)
            put("sla", ot.sla)
            put("distancia", ot.distancia)
            put("descripcion", ot.descripcion)
            put("exterior", if (ot.exterior) 1 else 0)
        }
        db.insertWithOnConflict(TABLE_OT, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateOtEstado(id: String, nuevoEstado: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("estado", nuevoEstado)
        }
        db.update(TABLE_OT, values, "id = ?", arrayOf(id))
    }

    // Additional methods for Equipment and Clients catalog (EP-05)
    fun getAllEquipment(): List<Map<String, Any>> {
        val list = mutableListOf<Map<String, Any>>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_EQUIPMENT", null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        mapOf(
                            "id" to it.getString(it.getColumnIndexOrThrow("id")),
                            "nombre" to it.getString(it.getColumnIndexOrThrow("nombre")),
                            "local" to it.getString(it.getColumnIndexOrThrow("local")),
                            "ubicacion" to it.getString(it.getColumnIndexOrThrow("ubicacion")),
                            "exterior" to (it.getInt(it.getColumnIndexOrThrow("exterior")) == 1),
                            "ultima_mantencion" to it.getString(it.getColumnIndexOrThrow("ultima_mantencion"))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun addEquipment(id: String, nombre: String, local: String, ubicacion: String, exterior: Boolean, ultimaMantencion: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", id)
            put("nombre", nombre)
            put("local", local)
            put("ubicacion", ubicacion)
            put("exterior", if (exterior) 1 else 0)
            put("ultima_mantencion", ultimaMantencion)
        }
        db.insertWithOnConflict(TABLE_EQUIPMENT, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }
}
