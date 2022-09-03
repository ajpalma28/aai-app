package com.example.iaa_project

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.doOnTextChanged
import com.example.iaa_project.R.drawable
import com.example.iaa_project.databinding.ActivityRegistroInvestBinding
import com.example.iaa_project.databinding.ActivityRegistroInvestBinding.inflate
import com.example.iaa_project.exceptions.*
import com.example.iaa_project.exceptions.InvalidPWException
import com.example.iaa_project.exceptions.InvalidTCException
import java.sql.DriverManager
import java.util.concurrent.Executors
import javax.net.ssl.*
import javax.security.cert.CertificateException
import kotlin.random.Random

class ActivityRegistroInvest : AppCompatActivity() {
    var variables: ActivityRegistroInvestBinding? = null
    private var errorProvocadoFecha = ""
    private var errorProvocadoDNI = ""

    private companion object {
        private const val CHANNEL_ID = "channel01"
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_invest)
        /*
        try {
            ProviderInstaller.installIfNeeded(applicationContext)
            val sslContext: SSLContext = SSLContext.getInstance("SSL")
            sslContext.init(null, null, null)
            sslContext.createSSLEngine()
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }*/

        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            } // Noncompliant (s4830)

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<java.security.cert.X509Certificate>,
                authType: String
            ) {
            } // Noncompliant (s4830)

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                return arrayOf()
            }
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        variables = inflate(layoutInflater)
        setContentView(variables!!.root)

        val botonAceptar = variables!!.btnAceptReg
        val botonCancelar = variables!!.btnCancReg

        val myExecutor = Executors.newSingleThreadExecutor()


        botonAceptar.setOnClickListener {
            myExecutor.execute {
                registerInvest()
            }
        }

        botonCancelar.setOnClickListener {
            super.onBackPressed()
        }

        val leetyc = variables!!.muestraTyC

        leetyc.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle("Términos y Condiciones de Uso de IAA App")
            //set message for alert dialog
            builder.setMessage(R.string.tyc_completo)
            builder.setIcon(R.mipmap.iaa_logo)
            builder.setPositiveButton("OK"){_, _ ->
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        variables!!.editInvestContr.doOnTextChanged { text, start, before, count ->
            val errorJJ : CharSequence = getString(R.string.formatoPW)
            val icon = AppCompatResources.getDrawable(this, R.drawable.ic_iaa_alert)
            icon?.setBounds(0, 0, icon.intrinsicWidth, icon.intrinsicHeight)
            variables!!.editInvestContr.setError(errorJJ,icon)
            //variables!!.editInvestContr.error=errorJJ
        }

    }

    fun registerInvest() {
        val entradaDNI = variables!!.editInvestDNI.text
        val entradaNombre = variables!!.editInvestName.text
        val entradaApellidos = variables!!.editInvestApell.text
        val entradaFecha = variables!!.editInvestNac.text
        val entradaPW1 = variables!!.editInvestContr.text
        val entradaPW2 = variables!!.editInvestConfContr.text
        val entradaTC = variables!!.checkTerminos
        val entradaCorreo = variables!!.editCorreoInvest.text

        val dni = entradaDNI.toString()
        val nombre = entradaNombre.toString()
        val apellidos = entradaApellidos.toString()
        val fecha = entradaFecha.toString()
        val pw1 = entradaPW1.toString()
        val pw2 = entradaPW2.toString()
        val term = entradaTC.isChecked
        val contra: String
        val correo = entradaCorreo.toString()

        val id = FuncionesAuxiliares().generaIdPersona(nombre, apellidos, dni, fecha)
        var tyc = "false"
        if (term) {
            tyc = "true"
        }

        val fechaDef = FuncionesAuxiliares().formateaFecha(fecha)

        try {
            println("Entro en el try")
            compruebaTyC(tyc)
            contra = verificaPW(pw1, pw2)
            compruebaPW(contra)
            compruebaFormatoFecha(fechaDef)
            compruebaDNINIE(dni)
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: INSERT INTO `db-tfg`.`investigador` (`idinvestigador`, `dni`, `apellidos`, `nombre`, `fnacimiento`, `contrasena`, `notificaciones`, `terminoscondiciones`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fechaDef', '$contra', 'true', '$tyc');")

            /*val connection = DriverManager.getConnection(
                "jdbc:mysql://tpfrgiw79q39.eu-west-3.psdb.cloud:3306/iaadb?sslMode=VERIFY_IDENTITY",
                "wq2es46v0vv7",
                "pscale_pw_u-YgidCTseLQ0tzJ8c6HThAEjIfcdwZNL6wqk_lImpE"
            )*/
            //"jdbc:mysql://tpfrgiw79q39.eu-west-3.psdb.cloud:3306/iaadb?sslMode=VERIFY_IDENTITY&ssl-ca=/system/etc/security/cacerts.bks"

            //TODO Probar esta configuración con el servicio de PlanetScale
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
            //"INSERT INTO `db-tfg`.`investigador` (`idinvestigador`, `dni`, `apellidos`, `nombre`, `fnacimiento`, `contrasena`, `notificaciones`, `terminoscondiciones`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fecha', '$contra', 'true', '$tyc');"
            val query =
                "INSERT INTO `b1l1rb6fzqnrv8549nvi`.`investigador` (`idinvestigador`, `dni`, `apellidos`, `nombre`, `fnacimiento`, `correo`, `contrasena`, `notificaciones`, `terminoscondiciones`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fechaDef', '$correo', '$contra', 'true', 'true');"
            println(query)
            val resultSet = statement.executeUpdate(query)

            Handler(Looper.getMainLooper()).post {
                // write your code here
                val txtMostrar = Toast.makeText(
                    this,
                    "¡REGISTRADO CORRECTAMENTE! Su identificador es $id",
                    Toast.LENGTH_LONG
                )
                createNotificationChannel()
                val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(drawable.ic_iaa_notif)
                    .setColor(this.getColor(R.color.IAA_naranjaNotif))
                    .setContentTitle("Nuevo investigador registrado")
                    .setContentText("El ID del investigador es $id, ¡inicie sesión con él!")
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText("El ID del investigador es $id, y la contraseña es la que ha indicado. ¡Inicie sesión con él!")
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                val notifationManagerCompat = NotificationManagerCompat.from(this)
                notifationManagerCompat.notify(
                    Random.nextInt(0,999999), builder.build())

                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                super.onBackPressed()
            }

        } catch (e1: InvalidTCException) {
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorTYC, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
            println(e1)
        } catch (e2: InvalidPWException) {
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorPW1, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
            println(e2)
        } catch (e3: InvalidFechaException){
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorProvocadoFecha, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
            }
            println(e3)
        } catch (e4: InvalidDNIException){
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(this, errorProvocadoDNI, Toast.LENGTH_LONG)
                txtMostrar.show()
            }
            println(e4)
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido registrar, ha ocurrido un error con la base de datos"
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                createNotificationChannel()
                val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(drawable.ic_iaa_notif)
                    .setColor(this.getColor(R.color.IAA_naranjaNotif))
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

    private fun compruebaTyC(tyc: String) {
        if (tyc == "false") {
            throw InvalidTCException(errorTYC)
        }
    }

    fun verificaPW(pw1: String, pw2: String): String {
        var contra = ""
        if (pw1 == pw2) {
            contra = pw1
        }
        return contra
    }

    private fun compruebaPW(contra: String) {
        if (contra == "") {
            throw InvalidPWException(errorPW1)
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

    private fun compruebaDNINIE(dni: String){
        if(!FuncionesAuxiliares().longitudCorrectaDNINIF(dni)){
            errorProvocadoDNI = errorDNI1
            throw InvalidDNIException(errorDNI1)
        }
        if(!FuncionesAuxiliares().formatoCorrectoDNINIF(dni)){
            errorProvocadoDNI = errorDNI2
            throw InvalidDNIException(errorDNI2)
        }
        if(!FuncionesAuxiliares().letraCorrectaDNINIF(dni)){
            errorProvocadoDNI = errorDNI3
            throw InvalidDNIException(errorDNI3)
        }
    }

}