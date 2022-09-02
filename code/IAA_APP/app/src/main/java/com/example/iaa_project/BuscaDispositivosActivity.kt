package com.example.iaa_project

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.AsyncTask.execute
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import com.example.iaa_project.R.mipmap.ib_bt_connect
import com.example.iaa_project.R.mipmap.ib_bt_search
import com.example.iaa_project.databinding.ActivityBuscaDispositivosBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.time.LocalTime
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.RunnableFuture
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.thread
import kotlin.concurrent.timerTask
import kotlin.random.Random


class BuscaDispositivosActivity : AppCompatActivity() {

    var variables: ActivityBuscaDispositivosBinding? = null
    var bAdapter: BluetoothAdapter? = null
    val dL2 = ArrayList<BluetoothDevice>()
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private var scanner: BluetoothLeScanner? = null
    private var callback: BleScanCallback? = null
    private val foundDevices = HashMap<String, BluetoothDevice>()
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var correoInvest = ""
    var pwUsuDef = ""
    var myHandler: Handler? = null
    var actividad = true
    var tablaDispositivos: TableLayout? = null
    var conectados = ArrayList<BluetoothDevice>()
    var estanConectados = false
    var btnConectarTodo: Button? = null
    var lecturaAutomatica = true

    private companion object {
        private const val CHANNEL_ID = "channel01"
        const val TAG = "IAAPROJECT"
        const val BLUETOOTH_REQUEST_CODE = 1
    }

    var bluetoothGatt: BluetoothGatt? = null

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        if(conectados.isEmpty()){
            Log.v(TAG,"No hay dispositivos conectados")
        }else{
            for(d in conectados){
                Log.v(TAG,"Dispositivo conectado: ${d.name}")
            }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_busca_dispositivos)

        variables = ActivityBuscaDispositivosBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        correoInvest = bundle?.getString("correoInvest").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        conectados.addAll(bundle?.getParcelableArrayList<BluetoothDevice>("conectados")!!)

        val btm = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = btm.adapter

        tablaDispositivos = variables!!.selectDeviceList

        btnConectarTodo = variables!!.btnConectaAll

        if (bAdapter == null) {
            Toast.makeText(this, "Este dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show()
            lanzaNotificacion(
                notifUsuDef,
                "Error con el Bluetooth",
                "Este dispositivo no soporta Bluetooth, por lo que no tendrá operativa esta funcionalidad."
            )
            return
        }
        if (!bAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            if (enableBluetoothIntent.resolveActivity(packageManager) != null) {
                getAction.launch(enableBluetoothIntent)
            }

        }

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

        println("Notificaciones: $notifUsuDef")


        if (bAdapter!!.isEnabled) {

            val handler2 = Handler(Looper.getMainLooper())
            handler2.post {
                starBLEScan()
            }

            myHandler = Handler(Looper.getMainLooper())
            myHandler!!.post {
                Toast.makeText(
                    this,
                    "Inicializando el escáner de dispositivos Bluetooth",
                    Toast.LENGTH_SHORT
                ).show()
            }
            myHandler!!.postDelayed(object : Runnable {
                override fun run() {
                    if (actividad) {
                            println("SEÑAL 1: Empiezo a ejecutarme aquí")
                            cargaListado()
                            myHandler!!.postDelayed(this, 5000)
                    }
                }
            }, 2000)

        }
        variables!!.selectDeviceRefresh.setOnClickListener {
            Log.v(TAG, imprimeMap(foundDevices))
            //
            this.onBackPressed()

        }

        btnConectarTodo!!.setOnClickListener {
            if(!estanConectados){
                conectaTodos(dL2)
            }else{
                desconectaTodos(dL2)
            }
        }

    }

