package com.example.myapplication.data.repository

import android.content.Context
import com.example.myapplication.data.LocalDatabaseHelper
import com.example.myapplication.model.OT

class OtRepository(context: Context) {
    private val dbHelper = LocalDatabaseHelper(context)

    fun getOts(): List<OT> {
        val list = dbHelper.getAllOts()
        return if (list.isEmpty()) MockRepository.otList else list
    }

    fun addOt(ot: OT) {
        dbHelper.insertOt(ot)
    }

    fun updateEstado(id: String, estado: String) {
        dbHelper.updateOtEstado(id, estado)
    }
}
