package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.repository.OtRepository
import com.example.myapplication.model.OT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TecnicoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OtRepository(application)

    private val _otList = MutableStateFlow(repository.getOts())
    val otList: StateFlow<List<OT>> = _otList.asStateFlow()

    private val _checkinDone = MutableStateFlow(false)
    val checkinDone: StateFlow<Boolean> = _checkinDone.asStateFlow()

    private val _pendingSyncCount = MutableStateFlow(2)
    val pendingSyncCount: StateFlow<Int> = _pendingSyncCount.asStateFlow()

    fun loadOts() {
        _otList.value = repository.getOts()
    }

    fun acceptOt(id: String) {
        repository.updateEstado(id, "Aceptada")
        loadOts()
    }

    fun doCheckIn() {
        _checkinDone.value = true
        _pendingSyncCount.value++
    }

    fun syncAll() {
        _pendingSyncCount.value = 0
    }
}
