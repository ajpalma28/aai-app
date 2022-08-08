package com.example.iaa_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.iaa_project.databinding.ActivityPrincipalBinding

class PrincipalActivity : AppCompatActivity() {

    var variables: ActivityPrincipalBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val bundle = intent.extras
        val idSesionIniciada = bundle?.getString("idSesionIniciada")
        Toast.makeText(this, "¡Bienvenido, $idSesionIniciada!", Toast.LENGTH_LONG).show()

        variables = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val btnRegistroUsu = variables!!.btnRegistroUsu
        val btnGestDisp = variables!!.btnGestionDispo
        val btnStartMed = variables!!.btnInicioMed
        val btnConsUsu = variables!!.btnConsultUsu
        val btnMiPerfil = variables!!.btnMiPerfil
        val btnNotificaciones = variables!!.btnNotific
        val btnCierreSesion = variables!!.btnCierreSesion

        btnRegistroUsu.setOnClickListener{
            val intent = Intent(this, RegistrarUsuarioActivity::class.java)
            startActivity(intent)
        }

        btnGestDisp.setOnClickListener{
            val intent = Intent(this, GestionDispActivity::class.java)
            startActivity(intent)
        }

        btnStartMed.setOnClickListener{
            val intent = Intent(this, VisualiMedActivity::class.java)
            startActivity(intent)
        }

        btnConsUsu.setOnClickListener{
            val intent = Intent(this, BuscarUsuarioActivity::class.java)
            startActivity(intent)
        }

        btnMiPerfil.setOnClickListener{
            val intent = Intent(this, MiPerfilActivity::class.java)
            startActivity(intent)
        }

        btnNotificaciones.setOnClickListener{
            //TODO
        }

        btnCierreSesion.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}