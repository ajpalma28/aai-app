package com.example.iaa_project

import android.app.AlertDialog
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.example.iaa_project.databinding.ActivityResumenMedicionesBinding
import java.util.concurrent.Executors

class ResumenMedicionesActivity : AppCompatActivity() {

    var variables : ActivityResumenMedicionesBinding? = null
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
    var correoInvest = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen_mediciones)

        variables = ActivityResumenMedicionesBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        correoInvest = bundle?.getString("correoInvest").toString()
        if(bundle?.getParcelableArrayList<BluetoothDevice>("conectados")!!.isNotEmpty()){
            conectados.addAll(bundle.getParcelableArrayList("conectados")!!)
        }
        pacienteMed = bundle.getString("pacienteMed").toString()
        resumenSesion = bundle.getString("resumenSesion").toString()

        val botonGuardado = variables!!.btnGuardarSesion
        val btnDescarteSesion = variables!!.btnDescartaSesion

        var resumen = variables!!.etResumenSesion
        resumen.setText(resumenSesion)

        val executorSesion = Executors.newSingleThreadExecutor()

        botonGuardado.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle(R.string.app_name)
            //set message for alert dialog
            builder.setMessage("¿Desea guardar los resultados de la sesión?")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Sí"){_, _ ->
                executorSesion.execute {
                    val intento2 = Intent(this, GuardadoSesionActivity::class.java)
                    intento2.putExtra("notifUsuDef", notifUsuDef)
                    intento2.putExtra("idUsuDef", idUsuDef)
                    intento2.putExtra("dniUsuDef", dniUsuDef)
                    intento2.putExtra("apellUsuDef", apellUsuDef)
                    intento2.putExtra("nombUsuDef", nombUsuDef)
                    intento2.putExtra("fechaUsuDef", fechaUsuDef)
                    intento2.putExtra("pwUsuDef", pwUsuDef)
                    intento2.putExtra("correoInvest",correoInvest)
                    intento2.putParcelableArrayListExtra("conectados",conectados)
                    intento2.putExtra("pacienteMed", pacienteMed)
                    intento2.putExtra("resumenSesion",resumenSesion)
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


        btnDescarteSesion.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle(R.string.app_name)
            //set message for alert dialog
            builder.setMessage(Html.fromHtml("¿Está seguro de descartar los resultados de la sesión? Recuerde, <b>no podrá recuperarlos</b>.",0))
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Sí"){_, _ ->
                executorSesion.execute {
                    val intento2 = Intent(this, PrincipalActivity::class.java)
                    intento2.putExtra("notifUsuDef", notifUsuDef)
                    intento2.putExtra("idUsuDef", idUsuDef)
                    intento2.putExtra("dniUsuDef", dniUsuDef)
                    intento2.putExtra("apellUsuDef", apellUsuDef)
                    intento2.putExtra("nombUsuDef", nombUsuDef)
                    intento2.putExtra("correoInvest",correoInvest)
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
        }

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}