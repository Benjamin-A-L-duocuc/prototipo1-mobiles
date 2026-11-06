package com.example.myapplication.util

import java.security.MessageDigest

object PasswordHasher {
    fun hash(password: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it.toInt() and 0xff) }
}
