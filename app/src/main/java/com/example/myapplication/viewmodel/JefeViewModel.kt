package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.myapplication.data.repository.OtRepository
import com.example.myapplication.model.OT
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class JefeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = OtRepository(application)

    private val _otList = MutableStateFlow(repository.getOts())
    val otList: StateFlow<List<OT>> = _otList.asStateFlow()

    fun loadOts() {
        _otList.value = repository.getOts()
    }

    fun addNewOt(ot: OT) {
        repository.addOt(ot)
        loadOts()
    }
}
