package com.example.ble2

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityOptions
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.media.tv.TvContract
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Selection
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ActivityChooserView
import androidx.core.app.ActivityCompat
import com.example.ble2.MainActivity.Companion.EXTRA_ADDRESS
import com.example.ble2.databinding.ActivityMainBinding
import com.example.ble2.databinding.ControlLayoutBinding
import java.io.IOException
import java.util.*

// A PRIORI CÓDIGO TERMINADO, REVISAR LO QUE FALLA

class ControlActivity: AppCompatActivity() {

    //lateinit var vario: ControlActivityBinding
    private lateinit var vario: ControlLayoutBinding

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
        // SERA ASI????
        m_address = intent.getStringExtra(EXTRA_ADDRESS).toString()

        // DOCUMENTACIÓN PARA BINDINGS
        // https://developer.android.com/topic/libraries/data-binding/generated-binding
        // https://developer.android.com/topic/libraries/view-binding?hl=es-419
        val variables = ControlLayoutBinding.inflate(layoutInflater)
        setContentView(variables.root)
        vario = variables

        ConnectToDevice(this).execute()

        variables.controlLedOn.setOnClickListener(sendCommand("a"))
        vario.controlLedOff.setOnClickListener(sendCommand("b"))
        vario.controlLedDisconnect.setOnClickListener(disconnect())
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (m_bluetoothSocket != null){
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
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
            m_progress = ProgressDialog.show(context, "Conectando...", "Por favor, espere")
        }

        @SuppressLint("MissingPermission")
        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket==null || !m_isConnected){
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }

            } catch (e: IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess) {
                Log.i("data", "couldn't connect")
            }else{
                m_isConnected = true
            }
            m_progress.dismiss()
        }
    }
}