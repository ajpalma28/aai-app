package com.example.iaa_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.iaa_project.databinding.ActivityBuscarUsuarioBinding
import com.example.iaa_project.databinding.ActivityPerfilUsuarioBinding

class PerfilUsuarioActivity : AppCompatActivity() {

    var variables : ActivityPerfilUsuarioBinding? = null
    var idUsuarioPac = ""
    var dniUsuarioPac = ""
    var nombUsuarioPac = ""
    var apellUsuarioPac = ""
    var fechaUsuarioPac = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)

        variables = ActivityPerfilUsuarioBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        idUsuarioPac = bundle?.getString("idUsuarioPac").toString()
        dniUsuarioPac = bundle?.getString("dniUsuarioPac").toString()
        nombUsuarioPac = bundle?.getString("nombUsuarioPac").toString()
        apellUsuarioPac = bundle?.getString("apellUsuarioPac").toString()
        fechaUsuarioPac = bundle?.getString("fechaUsuarioPac").toString()

        var textID = variables!!.textView12
        textID.text=idUsuarioPac

        var editDNI = variables!!.editTextUsuarioDNI
        editDNI.setText(dniUsuarioPac)

        var editNombre = variables!!.editTextUsuarioNombre
        editNombre.setText(nombUsuarioPac)

        var editApel = variables!!.editTextUsuarioApellidos
        editApel.setText(apellUsuarioPac)

        var editFecha = variables!!.editTextUsuarioFecha
        editFecha.setText(fechaUsuarioPac)

        var btnBorradoUsu = variables!!.btnBorrarUsuarioFull
        var btnBorraSesiones = variables!!.btnBorraSesionesUsuarioFull
        var btnEditarUsuario = variables!!.btnEditUsuario
        var btnCancelar = variables!!.btnCancReg

        btnCancelar.setOnClickListener {
            super.onBackPressed()
        }
    }
}