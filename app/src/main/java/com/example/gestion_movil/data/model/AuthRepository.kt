package com.example.gestion_movil.data.model

import java.security.MessageDigest

// Valida el inicio de sesión contra las cuentas registradas
class AuthRepository(
    private val cuentas: List<Credential> = listOf(Credential.Propietario, Credential.Operador)
) {
    // Devuelve la cuenta si usuario y contraseña son correctos; null si no
    fun login(username: String, password: String): Credential? {
        val hash = calcularHash(password)
        return cuentas.find { it.username == username && it.passwordHash == hash }
    }

    // Convierte la contraseña escrita en su hash SHA-256 (con sal) para compararla
    // sin guardar nunca la contraseña real en el código
    private fun calcularHash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest("$SAL$password".toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        private const val SAL = "taller-puente-alto:"
    }
}
