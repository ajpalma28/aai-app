package com.example.iaa_project.exceptions

val errorType1 = "Los datos que se han recibido del dispositivo Tipo 1 no tienen la estructura correcta"
val errorType2 = "Los datos que se han recibido del dispositivo Tipo 2 no tienen la estructura correcta"
val errorType3 = "Los datos que se han recibido del dispositivo Tipo 3 no tienen la estructura correcta"

internal class InvalidDataException(mensaje: String?) : Exception(mensaje) {
}