    @SuppressLint("MissingPermission")
    val getAction = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        bAdapter?.enable()
        Toast.makeText(this, "Encendido", Toast.LENGTH_SHORT).show()
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
                .setSmallIcon(R.drawable.ic_iaa_notif)
                .setColor(this.getColor(R.color.IAA_naranjaNotif))
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notifationManagerCompat = NotificationManagerCompat.from(this)
            notifationManagerCompat.notify(Random.nextInt(0,999999), builder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun starBLEScan() {
        Log.v(TAG, "${LocalTime.now()} - StartBLEScan")
        //val scanFilter = ScanFilter.Builder().build()
        /*val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid.fromString("0000acc0-0000-1000-8000-00805f9b34fb")).build()

        val scanFilters: MutableList<ScanFilter> = mutableListOf()
        scanFilters.add(scanFilter)*/

        val scanFilter1 = ScanFilter.Builder().setDeviceName("LegMonitor").build()
        val scanFilter2 = ScanFilter.Builder().setDeviceName("Type1").build()
        val scanFilter3 = ScanFilter.Builder().setDeviceName("WristMonitor").build()
        val scanFilter4 = ScanFilter.Builder().setDeviceName("Type2").build()
        val scanFilter5 = ScanFilter.Builder().setDeviceName("ChestMonitor").build()
        val scanFilter6 = ScanFilter.Builder().setDeviceName("Type3").build()

        val scanFilters: MutableList<ScanFilter> = mutableListOf()
        scanFilters.add(scanFilter1)
        scanFilters.add(scanFilter2)
        scanFilters.add(scanFilter3)
        scanFilters.add(scanFilter4)
        scanFilters.add(scanFilter5)
        scanFilters.add(scanFilter6)


        val scanSettings =
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        if (callback == null) {
            callback = BleScanCallback()
            scanner = bAdapter!!.bluetoothLeScanner

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("SEÑAL X: ¿He entrado aquí?")
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.BLUETOOTH_SCAN
                        ),
                        1
                    )
                }
            }
            Log.v(TAG, "Ejecuto fuera de los permisos")
            bAdapter!!.startDiscovery()
            scanner!!.startScan(scanFilters, scanSettings, callback)
            Log.v(TAG, "Escaner supuestamente iniciado")
        }

    }

    private fun stopScan() {
        if (callback != null) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("SEÑAL X: ¿He entrado aquí para detener el escaneo?")
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
            scanner!!.stopScan(callback)
            scanner = null
            callback = null
        }
    }

    inner class BleScanCallback : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            Log.v(TAG, "Pues aquí estamos porque hemos venido")
            foundDevices[result!!.device.address] = result.device

            Log.e(
                "${LocalTime.now()} - Nuevo dispositivo",
                "Dispositivo: ${result}\nDirección: ${result.device.address}"
            )
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            Log.v(TAG, "onBatchScanResults - Lo pongo porque no sé qué hace")
            super.onBatchScanResults(results)
            results!!.forEach { result ->
                foundDevices[result.device.address] = result.device
            }
        }

    }

    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        intent.putExtra("idUsuDef", idUsuDef)
        intent.putExtra("dniUsuDef", dniUsuDef)
        intent.putExtra("apellUsuDef", apellUsuDef)
        intent.putExtra("nombUsuDef", nombUsuDef)
        intent.putExtra("fechaUsuDef", fechaUsuDef)
        intent.putExtra("pwUsuDef", pwUsuDef)
        intent.putExtra("notifUsuDef", notifUsuDef)
        intent.putExtra("correoInvest", correoInvest)
        //intent.putExtra("conectados",conectados)
        intent.putParcelableArrayListExtra("conectados",conectados)
        startActivity(intent)
        stopScan()
        actividad=false
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this@BuscaDispositivosActivity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    1
                )
            }
        }
        bluetoothGatt?.close()
    }

    private fun imprimeMap(map: HashMap<String, BluetoothDevice>): String {
        val com = "IAA-PROJECT: Contenido del MAP"
        var anade = ""
        for (e in map) {
            anade = "$anade\n${e.key}"
        }
        return "$com $anade"
    }


    @SuppressLint("SetTextI18n")
    private fun cargaListado() {

        tablaDispositivos!!.removeAllViews()
        // variables
        if (foundDevices.isNotEmpty()) {
            Log.v(TAG, "Número de dispositivos encontrados: ${foundDevices.size}")
            for (e in foundDevices) {
                /*if (!mPairedDevices.contains(e.value)){
                    mPairedDevices.plusElement(e.value)
                }*/
                if (!dL2.contains(e.value)) {
                    dL2.add(e.value)
                }
            }
        }
        val list: ArrayList<BluetoothDevice> = ArrayList()
        if (dL2.isNotEmpty()) {
            Log.v(TAG, "Número de dispositivos en la lista: ${dL2.size}")
            for (device: BluetoothDevice in dL2) {
                //if(device.name=="LegMonitor" || device.name=="ChestMonitor" || device.name=="WristMonitor")
                list.add(device)
            }
        }
        if(conectados.isNotEmpty()){
            for (c in conectados){
                if(!list.contains(c)){
                    list.add(c)
                }
            }
        }
        if(list.isEmpty()){
            Toast.makeText(this, "No se han encontrado dispositivos", Toast.LENGTH_SHORT).show()
        }
        var compara = true
        for(l in list){
            if(!conectados.contains(l)){
                compara = false
            }
        }
        estanConectados = compara

        var layoutCelda: TableRow.LayoutParams
        val layoutFila = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            200
        )
        var aux = 0
        for (l in list) {
            val fila = TableRow(this)
            var imgBT: ImageView = ImageView(this).apply {
                id = ViewCompat.generateViewId()
                scaleType = ImageView.ScaleType.CENTER
                setImageResource(ib_bt_search)
            }
            if (conectados.contains(l)) {
                imgBT.setImageResource(ib_bt_connect)
            }
            fila.addView(imgBT)
            val numero = list.indexOf(l)
            fila.id = numero
            fila.layoutParams = layoutFila
            fila.textAlignment = TableRow.TEXT_ALIGNMENT_CENTER
            val texto = TextView(this)
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
            texto.text = "${l.name}\n${l.address}    "
            texto.gravity = Gravity.CENTER_VERTICAL
            texto.textSize = 16.0F
            layoutCelda = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                200
            )
            texto.layoutParams = layoutCelda
            texto.isLongClickable=true
            fila.addView(texto)
            val boton = Button(this)
            if (conectados.contains(l)) {
                boton.text = "Desconectar"
                boton.backgroundTintList= ColorStateList.valueOf(this.getColor(R.color.IAA_naranja))
            } else {
                boton.text = "Conectar"
                boton.backgroundTintList=ColorStateList.valueOf(this.getColor(R.color.IAA_azulClaro))
            }
            boton.id = list.indexOf(l)
            boton.width = 336
            fila.addView(boton)
            fila.gravity = Gravity.CENTER
            aux++
            tablaDispositivos!!.addView(fila)

            val executorSesion = Executors.newSingleThreadExecutor()

            boton.setOnClickListener {
                if (boton.text == "Conectar") {
                    val builder = AlertDialog.Builder(this)
                    //set title for alert dialog
                    builder.setTitle(R.string.app_name)
                    //set message for alert dialog
                    builder.setMessage("¿Quiere conectarse al dispositivo ${l.name}?")
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                    builder.setPositiveButton("Sí") { dialogInterface, which ->
                        executorSesion.execute {
                            executorSesion.execute {
                                //eliminaSesion(numero)
                                conectarDispositivo(l)
                                lanzaNotificacion(
                                    notifUsuDef,
                                    "Dispositivo Bluetooth conectado",
                                    "Se ha conectado correctamente con el dispositivo ${l.name}"
                                )
                                conectados.add(l)
                            }
                        }
                    }
                    builder.setNegativeButton("No") { dialogInterface, which ->

                    }
                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                } else {
                    println("El width del botón es ${boton.width}\nEl height es ${boton.height}")
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.app_name)
                    builder.setMessage("¿Quiere desconectar el dispositivo ${l.name}?")
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                    builder.setPositiveButton("Sí") { dialogInterface, which ->
                        executorSesion.execute {
                            executorSesion.execute {
                                bluetoothGatt?.disconnect()
                                bluetoothGatt?.close()
                                conectados.remove(l)
                                lanzaNotificacion(
                                    notifUsuDef,
                                    "Dispositivo Bluetooth desconectado",
                                    "Se ha desconectado correctamente del dispositivo ${l.name}"
                                )
                            }
                        }
                    }
                    builder.setNegativeButton("No") { dialogInterface, which ->

                    }
                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }
            }
            texto.setOnLongClickListener {
                if (boton.text == "Conectar") {
                    val builder = AlertDialog.Builder(this)
                    //set title for alert dialog
                    builder.setTitle(R.string.app_name)
                    //set message for alert dialog
                    builder.setMessage("¿Quiere conectarse al dispositivo ${l.name}?")
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                    builder.setPositiveButton("Sí") { dialogInterface, which ->
                        executorSesion.execute {
                            executorSesion.execute {
                                //eliminaSesion(numero)
                                conectarDispositivo(l)
                                lanzaNotificacion(
                                    notifUsuDef,
                                    "Dispositivo Bluetooth conectado",
                                    "Se ha conectado correctamente con el dispositivo ${l.name}"
                                )
                                conectados.add(l)
                            }
                        }
                    }
                    builder.setNegativeButton("No") { dialogInterface, which ->

                    }
                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                } else {
                    println("El width del botón es ${boton.width}\nEl height es ${boton.height}")
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(R.string.app_name)
                    builder.setMessage("¿Quiere desconectar el dispositivo ${l.name}?")
                    builder.setIcon(android.R.drawable.ic_dialog_alert)
                    builder.setPositiveButton("Sí") { dialogInterface, which ->
                        executorSesion.execute {
                            executorSesion.execute {
                                bluetoothGatt?.disconnect()
                                bluetoothGatt?.close()
                                conectados.remove(l)
                                lanzaNotificacion(
                                    notifUsuDef,
                                    "Dispositivo Bluetooth desconectado",
                                    "Se ha desconectado correctamente del dispositivo ${l.name}"
                                )
                            }
                        }
                    }
                    builder.setNegativeButton("No") { dialogInterface, which ->

                    }
                    // Create the AlertDialog
                    val alertDialog: AlertDialog = builder.create()
                    // Set other dialog properties
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                }
                true
            }

        }

        tablaDispositivos!!.textAlignment = TableLayout.TEXT_ALIGNMENT_CENTER
        tablaDispositivos!!.gravity = Gravity.CENTER_HORIZONTAL
        if(estanConectados){
            btnConectarTodo?.text = "Desconectar todo"
        }else{
            btnConectarTodo?.text="Conectar todo"
        }

    }

    private fun conectaTodos(lista: ArrayList<BluetoothDevice>){
        val executorSesion = Executors.newSingleThreadExecutor()
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(R.string.app_name)
        //set message for alert dialog
        builder.setMessage("¿Quiere conectarse a todos los dispositivos?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Sí") { dialogInterface, which ->
            executorSesion.execute {
                executorSesion.execute {
                    for(l in lista){
                        conectarDispositivo(l)
                        conectados.add(l)
                    }
                }
                lanzaNotificacion(
                    notifUsuDef,
                    "Dispositivos Bluetooth conectados",
                    "Se han conectado correctamente todos los dispositivos"
                )
            }
        }
        builder.setNegativeButton("No") { dialogInterface, which ->

        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
        estanConectados=true
    }

    private fun desconectaTodos(lista: ArrayList<BluetoothDevice>){
        val executorSesion = Executors.newSingleThreadExecutor()
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.app_name)
        builder.setMessage("¿Quiere desconectar todos los dispositivos?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Sí") { dialogInterface, which ->
            executorSesion.execute {
                executorSesion.execute {
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
                    for(l in lista){
                        bluetoothGatt?.disconnect()
                        bluetoothGatt?.close()
                        conectados.remove(l)

                    }
                    lanzaNotificacion(
                        notifUsuDef,
                        "Dispositivos Bluetooth desconectados",
                        "Se han desconectado correctamente todos los dispositivos"
                    )
                }
            }
        }
        builder.setNegativeButton("No") { dialogInterface, which ->

        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
        estanConectados=false
    }


    private fun conectarDispositivo(device: BluetoothDevice) {
        Log.v(TAG, "conectarDispositivo")
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
        //BluetoothClient(device).run()
    }

    private val bleGattCallback: BluetoothGattCallback by lazy {
        object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
//                super.onConnectionStateChange(gatt, status, newState)
                Log.v(TAG, "onConnectionStateChange")
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    if (ActivityCompat.checkSelfPermission(
                            this@BuscaDispositivosActivity,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                            ActivityCompat.requestPermissions(
                                this@BuscaDispositivosActivity,
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
                        this@BuscaDispositivosActivity,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                        ActivityCompat.requestPermissions(
                            this@BuscaDispositivosActivity,
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
                gatt?.services?.forEach{ it ->
                    if(it?.uuid.toString().lowercase() == "0000acc0-0000-1000-8000-00805f9b34fb"){
                        println("Prueba de fuego 1: ${it.uuid}")
                        println(gatt.getService(UUID.fromString("0000acc0-0000-1000-8000-00805f9b34fb")).toString())
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
                                if(gatt.device?.name=="LegMonitor" || gatt.device?.name=="Type1"){
                                    val fixedRateTimer1 = Timer().scheduleAtFixedRate(object: TimerTask(){
                                        override fun run(){
                                            if(!actividad) {

                                            }
                                            println("tamos ready")
                                            if (ActivityCompat.checkSelfPermission(
                                                    this@BuscaDispositivosActivity,
                                                    Manifest.permission.BLUETOOTH_CONNECT
                                                ) != PackageManager.PERMISSION_GRANTED
                                            ) {
                                                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                                                    ActivityCompat.requestPermissions(
                                                        this@BuscaDispositivosActivity,
                                                        arrayOf(
                                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                                        ),
                                                        1
                                                    )
                                                }
                                            }
                                            gatt.setCharacteristicNotification(x,true)
                                            gatt.readCharacteristic(x)
                                            println("leido AAAAAAAAAAAAAA")
                                        }
                                    },250,1000)
                                }
                                if(gatt.device?.name=="WristMonitor" || gatt.device?.name=="Type2"){
                                    val myExecutor2 = Executors.newSingleThreadExecutor()
                                    myExecutor2.execute {
                                        println("tamos ready")
                                        gatt.setCharacteristicNotification(x,true)
                                        gatt.readCharacteristic(x)
                                        println("leido AAAAAAAAAAAAAA")
                                    }
                                }
                                if(gatt.device?.name=="ChestMonitor" || gatt.device?.name=="Type3"){
                                    val fixedRateTimer3 = Timer().scheduleAtFixedRate(object: TimerTask(){
                                        override fun run(){
                                            if(!actividad) {

                                            }
                                            println("tamos ready")
                                            if (ActivityCompat.checkSelfPermission(
                                                    this@BuscaDispositivosActivity,
                                                    Manifest.permission.BLUETOOTH_CONNECT
                                                ) != PackageManager.PERMISSION_GRANTED
                                            ) {
                                                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                                                    ActivityCompat.requestPermissions(
                                                        this@BuscaDispositivosActivity,
                                                        arrayOf(
                                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                                                        ),
                                                        1
                                                    )
                                                }
                                            }
                                            gatt.setCharacteristicNotification(x,true)
                                            gatt.readCharacteristic(x)
                                            println("leido AAAAAAAAAAAAAA")
                                        }
                                    },0,1000)

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
                /*val data = characteristic!!.value
                println("reading")
                var resultadojeje = ""
                for(i in data){
                    resultadojeje += "$i "
                }
                print("Resultado de ${gatt?.device?.name}: $resultadojeje\n")*/
            }

            override fun onCharacteristicChanged(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?
            ) {
                Log.v(TAG, "onCharacteristicChanged")
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun lecturaAutomatica(bluetoothGatt: BluetoothGatt, it: BluetoothGattCharacteristic){
        bluetoothGatt.setCharacteristicNotification(it, lecturaAutomatica)
        bluetoothGatt.requestMtu(100)
        bluetoothGatt.readCharacteristic(it)
        println(it.value)
        println(bluetoothGatt.readCharacteristic(it).toString())
        /*
        for(d in it.descriptors){
            val descriptor = it.getDescriptor(UUID.fromString("0000acc0-0000-1000-8000-00805f9b34fb")).apply {
                if(lecturaAutomatica){
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    bluetoothGatt.requestMtu(100)
                    bluetoothGatt.readDescriptor(d)
                    println(it.value)
                }else{
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                }
            }*/
            /*println(bluetoothGatt.getService(UUID.fromString("0000acc0-0000-1000-8000-00805f9b34fb")).getCharacteristic(
                UUID.fromString("0000acc5-0000-1000-8000-00805f9b34fb")).value)*/
            //val success = bluetoothGatt.writeDescriptor(descriptor)
            //bluetoothGatt.readDescriptor(descriptor)

        //}

    }

    class BluetoothClient(device: BluetoothDevice): Thread() {
        @SuppressLint("MissingPermission")
        private val socket = device.createRfcommSocketToServiceRecord(UUID.fromString("0000acc5-0000-1000-8000-00805f9b34fb"))
        val movil = device

        @SuppressLint("MissingPermission")
        override fun run() {
            Log.i("client", "Connecting")
            this.socket.connect()

            Log.i("client", "Sending")
            val outputStream = this.socket.outputStream
            val inputStream = this.socket.inputStream
            try {
                val available = inputStream.available()
                val bytes = ByteArray(available)
                Log.i("server", "Reading")
                inputStream.read(bytes, 0, available)
                val text = String(bytes)
                Log.i("server", "Message received")
                Log.i("server", text)
                //activity.appendText(text)
            } catch (e: Exception) {
                Log.e("client", "Cannot read data", e)
            } finally {
                inputStream.close()
                outputStream.close()
                socket.close()
            }
        }
    }

}