package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.LocalDatabaseHelper
import com.example.myapplication.model.CheckIn
import com.example.myapplication.model.Evidence
import com.example.myapplication.model.OT
import com.example.myapplication.model.SyncItem

class OtRepository(context: Context) {
    private val dbHelper = LocalDatabaseHelper(context)

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

    fun addSyncItem(tipo: String, detalle: String, icono: String) {
        dbHelper.addSyncItem(tipo, detalle, icono)
    }

    fun getSyncItems(): List<SyncItem> = dbHelper.getSyncItems()

    fun clearSyncQueue() {
        dbHelper.clearSyncItems()
    }

    fun getEvidence(otId: String): List<Evidence> = dbHelper.getEvidence(otId)

    fun addEvidence(otId: String, path: String, createdAt: Long): Long =
        dbHelper.addEvidence(otId, path, createdAt)

    fun deleteEvidence(id: Long) = dbHelper.deleteEvidence(id)

    fun getCheckIn(otId: String): CheckIn? = dbHelper.getCheckIn(otId)

    fun addCheckIn(otId: String, latitude: Double, longitude: Double, accuracy: Float, createdAt: Long) =
        dbHelper.addCheckIn(otId, latitude, longitude, accuracy, createdAt)
}
