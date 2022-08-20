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
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import com.example.iaa_project.databinding.ActivityConectaDispositivosBinding
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.HashMap
import kotlin.random.Random

class ConectaDispositivosActivity : AppCompatActivity() {

    var variables: ActivityConectaDispositivosBinding? = null
    var bAdapter: BluetoothAdapter? = null
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var myHandler: Handler? = null
    var actividad = true
    var conectados = ArrayList<BluetoothDevice>()
    var bluetoothGatt: BluetoothGatt? = null
    var tablaDispositivos: TableLayout? = null

    private companion object {
        private const val CHANNEL_ID = "channel01"
        val TAG = "IAAPROJECT"
        val BLUETOOTH_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conecta_dispositivos)

        variables = ActivityConectaDispositivosBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        conectados.addAll(bundle?.getParcelableArrayList<BluetoothDevice>("conectados")!!)

        val btm = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = btm.adapter
        tablaDispositivos = variables!!.listaDispVinculados


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


        if (bAdapter!!.isEnabled) {
            /*myHandler = Handler(Looper.getMainLooper())

            myHandler!!.post(object : Runnable {
                override fun run() {
                    if (actividad) {
                        println("SEÑAL 1: Empiezo a ejecutarme aquí")
                        starBLEScan()
                        myHandler!!.postDelayed(this, 10000 /*5 segundos*/)
                        println("SEÑAL 7: Y A MIMIR")
                    }
                }
            })*/
            conectaDispositivos(conectados)
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
            cargaListado()
            myHandler = Handler(Looper.getMainLooper())
            /*myHandler!!.post(object : Runnable {
                override fun run() {
                    if (actividad) {
                        println("SEÑAL 1: Empiezo a ejecutarme aquí")
                        cargaListado()
                        myHandler!!.postDelayed(this, 10000 /*5 segundos*/)
                        println("SEÑAL 7: Y A MIMIR")
                    }
                }
            })*/
            /*myHandler = Handler(Looper.getMainLooper())
            myHandler!!.post {
                starBLEScan()
            }*/

        }
        variables!!.btnCancelaConex.setOnClickListener {
            val intent = Intent(this, GestionDispActivity::class.java)
            intent.putExtra("idUsuDef", idUsuDef)
            intent.putExtra("dniUsuDef", dniUsuDef)
            intent.putExtra("apellUsuDef", apellUsuDef)
            intent.putExtra("nombUsuDef", nombUsuDef)
            intent.putExtra("fechaUsuDef", fechaUsuDef)
            intent.putExtra("pwUsuDef", pwUsuDef)
            intent.putExtra("notifUsuDef", notifUsuDef)
            intent.putParcelableArrayListExtra("conectados",conectados)
            startActivity(intent)
            actividad = false
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
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notifationManagerCompat = NotificationManagerCompat.from(this)
            notifationManagerCompat.notify(Random.nextInt(0,999999), builder.build())
        }
    }

    private fun imprimeMap(map: HashMap<String, BluetoothDevice>): String {
        var res = ""
        var com = "IAA-PROJECT: Contenido del MAP"
        var añade = ""
        for (e in map) {
            añade = "$añade\n${e.key}"
        }
        res = "$com $añade"
        return res
    }

    private fun cargaListado() {

        tablaDispositivos!!.removeAllViews()
        var layoutCelda: TableRow.LayoutParams
        val layoutFila = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            200
        )
        var aux = 0
        for (l in conectados) {
            val fila = TableRow(this)
            var imgBT: ImageView = ImageView(this).apply {
                id = ViewCompat.generateViewId()
                scaleType = ImageView.ScaleType.CENTER
                setImageResource(R.mipmap.ib_bt_connect)
            }
            fila.addView(imgBT)
            val numero = conectados.indexOf(l)
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
            boton.text = "Desconectar"
            boton.id = numero
            boton.width = 336
            fila.addView(boton)
            fila.gravity = Gravity.CENTER
            aux++
            tablaDispositivos!!.addView(fila)

            val executorSesion = Executors.newSingleThreadExecutor()

            texto.setOnLongClickListener{
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
                            myHandler!!.post {
                                cargaListado()
                            }
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
                true
            }

            boton.setOnClickListener {
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
                            myHandler!!.post {
                                cargaListado()
                            }
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

        tablaDispositivos!!.textAlignment = TableLayout.TEXT_ALIGNMENT_CENTER
        tablaDispositivos!!.gravity = Gravity.CENTER_HORIZONTAL

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
                    if (ActivityCompat.checkSelfPermission(
                            this@ConectaDispositivosActivity,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                            ActivityCompat.requestPermissions(
                                this@ConectaDispositivosActivity,
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
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                Log.v(TAG, "onServicesDiscovered")
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

}