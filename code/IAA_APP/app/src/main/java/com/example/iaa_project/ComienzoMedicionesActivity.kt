package com.example.iaa_project

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityComienzoMedicionesBinding
import com.example.iaa_project.exceptions.InvalidIDException
import com.example.iaa_project.exceptions.errorID1
import java.sql.DriverManager
import java.util.concurrent.Executors
import kotlin.random.Random

class ComienzoMedicionesActivity : AppCompatActivity() {

    var variables : ActivityComienzoMedicionesBinding? = null
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var conectados = ArrayList<BluetoothDevice>()
    var pacienteMed = ""
    var idUsuMed : EditText? = null
    var errorProvocado = ""

    private companion object {
        private const val CHANNEL_ID = "channel01"
        const val TAG = "IAAPROJECT"
        const val GRUPO_NOTIFICACIONES = "com.example.iaa_project.NOTIFY"
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comienzo_mediciones)

        variables = ActivityComienzoMedicionesBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        if(bundle?.getParcelableArrayList<BluetoothDevice>("conectados")!!.isNotEmpty()){
            conectados.addAll(bundle.getParcelableArrayList("conectados")!!)
        }
        if(conectados.isEmpty()){
            Log.v(TAG,"No hay dispositivos conectados")
        }else{
            for(d in conectados){
                Log.v(TAG,"Dispositivo conectado: ${d.name}")
            }
        }

        idUsuMed = variables!!.etIDUsuarioMed
        val btnComienza = variables!!.btnComenzarMed
        val btnVolver = variables!!.btnVolverMenu

        val myExecutor = Executors.newSingleThreadExecutor()

        btnComienza.setOnClickListener {
            if(conectados.isNotEmpty()){
                var idUsuarioPrueba = idUsuMed!!.text.toString()
                if(conectados.size==1){
                    Handler(Looper.getMainLooper()).post {
                        // write your code here
                        val mensajeError =
                            "Se van a iniciar las pruebas con un solo dispositivo Bluetooth: ${conectados[0].name}"
                        val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                        createNotificationChannel()
                        val builder = NotificationCompat.Builder(
                            this,
                            CHANNEL_ID
                        )
                            .setSmallIcon(R.drawable.ic_iaa_notif)
                            .setColor(this.getColor(R.color.IAA_naranjaNotif))
                            .setContentTitle("Pruebas con un solo dispositivo")
                            .setContentText(mensajeError)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(mensajeError))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        val notifationManagerCompat = NotificationManagerCompat.from(this)
                        notifationManagerCompat.notify(Random.nextInt(0,999999), builder.build())
                        txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                        txtMostrar.show()
                    }
                }else if(conectados.size==2){
                    Handler(Looper.getMainLooper()).post {
                        // write your code here
                        val mensajeError =
                            "Se van a iniciar las pruebas con solo dos dispositivos Bluetooth: ${conectados[0].name} y ${conectados[1].name}"
                        val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                        createNotificationChannel()
                        val builder = NotificationCompat.Builder(
                            this,
                            CHANNEL_ID
                        )
                            .setSmallIcon(R.drawable.ic_iaa_notif)
                            .setColor(this.getColor(R.color.IAA_naranjaNotif))
                            .setContentTitle("Pruebas con solo dos dispositivos")
                            .setContentText(mensajeError)
                            .setStyle(NotificationCompat.BigTextStyle().bigText(mensajeError))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        val notifationManagerCompat = NotificationManagerCompat.from(this)
                        notifationManagerCompat.notify(Random.nextInt(0,999999), builder.build())
                        txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                        txtMostrar.show()
                    }
                }
                myExecutor.execute {
                    compruebaUsuario(idUsuarioPrueba)
                }
            }else{
                Handler(Looper.getMainLooper()).post {
                    // write your code here
                    val mensajeError =
                        "No hay dispositivos conectados. No se han podido iniciar las pruebas de mediciones.\n\nPor favor, conéctese a los dispositivos Bluetooth para poder iniciar las mediciones."
                    val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                    lanzaNotificacion(
                        notifUsuDef,
                        "No hay dispositivos conectados",
                        mensajeError
                    )

                    txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    txtMostrar.show()
                }
            }

        }

        btnVolver.setOnClickListener {
            onBackPressed()
        }

    }

    private fun compruebaUsuario(id: String){
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: SELECT idusuario FROM b1l1rb6fzqnrv8549nvi.usuario WHERE idusuario='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            //"INSERT INTO `db-tfg`.`investigador` (`idinvestigador`, `dni`, `apellidos`, `nombre`, `fnacimiento`, `contrasena`, `notificaciones`, `terminoscondiciones`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fecha', '$contra', 'true', '$tyc');"
            val query2 = "SELECT idusuario FROM b1l1rb6fzqnrv8549nvi.usuario WHERE idusuario='$id';"
            println(query2)
            val resultSet2 = statement.executeQuery(query2)
            var idUsuarioPac = ""
            while (resultSet2.next()){
                idUsuarioPac = resultSet2.getString(1)
            }
            if(idUsuarioPac==""){
                errorProvocado = errorID1
                throw InvalidIDException(errorProvocado)
            }

            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, "Comenzando las mediciones para el usuario $idUsuarioPac", Toast.LENGTH_SHORT)
                txtMostrar.show()
            }
            pacienteMed = idUsuarioPac
            val intento2 = Intent(this, VisualiMedActivity::class.java)
            intento2.putExtra("notifUsuDef", notifUsuDef)
            intento2.putExtra("idUsuDef", idUsuDef)
            intento2.putExtra("dniUsuDef", dniUsuDef)
            intento2.putExtra("apellUsuDef", apellUsuDef)
            intento2.putExtra("nombUsuDef", nombUsuDef)
            intento2.putExtra("fechaUsuDef", fechaUsuDef)
            intento2.putExtra("pwUsuDef", pwUsuDef)
            intento2.putParcelableArrayListExtra("conectados",conectados)
            intento2.putExtra("pacienteMed", pacienteMed)
            startActivity(intento2)
            println("La búsqueda del usuario se ha llevado a cabo con éxito: $idUsuarioPac")
        } catch (e1: InvalidIDException) {
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorProvocado, Toast.LENGTH_LONG)
                txtMostrar.show()
            }
            println(e1)
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                val mensajeError =
                    "No se ha podido encontrar el usuario, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
        }
    }

    private fun lanzaNotificacion(notif: Boolean, titulo: String, mensaje: String) {
        if (notif) {
            createNotificationChannel()
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_iaa_notif)
                .setColor(this.getColor(R.color.IAA_naranjaNotif))
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setGroup(GRUPO_NOTIFICACIONES)
                .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notifationManagerCompat = NotificationManagerCompat.from(this)
            notifationManagerCompat.notify(Random.nextInt(0,999999), builder.build())
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

}