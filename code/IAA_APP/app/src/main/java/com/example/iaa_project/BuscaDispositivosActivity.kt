package com.example.iaa_project

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityBuscaDispositivosBinding
import java.time.LocalTime
import java.util.*
import java.util.concurrent.Executors


class BuscaDispositivosActivity : AppCompatActivity() {

    var variables: ActivityBuscaDispositivosBinding? = null
    var bAdapter: BluetoothAdapter? = null
    private var mPairedDevices: Set<BluetoothDevice> = TreeSet<BluetoothDevice>()
    val dL2 = ArrayList<BluetoothDevice>()
    private val REQUEST_ENABLE_BLUETOOTH = 1
    private var scanner : BluetoothLeScanner? = null
    private var callback: BleScanCallback? = null
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

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
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
                        cargaListado()
                        myHandler!!.postDelayed(this, 5000 /*5 segundos*/)
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
                starBLEScan()
            }

        }
        //variables!!.selectDeviceRefresh.setOnClickListener{pairedDeviceList()}
        variables!!.selectDeviceRefresh.setOnClickListener {
            Log.v(TAG,imprimeMap(foundDevices))
            //
            this.onBackPressed()
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

    @RequiresApi(Build.VERSION_CODES.S)
    fun starBLEScan() {
        Log.v(TAG, "${LocalTime.now()} - StartBLEScan")
        val scanFilter = ScanFilter.Builder().build()

        val scanFilters: MutableList<ScanFilter> = mutableListOf()
        scanFilters.add(scanFilter)

        val scanSettings =
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        if(callback==null){
            callback = BleScanCallback()
            scanner = bAdapter!!.bluetoothLeScanner
            //checkBlePermissions()

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return
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
            scanner!!.startScan(scanFilters, scanSettings, callback)
        }

    }

    private fun getMissingLocationPermission(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // COARSE is needed for Android 6 to Android 10
            return Manifest.permission.ACCESS_COARSE_LOCATION;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // FINE is needed for Android 10 and above
            return Manifest.permission.ACCESS_FINE_LOCATION;
        }
        // No location permission is needed for Android 6 and below
        return null;
    }

    /*private fun hasLocationPermission(): Boolean {
        var missingLocationPermission: String = getMissingLocationPermission()!!
        if(missingLocationPermission == null) return true; // No permissions needed
        return ContextCompat.checkSelfPermission(requireContext(), missingLocationPermission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private fun checkLocationService(@Nullable r: Runnable?): Boolean {
        val locationServiceState: Boolean = isLocationServiceEnabled()
        val stateVerbose = if (locationServiceState) "Location is on" else "Location is off"
        Log.d(TAG, stateVerbose)
        if (!locationServiceState) {
            MaterialAlertDialogBuilder(requireContext())
                .setCancelable(false)
                .setTitle("Location Service Off")
                .setView("Location service must be enabled in order to scan the bluetooth devices.")
                .setPositiveButton(android.R.string.ok,
                    DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                        startActivity(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        )
                    })
                .setNegativeButton(android.R.string.cancel,
                    DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> r?.run() })
                .create().show()
        }
        return locationServiceState
    }

    private fun isLocationServiceEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is provided as of API 28
            val lm = this.baseContext.getSystemService(LOCATION_SERVICE) as LocationManager
            lm.isLocationServiceEnabled()
        } else {
            // This is deprecated as of API 28
            val mod: Int = Settings.Secure.getInt(
                baseContext.contentResolver, Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
            )
            mod != Settings.Secure.LOCATION_MODE_OFF
        }
    }

    private fun startScanning() {
        // Here we intervene the scanning process and check whether the user allowed us to use location.
        if(!hasLocationPermission()) {
            // Here you have to request the approprite location permission similar to that main activity class
            return;
        }
        // Location service must be enabled
        if(!checkLocationService(() -> // Pass a Runnable that starts scanning)) return;



        // Everything is good, CAN START SCANNING
    }*/



    private fun stopScan(){
        if(callback!=null){
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
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            Manifest.permission.BLUETOOTH_SCAN
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

            Log.e("${LocalTime.now()} - Nuevo dispositivo", "Dispositivo: ${result}\nDirección: ${result.device.address}")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {
            super.onBatchScanResults(results)
            results!!.forEach { result ->
                foundDevices[result.device.address] = result.device
            }
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        actividad = false
        stopScan()
    }

    private fun imprimeMap(map: HashMap<String, BluetoothDevice>): String{
        val com = "IAA-PROJECT: Contenido del MAP"
        var añade = ""
        for(e in map){
            añade = "$añade\n${e.key}"
        }
        return "$com $añade"
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
        /*if(mPairedDevices.isNotEmpty()) {
            Log.v(TAG,"Número de dispositivos en el conjunto: ${mPairedDevices.size}")
            for(device: BluetoothDevice in mPairedDevices){
                list.add(device)
            }
        }else*/ if(dL2.isNotEmpty()) {
            Log.v(TAG,"Número de dispositivos en la lista: ${dL2.size}")
            for(device: BluetoothDevice in dL2){
                //if(device.name=="LegMonitor" || device.name=="ChestMonitor" || device.name=="WristMonitor")
                list.add(device)
            }
        }else {
            Toast.makeText(this, "No se han encontrado dispositivos", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        variables!!.selectDeviceList.adapter = adapter
        variables!!.selectDeviceList.onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
            /*val device: BluetoothDevice = list[position]
            val address: String = device.address
            val name: String = device.name

            val intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)*/
            val dispositivo: BluetoothDevice = list[position]
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                println("SEÑAL X: ¿He entrado aquí para detener el escaneo?")
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

            if(dispositivo.bondState==BluetoothDevice.BOND_NONE){
                dispositivo.createBond()
                Toast.makeText(this, "Vinculando con el dispositivo ${dispositivo.name}, con dirección ${dispositivo.address}", Toast.LENGTH_SHORT).show()
                Log.v(TAG, "Vinculando con el dispositivo ${dispositivo.name}, con dirección ${dispositivo.address}")
            }else{
                removeBond(dispositivo)
                Toast.makeText(this, "Dispositivo ${dispositivo.name}, con dirección ${dispositivo.address}, DESVINCULADO", Toast.LENGTH_SHORT).show()
                Log.v(TAG, "Dispositivo ${dispositivo.name}, con dirección ${dispositivo.address}, DESVINCULADO")
            }

        }
    }

/*override fun onResume(){
    super.onResume()
    if(bAdapter!!.isEnabled){
        starBLEScan()
    }else{
        val btIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        getAction.launch(btIntent)
    }
}*/

    private fun removeBond(device: BluetoothDevice) {
        try {
            device::class.java.getMethod("removeBond").invoke(device)
        } catch (e: Exception) {
            Log.e(TAG, "Removing bond has been failed. ${e.message}")
        }
    }

}