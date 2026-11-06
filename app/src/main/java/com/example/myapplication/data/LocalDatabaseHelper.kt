package com.example.myapplication.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.data.repository.MockRepository
import com.example.myapplication.model.Evidence
import com.example.myapplication.model.OT
import com.example.myapplication.model.SyncItem

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "prototipo_acis.db"
        private const val DATABASE_VERSION = 4

        // Tables
        private const val TABLE_OT = "ots"
        private const val TABLE_CLIENT = "clients"
        private const val TABLE_EQUIPMENT = "equipment"
        private const val TABLE_MATERIAL = "materials"
        private const val TABLE_SYNC = "sync_items"
        private const val TABLE_USER = "users"
        private const val TABLE_EVIDENCE = "evidence"
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

        // 7. Evidence Table (EP-02)
        db.execSQL("""
            CREATE TABLE $TABLE_EVIDENCE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ot_id TEXT NOT NULL,
                path TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
        """)

        // Seed initial data
        seedData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 4) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_EVIDENCE (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    ot_id TEXT NOT NULL,
                    path TEXT NOT NULL,
                    created_at INTEGER NOT NULL
                )
            """)
        }
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
        val cursor = readableDatabase.rawQuery("SELECT * FROM $TABLE_OT", null)
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.toOt())
            }
        }
        return list
    }

    fun getOtById(id: String): OT? {
        val cursor = readableDatabase.rawQuery("SELECT * FROM $TABLE_OT WHERE id = ?", arrayOf(id))
        cursor.use {
            return if (it.moveToFirst()) it.toOt() else null
        }
    }

    private fun Cursor.toOt(): OT = OT(
        id = getString(getColumnIndexOrThrow("id")),
        cliente = getString(getColumnIndexOrThrow("cliente")),
        local = getString(getColumnIndexOrThrow("local")),
        direccion = getString(getColumnIndexOrThrow("direccion")),
        equipo = getString(getColumnIndexOrThrow("equipo")),
        equipoId = getString(getColumnIndexOrThrow("equipoId")),
        tipo = getString(getColumnIndexOrThrow("tipo")),
        prioridad = getString(getColumnIndexOrThrow("prioridad")),
        estado = getString(getColumnIndexOrThrow("estado")),
        sla = getString(getColumnIndexOrThrow("sla")),
        distancia = getString(getColumnIndexOrThrow("distancia")),
        descripcion = getString(getColumnIndexOrThrow("descripcion")),
        exterior = getInt(getColumnIndexOrThrow("exterior")) == 1
    )

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

    fun updateOt(ot: OT) {
        val db = writableDatabase
        val values = ContentValues().apply {
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
        db.update(TABLE_OT, values, "id = ?", arrayOf(ot.id))
    }

    fun deleteOt(id: String) {
        val db = writableDatabase
        db.delete(TABLE_EVIDENCE, "ot_id = ?", arrayOf(id))
        db.delete(TABLE_OT, "id = ?", arrayOf(id))
    }

    fun updateOtEstado(id: String, nuevoEstado: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("estado", nuevoEstado)
        }
        db.update(TABLE_OT, values, "id = ?", arrayOf(id))
    }

    fun addSyncItem(tipo: String, detalle: String, icono: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("tipo", tipo)
            put("detalle", detalle)
            put("icono", icono)
        }
        db.insert(TABLE_SYNC, null, values)
    }

    fun getSyncItems(): List<SyncItem> {
        val list = mutableListOf<SyncItem>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SYNC", null)
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(
                        SyncItem(
                            tipo = it.getString(it.getColumnIndexOrThrow("tipo")),
                            detalle = it.getString(it.getColumnIndexOrThrow("detalle")),
                            icono = it.getString(it.getColumnIndexOrThrow("icono"))
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun clearSyncItems() {
        val db = writableDatabase
        db.delete(TABLE_SYNC, null, null)
    }

    fun getEvidence(otId: String): List<Evidence> {
        val list = mutableListOf<Evidence>()
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_EVIDENCE WHERE ot_id = ? ORDER BY created_at ASC",
            arrayOf(otId)
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(
                    Evidence(
                        id = it.getLong(it.getColumnIndexOrThrow("id")),
                        otId = it.getString(it.getColumnIndexOrThrow("ot_id")),
                        path = it.getString(it.getColumnIndexOrThrow("path")),
                        createdAt = it.getLong(it.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
        }
        return list
    }

    fun addEvidence(otId: String, path: String, createdAt: Long): Long {
        val values = ContentValues().apply {
            put("ot_id", otId)
            put("path", path)
            put("created_at", createdAt)
        }
        return writableDatabase.insert(TABLE_EVIDENCE, null, values)
    }

    fun deleteEvidence(id: Long) {
        writableDatabase.delete(TABLE_EVIDENCE, "id = ?", arrayOf(id.toString()))
    }
}
