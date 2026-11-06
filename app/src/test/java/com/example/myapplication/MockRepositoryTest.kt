package com.example.myapplication

import com.example.myapplication.data.repository.MockRepository
import com.example.myapplication.domain.OtWorkflow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
        assertTrue(MockRepository.otList.all { it.estado in OtWorkflow.estados })
    }

    @Test
    fun seededOtsIncludeUnassignedPendingWork() {
        assertTrue(
            MockRepository.otList.any {
                it.estado == OtWorkflow.PENDIENTE && it.tecnicoId == null
            }
        )
    }

    @Test
    fun seededOtsReferenceKnownCatalogs() {
        val clientes = MockRepository.clientes.map { it.id }.toSet()
        val locales = MockRepository.locales.map { it.id }.toSet()
        val equipos = MockRepository.equipos.map { it.id }.toSet()

        MockRepository.otList.forEach { ot ->
            assertTrue("Cliente inexistente en ${ot.id}", ot.clienteId != null && ot.clienteId in clientes)
            assertTrue("Local inexistente en ${ot.id}", ot.localId != null && ot.localId in locales)
            assertTrue("Equipo inexistente en ${ot.id}", ot.equipoId in equipos)
        }
    }

    @Test
    fun seededCatalogsHaveRequiredFields() {
        assertTrue(MockRepository.clientes.isNotEmpty())
        assertTrue(MockRepository.locales.isNotEmpty())
        assertTrue(MockRepository.equipos.isNotEmpty())
        assertTrue(MockRepository.tecnicos.isNotEmpty())

        MockRepository.clientes.forEach { assertTrue(it.nombre.isNotBlank()) }
        MockRepository.locales.forEach {
            assertTrue(it.nombre.isNotBlank())
            assertTrue(it.clienteId > 0)
        }
        MockRepository.equipos.forEach {
            assertTrue(it.id.isNotBlank())
            assertTrue(it.localId > 0)
        }
        MockRepository.tecnicos.forEach {
            assertTrue(it.username.isNotBlank())
            assertTrue(it.nombre.isNotBlank())
        }
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

    @Test
    fun defaultOtHasTechnicianAndSchedule() {
        val ot = MockRepository.defaultOT
        assertNotNull(ot.tecnicoId)
        assertNotNull(ot.programadaPara)
        assertTrue(ot.clienteId != null)
        assertTrue(ot.localId != null)
    }
}
