package com.example.iaa_project

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.*
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityVisualiMedBinding
import kotlin.random.Random

class VisualiMedActivity : AppCompatActivity() {

    var variables : ActivityVisualiMedBinding? = null
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var conectados = ArrayList<BluetoothDevice>()
    var pacienteMed = ""
    var idUsuMed : EditText? = null
    var errorProvocado = ""

    var bAdapter: BluetoothAdapter? = null
    var bluetoothGatt: BluetoothGatt? = null

    var muestraAcelT1 : Button? = null
    var muestraGirosT1 : Button? = null
    var muestraTempAmbT1 : Button? = null
    var muestraBattT1 : Button? = null

    var muestraElectDermT2 : Button? = null
    var muestraTempCorpT2 : Button? = null
    var muestraAcelT2 : Button? = null
    var muestraGirosT2 : Button? = null
    var muestraBattT2 : Button? = null

    var muestraFrecCardT3 : Button? = null
    var muestraFrecRespT3 : Button? = null
    var muestraTempAmbT3 : Button? = null
    var muestraGirosT3 : Button? = null
    var muestraBattT3 : Button? = null

    private companion object {
        private const val CHANNEL_ID = "channel01"
        private const val TIPO1 = "LegMonitor"
        private const val TIPO2 = "WristMonitor"
        private const val TIPO3 = "ChestMonitor"
        val TAG = "IAAPROJECT"
        val BLUETOOTH_REQUEST_CODE = 1
        const val ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.iaa_project.ACTION_GATT_SERVICES_DISCOVERED"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visuali_med)

        variables = ActivityVisualiMedBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        if(bundle?.getParcelableArrayList<BluetoothDevice>("conectados")!!.isNotEmpty()){
            conectados.addAll(bundle.getParcelableArrayList("conectados")!!)
        }
        pacienteMed = bundle.getString("pacienteMed").toString()

        muestraAcelT1 = variables!!.repreAcelT1
        muestraGirosT1 = variables!!.repreGiroscT1
        muestraTempAmbT1 = variables!!.repreTempAmbT1
        muestraBattT1 = variables!!.repreBattT1

        muestraElectDermT2 = variables!!.repreElectDermT2
        muestraTempCorpT2 = variables!!.repreTempCorpT2
        muestraAcelT2 = variables!!.repreAcelT2
        muestraGirosT2 = variables!!.repreGirosT2
        muestraBattT2 = variables!!.repreBattT2

        muestraFrecCardT3 = variables!!.repreFreqCardT3
        muestraFrecRespT3 = variables!!.repreFreqRespT3
        muestraTempAmbT3 = variables!!.repreTempAmbT3
        muestraGirosT3 = variables!!.repreGirosT3
        muestraBattT3 = variables!!.repreBattT3

        if(conectados.isNotEmpty()){
            conectaDispositivos(conectados)
        }


    }

    private fun conectaDispositivos(lista: java.util.ArrayList<BluetoothDevice>) {
        for (l in lista) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ),
                        1
                    )
                }
            }
            bluetoothGatt = l.connectGatt(this, false, bleGattCallback)
        }
    }

    private val bleGattCallback: BluetoothGattCallback by lazy {
        object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
//                super.onConnectionStateChange(gatt, status, newState)
                Log.v(TAG, "onConnectionStateChange")
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.v(TAG, "Dispositivo conectado")
                    if (ActivityCompat.checkSelfPermission(
                            this@VisualiMedActivity,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                            ActivityCompat.requestPermissions(
                                this@VisualiMedActivity,
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                                    Manifest.permission.BLUETOOTH_CONNECT
                                ),
                                1
                            )
                        }
                    }
                    bluetoothGatt?.discoverServices()
                    Log.v(TAG,bluetoothGatt?.services.toString())
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                Log.v(TAG, "onServicesDiscovered recibido: $status")
                // TODO: ESTO ES DE PRUEBA
                gatt?.services?.forEach { it ->
                    it.characteristics.forEach{ x ->
                        Log.v(TAG, x.toString())
                    }
                }

            }

            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                Log.v(TAG, "onCharacteristicRead")
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                Log.v(TAG, "onCharacteristicChanged")
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.welcome)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun lanzaNotificacion(notif: Boolean, titulo: String, mensaje: String) {
        if (notif) {
            createNotificationChannel()
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notifationManagerCompat = NotificationManagerCompat.from(this)
            notifationManagerCompat.notify(Random.nextInt(0,999999), builder.build())
        }
    }

}