package com.example.iaa_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
            startActivity(intent)
        }

        btnVolver.setOnClickListener {
            super.onBackPressed()
        }

    }
}