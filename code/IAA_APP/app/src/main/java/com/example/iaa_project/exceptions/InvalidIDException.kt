package com.example.iaa_project.exceptions

val errorID1 = "No hay investigadores registrados con dicho ID"
val errorID2 = "El formato del ID no es el correcto, ¡revíselo!"

internal class InvalidIDException (mensaje: String?) : Exception(mensaje)