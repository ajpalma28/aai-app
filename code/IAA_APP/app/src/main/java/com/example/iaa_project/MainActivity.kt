package com.example.iaa_project

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.iaa_project.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    var variables: ActivityMainBinding? = null

    private val BLE_PERMISSIONS_REQUEST_CODE = 0x55 // Could be any other positive integer value

    private var contadorPermisos = 0

    private companion object {
        private const val CHANNEL_ID = "channel01"
        val TAG = "IAAPROJECT"
        val BLUETOOTH_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        variables = ActivityMainBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        compruebaPermisosBLE()

        val botonReg = variables!!.btnRegistro
        val botonInSes = variables!!.btnInicioSesion
        val botonAcercaDe = variables!!.btnAcercaDe

        botonReg.setOnClickListener{
            val intento1 = Intent(this, ActivityRegistroInvest::class.java)
            startActivity(intento1)
        }

        botonInSes.setOnClickListener{
            val intento2 = Intent(this, LoginActivity::class.java)
            startActivity(intento2)
        }

        botonAcercaDe.setOnClickListener{
            val intento3 = Intent(this, AcercaDeActivity::class.java)
            startActivity(intento3)
        }

    }

    private fun permisoUbicacionQueFalta(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        ) {
            // COARSE is needed for Android 6 to Android 10
            return Manifest.permission.ACCESS_COARSE_LOCATION
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // FINE is needed for Android 10 and above
            return Manifest.permission.ACCESS_FINE_LOCATION
        }
        // No location permission is needed for Android 6 and below
        return null
    }

    private fun compruebaPermisoUbicacion(permUbicacion: String?): Boolean {
        return if (permUbicacion == null) true else ContextCompat.checkSelfPermission(
            applicationContext,
            permUbicacion
        ) ==
                PackageManager.PERMISSION_GRANTED // An Android version that doesn't need a location permission
    }


    private fun getPermisosQueFaltanBLE(): Array<String?>? {
        var permisosQueFaltan: Array<String?>? = null
        val permisoUbicacion = permisoUbicacionQueFalta()
        // For Android 12 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.BLUETOOTH_SCAN
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                permisosQueFaltan = arrayOfNulls(1)
                permisosQueFaltan[0] = Manifest.permission.BLUETOOTH_SCAN
            }
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (permisosQueFaltan == null) {
                    permisosQueFaltan = arrayOfNulls(1)
                    permisosQueFaltan[0] = Manifest.permission.BLUETOOTH_CONNECT
                } else {
                    permisosQueFaltan =
                        Arrays.copyOf(permisosQueFaltan, permisosQueFaltan.size + 1)
                    permisosQueFaltan[permisosQueFaltan.size - 1] =
                        Manifest.permission.BLUETOOTH_CONNECT
                }
            }
        } else if (!compruebaPermisoUbicacion(permisoUbicacion)) {
            permisosQueFaltan = arrayOfNulls(1)
            permisosQueFaltan[0] = permisoUbicacionQueFalta()
        }
        return permisosQueFaltan
    }

    private fun compruebaPermisosBLE() {
        val permisosQueFaltan = getPermisosQueFaltanBLE()
        if (permisosQueFaltan == null || permisosQueFaltan.isEmpty()) {
            Log.i(TAG, "compruebaPermisosBLE: Todos los permisos se han concedido")
            return
        }
        for (perm in permisosQueFaltan) Log.d(
            TAG,
            "compruebaPermisosBLE: Falta el permiso $perm"
        )
        contadorPermisos = permisosQueFaltan.size
        requestPermissions(permisosQueFaltan, BLE_PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == BLE_PERMISSIONS_REQUEST_CODE) {
            val index = 0
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permiso concedido: " + permissions[index])
                    if (contadorPermisos > 0) contadorPermisos--
                    if (contadorPermisos === 0) {
                        // All permissions have been granted from user.
                        // Here you can notify other parts of the app ie. using a custom callback or a viewmodel so on.
                    }
                } else {
                    Log.d(TAG, "Permiso denegado: " + permissions[index])
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}