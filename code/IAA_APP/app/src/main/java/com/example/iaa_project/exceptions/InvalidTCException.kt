package com.example.iaa_project.exceptions

val errorTYC = "Debe aceptar los términos y condiciones para registrarse en el sistema"

internal class InvalidTCException(mensaje: String?) : Exception(mensaje)