package com.example.myapplication.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.data.repository.MockRepository
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
import com.example.myapplication.util.PasswordHasher

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "prototipo_acis.db"
        private const val DATABASE_VERSION = 7

        private const val TABLE_OT = "ots"
        private const val TABLE_CLIENT = "clients"
        private const val TABLE_LOCATION = "locations"
        private const val TABLE_EQUIPMENT = "equipment"
        private const val TABLE_MATERIAL = "materials"
        private const val TABLE_SYNC = "sync_items"
        private const val TABLE_USER = "users"
        private const val TABLE_EVIDENCE = "evidence"
        private const val TABLE_CHECKIN = "check_ins"
        private const val TABLE_EVENT = "ot_events"
        private const val TABLE_ACTIVITY = "ot_activities"
        private const val TABLE_CONFORMITY = "ot_conformities"
    }

    override fun onCreate(db: SQLiteDatabase) {
        createSchema(db)
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
        if (oldVersion < 5) {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS $TABLE_CHECKIN (
                    ot_id TEXT PRIMARY KEY,
                    latitude REAL NOT NULL,
                    longitude REAL NOT NULL,
                    accuracy REAL NOT NULL,
                    created_at INTEGER NOT NULL
                )
            """)
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE $TABLE_USER ADD COLUMN password_hash TEXT NOT NULL DEFAULT ''")
            db.execSQL("UPDATE $TABLE_USER SET password_hash = ?", arrayOf(PasswordHasher.hash("password123")))
        }
        if (oldVersion < 7) {
            migrateToV7(db)
        }
    }

    private fun createSchema(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_OT (
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
                exterior INTEGER,
                cliente_id INTEGER,
                local_id INTEGER,
                tecnico_id INTEGER,
                programada_para INTEGER,
                creada_en INTEGER NOT NULL DEFAULT 0,
                aceptada_en INTEGER,
                check_in_en INTEGER,
                check_out_en INTEGER,
                cerrada_en INTEGER,
                conformada_en INTEGER,
                diagnostico TEXT NOT NULL DEFAULT ''
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_CLIENT (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                contacto TEXT NOT NULL DEFAULT '',
                email TEXT NOT NULL DEFAULT '',
                telefono TEXT NOT NULL DEFAULT '',
                activo INTEGER NOT NULL DEFAULT 1
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_LOCATION (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_id INTEGER NOT NULL,
                nombre TEXT NOT NULL,
                direccion TEXT NOT NULL,
                comuna TEXT NOT NULL DEFAULT '',
                latitud REAL,
                longitud REAL,
                activo INTEGER NOT NULL DEFAULT 1
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_EQUIPMENT (
                id TEXT PRIMARY KEY,
                local_id INTEGER NOT NULL,
                nombre TEXT NOT NULL,
                tipo TEXT NOT NULL DEFAULT '',
                exterior INTEGER NOT NULL DEFAULT 0,
                ultima_mantencion TEXT NOT NULL DEFAULT '',
                activo INTEGER NOT NULL DEFAULT 1
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_MATERIAL (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ot_id TEXT,
                nombre TEXT,
                cantidad INTEGER,
                unidad TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_SYNC (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT,
                detalle TEXT,
                icono TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_USER (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                role TEXT NOT NULL,
                name TEXT NOT NULL,
                password_hash TEXT NOT NULL DEFAULT '',
                telefono TEXT NOT NULL DEFAULT '',
                disponible INTEGER NOT NULL DEFAULT 1,
                activo INTEGER NOT NULL DEFAULT 1,
                cliente_id INTEGER
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_EVIDENCE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ot_id TEXT NOT NULL,
                path TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                comentario TEXT NOT NULL DEFAULT ''
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_CHECKIN (
                ot_id TEXT PRIMARY KEY,
                latitude REAL,
                longitude REAL,
                accuracy REAL,
                created_at INTEGER NOT NULL,
                source TEXT NOT NULL DEFAULT 'none'
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_EVENT (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ot_id TEXT NOT NULL,
                tipo TEXT NOT NULL,
                actor TEXT NOT NULL DEFAULT '',
                detalle TEXT NOT NULL DEFAULT '',
                created_at INTEGER NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_ACTIVITY (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ot_id TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                completada INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_CONFORMITY (
                ot_id TEXT PRIMARY KEY,
                firmante TEXT NOT NULL,
                observacion TEXT NOT NULL DEFAULT '',
                conforme INTEGER NOT NULL,
                created_at INTEGER NOT NULL
            )
        """)

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_ots_tecnico ON $TABLE_OT(tecnico_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_events_ot ON $TABLE_EVENT(ot_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_activities_ot ON $TABLE_ACTIVITY(ot_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_materials_ot ON $TABLE_MATERIAL(ot_id)")
    }

    private fun migrateToV7(db: SQLiteDatabase) {
        addColumnIfMissing(db, TABLE_OT, "cliente_id", "INTEGER")
        addColumnIfMissing(db, TABLE_OT, "local_id", "INTEGER")
        addColumnIfMissing(db, TABLE_OT, "tecnico_id", "INTEGER")
        addColumnIfMissing(db, TABLE_OT, "programada_para", "INTEGER")
        addColumnIfMissing(db, TABLE_OT, "creada_en", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfMissing(db, TABLE_OT, "aceptada_en", "INTEGER")
        addColumnIfMissing(db, TABLE_OT, "check_in_en", "INTEGER")
        addColumnIfMissing(db, TABLE_OT, "check_out_en", "INTEGER")
        addColumnIfMissing(db, TABLE_OT, "cerrada_en", "INTEGER")
        addColumnIfMissing(db, TABLE_OT, "conformada_en", "INTEGER")
        addColumnIfMissing(db, TABLE_OT, "diagnostico", "TEXT NOT NULL DEFAULT ''")

        addColumnIfMissing(db, TABLE_CLIENT, "contacto", "TEXT NOT NULL DEFAULT ''")
        addColumnIfMissing(db, TABLE_CLIENT, "telefono", "TEXT NOT NULL DEFAULT ''")
        addColumnIfMissing(db, TABLE_CLIENT, "activo", "INTEGER NOT NULL DEFAULT 1")

        addColumnIfMissing(db, TABLE_EQUIPMENT, "local_id", "INTEGER NOT NULL DEFAULT 0")
        addColumnIfMissing(db, TABLE_EQUIPMENT, "tipo", "TEXT NOT NULL DEFAULT ''")
        addColumnIfMissing(db, TABLE_EQUIPMENT, "activo", "INTEGER NOT NULL DEFAULT 1")

        addColumnIfMissing(db, TABLE_USER, "telefono", "TEXT NOT NULL DEFAULT ''")
        addColumnIfMissing(db, TABLE_USER, "disponible", "INTEGER NOT NULL DEFAULT 1")
        addColumnIfMissing(db, TABLE_USER, "activo", "INTEGER NOT NULL DEFAULT 1")
        addColumnIfMissing(db, TABLE_USER, "cliente_id", "INTEGER")

        addColumnIfMissing(db, TABLE_EVIDENCE, "comentario", "TEXT NOT NULL DEFAULT ''")

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_LOCATION (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_id INTEGER NOT NULL,
                nombre TEXT NOT NULL,
                direccion TEXT NOT NULL,
                comuna TEXT NOT NULL DEFAULT '',
                latitud REAL,
                longitud REAL,
                activo INTEGER NOT NULL DEFAULT 1
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_EVENT (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ot_id TEXT NOT NULL,
                tipo TEXT NOT NULL,
                actor TEXT NOT NULL DEFAULT '',
                detalle TEXT NOT NULL DEFAULT '',
                created_at INTEGER NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_ACTIVITY (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                ot_id TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                completada INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL
            )
        """)
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS $TABLE_CONFORMITY (
                ot_id TEXT PRIMARY KEY,
                firmante TEXT NOT NULL,
                observacion TEXT NOT NULL DEFAULT '',
                conforme INTEGER NOT NULL,
                created_at INTEGER NOT NULL
            )
        """)

        migrateCheckIns(db)
        seedCatalogs(db, includeExistingUpdates = true)
        seedUsers(db, includeExistingUpdates = true)
        backfillSeedOts(db)
        seedEvents(db)
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_ots_tecnico ON $TABLE_OT(tecnico_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_events_ot ON $TABLE_EVENT(ot_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_activities_ot ON $TABLE_ACTIVITY(ot_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_materials_ot ON $TABLE_MATERIAL(ot_id)")
    }

    private fun addColumnIfMissing(db: SQLiteDatabase, table: String, column: String, definition: String) {
        val columns = mutableSetOf<String>()
        db.rawQuery("PRAGMA table_info($table)", null).use { cursor ->
            while (cursor.moveToNext()) {
                columns += cursor.getString(cursor.getColumnIndexOrThrow("name"))
            }
        }
        if (column !in columns) {
            db.execSQL("ALTER TABLE $table ADD COLUMN $column $definition")
        }
    }

    private fun migrateCheckIns(db: SQLiteDatabase) {
        val columns = mutableSetOf<String>()
        db.rawQuery("PRAGMA table_info($TABLE_CHECKIN)", null).use { cursor ->
            while (cursor.moveToNext()) {
                columns += cursor.getString(cursor.getColumnIndexOrThrow("name"))
            }
        }
        if ("source" in columns) return

        db.execSQL("ALTER TABLE $TABLE_CHECKIN RENAME TO ${TABLE_CHECKIN}_legacy")
        db.execSQL("""
            CREATE TABLE $TABLE_CHECKIN (
                ot_id TEXT PRIMARY KEY,
                latitude REAL,
                longitude REAL,
                accuracy REAL,
                created_at INTEGER NOT NULL,
                source TEXT NOT NULL DEFAULT 'none'
            )
        """)
        db.execSQL("""
            INSERT INTO $TABLE_CHECKIN (ot_id, latitude, longitude, accuracy, created_at, source)
            SELECT ot_id, latitude, longitude, accuracy, created_at, 'gps'
            FROM ${TABLE_CHECKIN}_legacy
        """)
        db.execSQL("DROP TABLE ${TABLE_CHECKIN}_legacy")
    }

    private fun seedData(db: SQLiteDatabase) {
        seedCatalogs(db)
        seedUsers(db)
        MockRepository.otList.forEach { insertOt(db, it) }
        MockRepository.otList.forEach { ot ->
            MockRepository.materiales.forEach { material ->
                insertMaterial(db, ot.id, material)
            }
        }
        seedEvents(db)
        MockRepository.syncItems.forEach { insertSyncItem(db, it) }
    }

    private fun seedCatalogs(db: SQLiteDatabase, includeExistingUpdates: Boolean = false) {
        MockRepository.clientes.forEach { cliente ->
            val values = clienteValues(cliente)
            if (includeExistingUpdates) {
                db.update(TABLE_CLIENT, values, "id = ?", arrayOf(cliente.id.toString()))
            }
            db.insertWithOnConflict(TABLE_CLIENT, null, values.apply { put("id", cliente.id) }, SQLiteDatabase.CONFLICT_IGNORE)
        }

        MockRepository.locales.forEach { local ->
            val values = localValues(local)
            if (includeExistingUpdates) {
                db.update(TABLE_LOCATION, values, "id = ?", arrayOf(local.id.toString()))
            }
            db.insertWithOnConflict(TABLE_LOCATION, null, values.apply { put("id", local.id) }, SQLiteDatabase.CONFLICT_IGNORE)
        }

        MockRepository.equipos.forEach { equipo ->
            val values = equipoValues(equipo)
            if (includeExistingUpdates) {
                db.update(TABLE_EQUIPMENT, values, "id = ?", arrayOf(equipo.id))
            }
            db.insertWithOnConflict(TABLE_EQUIPMENT, null, values.apply { put("id", equipo.id) }, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    private fun seedUsers(db: SQLiteDatabase, includeExistingUpdates: Boolean = false) {
        val passwordHash = PasswordHasher.hash("password123")
        val users = listOf(
            arrayOf("1", "cristian.rojas", "tecnico", "Cristian Rojas", "+56 9 8765 4321", "1", "1", ""),
            arrayOf("2", "gerardo.jefe", "jefe", "Gerardo Jefatura", "+56 9 8765 0000", "0", "1", ""),
            arrayOf("3", "maria.cliente", "cliente", "María Soto", "+56 9 8765 4321", "0", "1", "1"),
            arrayOf("4", "camila.vega", "tecnico", "Camila Vega", "+56 9 7654 3210", "1", "1", ""),
            arrayOf("5", "diego.herrera", "tecnico", "Diego Herrera", "+56 9 6543 2109", "1", "1", "")
        )
        users.forEach { row ->
            val values = ContentValues().apply {
                put("id", row[0].toLong())
                put("username", row[1])
                put("role", row[2])
                put("name", row[3])
                put("password_hash", passwordHash)
                put("telefono", row[4])
                put("disponible", row[5].toInt())
                put("activo", row[6].toInt())
                if (row[7].isNotBlank()) put("cliente_id", row[7].toLong())
            }
            if (includeExistingUpdates) {
                db.update(TABLE_USER, values, "id = ?", arrayOf(row[0]))
            }
            db.insertWithOnConflict(TABLE_USER, null, values, SQLiteDatabase.CONFLICT_IGNORE)
        }
    }

    private fun backfillSeedOts(db: SQLiteDatabase) {
        MockRepository.otList.forEach { ot ->
            val values = otValues(ot)
            db.update(TABLE_OT, values, "id = ?", arrayOf(ot.id))
        }
    }

    private fun seedEvents(db: SQLiteDatabase) {
        MockRepository.seedEvents.forEach { event ->
            insertEvent(db, event.otId, event.tipo, event.actor, event.detalle, event.createdAt)
        }
    }

    fun validateUser(username: String, role: String, password: String): Boolean {
        val cursor = readableDatabase.rawQuery(
            "SELECT 1 FROM $TABLE_USER WHERE username = ? AND role = ? AND password_hash = ? AND activo = 1",
            arrayOf(username, role, PasswordHasher.hash(password))
        )
        cursor.use {
            return it.moveToFirst()
        }
    }

    fun getUserSession(username: String, role: String): UserSession? {
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE username = ? AND role = ? AND activo = 1 LIMIT 1",
            arrayOf(username, role)
        )
        cursor.use {
            if (!it.moveToFirst()) return null
            val userId = it.getLong(it.getColumnIndexOrThrow("id"))
            return UserSession(
                id = userId,
                username = it.getString(it.getColumnIndexOrThrow("username")),
                nombre = it.getString(it.getColumnIndexOrThrow("name")),
                role = it.getString(it.getColumnIndexOrThrow("role")),
                tecnicoId = if (role == "tecnico") userId else null,
                clienteId = it.getNullableLong("cliente_id")
            )
        }
    }

    fun getAllOts(): List<OT> {
        val list = mutableListOf<OT>()
        val cursor = readableDatabase.rawQuery(
            "SELECT o.*, u.name AS tecnico_nombre FROM $TABLE_OT o LEFT JOIN $TABLE_USER u ON u.id = o.tecnico_id ORDER BY o.programada_para IS NULL, o.programada_para, o.creada_en",
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                list.add(it.toOt())
            }
        }
        return list
    }

    fun getOtById(id: String): OT? {
        val cursor = readableDatabase.rawQuery(
            "SELECT o.*, u.name AS tecnico_nombre FROM $TABLE_OT o LEFT JOIN $TABLE_USER u ON u.id = o.tecnico_id WHERE o.id = ?",
            arrayOf(id)
        )
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
        exterior = getInt(getColumnIndexOrThrow("exterior")) == 1,
        clienteId = getNullableLong("cliente_id"),
        localId = getNullableLong("local_id"),
        tecnicoId = getNullableLong("tecnico_id"),
        tecnicoNombre = getStringOrNull("tecnico_nombre"),
        programadaPara = getNullableLong("programada_para"),
        creadaEn = getLong(getColumnIndexOrThrow("creada_en")),
        aceptadaEn = getNullableLong("aceptada_en"),
        checkInEn = getNullableLong("check_in_en"),
        checkOutEn = getNullableLong("check_out_en"),
        cerradaEn = getNullableLong("cerrada_en"),
        conformadaEn = getNullableLong("conformada_en"),
        diagnostico = getString(getColumnIndexOrThrow("diagnostico"))
    )

    fun insertOt(ot: OT) {
        insertOt(writableDatabase, ot)
    }

    private fun insertOt(db: SQLiteDatabase, ot: OT) {
        db.insertWithOnConflict(TABLE_OT, null, otValues(ot).apply { put("id", ot.id) }, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateOt(ot: OT) {
        writableDatabase.update(TABLE_OT, otValues(ot), "id = ?", arrayOf(ot.id))
    }

    fun updateOtAssignment(id: String, tecnicoId: Long, programadaPara: Long?, estado: String) {
        val values = ContentValues().apply {
            put("tecnico_id", tecnicoId)
            if (programadaPara == null) putNull("programada_para") else put("programada_para", programadaPara)
            put("estado", estado)
            putNull("aceptada_en")
            putNull("check_in_en")
            putNull("check_out_en")
        }
        writableDatabase.update(TABLE_OT, values, "id = ?", arrayOf(id))
    }

    fun updateOtEstado(id: String, nuevoEstado: String) {
        val values = ContentValues().apply { put("estado", nuevoEstado) }
        writableDatabase.update(TABLE_OT, values, "id = ?", arrayOf(id))
    }

    fun updateOtAcceptance(id: String, timestamp: Long) {
        val values = ContentValues().apply {
            put("estado", "Aceptada")
            put("aceptada_en", timestamp)
        }
        writableDatabase.update(TABLE_OT, values, "id = ?", arrayOf(id))
    }

    fun updateOtRejection(id: String) {
        val values = ContentValues().apply {
            put("estado", "Pendiente")
            putNull("tecnico_id")
            putNull("programada_para")
            putNull("aceptada_en")
        }
        writableDatabase.update(TABLE_OT, values, "id = ?", arrayOf(id))
    }

    fun updateOtCheckIn(id: String, timestamp: Long) {
        val values = ContentValues().apply {
            put("estado", "En ejecución")
            put("check_in_en", timestamp)
        }
        writableDatabase.update(TABLE_OT, values, "id = ?", arrayOf(id))
    }

    fun updateOtDiagnostico(id: String, diagnostico: String) {
        val values = ContentValues().apply { put("diagnostico", diagnostico) }
        writableDatabase.update(TABLE_OT, values, "id = ?", arrayOf(id))
    }

    fun updateOtClosure(id: String, timestamp: Long) {
        val values = ContentValues().apply {
            put("estado", "Cerrada")
            put("check_out_en", timestamp)
            put("cerrada_en", timestamp)
        }
        writableDatabase.update(TABLE_OT, values, "id = ?", arrayOf(id))
    }

    fun updateOtConformityState(id: String, estado: String, timestamp: Long) {
        val values = ContentValues().apply {
            put("estado", estado)
            put("conformada_en", timestamp)
        }
        writableDatabase.update(TABLE_OT, values, "id = ?", arrayOf(id))
    }

    fun updateOtCancellation(id: String) {
        writableDatabase.update(TABLE_OT, ContentValues().apply { put("estado", "Cancelada") }, "id = ?", arrayOf(id))
    }

    fun deleteOt(id: String) {
        val db = writableDatabase
        db.delete(TABLE_EVIDENCE, "ot_id = ?", arrayOf(id))
        db.delete(TABLE_CHECKIN, "ot_id = ?", arrayOf(id))
        db.delete(TABLE_EVENT, "ot_id = ?", arrayOf(id))
        db.delete(TABLE_ACTIVITY, "ot_id = ?", arrayOf(id))
        db.delete(TABLE_CONFORMITY, "ot_id = ?", arrayOf(id))
        db.delete(TABLE_MATERIAL, "ot_id = ?", arrayOf(id))
        db.delete(TABLE_OT, "id = ?", arrayOf(id))
    }

    fun addSyncItem(tipo: String, detalle: String, icono: String) {
        insertSyncItem(writableDatabase, SyncItem(tipo, detalle, icono))
    }

    private fun insertSyncItem(db: SQLiteDatabase, sync: SyncItem) {
        val values = ContentValues().apply {
            put("tipo", sync.tipo)
            put("detalle", sync.detalle)
            put("icono", sync.icono)
        }
        db.insert(TABLE_SYNC, null, values)
    }

    fun getSyncItems(): List<SyncItem> {
        val list = mutableListOf<SyncItem>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_SYNC", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    SyncItem(
                        tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        detalle = cursor.getString(cursor.getColumnIndexOrThrow("detalle")),
                        icono = cursor.getString(cursor.getColumnIndexOrThrow("icono"))
                    )
                )
            }
        }
        return list
    }

    fun clearSyncItems() {
        writableDatabase.delete(TABLE_SYNC, null, null)
    }

    fun getEvidence(otId: String): List<Evidence> {
        val list = mutableListOf<Evidence>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_EVIDENCE WHERE ot_id = ? ORDER BY created_at ASC",
            arrayOf(otId)
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    Evidence(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        otId = cursor.getString(cursor.getColumnIndexOrThrow("ot_id")),
                        path = cursor.getString(cursor.getColumnIndexOrThrow("path")),
                        createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
                        comentario = cursor.getString(cursor.getColumnIndexOrThrow("comentario"))
                    )
                )
            }
        }
        return list
    }

    fun addEvidence(otId: String, path: String, createdAt: Long, comentario: String): Long {
        val values = ContentValues().apply {
            put("ot_id", otId)
            put("path", path)
            put("created_at", createdAt)
            put("comentario", comentario)
        }
        return writableDatabase.insert(TABLE_EVIDENCE, null, values)
    }

    fun updateEvidenceComment(id: Long, comentario: String) {
        writableDatabase.update(TABLE_EVIDENCE, ContentValues().apply { put("comentario", comentario) }, "id = ?", arrayOf(id.toString()))
    }

    fun deleteEvidence(id: Long) {
        writableDatabase.delete(TABLE_EVIDENCE, "id = ?", arrayOf(id.toString()))
    }

    fun getCheckIn(otId: String): CheckIn? {
        readableDatabase.rawQuery("SELECT * FROM $TABLE_CHECKIN WHERE ot_id = ?", arrayOf(otId)).use { cursor ->
            return if (cursor.moveToFirst()) {
                CheckIn(
                    otId = cursor.getString(cursor.getColumnIndexOrThrow("ot_id")),
                    latitude = cursor.getNullableDouble("latitude"),
                    longitude = cursor.getNullableDouble("longitude"),
                    accuracy = cursor.getNullableFloat("accuracy"),
                    createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at")),
                    source = cursor.getString(cursor.getColumnIndexOrThrow("source"))
                )
            } else {
                null
            }
        }
    }

    fun addCheckIn(otId: String, latitude: Double?, longitude: Double?, accuracy: Float?, createdAt: Long, source: String) {
        val values = ContentValues().apply {
            put("ot_id", otId)
            if (latitude == null) putNull("latitude") else put("latitude", latitude)
            if (longitude == null) putNull("longitude") else put("longitude", longitude)
            if (accuracy == null) putNull("accuracy") else put("accuracy", accuracy)
            put("created_at", createdAt)
            put("source", source)
        }
        writableDatabase.insertWithOnConflict(TABLE_CHECKIN, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getOtEvents(otId: String): List<OtEvento> {
        val list = mutableListOf<OtEvento>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_EVENT WHERE ot_id = ? ORDER BY created_at ASC, id ASC",
            arrayOf(otId)
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    OtEvento(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        otId = cursor.getString(cursor.getColumnIndexOrThrow("ot_id")),
                        tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        actor = cursor.getString(cursor.getColumnIndexOrThrow("actor")),
                        detalle = cursor.getString(cursor.getColumnIndexOrThrow("detalle")),
                        createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
        }
        return list
    }

    fun addOtEvent(otId: String, tipo: String, actor: String, detalle: String, createdAt: Long = System.currentTimeMillis()) {
        insertEvent(writableDatabase, otId, tipo, actor, detalle, createdAt)
    }

    private fun insertEvent(db: SQLiteDatabase, otId: String, tipo: String, actor: String, detalle: String, createdAt: Long) {
        val values = ContentValues().apply {
            put("ot_id", otId)
            put("tipo", tipo)
            put("actor", actor)
            put("detalle", detalle)
            put("created_at", createdAt)
        }
        db.insert(TABLE_EVENT, null, values)
    }

    fun getActivities(otId: String): List<ActividadOT> {
        val list = mutableListOf<ActividadOT>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_ACTIVITY WHERE ot_id = ? ORDER BY created_at ASC, id ASC",
            arrayOf(otId)
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    ActividadOT(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        otId = cursor.getString(cursor.getColumnIndexOrThrow("ot_id")),
                        descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                        completada = cursor.getInt(cursor.getColumnIndexOrThrow("completada")) == 1,
                        createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))
                    )
                )
            }
        }
        return list
    }

    fun addActivity(otId: String, descripcion: String): Long {
        val values = ContentValues().apply {
            put("ot_id", otId)
            put("descripcion", descripcion)
            put("completada", 0)
            put("created_at", System.currentTimeMillis())
        }
        return writableDatabase.insert(TABLE_ACTIVITY, null, values)
    }

    fun updateActivityCompleted(id: Long, completada: Boolean) {
        writableDatabase.update(TABLE_ACTIVITY, ContentValues().apply { put("completada", if (completada) 1 else 0) }, "id = ?", arrayOf(id.toString()))
    }

    fun deleteActivity(id: Long) {
        writableDatabase.delete(TABLE_ACTIVITY, "id = ?", arrayOf(id.toString()))
    }

    fun getMaterials(otId: String): List<Material> {
        val list = mutableListOf<Material>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_MATERIAL WHERE ot_id = ? ORDER BY id ASC",
            arrayOf(otId)
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    Material(
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad")),
                        unidad = cursor.getString(cursor.getColumnIndexOrThrow("unidad")),
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        otId = cursor.getString(cursor.getColumnIndexOrThrow("ot_id"))
                    )
                )
            }
        }
        return list
    }

    fun replaceMaterials(otId: String, materiales: List<Material>) {
        val db = writableDatabase
        db.delete(TABLE_MATERIAL, "ot_id = ?", arrayOf(otId))
        materiales.forEach { insertMaterial(db, otId, it) }
    }

    private fun insertMaterial(db: SQLiteDatabase, otId: String, material: Material) {
        val values = ContentValues().apply {
            put("ot_id", otId)
            put("nombre", material.nombre)
            put("cantidad", material.cantidad)
            put("unidad", material.unidad)
        }
        db.insert(TABLE_MATERIAL, null, values)
    }

    fun getConformity(otId: String): Conformidad? {
        readableDatabase.rawQuery("SELECT * FROM $TABLE_CONFORMITY WHERE ot_id = ?", arrayOf(otId)).use { cursor ->
            return if (cursor.moveToFirst()) {
                Conformidad(
                    otId = cursor.getString(cursor.getColumnIndexOrThrow("ot_id")),
                    firmante = cursor.getString(cursor.getColumnIndexOrThrow("firmante")),
                    observacion = cursor.getString(cursor.getColumnIndexOrThrow("observacion")),
                    conforme = cursor.getInt(cursor.getColumnIndexOrThrow("conforme")) == 1,
                    createdAt = cursor.getLong(cursor.getColumnIndexOrThrow("created_at"))
                )
            } else {
                null
            }
        }
    }

    fun saveConformity(conformidad: Conformidad) {
        val values = ContentValues().apply {
            put("ot_id", conformidad.otId)
            put("firmante", conformidad.firmante)
            put("observacion", conformidad.observacion)
            put("conforme", if (conformidad.conforme) 1 else 0)
            put("created_at", conformidad.createdAt)
        }
        writableDatabase.insertWithOnConflict(TABLE_CONFORMITY, null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun getClientes(): List<Cliente> {
        val list = mutableListOf<Cliente>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_CLIENT ORDER BY activo DESC, nombre ASC", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    Cliente(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        contacto = cursor.getString(cursor.getColumnIndexOrThrow("contacto")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                        telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
                    )
                )
            }
        }
        return list
    }

    fun insertCliente(cliente: Cliente): Long {
        val values = clienteValues(cliente)
        return writableDatabase.insert(TABLE_CLIENT, null, values)
    }

    fun updateCliente(cliente: Cliente) {
        writableDatabase.update(TABLE_CLIENT, clienteValues(cliente), "id = ?", arrayOf(cliente.id.toString()))
    }

    fun setClienteActivo(id: Long, activo: Boolean) {
        writableDatabase.update(TABLE_CLIENT, ContentValues().apply { put("activo", if (activo) 1 else 0) }, "id = ?", arrayOf(id.toString()))
    }

    fun getLocales(): List<LocalComercial> {
        val list = mutableListOf<LocalComercial>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_LOCATION ORDER BY activo DESC, nombre ASC", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    LocalComercial(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        clienteId = cursor.getLong(cursor.getColumnIndexOrThrow("cliente_id")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        direccion = cursor.getString(cursor.getColumnIndexOrThrow("direccion")),
                        comuna = cursor.getString(cursor.getColumnIndexOrThrow("comuna")),
                        latitud = cursor.getNullableDouble("latitud"),
                        longitud = cursor.getNullableDouble("longitud"),
                        activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
                    )
                )
            }
        }
        return list
    }

    fun insertLocal(local: LocalComercial): Long {
        val values = localValues(local)
        return writableDatabase.insert(TABLE_LOCATION, null, values)
    }

    fun updateLocal(local: LocalComercial) {
        writableDatabase.update(TABLE_LOCATION, localValues(local), "id = ?", arrayOf(local.id.toString()))
    }

    fun setLocalActivo(id: Long, activo: Boolean) {
        writableDatabase.update(TABLE_LOCATION, ContentValues().apply { put("activo", if (activo) 1 else 0) }, "id = ?", arrayOf(id.toString()))
    }

    fun getEquipos(): List<Equipo> {
        val list = mutableListOf<Equipo>()
        readableDatabase.rawQuery("SELECT * FROM $TABLE_EQUIPMENT ORDER BY activo DESC, nombre ASC", null).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    Equipo(
                        id = cursor.getString(cursor.getColumnIndexOrThrow("id")),
                        localId = cursor.getLong(cursor.getColumnIndexOrThrow("local_id")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")),
                        exterior = cursor.getInt(cursor.getColumnIndexOrThrow("exterior")) == 1,
                        ultimaMantencion = cursor.getString(cursor.getColumnIndexOrThrow("ultima_mantencion")),
                        activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
                    )
                )
            }
        }
        return list
    }

    fun insertEquipo(equipo: Equipo) {
        writableDatabase.insertWithOnConflict(TABLE_EQUIPMENT, null, equipoValues(equipo).apply { put("id", equipo.id) }, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun updateEquipo(equipo: Equipo) {
        writableDatabase.update(TABLE_EQUIPMENT, equipoValues(equipo), "id = ?", arrayOf(equipo.id))
    }

    fun setEquipoActivo(id: String, activo: Boolean) {
        writableDatabase.update(TABLE_EQUIPMENT, ContentValues().apply { put("activo", if (activo) 1 else 0) }, "id = ?", arrayOf(id))
    }

    fun getTecnicos(): List<Tecnico> {
        val list = mutableListOf<Tecnico>()
        readableDatabase.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE role = 'tecnico' ORDER BY activo DESC, disponible DESC, name ASC",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                list.add(
                    Tecnico(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        username = cursor.getString(cursor.getColumnIndexOrThrow("username")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        disponible = cursor.getInt(cursor.getColumnIndexOrThrow("disponible")) == 1,
                        activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1
                    )
                )
            }
        }
        return list
    }

    fun insertTecnico(username: String, nombre: String, telefono: String, password: String, disponible: Boolean): Long {
        val values = ContentValues().apply {
            put("username", username)
            put("role", "tecnico")
            put("name", nombre)
            put("password_hash", PasswordHasher.hash(password))
            put("telefono", telefono)
            put("disponible", if (disponible) 1 else 0)
            put("activo", 1)
        }
        return writableDatabase.insert(TABLE_USER, null, values)
    }

    fun updateTecnico(tecnico: Tecnico, password: String = "") {
        val values = ContentValues().apply {
            put("username", tecnico.username)
            put("name", tecnico.nombre)
            put("telefono", tecnico.telefono)
            put("disponible", if (tecnico.disponible) 1 else 0)
            put("activo", if (tecnico.activo) 1 else 0)
            if (password.isNotBlank()) put("password_hash", PasswordHasher.hash(password))
        }
        writableDatabase.update(TABLE_USER, values, "id = ?", arrayOf(tecnico.id.toString()))
    }

    fun setTecnicoActivo(id: Long, activo: Boolean) {
        writableDatabase.update(TABLE_USER, ContentValues().apply { put("activo", if (activo) 1 else 0) }, "id = ?", arrayOf(id.toString()))
    }

    fun setTecnicoDisponible(id: Long, disponible: Boolean) {
        writableDatabase.update(TABLE_USER, ContentValues().apply { put("disponible", if (disponible) 1 else 0) }, "id = ?", arrayOf(id.toString()))
    }

    private fun clienteValues(cliente: Cliente) = ContentValues().apply {
        put("nombre", cliente.nombre)
        put("contacto", cliente.contacto)
        put("email", cliente.email)
        put("telefono", cliente.telefono)
        put("activo", if (cliente.activo) 1 else 0)
    }

    private fun localValues(local: LocalComercial) = ContentValues().apply {
        put("cliente_id", local.clienteId)
        put("nombre", local.nombre)
        put("direccion", local.direccion)
        put("comuna", local.comuna)
        if (local.latitud == null) putNull("latitud") else put("latitud", local.latitud)
        if (local.longitud == null) putNull("longitud") else put("longitud", local.longitud)
        put("activo", if (local.activo) 1 else 0)
    }

    private fun equipoValues(equipo: Equipo) = ContentValues().apply {
        put("local_id", equipo.localId)
        put("nombre", equipo.nombre)
        put("tipo", equipo.tipo)
        put("exterior", if (equipo.exterior) 1 else 0)
        put("ultima_mantencion", equipo.ultimaMantencion)
        put("activo", if (equipo.activo) 1 else 0)
    }

    private fun otValues(ot: OT) = ContentValues().apply {
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
        if (ot.clienteId == null) putNull("cliente_id") else put("cliente_id", ot.clienteId)
        if (ot.localId == null) putNull("local_id") else put("local_id", ot.localId)
        if (ot.tecnicoId == null) putNull("tecnico_id") else put("tecnico_id", ot.tecnicoId)
        if (ot.programadaPara == null) putNull("programada_para") else put("programada_para", ot.programadaPara)
        put("creada_en", ot.creadaEn)
        if (ot.aceptadaEn == null) putNull("aceptada_en") else put("aceptada_en", ot.aceptadaEn)
        if (ot.checkInEn == null) putNull("check_in_en") else put("check_in_en", ot.checkInEn)
        if (ot.checkOutEn == null) putNull("check_out_en") else put("check_out_en", ot.checkOutEn)
        if (ot.cerradaEn == null) putNull("cerrada_en") else put("cerrada_en", ot.cerradaEn)
        if (ot.conformadaEn == null) putNull("conformada_en") else put("conformada_en", ot.conformadaEn)
        put("diagnostico", ot.diagnostico)
    }

    private fun Cursor.getNullableLong(column: String): Long? {
        val index = getColumnIndex(column)
        return if (index >= 0 && !isNull(index)) getLong(index) else null
    }

    private fun Cursor.getNullableDouble(column: String): Double? {
        val index = getColumnIndex(column)
        return if (index >= 0 && !isNull(index)) getDouble(index) else null
    }

    private fun Cursor.getNullableFloat(column: String): Float? {
        val index = getColumnIndex(column)
        return if (index >= 0 && !isNull(index)) getFloat(index) else null
    }

    private fun Cursor.getStringOrNull(column: String): String? {
        val index = getColumnIndex(column)
        return if (index >= 0 && !isNull(index)) getString(index) else null
    }
}
