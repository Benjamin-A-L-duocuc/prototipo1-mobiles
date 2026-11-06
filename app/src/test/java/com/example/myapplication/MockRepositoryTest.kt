package com.example.myapplication

import com.example.myapplication.data.repository.MockRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockRepositoryTest {
    @Test
    fun otIdsAreUnique() {
        val ids = MockRepository.otList.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun defaultOtExistsInSeedList() {
        assertTrue(MockRepository.otList.any { it.id == MockRepository.defaultOT.id })
    }

    @Test
    fun seededOtsHaveRequiredFields() {
        MockRepository.otList.forEach { ot ->
            assertTrue(ot.id.isNotBlank())
            assertTrue(ot.cliente.isNotBlank())
            assertTrue(ot.local.isNotBlank())
            assertTrue(ot.equipo.isNotBlank())
            assertTrue(ot.prioridad.isNotBlank())
            assertTrue(ot.estado.isNotBlank())
            assertTrue(ot.descripcion.isNotBlank())
        }
    }

    @Test
    fun seededOtsUseKnownStates() {
        val validStates = setOf("Asignada", "Aceptada", "En ejecución", "Cerrada", "Conformada")
        assertTrue(MockRepository.otList.all { it.estado in validStates })
    }

    @Test
    fun seededSyncItemsHaveRequiredFields() {
        assertTrue(MockRepository.syncItems.isNotEmpty())
        MockRepository.syncItems.forEach { item ->
            assertTrue(item.tipo.isNotBlank())
            assertTrue(item.detalle.isNotBlank())
            assertTrue(item.icono.isNotBlank())
        }
    }
}
