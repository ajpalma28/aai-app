package com.example.iaa_project.exceptions

val errorFecha1 = "El formato de la fecha no es correcto: Año inválido"
val errorFecha2 = "El formato de la fecha no es correcto: Mes inválido"
val errorFecha3 = "El formato de la fecha no es correcto: Día inválido"
val errorFecha4 = "La fecha de nacimiento debe ser anterior a la actual"

internal class InvalidFechaException(mensaje: String?) : Exception(mensaje)