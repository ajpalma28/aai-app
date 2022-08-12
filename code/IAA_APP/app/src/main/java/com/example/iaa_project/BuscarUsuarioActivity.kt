package com.example.iaa_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.example.iaa_project.databinding.ActivityBuscarUsuarioBinding
import com.example.iaa_project.databinding.ActivityPrincipalBinding
import com.example.iaa_project.exceptions.InvalidIDException
import com.example.iaa_project.exceptions.InvalidPWException
import com.example.iaa_project.exceptions.errorID1
import com.example.iaa_project.exceptions.errorPW3
import java.sql.DriverManager
import java.util.concurrent.Executors

class BuscarUsuarioActivity : AppCompatActivity() {

    var variables : ActivityBuscarUsuarioBinding? = null
    var btnBusq : Button? = null
    var editID : String = ""
    var notifUsuDef : Boolean = false
    var errorProvocado = ""
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var contadorSesiones = 0
    var listaSesionID : ArrayList<String> = ArrayList()
    var listaSesionOrg : ArrayList<String> = ArrayList()
    var listaSesionInv : ArrayList<String> = ArrayList()
    var listaSesionUsu : ArrayList<String> = ArrayList()
    var listaSesionFecha : ArrayList<String> = ArrayList()
    var listaSesionRes : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_usuario)

        variables = ActivityBuscarUsuarioBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()

        btnBusq = variables!!.btnBusquedaUsuario
        var campoID = variables!!.idBusqIDUsu
        var btnVolver = variables!!.btnVolverBusqueda

        val myExecutor = Executors.newSingleThreadExecutor()

        btnBusq!!.setOnClickListener {
            editID = campoID.text.toString()
            myExecutor.execute {
                buscaUsuario(editID)
            }
        }

        btnVolver.setOnClickListener {
            super.onBackPressed()
        }

    }

    private fun buscaUsuario(id: String){
        try {
            contadorSesiones=0
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: SELECT * FROM b1l1rb6fzqnrv8549nvi.usuario WHERE idusuario='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            //"INSERT INTO `db-tfg`.`investigador` (`idinvestigador`, `dni`, `apellidos`, `nombre`, `fnacimiento`, `contrasena`, `notificaciones`, `terminoscondiciones`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fecha', '$contra', 'true', '$tyc');"
            val query2 = "SELECT * FROM b1l1rb6fzqnrv8549nvi.usuario WHERE idusuario='$id';"
            println(query2)
            val resultSet2 = statement.executeQuery(query2)
            var idUsuarioPac = ""
            var dniUsuarioPac = ""
            var apellUsuarioPac = ""
            var nombUsuarioPac = ""
            var fechaAux = ""
            var fechaUsuarioPac = ""
            while (resultSet2.next()){
                idUsuarioPac = resultSet2.getString(1)
                dniUsuarioPac = resultSet2.getString(2)
                apellUsuarioPac = resultSet2.getString(3)
                nombUsuarioPac = resultSet2.getString(4)
                fechaAux = resultSet2.getString(5)
            }
            if(idUsuarioPac==""){
                errorProvocado = errorID1
                throw InvalidIDException(errorProvocado)
            }
            fechaUsuarioPac = FuncionesAuxiliares().formateaFechaRev(fechaAux)

            val query3 = "SELECT * FROM b1l1rb6fzqnrv8549nvi.sesion WHERE idusuario='$id';"
            println("Siguiente query: $query3")
            val resultSet3 = statement.executeQuery(query3)
            while(resultSet3.next()){
                listaSesionID.add(resultSet3.getString(1))
                listaSesionOrg.add(resultSet3.getString(2))
                listaSesionInv.add(resultSet3.getString(3))
                listaSesionUsu.add(resultSet3.getString(4))
                listaSesionFecha.add(resultSet3.getString(5))
                listaSesionRes.add(resultSet3.getString(6))
                contadorSesiones++
            }

            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, "Éxito en la búsqueda del usuario $idUsuarioPac", Toast.LENGTH_SHORT)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
            val intento2 = Intent(this, PerfilUsuarioActivity::class.java)
            intento2.putExtra("idUsuarioPac", idUsuarioPac)
            intento2.putExtra("dniUsuarioPac", dniUsuarioPac)
            intento2.putExtra("apellUsuarioPac", apellUsuarioPac)
            intento2.putExtra("nombUsuarioPac", nombUsuarioPac)
            intento2.putExtra("fechaUsuarioPac", fechaUsuarioPac)
            intento2.putExtra("notifUsuDef", notifUsuDef)
            intento2.putExtra("idUsuDef", idUsuDef)
            intento2.putExtra("dniUsuDef", dniUsuDef)
            intento2.putExtra("apellUsuDef", apellUsuDef)
            intento2.putExtra("nombUsuDef", nombUsuDef)
            intento2.putExtra("fechaUsuDef", fechaUsuDef)
            intento2.putExtra("pwUsuDef", pwUsuDef)
            intento2.putExtra("listaSesionID",listaSesionID)
            intento2.putExtra("listaSesionOrg",listaSesionOrg)
            intento2.putExtra("listaSesionInv",listaSesionInv)
            intento2.putExtra("listaSesionUsu",listaSesionUsu)
            intento2.putExtra("listaSesionFecha",listaSesionFecha)
            intento2.putExtra("listaSesionRes",listaSesionRes)
            intento2.putExtra("contadorSesiones",contadorSesiones)
            startActivity(intento2)
            println("La búsqueda del usuario se ha llevado a cabo con éxito.")
            println("$idUsuarioPac - $dniUsuarioPac - $apellUsuarioPac - $nombUsuarioPac - $fechaUsuarioPac")

        } catch (e1: InvalidIDException) {
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorProvocado, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
            println(e1)
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                val mensajeError =
                    "No se ha podido iniciar sesión, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
        }
    }
}