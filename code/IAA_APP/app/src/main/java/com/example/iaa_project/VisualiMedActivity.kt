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
import android.os.Handler
import android.os.Looper
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
import kotlinx.coroutines.runBlocking
import java.io.DataInputStream
import java.io.InputStream
import java.util.*
import java.util.concurrent.Executors
import kotlin.random.Random

class VisualiMedActivity : AppCompatActivity() {

    var variables: ActivityVisualiMedBinding? = null
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var conectados = ArrayList<BluetoothDevice>()
    var pacienteMed = ""
    var idUsuMed: EditText? = null
    var errorProvocado = ""
    var myHandler: Handler? = null

    var bAdapter: BluetoothAdapter? = null
    var bluetoothGatt: BluetoothGatt? = null

    var muestraAcelT1: Button? = null
    var muestraGirosT1: Button? = null
    var muestraTempAmbT1: Button? = null
    var muestraBattT1: Button? = null

    var muestraElectDermT2: Button? = null
    var muestraTempCorpT2: Button? = null
    var muestraAcelT2: Button? = null
    var muestraGirosT2: Button? = null
    var muestraBattT2: Button? = null

    var muestraFrecCardT3: Button? = null
    var muestraFrecRespT3: Button? = null
    var muestraTempAmbT3: Button? = null
    var muestraGirosT3: Button? = null
    var muestraBattT3: Button? = null

    var actividad = true

