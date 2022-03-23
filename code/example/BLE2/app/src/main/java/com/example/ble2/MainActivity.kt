package com.example.ble2

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.tabs.TabLayout

// Tomando como modelo https://www.youtube.com/watch?v=Oz4CBHrxMMs
// lo dejo por el min 21:53

class MainActivity : AppCompatActivity() {

    lateinit var bAdapter: BluetoothAdapter
    lateinit var mPairedDevices: Set<BluetoothDevice>
    val REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btM = this.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = btM.adapter

        if(bAdapter==null){
            Toast.makeText(this, "this device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
        }

    }

    private fun pairedDeviceList(){

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

}