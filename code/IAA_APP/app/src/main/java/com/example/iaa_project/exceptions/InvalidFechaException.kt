package com.example.iaa_project.exceptions

val errorFecha = "La fecha introducida no es correcta"

internal class InvalidFechaException(mensaje: String?) : Exception(errorFecha)