package com.example.iaa_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityRegistrarUsuarioBinding
import com.example.iaa_project.databinding.ActivityRegistroInvestBinding
import com.example.iaa_project.exceptions.*
import com.example.iaa_project.exceptions.InvalidFechaException
import java.sql.DriverManager
import java.util.concurrent.Executors
import kotlin.random.Random

class RegistrarUsuarioActivity : AppCompatActivity() {

    var variables: ActivityRegistrarUsuarioBinding? = null
    private var errorProvocadoFecha = ""

    private companion object {
        private const val CHANNEL_ID = "channel01"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_usuario)

        variables = ActivityRegistrarUsuarioBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val btnAceptarUsu = variables!!.btnAceptReg
        val btnCancelar = variables!!.btnCancReg

        val myExecutor = Executors.newSingleThreadExecutor()

        btnAceptarUsu.setOnClickListener {
            myExecutor.execute {
                registraUsuario()
            }
        }

        btnCancelar.setOnClickListener {
            super.onBackPressed()
        }

    }

    private fun registraUsuario() {
        val dni = variables!!.editUsuDNI.text.toString()
        val apellidos = variables!!.editUsuApell.text.toString()
        val nombre = variables!!.editUsuName.text.toString()
        val fecha = variables!!.editUsuNac.text.toString()

        val id = FuncionesAuxiliares().generaIdPersona(nombre,apellidos, dni, fecha)
        val fechaDef = FuncionesAuxiliares().formateaFecha(fecha)

        try {
            println("Entro en el try")
            compruebaFormatoFecha(fechaDef)
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: INSERT INTO `db-tfg`.`usuario` (`idusuario`, `dni`, `apellidos`, `nombre`, `fnacimiento`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fechaDef');")

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
            //Guardo en resultSet el resultado de la consulta
            //val resultSet =
            //statement.executeQuery("select * from usuarios where usuario = '$resUsuario' and pass = '$resPassword'")
            //"INSERT INTO `b1l1rb6fzqnrv8549nvi`.`investigador` (`idusuario`, `dni`, `apellidos`, `nombre`, `fnacimiento`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fecha');"
            val query =
                "INSERT INTO `b1l1rb6fzqnrv8549nvi`.`usuario` (`idusuario`, `dni`, `apellidos`, `nombre`, `fnacimiento`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fechaDef');"
            println(query)
            val resultSet = statement.executeUpdate(query)

            Handler(Looper.getMainLooper()).post {
                // write your code here
                val txtMostrar = Toast.makeText(
                    this,
                    "¡REGISTRADO CORRECTAMENTE! El identificador del usuario es $id",
                    Toast.LENGTH_LONG
                )
                createNotificationChannel()
                val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Nuevo usuario registrado")
                    .setContentText("El ID del usuario $nombre $apellidos es $id, ¡infórmelo y no pierda este dato!")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("El ID del usuario $nombre $apellidos es $id, ¡infórmelo y no pierda este dato!")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                val notifationManagerCompat = NotificationManagerCompat.from(this)
                notifationManagerCompat.notify(Random.nextInt(0,999999), builder.build())

                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                super.onBackPressed()
            }
        } catch (e1: InvalidFechaException) {
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorProvocadoFecha, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
            println(e1)
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido registrar, ha ocurrido un error con la base de datos"
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                createNotificationChannel()
                val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Error en la base de datos")
                    .setContentText(mensajeError)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(mensajeError))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                val notifationManagerCompat = NotificationManagerCompat.from(this)
                notifationManagerCompat.notify(Random.nextInt(0,999999), builder.build())

                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.welcome)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun compruebaFormatoFecha(fecha: String) {
        if(!FuncionesAuxiliares().formatoCorrectoAnyoFecha(fecha)){
            errorProvocadoFecha = errorFecha1
            throw InvalidFechaException(errorFecha1)
        }
        if(!FuncionesAuxiliares().formatoCorrectoMesFecha(fecha)){
            errorProvocadoFecha = errorFecha2
            throw InvalidFechaException(errorFecha2)
        }
        if(!FuncionesAuxiliares().formatoCorrectoDiaFecha(fecha)){
            errorProvocadoFecha = errorFecha3
            throw InvalidFechaException(errorFecha3)
        }
    }


}