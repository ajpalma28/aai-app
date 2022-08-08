package com.example.iaa_project.exceptions

val errorID1 = "No hay investigadores registrados con dicho ID"

internal class InvalidIDException (mensaje: String?) : Exception(mensaje)