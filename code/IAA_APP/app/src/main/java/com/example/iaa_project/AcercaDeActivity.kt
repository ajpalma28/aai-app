package com.example.iaa_project

import android.content.Intent
import android.net.Uri
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

        val imgDTE = variables!!.imageView
        imgDTE.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dte.us.es/")))
        }

        val imgUS = variables!!.imageView2
        imgUS.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.us.es/")))
        }

        val btnVolver = variables!!.btnVolver

        btnVolver.setOnClickListener{
            super.onBackPressed()
        }
    }
}