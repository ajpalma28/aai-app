package com.example.iaa_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.marginTop
import com.example.iaa_project.databinding.ActivityDatosOrganizacionBinding
import java.util.concurrent.Executors

class DatosOrganizacionActivity : AppCompatActivity() {

    var variables: ActivityDatosOrganizacionBinding? = null
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var contadorInvestigadores = 0
    var listaAsocInvOriginal : ArrayList<String> = ArrayList()
    var listaAsocInv : ArrayList<String> = ArrayList()
    var idOrgMarc = ""
    var nomOrgMarc = ""
    var dirOrgMarc = ""
    var locOrgMarc = ""
    var tablaInvestigadores: TableLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datos_organizacion)

        variables = ActivityDatosOrganizacionBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        listaAsocInvOriginal = bundle?.getStringArrayList("listaAsocInv")!!
        listaAsocInv = limpiaLista(listaAsocInvOriginal)
        contadorInvestigadores = listaAsocInv.size
        idOrgMarc = bundle?.getString("idOrgMarc").toString()
        nomOrgMarc = bundle?.getString("nomOrgMarc").toString()
        dirOrgMarc = bundle?.getString("dirOrgMarc").toString()
        locOrgMarc = bundle?.getString("locOrgMarc").toString()

        var tvIdOrg = variables!!.muestraIdOrganizacion
        tvIdOrg.text=idOrgMarc

        var nombOrganizacion = variables!!.muestraNomOrg
        nombOrganizacion.setText(nomOrgMarc)
        nombOrganizacion.isEnabled=false

        var direcOrganizacion = variables!!.muestraDirOrg
        direcOrganizacion.setText(dirOrgMarc)
        direcOrganizacion.isEnabled=false

        var localiOrganizacion = variables!!.muestraLocOrg
        localiOrganizacion.setText(locOrgMarc)
        localiOrganizacion.isEnabled=false

        var textoInv = variables!!.listaInvOrg.text.toString()
        variables!!.listaInvOrg.text="$textoInv $contadorInvestigadores"

        tablaInvestigadores = variables!!.muestraInvOrg

        cargaInvestigadores(listaAsocInv)

        var btnVolver = variables!!.btnvolverDeOrg

        btnVolver.setOnClickListener {
            super.onBackPressed()
        }

    }

    private fun cargaInvestigadores(lista: ArrayList<String>) {
        var layoutCelda: TableRow.LayoutParams
        val layoutFila = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        var aux = 0
        for (l in lista) {
            val fila = TableRow(this)
            val numero = lista.indexOf(l)
            fila.id=numero
            fila.layoutParams = layoutFila
            fila.textAlignment = TableRow.TEXT_ALIGNMENT_CENTER
            val texto = TextView(this)
            texto.text = l
            layoutCelda = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            texto.layoutParams = layoutCelda
            texto.textSize=18.0F
            fila.addView(texto)
            fila.gravity = Gravity.CENTER
            aux++
            tablaInvestigadores!!.addView(fila)
        }

        tablaInvestigadores!!.textAlignment = TableLayout.TEXT_ALIGNMENT_CENTER
        tablaInvestigadores!!.gravity=Gravity.CENTER

    }

    private fun limpiaLista(lista: ArrayList<String>): ArrayList<String>{
        var listaDef = ArrayList<String>()
        for (l in lista){
            if(!listaDef.contains(l)){
                listaDef.add(l)
            }
        }
        return listaDef
    }
}