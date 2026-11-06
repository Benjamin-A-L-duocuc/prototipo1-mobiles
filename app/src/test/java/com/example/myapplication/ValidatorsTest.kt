package com.example.myapplication

import com.example.myapplication.validation.Validators
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidatorsTest {
    @Test
    fun loginUsernameRejectsBlankValues() {
        assertNotNull(Validators.loginUsername(""))
        assertNotNull(Validators.loginUsername("   "))
        assertNull(Validators.loginUsername("cristian.rojas"))
    }

    @Test
    fun loginPasswordRequiresMinimumLength() {
        assertNotNull(Validators.loginPassword(""))
        assertNotNull(Validators.loginPassword("1234567"))
        assertNull(Validators.loginPassword("12345678"))
    }

    @Test
    fun evidenceCommentRequiresTenToThreeHundredCharacters() {
        assertNotNull(Validators.evidenceComment("corta"))
        assertNull(Validators.evidenceComment("una evidencia valida"))
        assertNotNull(Validators.evidenceComment("a".repeat(301)))
    }

    @Test
    fun otClientRequiresAtLeastThreeCharacters() {
        assertNotNull(Validators.otCliente(""))
        assertNotNull(Validators.otCliente("ab"))
        assertNull(Validators.otCliente("ACME"))
    }

    @Test
    fun otEquipmentRequiresAtLeastThreeCharacters() {
        assertNotNull(Validators.otEquipo(""))
        assertNotNull(Validators.otEquipo("ab"))
        assertNull(Validators.otEquipo("Condensadora"))
    }

    @Test
    fun otDescriptionRequiresTenToThreeHundredCharacters() {
        assertNotNull(Validators.otDescripcion(""))
        assertNotNull(Validators.otDescripcion("corta"))
        assertNull(Validators.otDescripcion("Mantención preventiva del equipo."))
        assertNotNull(Validators.otDescripcion("a".repeat(301)))
    }
}
