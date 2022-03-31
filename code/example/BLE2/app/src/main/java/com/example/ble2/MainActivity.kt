package com.example.ble2

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.ble2.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout

// Tomando como modelo https://www.youtube.com/watch?v=Oz4CBHrxMMs
// P2 = https://www.youtube.com/watch?v=eg-t_rhDSoM
// La P2 la he dejao por el 6:26

class MainActivity : AppCompatActivity() {

    private var bAdapter: BluetoothAdapter? = null
    private lateinit var mPairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1

    lateinit var vario: ActivityMainBinding

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // variables
        val variables = ActivityMainBinding.inflate(layoutInflater)
        setContentView(variables.root)
        vario = variables

        val btM = this.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = btM.adapter

        if(bAdapter==null){
            Toast.makeText(this, "Este dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show()
            return
        }
        if(!bAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        vario.selectDeviceRefresh.setOnClickListener{pairedDeviceList()}

    }

    @SuppressLint("MissingPermission")
    private fun pairedDeviceList(){
        // variables
        val variables = ActivityMainBinding.inflate(layoutInflater)
        setContentView(variables.root)
        vario = variables
        mPairedDevices = bAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()
        if(mPairedDevices.isNotEmpty()) {
            for(device: BluetoothDevice in mPairedDevices){
                list.add(device)
            }
        }else{
            Toast.makeText(this, "No se han encontrado dispositivos", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        vario.selectDeviceList.adapter = adapter
        vario.selectDeviceList.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address
            val name: String = device.name

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_ENABLE_BLUETOOTH){
            if(resultCode==Activity.RESULT_OK){
                if(bAdapter!!.isEnabled){
                    Toast.makeText(this, "Bluetooth se ha activado", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, "Bluetooth se ha desactivado", Toast.LENGTH_SHORT).show()
                }
            }else if(resultCode==Activity.RESULT_CANCELED){
                Toast.makeText(this, "Cancelada la activación de Bluetooth", Toast.LENGTH_SHORT).show()
            }
        }
    }

}