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

    private var permissionsCount = 0

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

    private fun getMissingLocationPermission(): String? {
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

    private fun hasLocationPermission(locPermission: String?): Boolean {
        return if (locPermission == null) true else ContextCompat.checkSelfPermission(
            applicationContext,
            locPermission
        ) ==
                PackageManager.PERMISSION_GRANTED // An Android version that doesn't need a location permission
    }


    private fun getMissingBlePermissions(): Array<String?>? {
        var missingPermissions: Array<String?>? = null
        val locationPermission = getMissingLocationPermission()
        // For Android 12 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.BLUETOOTH_SCAN
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                missingPermissions = arrayOfNulls(1)
                missingPermissions[0] = Manifest.permission.BLUETOOTH_SCAN
            }
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (missingPermissions == null) {
                    missingPermissions = arrayOfNulls(1)
                    missingPermissions[0] = Manifest.permission.BLUETOOTH_CONNECT
                } else {
                    missingPermissions =
                        Arrays.copyOf(missingPermissions, missingPermissions.size + 1)
                    missingPermissions[missingPermissions.size - 1] =
                        Manifest.permission.BLUETOOTH_CONNECT
                }
            }
        } else if (!hasLocationPermission(locationPermission)) {
            missingPermissions = arrayOfNulls(1)
            missingPermissions[0] = getMissingLocationPermission()
        }
        return missingPermissions
    }

    private fun checkBlePermissions() {
        val missingPermissions = getMissingBlePermissions()
        if (missingPermissions == null || missingPermissions.size == 0) {
            Log.i(TAG, "checkBlePermissions: Permissions is already granted")
            return
        }
        for (perm in missingPermissions) Log.d(
            TAG,
            "checkBlePermissions: missing permissions $perm"
        )
        permissionsCount = missingPermissions.size
        requestPermissions(missingPermissions, BLE_PERMISSIONS_REQUEST_CODE)
    }

    // Maybe some other codes...

    // Maybe some other codes...
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == BLE_PERMISSIONS_REQUEST_CODE) {
            val index = 0
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission granted for " + permissions[index])
                    if (permissionsCount > 0) permissionsCount--
                    if (permissionsCount === 0) {
                        // All permissions have been granted from user.
                        // Here you can notify other parts of the app ie. using a custom callback or a viewmodel so on.
                    }
                } else {
                    Log.d(TAG, "Permission denied for " + permissions[index])
                    // TODO handle user denial i.e. show an informing dialog
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