package com.example.iaa_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.widget.doOnTextChanged
import com.example.iaa_project.databinding.ActivityCambioPwBinding
import com.example.iaa_project.exceptions.*
import com.example.iaa_project.exceptions.InvalidPWException
import java.sql.DriverManager
import java.util.concurrent.Executors
import kotlin.random.Random

class CambioPwActivity : AppCompatActivity() {

    var variables : ActivityCambioPwBinding? = null
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var correoInvest = ""
    var pwUsuDef = ""
    var editPW1 : EditText? = null
    var editPW2 : EditText? = null
    var editPW3 : EditText? = null
    var errorProvocadoPW = ""

    private companion object {
        private const val CHANNEL_ID = "channel01"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambio_pw)

        variables = ActivityCambioPwBinding.inflate(layoutInflater)
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

        val txtID = variables!!.textView12
        txtID.text=idUsuDef

        editPW1 = variables!!.editPWold
        editPW2 = variables!!.editPWnew1
        editPW3 = variables!!.editPWnew2

        val btnAceptar = variables!!.btnAcepPW
        val btnCancelar = variables!!.btnCancPW

        val myExecutor = Executors.newSingleThreadExecutor()

        btnAceptar.setOnClickListener {
            var epw1 = editPW1!!.text.toString()
            var epw2 = editPW2!!.text.toString()
            var epw3 = editPW3!!.text.toString()
            myExecutor.execute {
                actualizaPW(idUsuDef, epw1, epw2, epw3)
            }
        }

        btnCancelar.setOnClickListener {
            super.onBackPressed()
        }

        editPW2!!.doOnTextChanged { text, start, before, count ->
            val errorJJ : CharSequence = getString(R.string.formatoPW)
            val icon = AppCompatResources.getDrawable(this, R.drawable.ic_iaa_alert)
            icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
            editPW2!!.setError(errorJJ,icon)
            //variables!!.editInvestContr.error=errorJJ
        }

    }

    private fun actualizaPW(id: String, pwOld: String, pwNew: String, pwConf: String){
        try {
            println("Entro en el try")
            compruebaCambio(id, pwOld, pwNew, pwConf)
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: UPDATE b1l1rb6fzqnrv8549nvi.investigador SET contrasena='$pwNew' WHERE idinvestigador='$id';")


            if(pwUsuDef!=pwOld){
                errorProvocadoPW = errorPW4
                throw InvalidPWException(errorPW4)
            }
            if(pwUsuDef==pwNew){
                errorProvocadoPW = errorPW5
                throw InvalidPWException(errorPW5)
            }
            if(pwNew!=pwConf){
                errorProvocadoPW = errorPW6
                throw InvalidPWException(errorPW6)
            }

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 =
                "UPDATE b1l1rb6fzqnrv8549nvi.investigador SET contrasena='$pwNew' WHERE idinvestigador='$id';"
            println(query2)
            statement.executeUpdate(query2)
            Handler(Looper.getMainLooper()).post {
                pwUsuDef = pwNew
                val txtMostrar = Toast.makeText(
                    this,
                    "Se ha actualizado correctamente la contraseña del investigador $id",
                    Toast.LENGTH_SHORT
                )
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Investigador actualizado",
                    "Se ha actualizado la contraseña del investigador $nombUsuDef $apellUsuDef (id: $idUsuDef)"
                )
                val intento2 = Intent(this, MiPerfilActivity::class.java)
                intento2.putExtra("notifUsuDef", notifUsuDef)
                intento2.putExtra("idUsuDef", idUsuDef)
                intento2.putExtra("dniUsuDef", dniUsuDef)
                intento2.putExtra("apellUsuDef", apellUsuDef)
                intento2.putExtra("nombUsuDef", nombUsuDef)
                intento2.putExtra("fechaUsuDef", fechaUsuDef)
                intento2.putExtra("pwUsuDef", pwUsuDef)
                intento2.putExtra("correoInvest", correoInvest)
                startActivity(intento2)
                println("Se ha actualizado la contraseña de $id con éxito.")
            }
        } catch (e1: InvalidPWException) {
            println(e1)
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido actualizar la contraseña del usuario $id: $errorProvocadoPW"
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Error al actualizar el usuario",
                    mensajeError
                )
            }
        }  catch (e5: InvalidFormException){
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorFormulario1, Toast.LENGTH_LONG)
                txtMostrar.show()
            }
            println(e5)
        }catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido actualizar el usuario $id, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Error al actualizar la contraseña del usuario",
                    mensajeError
                )
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

    private fun compruebaCambio(id: String, pwOld: String, pwNew: String, pwConf: String){
        if(id.isEmpty() || pwOld.isEmpty() || pwNew.isEmpty() || pwConf.isEmpty()){
            throw InvalidFormException(errorFormulario1)
        }
    }
}