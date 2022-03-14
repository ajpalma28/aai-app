package com.example.ble1_c

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ble1_c.databinding.ActivityMainBinding
import android.bluetooth.le.*
import android.content.Context

// Tomando como referencia: https://www.youtube.com/watch?v=PtN6UTIu7yw

class MainActivity : AppCompatActivity() {

    // Bluetooth Adapter
    lateinit var bAdapter:BluetoothAdapter

    private val REQUEST_CODE_ENABLE_BT:Int = 1
    private val REQUEST_CODE_DISCOVERABLE_BT:Int = 1
    
    lateinit var vario: ActivityMainBinding

    val deviceList = ArrayList<String>()

    val pairedDevices = HashSet<BluetoothDevice>()


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // variables
        val variables = ActivityMainBinding.inflate(layoutInflater)
        setContentView(variables.root)
        vario = variables

        // init bluetooth adapter
        // getDefaultAdapter() es un metodo obsoleto
        //bAdapter = BluetoothAdapter.getDefaultAdapter()
        // Esta seria la forma de hacerlo actualmente
        val btM = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = btM.adapter

        // comprobar
        if(bAdapter==null){
            variables.bluetoothStatusTv.text = "Bluetooth no disponible"
        }else{
            variables.bluetoothStatusTv.text = "Bluetooth disponible"
        }

        if (bAdapter.isEnabled){
            // be encendido
            variables.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
        }else{
            // be apagado
            variables.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
        }


        // Sacado de https://es.stackoverflow.com/questions/282294/listar-dispositivos-ble-cercanos-android,
        // no parece funcionarme ya que no detecta la Mi Band 2

        val mScanCallback = object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)

                if("Dispositivo: ${result?.device?.address} - ${result?.device?.name}" in deviceList) {
                    //Dispositivo ya se encuentra en lista
                }else {
                    //Agrega dispositivo a lista.
                    //deviceList.add("Dispositivo: ${result?.device?.address} - ${result?.device?.name}")
                    deviceList.add(result?.device?.toString().toString())
                }

            }
        }

        variables.turnOnBtn.setOnClickListener {

            if (bAdapter.isEnabled){
                // Preparado
                Toast.makeText(this,"Ya encendido", Toast.LENGTH_SHORT).show()
            }else{
                // Enciende
                bAdapter.enable()
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
                variables.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
            }

        }

        variables.turnOffBtn.setOnClickListener {

            if (!bAdapter.isEnabled){
                // Preparado
                Toast.makeText(this,"Ya apagado", Toast.LENGTH_SHORT).show()
            }else{
                // Apaga
                bAdapter.disable()
                variables.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_off)
                Toast.makeText(this,"BT apagado", Toast.LENGTH_SHORT).show()
            }

        }

        variables.discoverableBtn.setOnClickListener {

            if(!bAdapter.isDiscovering){
                Toast.makeText(this,"Haciendo descubrible tu móvil", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
            }

        }

        variables.pairedBtn.setOnClickListener {
            if(bAdapter.isEnabled){
                variables.pairedTv.text = "Dispositivos emparejados"
                // OBJETIVO!! Sustituir los devices del bondedDevices
                // Por los que se han obtenido mediante el uso del escaner
                // bAdapter.bluetoothLeScanner
                // TODO
                mScanCallback
                //val devices = bAdapter.bondedDevices
                val devices = deviceList
                for(device in devices){
                    //val deviceName = device.name
                    //val deviceAddress = device.address
                    //variables.pairedTv.append("\nDispositivo: $deviceName , $deviceAddress")
                    variables.pairedTv.append("\nDispositivo: $device")
                }

            }else{
                Toast.makeText(this,"Active Bluetooth primero", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Revisar por qué no funciona esto bien. Me activa el BT pero
    // me sale el mensaje de que no se puede encender.
    // SERÁ POR LOS DEPRECATES??
    // TODO
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_CODE_ENABLE_BT ->
                if (requestCode == RESULT_OK){
                    vario.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
                    Toast.makeText(this,"Encendido", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"No se puede encender", Toast.LENGTH_SHORT).show()
                }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}
