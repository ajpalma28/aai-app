package com.example.iaa_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityPrincipalBinding
import java.sql.DriverManager
import java.util.concurrent.Executors

class PrincipalActivity : AppCompatActivity() {

    var variables: ActivityPrincipalBinding? = null
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var swNotif: SwitchCompat? = null
    var notifUsuDef: Boolean = true

    private companion object {
        private const val CHANNEL_ID = "channel01"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        val bundle = intent.extras
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true

        variables = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val btnRegistroUsu = variables!!.btnRegistroUsu
        val btnGestDisp = variables!!.btnGestionDispo
        val btnStartMed = variables!!.btnInicioMed
        val btnConsUsu = variables!!.btnConsultUsu
        val btnMiPerfil = variables!!.btnMiPerfil
        //btnNotificaciones = variables!!.btnNotific
        swNotif = variables!!.swNotif
        /*if(bundle?.getBoolean("notifUsuDef") == true){
            variables!!.btnNotific.setText("Desactivar Notificaciones")
            swNotif!!.isChecked = true
        }else{
            variables!!.btnNotific.setText("Activar notificaciones")
            swNotif!!.isChecked = false
        }*/
        swNotif!!.isChecked = bundle?.getBoolean("notifUsuDef") == true
        if(swNotif!!.isChecked){
            swNotif!!.text="Notificaciones ACTIVADAS"
            swNotif!!.setTextColor(Color.BLACK)
        }else{
            swNotif!!.text="Notificaciones DESACTIVADAS"
            swNotif!!.setTextColor(Color.RED)
        }
        val btnCierreSesion = variables!!.btnCierreSesion

        val myExecutor = Executors.newSingleThreadExecutor()

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
            intent.putExtra("idUsuDef", idUsuDef)
            intent.putExtra("dniUsuDef", dniUsuDef)
            intent.putExtra("apellUsuDef", apellUsuDef)
            intent.putExtra("nombUsuDef", nombUsuDef)
            intent.putExtra("fechaUsuDef", fechaUsuDef)
            intent.putExtra("pwUsuDef", pwUsuDef)
            intent.putExtra("notifUsuDef", notifUsuDef)
            startActivity(intent)
        }

        swNotif!!.setOnClickListener{
            myExecutor.execute{
                if(notifUsuDef!=null){
                    if(swNotif!!.isChecked) {
                        alternaNotificaciones(false)
                    } else {
                        alternaNotificaciones(true)
                    }
                }
            }
        }

        /*btnNotificaciones!!.setOnClickListener{
            myExecutor.execute{
                if (notifUsuDef != null) {
                    alternaNotificaciones(notifUsuDef)
                }
            }

        }*/

