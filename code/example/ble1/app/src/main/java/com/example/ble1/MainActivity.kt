package com.example.ble1

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    var msgMostrar = ""

    companion object{
        private const val TAG = "BLE1"
        const val BLUETOOTH_REQUEST_CODE = 1
    }

    private val bluetoothAdapter : BluetoothAdapter by lazy {
        (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        if(bluetoothAdapter.isEnabled){
            // Comienza a escanear
            StartBLEScan()
        }else{
            Log.v(TAG,"BT is disabled")
            val btIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            startActivityForResult(btIntent, BLUETOOTH_REQUEST_CODE)
        }
    }

    fun StartBLEScan(){
        Log.v(TAG,"StartBLEScan")

        val scanFilter = ScanFilter.Builder().build()
        val scanFilters:MutableList<ScanFilter> = mutableListOf()
        scanFilters.add(scanFilter)

        val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

        Log.v(TAG,"Start Scan")

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        bluetoothAdapter.bluetoothLeScanner.startScan(scanFilters, scanSettings,bleScanCallback)
    }

    private val bleScanCallback : ScanCallback by lazy {
        object : ScanCallback(){
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                //super.onScanResult(callbackType, result)
                Log.v(TAG,"onScanResult")

                val bluetoothDevice = result?.device
                if(bluetoothDevice != null){
                    /*if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return
                    }*/
                    msgMostrar = "Device Name ${bluetoothDevice.name} Device Address ${bluetoothDevice.uuids}"
                    Log.v(TAG, msgMostrar)
                    Toast.makeText(applicationContext, msgMostrar,
                            Toast.LENGTH_LONG).show()

                }
            }
        }
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
    
    CÓDIGO ACTUAL SACADO DE:
    https://www.youtube.com/watch?v=SSHx4MSvfAg
 */