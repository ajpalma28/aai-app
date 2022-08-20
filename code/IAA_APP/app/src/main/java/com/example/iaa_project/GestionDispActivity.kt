package com.example.iaa_project

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.iaa_project.databinding.ActivityGestionDispBinding

class GestionDispActivity : AppCompatActivity() {

    var variables : ActivityGestionDispBinding? = null
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var conectados = ArrayList<BluetoothDevice>()

    private companion object{
        const val TAG = "IAAPROJECT"
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_disp)

        variables = ActivityGestionDispBinding.inflate(layoutInflater)
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

        if(conectados.isEmpty()){
            Log.v(TAG,"No hay dispositivos conectados")
        }else{
            for(d in conectados){
                Log.v(TAG,"Dispositivo conectado: ${d.name}")
            }
        }

        val btnBuscar = variables!!.btnBuscaDisp
        val btnVerVinc = variables!!.btnVerVinculados
        val btnVolver = variables!!.btnVolverPrin

        btnBuscar.setOnClickListener {
            val intent = Intent(this, BuscaDispositivosActivity::class.java)
            intent.putExtra("idUsuDef", idUsuDef)
            intent.putExtra("dniUsuDef", dniUsuDef)
            intent.putExtra("apellUsuDef", apellUsuDef)
            intent.putExtra("nombUsuDef", nombUsuDef)
            intent.putExtra("fechaUsuDef", fechaUsuDef)
            intent.putExtra("pwUsuDef", pwUsuDef)
            intent.putExtra("notifUsuDef", notifUsuDef)
            intent.putParcelableArrayListExtra("conectados",conectados)
            startActivity(intent)
        }

        btnVerVinc.setOnClickListener {
            val intent = Intent(this, ConectaDispositivosActivity::class.java)
            intent.putExtra("idUsuDef", idUsuDef)
            intent.putExtra("dniUsuDef", dniUsuDef)
            intent.putExtra("apellUsuDef", apellUsuDef)
            intent.putExtra("nombUsuDef", nombUsuDef)
            intent.putExtra("fechaUsuDef", fechaUsuDef)
            intent.putExtra("pwUsuDef", pwUsuDef)
            intent.putExtra("notifUsuDef", notifUsuDef)
            intent.putParcelableArrayListExtra("conectados",conectados)
            startActivity(intent)
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
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

        btnVolver.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            intent.putExtra("idUsuDef", idUsuDef)
            intent.putExtra("dniUsuDef", dniUsuDef)
            intent.putExtra("apellUsuDef", apellUsuDef)
            intent.putExtra("nombUsuDef", nombUsuDef)
            intent.putExtra("fechaUsuDef", fechaUsuDef)
            intent.putExtra("pwUsuDef", pwUsuDef)
            intent.putExtra("notifUsuDef", notifUsuDef)
            intent.putParcelableArrayListExtra("conectados",conectados)
            startActivity(intent)
        }

    }
}