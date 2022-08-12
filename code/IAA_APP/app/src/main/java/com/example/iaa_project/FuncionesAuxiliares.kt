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
            if(i==8){
                if(e.isDigit()){
                    res=false
                    break
                }
            }else if(i in 1..7){
                if(e.isLetter()){
                    res=false
                    break
                }
            }
            i++
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

    fun formatoCorrectoAnyoFecha(fecha: String): Boolean{
        var res=true
        val partes = fecha.split("-")
        if (partes[0].length != 4) {
            res = false
        }
        return res
    }

    fun formatoCorrectoMesFecha(fecha: String): Boolean{
        var res=true
        val partes = fecha.split("-")
        val mes = partes[1].toInt()
        if (partes[1].length != 2 || mes < 1 || mes > 12) {
            res=false
        }
        return res
    }

    fun formatoCorrectoDiaFecha(fecha: String): Boolean{
        var res = true
        val partes = fecha.split("-")
        val ano = partes[0].toInt()
        val mes = partes[1].toInt()
        val dia = partes[2].toInt()
        if (partes[2].length != 2) {
            res = false
        } else {
            if (mes == 2) {
                if(ano%4==0 && dia>29){
                    res = false
                }
                if(ano%4!=0 && dia>28){
                    res = false
                }
            } else if (mes == 4 || mes == 6 || mes == 9 || mes == 11) {
                if (dia > 30) {
                    res = false
                }
            } else if (dia > 31) {
                res = false
            }
        }
        return res
    }

    fun longitudCorrectaPW(pw: String): Boolean{
        var res = true
        if(pw.length>6){
            res = false
        }
        return res
    }

    fun estructuraCorrectaPW(pw: String): Boolean{
        var res=false
        var res1=false; var res2=false; var res3=false; var res4=false
        if(pw.contains('@') || pw.contains('.') || pw.contains('-') || pw.contains('#') ||
            pw.contains('+') || pw.contains('*') || pw.contains('~') || pw.contains('=') ||
                pw.contains('$')){
            res1=true
        }
        for(l in pw){
            if(l.isDigit()){
                res2=true
            }else if(l.isUpperCase()){
                res3=true
            }else if(l.isLowerCase()){
                res4=true
            }
        }
        if(res1 && res2 && res3 && res4){
            res=true
        }
        return res
    }

}