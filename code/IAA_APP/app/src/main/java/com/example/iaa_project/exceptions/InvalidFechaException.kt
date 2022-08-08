package com.example.iaa_project.exceptions

val errorFecha1 = "El formato de la fecha no es correcto"
val errorFecha2 = "La fecha de nacimiento debe ser anterior a la actual"

internal class InvalidFechaException(mensaje: String?) : Exception(mensaje)