    var extendedFab: ExtendedFloatingActionButton? = null

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
        if (bundle?.getParcelableArrayList<BluetoothDevice>("conectados")!!.isNotEmpty()) {
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

        if (conectados.isNotEmpty()) {
            for (i in conectados) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
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
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                ),
                                1
                            )
                        }
                    }
                }
                if (i.name == "LegMonitor" || i.name == "Type1") {
                    conectaDispositivos(i)
                    runBlocking {
                        delay(1500)
                    }
                } else if (i.name == "WristMonitor" || i.name == "Type2") {
                    conectaDispositivos(i)
                    runBlocking {
                        delay(1500)
                    }
                } else {
                    conectaDispositivos(i)
                    runBlocking {
                        delay(1500)
                    }
                }
            }
        }

        val executorSesion = Executors.newSingleThreadExecutor()

        extendedFab?.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle(R.string.app_name)
            //set message for alert dialog
            builder.setMessage("¿Está seguro de querer detener la sesión?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Sí") { _, _ ->
                executorSesion.execute {
                    val intento2 = Intent(this, ResumenMedicionesActivity::class.java)
                    intento2.putExtra("notifUsuDef", notifUsuDef)
                    intento2.putExtra("idUsuDef", idUsuDef)
                    intento2.putExtra("dniUsuDef", dniUsuDef)
                    intento2.putExtra("apellUsuDef", apellUsuDef)
                    intento2.putExtra("nombUsuDef", nombUsuDef)
                    intento2.putExtra("fechaUsuDef", fechaUsuDef)
                    intento2.putExtra("pwUsuDef", pwUsuDef)
                    intento2.putParcelableArrayListExtra("conectados", conectados)
                    intento2.putExtra("pacienteMed", pacienteMed)
                    intento2.putExtra("resumenSesion", resumen)
                    actividad = false
                    bluetoothGatt?.disconnect()
                    bluetoothGatt?.close()
                    bAdapter?.disable()
                    startActivity(intento2)
                }
            }
            builder.setNegativeButton("No") { _, _ ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }


    }

    private fun conectaDispositivos(device: BluetoothDevice) {
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
        bluetoothGatt = device.connectGatt(this, false, bleGattCallback)

    }

    private val bleGattCallback: BluetoothGattCallback by lazy {
        object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
//                super.onConnectionStateChange(gatt, status, newState)
                Log.v(TAG, "onConnectionStateChange")
                if (newState == BluetoothProfile.STATE_CONNECTED) {
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
                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                ),
                                1
                            )
                        }
                    }
                    bluetoothGatt?.discoverServices()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                Log.v(TAG, "onServicesDiscovered, status: $status")
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
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ),
                            1
                        )
                    }
                }
                gatt?.requestMtu(100)
                runBlocking {
                    delay(1500)
                }
                gatt?.services?.forEach { it ->
                    if (it?.uuid.toString().lowercase() == "0000acc0-0000-1000-8000-00805f9b34fb") {
                        println("Prueba de fuego 1: ${it.uuid}")
                        println(
                            gatt.getService(UUID.fromString("0000acc0-0000-1000-8000-00805f9b34fb"))
                                .toString()
                        )
                        runOnUiThread {
                            // TODO
                            it.characteristics.forEach { x ->
                                println("Se viene esto, ojito: ${x.uuid}")
                                /*if(it.uuid.toString().lowercase()=="0000acc5-0000-1000-8000-00805f9b34fb"){
                                    //lecturaAutomatica(gatt,it)
                                    println("Entro en el if")

                                    println("Hola que pasa omio")
                                    gatt.readCharacteristic(x)
                                    println("CHACHAAAAN")
                                }*/
                                //TODO: VER COMO HACERLO PARA QUE SE REPITA SIN PETAR MUCHO
                                if (gatt.device?.name == "LegMonitor" || gatt.device?.name == "Type1") {
                                    var fixedRateTimer1 = Timer()
                                    fixedRateTimer1.scheduleAtFixedRate(object : TimerTask() {
                                            override fun run() {
                                                if (!actividad) {
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
                                                                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                                                ),
                                                                1
                                                            )
                                                        }
                                                    }
                                                    gatt.setCharacteristicNotification(x, false)

                                                    gatt.disconnect()
                                                    gatt.close()
                                                }
                                                println("tamos ready")
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
                                                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                                            ),
                                                            1
                                                        )
                                                    }
                                                }
                                                gatt.setCharacteristicNotification(x, true)
                                                gatt.readCharacteristic(x)
                                            }
                                        }, 2000, 1000)
                                    if(!actividad){
                                        fixedRateTimer1.cancel()
                                    }
                                }
                                if (gatt.device?.name == "WristMonitor" || gatt.device?.name == "Type2") {
                                    val myExecutor2 = Executors.newSingleThreadExecutor()
                                    myExecutor2.execute {
                                        println("tamos ready")
                                        gatt.setCharacteristicNotification(x, true)
                                        gatt.readCharacteristic(x)
                                    }
                                }
                                if (gatt.device?.name == "ChestMonitor" || gatt.device?.name == "Type3") {
                                    val fixedRateTimer3 =
                                        Timer().scheduleAtFixedRate(object : TimerTask() {
                                            override fun run() {
                                                /*if (!actividad) {
                                                    onDestroy()
                                                }*/
                                                println("tamos ready")
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
                                                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                                            ),
                                                            1
                                                        )
                                                    }
                                                }
                                                gatt.setCharacteristicNotification(x, true)
                                                gatt.readCharacteristic(x)
                                            }
                                        }, 0, 1000)

                                }
                            }
                        }

                    }
                }
            }

            @SuppressLint("MissingPermission")
            override fun onCharacteristicRead(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                val data = characteristic!!.value
                println("reading")
                var resultadojeje = ""
                for (i in data) {
                    resultadojeje += "$i "
                }
                print("Resultado de ${gatt?.device?.name}: $resultadojeje\n")
                myHandler = Handler(Looper.getMainLooper())
                myHandler!!.post {
                    when (gatt?.device?.name) {
                        "Type1" -> lecturaLeg(data)
                        "LegMonitor" -> lecturaLeg(data)
                        "Type2" -> lecturaWrist(data)
                        "WristMonitor" -> lecturaWrist(data)
                        "Type3" -> lecturaChest(data)
                        "ChestMonitor" -> lecturaChest(data)
                    }
                }
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
            notifationManagerCompat.notify(Random.nextInt(0, 999999), builder.build())
        }
    }

    private fun normal(boton: Button, valor: String) {
        boton.text = valor
        boton.setTextColor(this.getColor(R.color.black))
        boton.backgroundTintList = ColorStateList.valueOf(this.getColor(R.color.cuadroNormal))
    }

    private fun peligroso(boton: Button, valor: String) {
        boton.text = valor
        boton.setTextColor(this.getColor(R.color.white))
        boton.backgroundTintList = ColorStateList.valueOf(this.getColor(R.color.cuadroPeligro))
    }

    private fun cuidado(boton: Button, valor: String) {
        boton.text = valor
        boton.setTextColor(this.getColor(R.color.black))
        boton.backgroundTintList = ColorStateList.valueOf(this.getColor(R.color.cuadroCuidado))
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

    fun lecturaChest(data: ByteArray) {
        if (data.size != 72) {
            //TODO: ERROR EN LA LECTURA
        } else {
            var frecuenciaCard = data.slice(0..31)
            var acelerometro = data.slice(32..43)
            var giroscopio = data.slice(44..55)
            var respiracion = data.slice(56..59)
            var bateria = data.slice(60..61)
            var tempAmbiente = data.slice(62..63)
            colocaDatoT3("frecuenciaCard", frecuenciaCard)
            //colocaDatoT3("acelerometro",acelerometro)
            colocaDatoT3("giroscopio", giroscopio)
            colocaDatoT3("respiracion", respiracion)
            colocaDatoT3("bateria", bateria)
            colocaDatoT3("tempAmbiente", tempAmbiente)
        }
    }

    fun lecturaWrist(data: ByteArray) {
        if (data.size != 36) {
            // TODO: ERROR EN LA LECTURA
        } else {
            val electroDermica = data.slice(0..4)
            val acelerometro = data.slice(4..16)
            val giroscopio = data.slice(16..28)
            val bateria = data.slice(28..30)
            val temperaturaCorporal = data.slice(32..34)
            val tempAmbiente = data.slice(30..32)
            colocaDatoT2("electroDermica", electroDermica)
            colocaDatoT2("acelerometro", acelerometro)
            colocaDatoT2("giroscopio", giroscopio)
            colocaDatoT2("bateria", bateria)
            colocaDatoT2("temperaturaCorporal", temperaturaCorporal)
            colocaDatoT2("tempAmbiente", tempAmbiente)

        }
    }

    fun lecturaLeg(data: ByteArray) {
        if (data.size != 28) {
            // TODO: ERROR EN LA LECTURA
        } else {
            val acelerometro = data.slice(0..11)
            val giroscopio = data.slice(12..23)
            val bateria = data.slice(24..25)
            val tempAmbiente = data.slice(26..27)
            colocaDatoT1("acelerometro", acelerometro)
            colocaDatoT1("giroscopio", giroscopio)
            colocaDatoT1("bateria", bateria)
            colocaDatoT1("tempAmbiente", tempAmbiente)

            //TODO: CONTINUAR CODIGO
        }
    }

    //TODO: Hay que añadir todos los métodos

    fun lecturaBateria(br: List<Byte>): Float {
        val lista = ArrayList<Float>()
        val largo = br.size
        println("largo = $largo")
        for (j in 0..1) {
            val aux = br[1]
            val mf = aux.toUInt()
            var ax = (mf.toFloat() * 3.3 / 1023.0).toFloat()
            ax = 5 * ax / 3
            lista.add(ax)
        }
        return lista[0]
    }

    fun colocaDatoT1(campo: String, br: List<Byte>) {
        when (campo) {
            "acelerometro" -> muestraAcelT1?.let { normal(it, br[0].toString()) }
            "giroscopio" -> muestraGirosT1?.text = br[0].toString()
            "bateria" -> muestraBattT1?.let { normal(it, lecturaBateria(br).toString()) }
            "tempAmbiente" -> muestraTempAmbT1?.text = br[0].toString()
        }
    }

    fun colocaDatoT2(campo: String, br: List<Byte>) {
        when (campo) {
            "electroDermica" -> muestraElectDermT2?.let { normal(it, br[0].toString()) }
            "acelerometro" -> muestraAcelT2?.let { normal(it, br[0].toString()) }
            "giroscopio" -> muestraGirosT2?.let { normal(it, br[0].toString()) }
            "bateria" -> muestraBattT2?.let { normal(it, lecturaBateria(br).toString()) }
            "temperaturaCorporal" -> muestraTempCorpT2?.let { normal(it, br[0].toString()) }
        }
    }

    fun colocaDatoT3(campo: String, br: List<Byte>) {
        when (campo) {
            "frecuenciaCard" -> muestraFrecCardT3?.let { normal(it, br[0].toString()) }
            "respiracion" -> muestraFrecRespT3?.let { normal(it, br[0].toString()) }
            "giroscopio" -> muestraGirosT3?.text=br[0].toString()
            "bateria" -> muestraBattT3?.let { normal(it, lecturaBateria(br).toString()) }
            "tempAmbiente" -> muestraTempAmbT3?.let { normal(it, br[0].toString()) }
        }
    }


}