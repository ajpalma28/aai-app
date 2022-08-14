package com.example.iaa_project

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityConectaDispositivosBinding
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.HashMap

class ConectaDispositivosActivity : AppCompatActivity() {

    var variables: ActivityConectaDispositivosBinding? = null
    var bAdapter: BluetoothAdapter? = null
    private var mPairedDevices: Set<BluetoothDevice> = TreeSet<BluetoothDevice>()
    val dL2 = ArrayList<BluetoothDevice>()
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private var scanner : BluetoothLeScanner? = null
    private var callback: BuscaDispositivosActivity.BleScanCallback? = null
    private val foundDevices = HashMap<String, BluetoothDevice>()
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var myHandler: Handler? = null
    var actividad = true

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

        val btm = getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bAdapter = btm.adapter

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
            myHandler = Handler(Looper.getMainLooper())
            myHandler!!.post(object : Runnable {
                override fun run() {
                    if (actividad) {
                        println("SEÑAL 1: Empiezo a ejecutarme aquí")
                        //cargaListado()
                        myHandler!!.postDelayed(this, 10000 /*5 segundos*/)
                        println("SEÑAL 7: Y A MIMIR")
                    }
                }
            })
            /*myHandler = Handler(Looper.getMainLooper())
            myHandler!!.post {
                starBLEScan()
            }*/
            var myExecutor = Executors.newSingleThreadExecutor()

            myExecutor.execute {
                //starBLEScan()
            }

        }
        //variables!!.selectDeviceRefresh.setOnClickListener{pairedDeviceList()}
        variables!!.btnCancelaConex.setOnClickListener {
            onBackPressed()
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
            notifationManagerCompat.notify(123456, builder.build())
        }
    }

    private fun imprimeMap(map: HashMap<String, BluetoothDevice>): String{
        var res = ""
        var com = "IAA-PROJECT: Contenido del MAP"
        var añade = ""
        for(e in map){
            añade = "$añade\n${e.key}"
        }
        res = "$com $añade"
        return res
    }

    private fun cargaListado(){
        // variables
        if(foundDevices.isNotEmpty()){
            Log.v(TAG,"Número de dispositivos encontrados: ${foundDevices.size}")
            for (e in foundDevices) {
                /*if (!mPairedDevices.contains(e.value)){
                    mPairedDevices.plusElement(e.value)
                }*/
                if(!dL2.contains(e.value)) {
                    dL2.add(e.value)
                }
            }
        }
        val list : ArrayList<BluetoothDevice> = ArrayList()
        if(mPairedDevices.isNotEmpty()) {
            Log.v(TAG,"Número de dispositivos en el conjunto: ${mPairedDevices.size}")
            for(device: BluetoothDevice in mPairedDevices){
                list.add(device)
            }
        }else if(dL2.isNotEmpty()) {
            Log.v(TAG,"Número de dispositivos en la lista: ${dL2.size}")
            for(device: BluetoothDevice in dL2){
                list.add(device)
            }
        }else {
            Toast.makeText(this, "No se han encontrado dispositivos", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        variables!!.listaDispVinculados.adapter = adapter
        /*vario.selectDeviceList.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address
            val name: String = device.name

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)

        }*/
    }

}