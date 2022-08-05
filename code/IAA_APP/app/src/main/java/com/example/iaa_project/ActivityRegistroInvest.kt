package com.example.iaa_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.example.iaa_project.databinding.ActivityRegistroInvestBinding
import com.example.iaa_project.databinding.ActivityRegistroInvestBinding.*

class ActivityRegistroInvest : AppCompatActivity() {
    var variables: ActivityRegistroInvestBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_invest)

        variables = inflate(layoutInflater)
        setContentView(variables!!.root)
    }

    fun registerInvest(view: View) {
        val entradaDNI = variables!!.editInvestDNI
        val entradaNombre = variables!!.editInvestName
        val entradaApellidos = variables!!.editInvestApell
        val entradaFecha = variables!!.editInvestNac
        val entradaPW1 = variables!!.editInvestContr
        val entradaPW2 = variables!!.editInvestConfContr
        val entradaTC = variables!!.checkTerminos
    }
}