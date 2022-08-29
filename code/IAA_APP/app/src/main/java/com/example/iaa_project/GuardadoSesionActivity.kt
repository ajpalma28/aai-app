package com.example.iaa_project

import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityGuardadoSesionBinding
import com.example.iaa_project.databinding.ActivityResumenMedicionesBinding
import java.util.concurrent.Executors

class GuardadoSesionActivity : AppCompatActivity() {

    var variables : ActivityGuardadoSesionBinding? = null
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var conectados = ArrayList<BluetoothDevice>()
    var pacienteMed = ""
    var resumenSesion = ""
    var organizacion = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardado_sesion)

        variables = ActivityGuardadoSesionBinding.inflate(layoutInflater)
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
        resumenSesion = bundle.getString("resumenSesion").toString()

        val executorSesion = Executors.newSingleThreadExecutor()

        val btnGuardadoSesion = variables!!.btnGuardaResumenDef
        val btnCancelar = variables!!.btnCancelarGuardado

        var etOrganizacion = variables!!.etIdOrganizacion

        btnGuardadoSesion.setOnClickListener {
            var idOrg = etOrganizacion.text.toString()
            if(compruebaOrganizacionLleno(idOrg)){
                val builder = AlertDialog.Builder(this)
                //set title for alert dialog
                builder.setTitle(R.string.app_name)
                //set message for alert dialog
                builder.setMessage("¿Confirma el guardado de los resultados de la sesión en la organización $idOrg?")
                // TODO: Probar con el logo de la silueta naranja solo
                builder.setIcon(R.mipmap.iaa_logo)
                builder.setPositiveButton("Sí"){_, _ ->
                    executorSesion.execute {
                        // TODO: Queda pendiente hacer todo lo de insertar en la base de datos el registro de la Sesión
                        // TODO: Además, se debe crear una Asociación si el Investigador no está relacionado con la Organización aún
                        val intento2 = Intent(this, PrincipalActivity::class.java)
                        intento2.putExtra("notifUsuDef", notifUsuDef)
                        intento2.putExtra("idUsuDef", idUsuDef)
                        intento2.putExtra("dniUsuDef", dniUsuDef)
                        intento2.putExtra("apellUsuDef", apellUsuDef)
                        intento2.putExtra("nombUsuDef", nombUsuDef)
                        intento2.putExtra("fechaUsuDef", fechaUsuDef)
                        intento2.putExtra("pwUsuDef", pwUsuDef)
                        intento2.putParcelableArrayListExtra("conectados",conectados)
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
            }else{
                Handler(Looper.getMainLooper()).post {
                    val mensajeError =
                        "No ha introducido el identificador de la Organización"
                    val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                    txtMostrar.show()
                }
            }
        }

        btnCancelar.setOnClickListener {
            onBackPressed()
        }

    }

    fun compruebaOrganizacionLleno(organizacion: String): Boolean{
        var res = true
        if(organizacion.isEmpty() || organizacion=="ID"){
            res = false
        }
        return res
    }
}