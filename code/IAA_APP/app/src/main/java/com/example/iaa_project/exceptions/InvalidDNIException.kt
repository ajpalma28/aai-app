package com.example.iaa_project.exceptions

val errorDNI1 = "La longitud del DNI/NIF no es la adecuada"
val errorDNI2 = "El formato del DNI/NIF no es el adecuado"

internal class InvalidDNIException(mensaje: String?) : Exception(mensaje)