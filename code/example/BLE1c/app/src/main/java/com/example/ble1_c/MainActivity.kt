package com.example.ble1_c

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.*
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ble1_c.databinding.ActivityMainBinding
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context

// Tomando como referencia: https://www.youtube.com/watch?v=PtN6UTIu7yw

// GUIAS A SEGUIR PARA CONECTAR:
// https://developer.android.com/guide/topics/connectivity/bluetooth?hl=es-419#kotlin
// https://learntutorials.net/es/android/topic/10020/bluetooth-low-energy

class MainActivity : AppCompatActivity() {

    // Bluetooth Adapter
    lateinit var bAdapter:BluetoothAdapter

    //private val REQUEST_CODE_ENABLE_BT:Int = 1
    //private val REQUEST_CODE_DISCOVERABLE_BT:Int = 1
    
    lateinit var vario: ActivityMainBinding

    var deviceList = ArrayList<String>()
    val dL2 = ArrayList<BluetoothDevice>()

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

        // FUNCIONA CORRECTAMENTE!
        variables.turnOnBtn.setOnClickListener {
            if (bAdapter.isEnabled){
                // Preparado
                Toast.makeText(this,"Ya encendido", Toast.LENGTH_SHORT).show()
            }else{
                // Enciende
                //bAdapter.enable()
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                //startActivityForResult(intent, REQUEST_CODE_ENABLE_BT)
                // Corregido el startActivityForResult con
                // https://www.youtube.com/watch?v=5GMwP9ppjdk
                if(intent.resolveActivity(packageManager) != null){
                    variables.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
                    getAction.launch(intent)
                }
                //variables.bluetoothIv.setImageResource(R.drawable.ic_bluetooth_on)
            }
        }

        // FUNCIONA CORRECTAMENTE
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

        // SE SUPONE QUE FUNCIONA CORRECTAMENTE
        variables.discoverableBtn.setOnClickListener {
            if(!bAdapter.isDiscovering){
                Toast.makeText(this,"Haciendo descubrible tu móvil", Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE))
                //startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE_BT)
                if(intent.resolveActivity(packageManager) != null){
                    getAction1.launch(intent)
                }
            }
        }

        /*
        thread(start=true){
            var i = 0
            while(true){
                variables.pairedTv.text = "Segundo $i"
                i += 1
                TimeUnit.SECONDS.sleep(1L)
            }
            //variables.pairedTv.text = "${Thread.currentThread()} has run."
        }*/
        /*thread(start=true){
            var itera = 0
            while(true){
                if(itera==0){
                    variables.pairedTv.text = "Cargando el servicio de búsqueda de dispositivos"
                    itera+=1
                }else{
                    if(bAdapter.isEnabled){
                        var resultado = escanea()
                        if (resultado!=""){
                            var ponTexto = "Dispositivos encontrados"+resultado
                            variables.pairedTv.text = ponTexto
                        }else{
                            variables.pairedTv.text = "No hay dispositivos cerca"
                        }
                    }else{
                        variables.pairedTv.text = "Imposible encontrar dispositivos. ¡Active Bluetooth!"
                    }
                }
                TimeUnit.SECONDS.sleep(5L)

            }
        }*/

        variables.pairedBtn.setOnClickListener {
            if(bAdapter.isEnabled){
                variables.pairedTv.text = "Dispositivos emparejados"
                    // OBJETIVO!! Sustituir los devices del bondedDevices
                    // Por los que se han obtenido mediante el uso del escaner
                    // bAdapter.bluetoothLeScanner
                    // TODO
                    //mScanCallback
                var resultado = escanea()
                if (resultado!=""){
                    var ponTexto = "Dispositivos encontrados"+resultado
                    variables.pairedTv.text = ponTexto
                }else{
                    variables.pairedTv.text = "No hay dispositivos cerca"
                }
            }else{
                Toast.makeText(this,"Active Bluetooth primero", Toast.LENGTH_SHORT).show()
                variables.pairedTv.text = "Imposible encontrar dispositivos. ¡Active Bluetooth!"
            }


        }

    }


    @SuppressLint("MissingPermission")
    fun escanea(): String {
        var dispCercanos = ""
        val scanFilter = ScanFilter.Builder().build()

        val scanFilters:MutableList<ScanFilter> = mutableListOf()
        scanFilters.add(scanFilter)

        val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

        bAdapter.bluetoothLeScanner.startScan(scanFilters, scanSettings, bleScanCallback)
        //val devices = bAdapter.bondedDevices
        //val devices = deviceList
        val devices = dL2
        if(devices.size>0){
            for(device in devices){
                //val deviceName = device.name
                //val deviceAddress = device.address
                //variables.pairedTv.append("\nDispositivo: $deviceName , $deviceAddress")
                dispCercanos += ("\nDispositivo: ${device.name} - ${device.address} - ${device.uuids}")
                //dispCercanos += ("\nDispositivo: $device")
            }
        }
        return dispCercanos
    }

    @SuppressLint("MissingPermission")
    val getAction = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        bAdapter.enable()
        Toast.makeText(this,"Encendido", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("MissingPermission")
    val getAction1 = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        bAdapter.startDiscovery()
        Toast.makeText(this,"Haciendo descubrible", Toast.LENGTH_SHORT).show()
    }

    private val bleScanCallback : ScanCallback by lazy {
        object : ScanCallback() {
            @SuppressLint("MissingPermission")
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                val bluetoothDevice = result?.device
                if(bluetoothDevice != null) {
                    //deviceList.add("Nombre del dispositivo ${bluetoothDevice.name}, Dirección ${bluetoothDevice.uuids}")
                    if(!dL2.contains(bluetoothDevice)){
                      dL2.add(bluetoothDevice)
                    }
                }/*else{
                    deviceList.add("Nada encontrado")
                }*/

            }
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    // Esto sirve para recibir información sobre cada dispositivo detectado.
    private val receiver = object : BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //...

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }


}