package com.example.iaa_project

import com.example.iaa_project.exceptions.*

class FuncionesAuxiliares {

    fun formateaFecha(fecha: String): String {
        val partes = fecha.split("/")
        return partes[2] + "-" + partes[1] + "-" + partes[0]
    }

    fun generaIdPersona(nombre: String, apellidos: String, dni: String, fecha: String): String {
        var identificador = ""
        for (i in nombre.indices) {
            if (i < 4) {
                identificador += nombre[i]
            } else {
                break
            }
        }
        for (j in dni.indices) {
            if (j > 5) {
                identificador += dni[j]
            }
        }
        for (i in apellidos.indices) {
            if (i < 4) {
                identificador += apellidos[i]
            } else {
                break
            }
        }
        val partes = fecha.split("/")
        val ano = partes[2]
        for (j in ano.indices) {
            if (j > 1) {
                identificador += ano[j]
            }
        }
        return identificador
    }

    fun longitudCorrectaDNINIF(dni:String) : Boolean {
        var res = false
        if(dni.length==9){
            res = true
        }
        return res
    }

    fun formatoCorrectoDNINIF(dni: String) : Boolean {
        var res = true
        var i = 0
        for (e in dni){
            if(i==9){
                if(e.isDigit()){
                    res=false
                    break
                }
            }else if(i in 1..8){
                if(e.isLetter()){
                    res=false
                    break
                }
            }
        }
        return res
    }

    fun formateaFechaRev(fecha: String): String {
        val partes = fecha.split("-")
        return partes[2] + "/" + partes[1] + "/" + partes[0]
    }

    fun traduceNotificaciones(notific: String): Boolean{
        return notific=="true"
    }

}