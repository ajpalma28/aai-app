package com.example.iaa_project.exceptions

val errorPW1 = "La contraseña y su confirmación no coinciden"
val errorPW2 = "El formato de la contraseña no es el adecuado"
val errorPW3 = "ID o contraseña incorrectos. ¿Ha olvidado sus datos?"

internal class InvalidPWException(mensaje: String?) : Exception(mensaje)