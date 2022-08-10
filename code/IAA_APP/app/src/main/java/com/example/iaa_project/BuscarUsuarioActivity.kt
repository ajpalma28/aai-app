package com.example.iaa_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.Toast
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar_usuario)

        variables = ActivityBuscarUsuarioBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true

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
            //Guardo en resultSet el resultado de la consulta
            //statement.executeQuery("select * from usuarios where usuario = '$resUsuario' and pass = '$resPassword'")
            //"INSERT INTO `db-tfg`.`investigador` (`idinvestigador`, `dni`, `apellidos`, `nombre`, `fnacimiento`, `contrasena`, `notificaciones`, `terminoscondiciones`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fecha', '$contra', 'true', '$tyc');"
            val query2 = "SELECT * FROM b1l1rb6fzqnrv8549nvi.usuario WHERE idusuario='$id';"
            println(query2)
            val resultSet2 = statement.executeQuery(query2)
            //println(resultSet2.get)
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
                // write your code here
                val mensajeError =
                    "No se ha podido iniciar sesión, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
        }
    }
}