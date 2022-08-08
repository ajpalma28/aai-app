package com.example.iaa_project.exceptions

val errorPW1 = "La contraseña y su confirmación no coinciden"

internal class InvalidPWException(mensaje: String?) : Exception(errorPW1)