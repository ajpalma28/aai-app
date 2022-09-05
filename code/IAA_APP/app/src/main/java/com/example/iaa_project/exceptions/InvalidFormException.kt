package com.example.iaa_project.exceptions

val errorFormulario1 = "Ha dejado campos vacíos. Por favor, rellénelos todos."

internal class InvalidFormException(mensaje: String?) : Exception(mensaje)