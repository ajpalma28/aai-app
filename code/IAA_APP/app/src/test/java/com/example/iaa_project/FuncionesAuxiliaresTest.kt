package com.example.iaa_project

import org.junit.Test
import org.junit.Assert.*

class FuncionesAuxiliaresTest {

    @Test
    fun formateaFechaEsCorrecto1(){
        println("\nTEST 01: ¿formateaFecha('26/08/2022') es correcto?")
        println("Resultado: 2022-08-26")
        assertEquals("2022-08-26",FuncionesAuxiliares().formateaFecha("26/08/2022"))
    }

    @Test
    fun generaIdPersona1(){
        val nombre = "Jesús"
        val apellidos = "Roldán"
        val dni = "00000000T"
        val fecha = "01/01/1986"
        println("\nTEST 02: ¿generaIdPersona($nombre, $apellidos, $dni, $fecha) es correcto?")
        println("Resultado: Jesú00TRold86")
        assertEquals("Jesú00TRold86", FuncionesAuxiliares().generaIdPersona(nombre, apellidos, dni, fecha))
    }

    @Test
    fun generaIdPersona2(){
        val nombre = "Ana"
        val apellidos = "Gil Pérez"
        val dni = "00000000T"
        val fecha = "01/01/1950"
        println("TEST 03: ¿generaIdPersona($nombre, $apellidos, $dni, $fecha) es correcto?")
        println("Resultado: AnaX00TGilX50")
        assertEquals("AnaX00TGilX50", FuncionesAuxiliares().generaIdPersona(nombre, apellidos, dni, fecha))
    }

    @Test
    fun generaIdPersona3(){
        val nombre = "Ana María"
        val apellidos = "Broncano"
        val dni = "00000000T"
        val fecha = "01/01/1950"
        println("TEST 04: ¿generaIdPersona($nombre, $apellidos, $dni, $fecha) es correcto?")
        println("Resultado: AnaX00TBron50")
        assertEquals("AnaX00TBron50", FuncionesAuxiliares().generaIdPersona(nombre, apellidos, dni, fecha))
    }

    @Test
    fun longitudCorrectaDNINIF1(){
        val dni = "00000000T"
        println("\nTEST 05: ¿longitudCorrectaDNINIF($dni) es correcto?")
        println("Resultado: true")
        assertTrue(FuncionesAuxiliares().longitudCorrectaDNINIF(dni))
    }

    @Test
    fun longitudCorrectaDNINIF2(){
        val dni = "000000000T"
        println("TEST 06: ¿longitudCorrectaDNINIF($dni) es correcto?")
        println("Resultado: false")
        assertFalse(FuncionesAuxiliares().longitudCorrectaDNINIF(dni))
    }

    @Test
    fun formatoCorrectoDNINIF1(){
        val dni = "00000000T"
        println("\nTEST 07: ¿formatoCorrectoDNINIF($dni) es correcto?")
        println("Resultado: true")
        assertTrue(FuncionesAuxiliares().formatoCorrectoDNINIF(dni))
    }

    @Test
    fun formatoCorrectoDNINIF2(){
        val dni = "X0000000T"
        println("TEST 08: ¿formatoCorrectoDNINIF($dni) es correcto?")
        println("Resultado: true")
        assertTrue(FuncionesAuxiliares().formatoCorrectoDNINIF(dni))
    }

    @Test
    fun formatoCorrectoDNINIF3(){
        val dni = "XX000000T"
        println("TEST 09: ¿formatoCorrectoDNINIF($dni) es correcto?")
        println("Resultado: false")
        assertFalse(FuncionesAuxiliares().formatoCorrectoDNINIF(dni))
    }

    @Test
    fun formatoCorrectoDNINIF4(){
        val dni = "X00000000"
        println("TEST 10: ¿formatoCorrectoDNINIF($dni) es correcto?")
        println("Resultado: false")
        assertFalse(FuncionesAuxiliares().formatoCorrectoDNINIF(dni))
    }

    @Test
    fun formatoCorrectoDNINIF5(){
        val dni = "000000000"
        println("TEST 11: ¿formatoCorrectoDNINIF($dni) es correcto?")
        println("Resultado: false")
        assertFalse(FuncionesAuxiliares().formatoCorrectoDNINIF(dni))
    }

    @Test
    fun formateaFechaRev1(){
        val fecha = "1999-03-01"
        val resultado = "01/03/1999"
        println("\nTEST 12: ¿formateaFechaRev($fecha) es correcto?")
        println("Resultado: $resultado")
        assertEquals(resultado,FuncionesAuxiliares().formateaFechaRev(fecha))
    }

    @Test
    fun traduceNotificaciones1(){
        val notif = "true"
        println("\nTEST 13: ¿traduceNotificaciones('$notif') devuelve true?")
        println("Resultado: true")
        assertTrue(FuncionesAuxiliares().traduceNotificaciones(notif))
    }

    @Test
    fun traduceNotificaciones2(){
        val notif = "false"
        println("TEST 14: ¿traduceNotificaciones('$notif') devuelve false?")
        println("Resultado: false")
        assertFalse(FuncionesAuxiliares().traduceNotificaciones(notif))
    }

}