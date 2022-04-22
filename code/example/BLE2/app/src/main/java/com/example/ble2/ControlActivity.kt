package com.example.ble2

import android.app.ActivityManager
import android.app.ActivityOptions
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Selection
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActivityChooserView
import androidx.core.app.ActivityCompat
import com.example.ble2.databinding.ActivityMainBinding
import java.util.*

// DEJADO EL VIDEO (PARTE 2) POR EL 18:36

class ControlActivity: AppCompatActivity() {

    companion object {
        var m_myUUID: UUID = UUID.fromString("revisar como lo ha sacao el tio")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        // NO SE COMO SOLUCIONARLO
        //m_address = intent.getStringExtra(SelectDeviceActivity.EXTRA_ADDRESS)

        // TENGO QUE REPASAR CÓMO PASAR LAS VARIABLES EN UN ACTIVITY QUE NO SEA EL PRINCIPAL
        // Utilizar para ello: https://developer.android.com/topic/libraries/data-binding/generated-binding
        val variables = ActivityMainBinding.inflate(layoutInflater)
        setContentView(variables.root)

        ConnectToDevice(this).execute()

        variables.control_led_on.setOnClickListener(sendCommand("a"))
        control_led_off.setOnClickListener(sendCommand("a"))
        control_led_disconnect.setOnClickListener(sendCommand("a"))
    }

    private fun sendCommand(input: String) {

    }

    private fun disconnect() {

    }

    // VER CÓMO UTILIZAR FUNCIONES NO OBSOLETAS
    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {

        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = c
        }
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg p0: Void?): String {
            TODO("Not yet implemented")
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }
    }
}