package com.example.iaa_project

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityVisualiMedBinding
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.delay
import java.io.DataInputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.Executors
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

    var extendedFab : ExtendedFloatingActionButton? = null

    var serverSocket: BluetoothServerSocket? = null
    val myExecutor = Executors.newSingleThreadExecutor()

    var resumen = "Esto es un texto de prueba.\n\nMuchas gracias."

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

        extendedFab = variables!!.extendedFab

        if(conectados.isNotEmpty()){
            conectaDispositivos(conectados)
            myExecutor.execute {
                var i = 0
                while(i%1000==0){
                    leeDatosDispositivos(conectados)
                    i++
                }
            }
        }

        val executorSesion = Executors.newSingleThreadExecutor()

        extendedFab?.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle(R.string.app_name)
            //set message for alert dialog
            builder.setMessage("¿Está seguro de querer detener la sesión?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Sí"){_, _ ->
                executorSesion.execute {
                    val intento2 = Intent(this, ResumenMedicionesActivity::class.java)
                    intento2.putExtra("notifUsuDef", notifUsuDef)
                    intento2.putExtra("idUsuDef", idUsuDef)
                    intento2.putExtra("dniUsuDef", dniUsuDef)
                    intento2.putExtra("apellUsuDef", apellUsuDef)
                    intento2.putExtra("nombUsuDef", nombUsuDef)
                    intento2.putExtra("fechaUsuDef", fechaUsuDef)
                    intento2.putExtra("pwUsuDef", pwUsuDef)
                    intento2.putParcelableArrayListExtra("conectados",conectados)
                    intento2.putExtra("pacienteMed", pacienteMed)
                    intento2.putExtra("resumenSesion",resumen)
                    startActivity(intento2)
                }
            }
            builder.setNegativeButton("No"){_, _ ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }


    }

    private fun conectaDispositivos(lista: ArrayList<BluetoothDevice>) {
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
                        Log.v(TAG, "Descubierto ${x.toString()}")
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

    // TODO: ESTO PARECE QUE NO FUNCIONA
    @SuppressLint("MissingPermission")
    private fun leeDatosDispositivos(lista: ArrayList<BluetoothDevice>){
        for(l in lista){
            if(l.name=="LegMonitor"){
                //actuaTipo1(l)
            }
            if(l.name=="WristMonitor"){

            }
            if(l.name=="ChestMonitor"){

            }
        }
    }

    private fun normal(boton: Button, valor: String){
        boton.text=valor
        boton.setTextColor(this.getColor(R.color.black))
        boton.backgroundTintList=ColorStateList.valueOf(this.getColor(R.color.cuadroNormal))
    }

    private fun peligroso(boton: Button, valor: String){
        boton.text=valor
        boton.setTextColor(this.getColor(R.color.white))
        boton.backgroundTintList=ColorStateList.valueOf(this.getColor(R.color.cuadroPeligro))
    }

    private fun cuidado(boton: Button, valor: String){
        boton.text=valor
        boton.setTextColor(this.getColor(R.color.black))
        boton.backgroundTintList=ColorStateList.valueOf(this.getColor(R.color.cuadroCuidado))
    }

    /*
    private fun actuaTipo1(device: BluetoothDevice){
        var socket: BluetoothSocket? = null
        try {
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
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ),
                        1
                    )
                }
            }
            serverSocket = bAdapter?.listenUsingRfcommWithServiceRecord(R.string.app_name.toString(), UUID.randomUUID())
            socket = serverSocket?.accept()
        } catch (e: Exception){
            Log.v(TAG,e.toString())
        }
        val buffer = ByteArray(255) // buffer store for the stream

        val bytes: Int // bytes returned from read()

        try {
            Log.d(this.title as String, "Closing Server Socket.....")
            serverSocket?.close()
            var recibido: InputStream? = null

            // Get the BluetoothSocket input and output streams
            recibido = socket!!.inputStream
            val mmInStream = DataInputStream(recibido)
            // here you can use the Input Stream to take the string from the client  whoever is connecting
            //similarly use the output stream to send the data to the client

            // Read from the InputStream
            bytes = mmInStream.read(buffer)
            val readMessage = String(buffer, 0, bytes)
            // Send the obtained bytes to the UI Activity
            //text.setText(readMessage)
            Log.v(TAG,"Recibido esto: $readMessage")
        } catch (e: java.lang.Exception) {
            //catch your exception here
        }
    }
    // TODO: HASTA AQUÍ
*/

    fun lecturaChest(data: ByteArray){
        if(data.size!=64){
            //TODO: ERROR EN LA LECTURA
        }else{
            var frecuenciaCard = data.slice(0..32)
            var acelerometro = data.slice(32..44)
            var giroscopio = data.slice(44..56)
            var respiracion = data.slice(56..60)
            var bateria = data.slice(60..62)
            var tempAmbiente = data.slice(62..64)
            //TODO: CONTINUAR CODIGO
        }
    }

    fun lecturaWrist(data: ByteArray){
        if(data.size!=36){
            // TODO: ERROR EN LA LECTURA
        }else{
            var electroDermica = data.slice(0..4)
            var acelerometro = data.slice(4..16)
            var giroscopio = data.slice(16..28)
            var bateria = data.slice(28..30)
            var temperaturaCorporal = data.slice(32..34)
            var tempAmbiente = data.slice(30..32)
            //TODO: CONTINUAR CODIGO
        }
    }

    fun lecturaLeg(data: ByteArray){
        if(data.size!=28){
            // TODO: ERROR EN LA LECTURA
        }else{
            var acelerometro = data.slice(0..12)
            var giroscopio = data.slice(12..24)
            var bateria = data.slice(24..26)
            var tempAmbiente = data.slice(26..28)
            //TODO: CONTINUAR CODIGO
        }
    }

    //TODO: Hay que añadir todos los métodos

    fun lecturaBateria(br: ByteArray){
        var lista = ArrayList<Int>()
        var largo = br.size
        // TODO: CONTINUAR
    }

}