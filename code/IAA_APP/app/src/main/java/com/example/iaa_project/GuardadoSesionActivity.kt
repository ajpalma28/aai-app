package com.example.iaa_project

import android.app.AlertDialog
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
import android.view.Gravity
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityGuardadoSesionBinding
import com.example.iaa_project.databinding.ActivityResumenMedicionesBinding
import com.example.iaa_project.exceptions.*
import com.example.iaa_project.exceptions.InvalidFormException
import com.example.iaa_project.exceptions.InvalidIDException
import java.sql.DriverManager
import java.time.LocalDate
import java.util.concurrent.Executors
import kotlin.random.Random

class GuardadoSesionActivity : AppCompatActivity() {

    var variables : ActivityGuardadoSesionBinding? = null
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var conectados = ArrayList<BluetoothDevice>()
    var pacienteMed = ""
    var resumenSesion = ""
    var organizacion = ""
    var correoInvest = ""

    private companion object {
        private const val CHANNEL_ID = "channel01"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardado_sesion)

        variables = ActivityGuardadoSesionBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        correoInvest = bundle?.getString("correoInvest").toString()
        if(bundle?.getParcelableArrayList<BluetoothDevice>("conectados")!!.isNotEmpty()){
            conectados.addAll(bundle.getParcelableArrayList("conectados")!!)
        }
        pacienteMed = bundle.getString("pacienteMed").toString()
        resumenSesion = bundle.getString("resumenSesion").toString()

        val executorSesion = Executors.newSingleThreadExecutor()

        val btnGuardadoSesion = variables!!.btnGuardaResumenDef
        val btnCancelar = variables!!.btnCancelarGuardado

        val etOrganizacion = variables!!.etIdOrganizacion

        btnGuardadoSesion.setOnClickListener {
            var idOrg = etOrganizacion.text.toString()
            if(compruebaOrganizacionLleno(idOrg)){
                val builder = AlertDialog.Builder(this)
                //set title for alert dialog
                builder.setTitle(R.string.app_name)
                //set message for alert dialog
                builder.setMessage("¿Confirma el guardado de los resultados de la sesión en la organización $idOrg?")
                builder.setIcon(R.mipmap.iaa_logo)
                builder.setPositiveButton("Sí"){_, _ ->
                    executorSesion.execute {
                        creaAsociacion(idOrg, idUsuDef)
                        val fechaActual = LocalDate.now().toString()
                        guardaSesion(idOrg, idUsuDef, pacienteMed, fechaActual, resumenSesion)
                    }
                }
                builder.setNegativeButton("No"){_, _ ->

                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
                alertDialog.show()
            }else{
                Handler(Looper.getMainLooper()).post {
                    val mensajeError =
                        "No ha introducido el identificador de la Organización"
                    val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                    txtMostrar.show()
                }
            }
        }

        btnCancelar.setOnClickListener {
            onBackPressed()
        }

    }

    fun compruebaOrganizacionLleno(organizacion: String): Boolean{
        var res = true
        if(organizacion.isEmpty() || organizacion=="ID"){
            res = false
        }
        return res
    }

    private fun creaAsociacion(organizacion: String, invest: String){
        var asociacion = ""
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: SELECT * FROM b1l1rb6fzqnrv8549nvi.asociacion WHERE investigador='$invest' AND organizacion='$organizacion';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            //"INSERT INTO `db-tfg`.`investigador` (`idinvestigador`, `dni`, `apellidos`, `nombre`, `fnacimiento`, `contrasena`, `notificaciones`, `terminoscondiciones`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fecha', '$contra', 'true', '$tyc');"
            val query2 = "SELECT idasociacion FROM b1l1rb6fzqnrv8549nvi.asociacion WHERE investigador='$invest' AND organizacion='$organizacion';"
            println(query2)
            val resultSet2 = statement.executeQuery(query2)
            var idasociacion = ""
            while (resultSet2.next()){
                idasociacion = resultSet2.getString(1)
            }
            if(idasociacion==""){
                asociacion = FuncionesAuxiliares().generaIdAsociacion(organizacion, invest)
                val query1 = "INSERT INTO `b1l1rb6fzqnrv8549nvi`.`asociacion` (`idasociacion`, `organizacion`, `investigador`) VALUES ('$asociacion', '$organizacion', '$invest');"
                println("No existe la asociación, la añadimos: $query1")
                val resultSet2 = statement.executeUpdate(query1)
            }
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                val mensajeError =
                    "No se ha podido registrar la Asociación $asociacion, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
        }
    }

    private fun guardaSesion(organizacion: String, invest: String, usuario: String, fecha: String, resumen: String){
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            campoVacio(organizacion)
            lanzaErrorId(organizacion)
            val idSesion = FuncionesAuxiliares().generaIdSesion(fecha, usuario, organizacion)
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: INSERT INTO `b1l1rb6fzqnrv8549nvi`.`sesion` (`idsesion`, `idorganizacion`, `idinvestigador`, `idusuario`, `fecha`, `resumen`) VALUES ('$idSesion', '$organizacion', '$invest', '$usuario', '$fecha', '$resumen');")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            //"INSERT INTO `db-tfg`.`investigador` (`idinvestigador`, `dni`, `apellidos`, `nombre`, `fnacimiento`, `contrasena`, `notificaciones`, `terminoscondiciones`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fecha', '$contra', 'true', '$tyc');"
            val query2 = "INSERT INTO `b1l1rb6fzqnrv8549nvi`.`sesion` (`idsesion`, `idorganizacion`, `idinvestigador`, `idusuario`, `fecha`, `resumen`) VALUES ('$idSesion', '$organizacion', '$invest', '$usuario', '$fecha', '$resumen');"
            println(query2)
            statement.executeUpdate(query2)
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(
                    this,
                    "Se ha guardado correctamente la sesión del usuario $usuario",
                    Toast.LENGTH_SHORT
                )
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Sesión guardada",
                    "Se ha guardado la sesión del usuario $usuario del ${FuncionesAuxiliares().formateaFechaRev(fecha)} (id de la sesión: $idSesion)"
                )
                val intento2 = Intent(this, PrincipalActivity::class.java)
                intento2.putExtra("notifUsuDef", notifUsuDef)
                intento2.putExtra("idUsuDef", idUsuDef)
                intento2.putExtra("dniUsuDef", dniUsuDef)
                intento2.putExtra("apellUsuDef", apellUsuDef)
                intento2.putExtra("nombUsuDef", nombUsuDef)
                intento2.putExtra("fechaUsuDef", fechaUsuDef)
                intento2.putExtra("pwUsuDef", pwUsuDef)
                intento2.putExtra("correoInvest",correoInvest)
                intento2.putParcelableArrayListExtra("conectados",conectados)
                startActivity(intento2)
                println("Se ha guardado la sesión $idSesion con éxito.")
            }

        } catch (e1: InvalidIDException) {
            println(e1.toString())
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorID2, Toast.LENGTH_LONG)
                txtMostrar.show()
            }
        } catch (e5: InvalidFormException){
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorFormulario1, Toast.LENGTH_LONG)
                txtMostrar.show()
            }
            println(e5)
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                val mensajeError =
                    "No se ha podido registrar la sesión de $usuario del ${FuncionesAuxiliares().formateaFechaRev(fecha)}, ha ocurrido un error con la base de datos."
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

    private fun lanzaErrorId(id: String){
        if(!FuncionesAuxiliares().compruebaIdOrg(id)){
            throw InvalidIDException(errorID2)
        }
    }

    private fun campoVacio(id: String){
        if(id.isEmpty()){
            throw InvalidFormException(errorFormulario1)
        }
    }

}