        btnCierreSesion.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.app_name)
            val descriptionText = getString(R.string.welcome)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(PrincipalActivity.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun alternaNotificaciones(notifUsuDef: Boolean){
        if(notifUsuDef){
            try {
                Class.forName("com.mysql.jdbc.Driver")
                //Configuracion de la conexión
                //Configuracion de la conexión
                println("UPDATE b1l1rb6fzqnrv8549nvi.investigador SET notificaciones='false' WHERE idinvestigador='$idUsuDef';")

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
                //"UPDATE b1l1rb6fzqnrv8549nvi.investigador SET notificaciones='false' WHERE idinvestigador='$idUsuDef';"
                val query =
                    "UPDATE b1l1rb6fzqnrv8549nvi.investigador SET notificaciones='false' WHERE idinvestigador='$idUsuDef';"
                println(query)
                val resultSet = statement.executeUpdate(query)

                Handler(Looper.getMainLooper()).post {
                    // write your code here
                    val txtMostrar = Toast.makeText(
                        this,
                        "Se han desactivado las notificaciones para el investigador $idUsuDef",
                        Toast.LENGTH_LONG
                    )
                    txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    txtMostrar.show()
                    this@PrincipalActivity.notifUsuDef = notifUsuDef
                    swNotif!!.text="Notificaciones DESACTIVADAS"
                    swNotif!!.setTextColor(Color.RED)
                }
                /*val intent = Intent(this, PrincipalActivity::class.java)
                intent.putExtra("idUsuDef", idUsuDef)
                intent.putExtra("dniUsuDef", dniUsuDef)
                intent.putExtra("apellUsuDef", apellUsuDef)
                intent.putExtra("nombUsuDef", nombUsuDef)
                intent.putExtra("fechaUsuDef", fechaUsuDef)
                intent.putExtra("pwUsuDef", pwUsuDef)
                intent.putExtra("notifUsuDef", b1)
                startActivity(intent)*/
            } catch (e: Exception) {
                println(e.toString())
                Handler(Looper.getMainLooper()).post {
                    // write your code here
                    val mensajeError =
                        "No se han podido desactivar las notificaciones para el investigador $idUsuDef, inténtelo de nuevo más tarde"
                    val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                    txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    txtMostrar.show()
                    swNotif!!.isChecked=true
                    createNotificationChannel()
                    val builder = NotificationCompat.Builder(this,
                        PrincipalActivity.CHANNEL_ID
                    )
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Error en el cambio de las notificaciones")
                        .setContentText("No se han podido desactivar las notificaciones de la aplicación para el investigador $nombUsuDef $apellUsuDef (id: $idUsuDef). Inténtelo de nuevo más tarde.")
                        .setStyle(NotificationCompat.BigTextStyle().bigText("No se han podido desactivar las notificaciones de la aplicación para el investigador $nombUsuDef $apellUsuDef (id: $idUsuDef). Inténtelo de nuevo más tarde."))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    val notifationManagerCompat = NotificationManagerCompat.from(this)
                    notifationManagerCompat.notify(123456, builder.build())
                }
            }

        }else{
            try {
                Class.forName("com.mysql.jdbc.Driver")
                //Configuracion de la conexión
                //Configuracion de la conexión
                println("UPDATE b1l1rb6fzqnrv8549nvi.investigador SET notificaciones='true' WHERE idinvestigador='$idUsuDef';")

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
                //"UPDATE b1l1rb6fzqnrv8549nvi.investigador SET notificaciones='true' WHERE idinvestigador='$idUsuDef';"
                val query =
                    "UPDATE b1l1rb6fzqnrv8549nvi.investigador SET notificaciones='true' WHERE idinvestigador='$idUsuDef';"
                println(query)
                val resultSet = statement.executeUpdate(query)

                Handler(Looper.getMainLooper()).post {
                    val txtMostrar = Toast.makeText(
                        this,
                        "Se han activado las notificaciones para el investigador $idUsuDef",
                        Toast.LENGTH_LONG
                    )
                    txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    txtMostrar.show()
                    createNotificationChannel()
                    val builder = NotificationCompat.Builder(this,
                        PrincipalActivity.CHANNEL_ID
                    )
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("Notificaciones activadas")
                        .setContentText("Se han activado las notificaciones de la aplicación para el investigador $nombUsuDef $apellUsuDef (id: $idUsuDef)")
                        .setStyle(NotificationCompat.BigTextStyle().bigText("Se han activado las notificaciones de la aplicación para el investigador $nombUsuDef $apellUsuDef (id: $idUsuDef)"))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    val notifationManagerCompat = NotificationManagerCompat.from(this)
                    notifationManagerCompat.notify(123456, builder.build())
                    this@PrincipalActivity.notifUsuDef = notifUsuDef
                    swNotif!!.text="Notificaciones ACTIVADAS"
                    swNotif!!.setTextColor(Color.BLACK)

                }
                /*val intent = Intent(this, PrincipalActivity::class.java)
                intent.putExtra("idUsuDef", idUsuDef)
                intent.putExtra("dniUsuDef", dniUsuDef)
                intent.putExtra("apellUsuDef", apellUsuDef)
                intent.putExtra("nombUsuDef", nombUsuDef)
                intent.putExtra("fechaUsuDef", fechaUsuDef)
                intent.putExtra("pwUsuDef", pwUsuDef)
                intent.putExtra("notifUsuDef", b1)
                startActivity(intent)*/
            } catch (e: Exception) {
                println(e.toString())
                Handler(Looper.getMainLooper()).post {
                    // write your code here
                    val mensajeError =
                        "No se han podido activar las notificaciones para el investigador $idUsuDef, inténtelo de nuevo más tarde"
                    val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                    txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    txtMostrar.show()
                    swNotif!!.isChecked=false
                }
            }
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}