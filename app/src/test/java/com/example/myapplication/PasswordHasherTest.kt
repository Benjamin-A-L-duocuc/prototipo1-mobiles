package com.example.myapplication

import com.example.myapplication.util.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PasswordHasherTest {
    @Test
    fun hashesKnownPasswordDeterministically() {
        assertEquals(
            "ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f",
            PasswordHasher.hash("password123")
        )
    }

    @Test
    fun differentPasswordsProduceDifferentHashes() {
        assertNotEquals(PasswordHasher.hash("password123"), PasswordHasher.hash("password124"))
    }
}
