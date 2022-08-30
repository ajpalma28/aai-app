package com.example.iaa_project

class FuncionesAuxiliares {

    fun formateaFecha(fecha: String): String {
        val partes = fecha.split("/")
        return partes[2] + "-" + partes[1] + "-" + partes[0]
    }

    fun generaIdPersona(nombre: String, apellidos: String, dni: String, fecha: String): String {
        var identificador = ""
        val largoNombre = nombre.length
        val largoApellidos = apellidos.length
        var i = 0
        var k = 0
        while(true) {
            if (i < 4) {
                if(i>=largoNombre){
                    identificador += "X"
                }else if(nombre[i].isWhitespace()){
                    identificador += "X"
                }else{
                    identificador += nombre[i]
                }
            }else{
                break
            }
            i++
        }
        for (j in dni.indices) {
            if (j > 5) {
                identificador += dni[j]
            }
        }
        while(true) {
            if (k < 4) {
                if(k>largoApellidos){
                    identificador += "X"
                }else if(apellidos[k].isWhitespace()){
                    identificador += "X"
                }else{
                    identificador += apellidos[k]
                }
            }else{
                break
            }
            k++
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
            }else if(i==0 && e.isLetter()){
                if(e!='X' && e!='Y' && e!='Z'){
                    res=false
                    break
                }
            }
            i++
        }
        return res
    }

    fun letraCorrectaDNINIF(dni: String): Boolean{
        var res = false
        var numDNI = 0
        val letras = "TRWAGMYFPDXBNJZSQVHLCKE"
        if(formatoCorrectoDNINIF(dni)){
            if(dni[0].isDigit()){
                numDNI = dni.subSequence(0,8).toString().toInt()
                val numero = numDNI%23
                if(letras[numero]==(dni[8])){
                    res = true
                }
            }else{
                var inicial = ""
                when(dni[0]){
                    'X' -> inicial="0"
                    'Y' -> inicial="1"
                    'Z' -> inicial="2"
                }
                val completo = inicial+dni.subSequence(1,8)
                val numero = completo.toInt()
                val letra = numero%23
                if(letras[letra]==dni[8]){
                    res=true
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
        if(pw.length<6){
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
        res = res1 && res2 && res3 && res4
        return res
    }

    fun generaIdAsociacion(org: String, invest: String) : String{
        val aux = "_$invest"
        return "$org$aux"
    }

    fun generaIdSesion(date: String, usu: String, org: String) : String {
        val aux0 = date.replace("-","")
        val aux1 = "_$usu"
        val aux2 = "_$org"
        return "$aux0$aux1$aux2"
    }

    fun compruebaIdOrg(id: String) : Boolean {
        var res = true
        if(id.length!=14 || id.contains(" ")){
            res = false
        }
        return res
    }

}