package com.example.iaa_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.iaa_project.databinding.ActivityAcercaDeBinding

class AcercaDeActivity : AppCompatActivity() {

    var variables: ActivityAcercaDeBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acerca_de)

        variables = ActivityAcercaDeBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val btnVolver = variables!!.btnVolver

        btnVolver.setOnClickListener{
            super.onBackPressed()
        }
    }
}