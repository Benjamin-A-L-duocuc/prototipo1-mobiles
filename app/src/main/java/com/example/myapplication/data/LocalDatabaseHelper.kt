package com.example.myapplication.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.model.OT

class LocalDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "prototipo_ot.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_OT = "ots"
        private const val COL_ID = "id"
        private const val COL_CLIENTE = "cliente"
        private const val COL_LOCAL = "local"
        private const val COL_DIRECCION = "direccion"
        private const val COL_EQUIPO = "equipo"
        private const val COL_EQUIPO_ID = "equipoId"
        private const val COL_TIPO = "tipo"
        private const val COL_PRIORIDAD = "prioridad"
        private const val COL_ESTADO = "estado"
        private const val COL_SLA = "sla"
        private const val COL_DISTANCIA = "distancia"
        private const val COL_DESCRIPCION = "descripcion"
        private const val COL_EXTERIOR = "exterior"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_OT (" +
                "$COL_ID TEXT PRIMARY KEY, " +
                "$COL_CLIENTE TEXT, " +
                "$COL_LOCAL TEXT, " +
                "$COL_DIRECCION TEXT, " +
                "$COL_EQUIPO TEXT, " +
                "$COL_EQUIPO_ID TEXT, " +
                "$COL_TIPO TEXT, " +
                "$COL_PRIORIDAD TEXT, " +
                "$COL_ESTADO TEXT, " +
                "$COL_SLA TEXT, " +
                "$COL_DISTANCIA TEXT, " +
                "$COL_DESCRIPCION TEXT, " +
                "$COL_EXTERIOR INTEGER)")
        db.execSQL(createTableQuery)

        // Seed initial mock OTs
        for (ot in MockRepository.otList) {
            insertOtInternal(db, ot)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OT")
        onCreate(db)
    }

    private fun insertOtInternal(db: SQLiteDatabase, ot: OT) {
        val values = ContentValues().apply {
            put(COL_ID, ot.id)
            put(COL_CLIENTE, ot.cliente)
            put(COL_LOCAL, ot.local)
            put(COL_DIRECCION, ot.direccion)
            put(COL_EQUIPO, ot.equipo)
            put(COL_EQUIPO_ID, ot.equipoId)
            put(COL_TIPO, ot.tipo)
            put(COL_PRIORIDAD, ot.prioridad)
            put(COL_ESTADO, ot.estado)
            put(COL_SLA, ot.sla)
            put(COL_DISTANCIA, ot.distancia)
            put(COL_DESCRIPCION, ot.descripcion)
            put(COL_EXTERIOR, if (ot.exterior) 1 else 0)
        }
        db.insertWithOnConflict(TABLE_OT, null, values, SQLiteDatabase.CONFLICT_REPLACE)
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
                            id = it.getString(it.getColumnIndexOrThrow(COL_ID)),
                            cliente = it.getString(it.getColumnIndexOrThrow(COL_CLIENTE)),
                            local = it.getString(it.getColumnIndexOrThrow(COL_LOCAL)),
                            direccion = it.getString(it.getColumnIndexOrThrow(COL_DIRECCION)),
                            equipo = it.getString(it.getColumnIndexOrThrow(COL_EQUIPO)),
                            equipoId = it.getString(it.getColumnIndexOrThrow(COL_EQUIPO_ID)),
                            tipo = it.getString(it.getColumnIndexOrThrow(COL_TIPO)),
                            prioridad = it.getString(it.getColumnIndexOrThrow(COL_PRIORIDAD)),
                            estado = it.getString(it.getColumnIndexOrThrow(COL_ESTADO)),
                            sla = it.getString(it.getColumnIndexOrThrow(COL_SLA)),
                            distancia = it.getString(it.getColumnIndexOrThrow(COL_DISTANCIA)),
                            descripcion = it.getString(it.getColumnIndexOrThrow(COL_DESCRIPCION)),
                            exterior = it.getInt(it.getColumnIndexOrThrow(COL_EXTERIOR)) == 1
                        )
                    )
                } while (it.moveToNext())
            }
        }
        return list
    }

    fun insertOt(ot: OT) {
        val db = writableDatabase
        insertOtInternal(db, ot)
    }

    fun updateOtEstado(id: String, nuevoEstado: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_ESTADO, nuevoEstado)
        }
        db.update(TABLE_OT, values, "$COL_ID = ?", arrayOf(id))
    }
}
