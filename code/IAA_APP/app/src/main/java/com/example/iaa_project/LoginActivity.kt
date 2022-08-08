package com.example.iaa_project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityLoginBinding
import com.example.iaa_project.databinding.ActivityRegistroInvestBinding
import com.example.iaa_project.exceptions.*
import com.example.iaa_project.exceptions.InvalidIDException
import com.example.iaa_project.exceptions.InvalidPWException
import com.example.iaa_project.exceptions.InvalidTCException
import java.sql.DriverManager
import java.util.concurrent.Executors

class LoginActivity : AppCompatActivity() {
    var variables: ActivityLoginBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        variables = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val aceptar = variables!!.btnAcepIS
        val cancelar = variables!!.btnCanceIS

        val myExecutor = Executors.newSingleThreadExecutor()

        aceptar.setOnClickListener{
            var idInv : String = variables!!.insertaID.text.toString()
            var pwInv : String = variables!!.insertaPW.text.toString()
            myExecutor.execute{
                consultaUsuario(idInv,pwInv)
            }
        }

        cancelar.setOnClickListener{
            super.onBackPressed()
        }


    }

    fun consultaUsuario(id: String, pw: String){
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: SELECT * FROM b1l1rb6fzqnrv8549nvi.investigador WHERE idinvestigador='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            println(connection.isValid(0))
            println("hola 2")


            val statement = connection.createStatement()
            println("Voy a la query")
            //Guardo en resultSet el resultado de la consulta
            //statement.executeQuery("select * from usuarios where usuario = '$resUsuario' and pass = '$resPassword'")
            //"INSERT INTO `db-tfg`.`investigador` (`idinvestigador`, `dni`, `apellidos`, `nombre`, `fnacimiento`, `contrasena`, `notificaciones`, `terminoscondiciones`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fecha', '$contra', 'true', '$tyc');"
            val query2 = "SELECT idinvestigador FROM b1l1rb6fzqnrv8549nvi.investigador WHERE idinvestigador='$id' and contrasena='$pw';"
            println(query2)
            val resultSet2 = statement.executeQuery(query2)
            var usuDef = ""
            while (resultSet2.next()){
                usuDef = resultSet2.getString(1)
            }
            if(usuDef==""){
                throw InvalidPWException(errorPW3)
            }

            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, "Éxito en el inicio de sesión", Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
            val intento2 = Intent(this, PrincipalActivity::class.java)
            intento2.putExtra("idSesionIniciada", id)
            startActivity(intento2)
            println("El inicio de sesión se ha llevado a cabo con éxito.")

        } catch (e1: InvalidIDException) {
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorID1, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
            println(e1)
        } catch (e2: InvalidPWException) {
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorPW3, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
            println(e2)
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