package com.example.ble1_c

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.ble1_c.databinding.ActivityMainBinding
import androidx.annotation.NonNull as NonNull1

// Tomando como referencia: https://www.youtube.com/watch?v=PtN6UTIu7yw

class MainActivity : AppCompatActivity() {

    // Bluetooth Adapter
    lateinit var bAdapter:BluetoothAdapter

    private val REQUEST_CODE_ENABLE_BT:Int = 1
    private val REQUEST_CODE_DISCOVERABLE_BT:Int = 1
    
    lateinit var vario: ActivityMainBinding


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // variables
        val variables = ActivityMainBinding.inflate(layoutInflater)
        setContentView(variables.root)
        vario = variables

        // init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()
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
                val devices = bAdapter.bondedDevices
                for(device in devices){
                    val deviceName = device.name
                    val deviceAddress = device.address
                    variables.pairedTv.append("\nDispositivo: $deviceName , $deviceAddress")
                }
            }else{
                Toast.makeText(this,"Encendiendo Bluetooth primero", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Revisar por qué no funciona esto bien. Me activa el BT pero
    // me sale el mensaje de que no se puede encender.
    // SERÁ POR LOS DEPRECATES??
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