package com.example.gestion_movil.data.model

// Credenciales de acceso (cuentas ficticias, solo para la demostración).
// La contraseña NO se guarda como texto visible: se guarda su hash SHA-256 con sal.
data class Credential(val username: String, val passwordHash: String, val rol: Rol) {
    companion object {
        // Propietario del taller: usuario principal de la app
        val Propietario = Credential(
            username = "admin",
            passwordHash = "4ebe28d89a8a24f087726aa01fc0fc505a1722c46b0096fd5c36e23a89c6b974",
            rol = Rol.PROPIETARIO
        )

        // Operador o asistente con permisos limitados
        val Operador = Credential(
            username = "operador",
            passwordHash = "096b9d809f832f878bd17262a990ff102cf88f7d12cbc42878e9ec876a7be6b6",
            rol = Rol.OPERADOR
        )
    }
}
