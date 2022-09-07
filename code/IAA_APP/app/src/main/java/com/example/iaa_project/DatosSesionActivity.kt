package com.example.iaa_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TableLayout
import com.example.iaa_project.databinding.ActivityDatosSesionBinding

class DatosSesionActivity : AppCompatActivity() {

    var variables: ActivityDatosSesionBinding? = null
    var idUsuarioPac = ""
    var dniUsuarioPac = ""
    var nombUsuarioPac = ""
    var apellUsuarioPac = ""
    var fechaUsuarioPac = ""
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var correoInvest = ""
    var contadorSesiones = 0
    var listaSesionID: ArrayList<String> = ArrayList()
    var listaSesionOrg: ArrayList<String> = ArrayList()
    var listaSesionInv: ArrayList<String> = ArrayList()
    var listaSesionUsu: ArrayList<String> = ArrayList()
    var listaSesionFecha: ArrayList<String> = ArrayList()
    var listaSesionRes: ArrayList<String> = ArrayList()
    var idSesionMarc = ""
    var idOrgMarc = ""
    var idInvMarc = ""
    var idUsuMarc = ""
    var fechaMarc = ""
    var resumenMarc = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_sesion)

        variables = ActivityDatosSesionBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        idUsuarioPac = bundle?.getString("idUsuarioPac").toString()
        dniUsuarioPac = bundle?.getString("dniUsuarioPac").toString()
        nombUsuarioPac = bundle?.getString("nombUsuarioPac").toString()
        apellUsuarioPac = bundle?.getString("apellUsuarioPac").toString()
        fechaUsuarioPac = bundle?.getString("fechaUsuarioPac").toString()
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        correoInvest = bundle?.getString("correoInvest").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        listaSesionID.addAll(bundle?.getStringArrayList("listaSesionID")!!)
        listaSesionOrg.addAll(bundle.getStringArrayList("listaSesionOrg")!!)
        listaSesionInv.addAll(bundle.getStringArrayList("listaSesionInv")!!)
        listaSesionUsu.addAll(bundle.getStringArrayList("listaSesionUsu")!!)
        listaSesionFecha.addAll(bundle.getStringArrayList("listaSesionFecha")!!)
        listaSesionRes.addAll(bundle.getStringArrayList("listaSesionRes")!!)
        contadorSesiones = bundle.getInt("contadorSesiones")
        idSesionMarc = bundle.getString("idSesionMarc").toString()
        idOrgMarc = bundle.getString("idOrgMarc").toString()
        idInvMarc = bundle.getString("idInvMarc").toString()
        idUsuMarc = bundle.getString("idUsuMarc").toString()
        fechaMarc = bundle.getString("fechaMarc").toString()
        resumenMarc = bundle.getString("resumenMarc").toString()

        val muestraIdSes = variables!!.muestraIdSesion
        muestraIdSes.text=idSesionMarc

        val muestraIdOrg = variables!!.muestraIdOrg
        muestraIdOrg.setText(idOrgMarc)
        muestraIdOrg.isEnabled=false

        val muestraIdInv = variables!!.muestraIdInv
        muestraIdInv.setText(idInvMarc)
        muestraIdInv.isEnabled=false

        val muestraIdUsu = variables!!.muestraIdUsuario
        muestraIdUsu.setText(idUsuMarc)
        muestraIdUsu.isEnabled=false

        val muestraFecha = variables!!.muestraFechaSes
        muestraFecha.setText(fechaMarc)
        muestraFecha.isEnabled=false

        val muestraRes = variables!!.muestraResuSes
        muestraRes.setText(resumenMarc)
        muestraRes.isEnabled=false

        val btnVolver = variables!!.btnvolverDeSesion

        btnVolver.setOnClickListener {
            super.onBackPressed()
        }

    }
}