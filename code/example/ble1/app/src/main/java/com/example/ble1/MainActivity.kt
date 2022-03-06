package com.example.ble1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

/*
    DOCUMENTACIÓN PARA LAS PRUEBAS:
    https://developer.android.com/guide/topics/connectivity/bluetooth-le?hl=es-419#kotlin
    https://developer.android.com/reference/android/content/Context?hl=es-419#getSystemService(java.lang.Class%3CT%3E)
    https://developer.android.com/reference/android/bluetooth/BluetoothAdapter?hl=es-419
    https://developer.android.com/guide/topics/connectivity/bluetooth/ble-overview
    https://zoewave.medium.com/kotlin-beautiful-low-energy-ble-91db3c0ab887
    https://punchthrough.com/android-ble-guide/
    https://medium.com/@nithinjith.p/ble-in-android-kotlin-c485f0e83c16
    https://developer.android.com/guide/topics/connectivity/bluetooth/find-ble-devices#kotlin

    Búsquedas en Google:
    - kotlin bluetooth ble
    - app kotlin ble

    IMPORTANTE: Leer más documentación antes de intentar probar nada. Buscar posibles videotutoriales para
    comprenderlo mejor to. Hace años que no toco Android Studio y no he tocado nunca Kotlin.
 */