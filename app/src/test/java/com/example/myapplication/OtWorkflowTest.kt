package com.example.myapplication

import com.example.myapplication.data.repository.MockRepository
import com.example.myapplication.domain.OtWorkflow
import com.example.myapplication.model.OT
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OtWorkflowTest {
    private val tecnicoId = 1L

    private fun sample(estado: String, tecnicoId: Long? = this.tecnicoId): OT =
        MockRepository.defaultOT.copy(estado = estado, tecnicoId = tecnicoId)

    @Test
    fun allWorkflowStatesAreRegistered() {
        assertEquals(8, OtWorkflow.estados.size)
        assertTrue(OtWorkflow.PENDIENTE in OtWorkflow.estados)
        assertTrue(OtWorkflow.CONFORMADA in OtWorkflow.estados)
    }

    @Test
    fun canAssignPendingAndAssignedOrders() {
        assertTrue(OtWorkflow.puedeAsignar(sample(OtWorkflow.PENDIENTE, null)))
        assertTrue(OtWorkflow.puedeAsignar(sample(OtWorkflow.ASIGNADA)))
        assertFalse(OtWorkflow.puedeAsignar(sample(OtWorkflow.EN_EJECUCION)))
    }

    @Test
    fun onlyAssignedTechnicianCanAccept() {
        val assigned = sample(OtWorkflow.ASIGNADA)
        assertTrue(OtWorkflow.puedeAceptar(assigned, tecnicoId))
        assertFalse(OtWorkflow.puedeAceptar(assigned, 99L))
        assertFalse(OtWorkflow.puedeAceptar(assigned, null))
        assertFalse(OtWorkflow.puedeAceptar(assigned.copy(estado = OtWorkflow.ACEPTADA), tecnicoId))
    }

    @Test
    fun checkInRequiresAcceptedOrderAndAssignedTechnician() {
        assertTrue(OtWorkflow.puedeCheckIn(sample(OtWorkflow.ACEPTADA), tecnicoId))
        assertFalse(OtWorkflow.puedeCheckIn(sample(OtWorkflow.ASIGNADA), tecnicoId))
        assertFalse(OtWorkflow.puedeCheckIn(sample(OtWorkflow.ACEPTADA), 99L))
    }

    @Test
    fun executionAndClosureRequireExecutionState() {
        assertTrue(OtWorkflow.puedeEjecutar(sample(OtWorkflow.EN_EJECUCION), tecnicoId))
        assertTrue(OtWorkflow.puedeCerrar(sample(OtWorkflow.EN_EJECUCION), tecnicoId))
        assertFalse(OtWorkflow.puedeCerrar(sample(OtWorkflow.ACEPTADA), tecnicoId))
    }

    @Test
    fun conformityRequiresClosedOrder() {
        assertTrue(OtWorkflow.puedeConformar(sample(OtWorkflow.CERRADA)))
        assertFalse(OtWorkflow.puedeConformar(sample(OtWorkflow.EN_EJECUCION)))
        assertFalse(OtWorkflow.puedeConformar(sample(OtWorkflow.CONFORMADA)))
    }

    @Test
    fun finalStatesCannotBeCancelled() {
        assertFalse(OtWorkflow.puedeCancelar(sample(OtWorkflow.CONFORMADA)))
        assertFalse(OtWorkflow.puedeCancelar(sample(OtWorkflow.RECHAZADA_CLIENTE)))
        assertFalse(OtWorkflow.puedeCancelar(sample(OtWorkflow.CANCELADA)))
        assertTrue(OtWorkflow.puedeCancelar(sample(OtWorkflow.ASIGNADA)))
    }

    @Test
    fun happyPathAdvancesFromAssignmentToConformity() {
        val pending = sample(OtWorkflow.PENDIENTE, null)
        assertTrue(OtWorkflow.puedeReasignar(pending))

        val assigned = pending.copy(estado = OtWorkflow.ASIGNADA, tecnicoId = tecnicoId)
        assertTrue(OtWorkflow.puedeAceptar(assigned, tecnicoId))

        val accepted = assigned.copy(estado = OtWorkflow.ACEPTADA)
        assertTrue(OtWorkflow.puedeCheckIn(accepted, tecnicoId))

        val executing = accepted.copy(estado = OtWorkflow.EN_EJECUCION)
        assertTrue(OtWorkflow.puedeCerrar(executing, tecnicoId))

        val closed = executing.copy(estado = OtWorkflow.CERRADA)
        assertTrue(OtWorkflow.puedeConformar(closed))
        assertTrue(OtWorkflow.estaCerrada(closed.estado))

        val conformed = closed.copy(estado = OtWorkflow.CONFORMADA)
        assertTrue(OtWorkflow.esFinal(conformed.estado))
        assertTrue(OtWorkflow.esFinal(OtWorkflow.RECHAZADA_CLIENTE))
        assertTrue(OtWorkflow.esFinal(OtWorkflow.CANCELADA))
    }

    @Test
    fun everyStateHasNextStepDescription() {
        OtWorkflow.estados.forEach { estado ->
            assertTrue(OtWorkflow.siguientePaso(sample(estado)).isNotBlank())
        }
    }
}
