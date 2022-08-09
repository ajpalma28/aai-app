package com.example.iaa_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.iaa_project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var variables: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        variables = ActivityMainBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val botonReg = variables!!.btnRegistro
        val botonInSes = variables!!.btnInicioSesion
        val botonAcercaDe = variables!!.btnAcercaDe

        botonReg.setOnClickListener{
            val intento1 = Intent(this, ActivityRegistroInvest::class.java)
            startActivity(intento1)
        }

        botonInSes.setOnClickListener{
            val intento2 = Intent(this, LoginActivity::class.java)
            startActivity(intento2)
        }

        botonAcercaDe.setOnClickListener{
            val intento3 = Intent(this, AcercaDeActivity::class.java)
            startActivity(intento3)
        }

    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

}