package com.example.iaa_project

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.iaa_project.databinding.ActivityPerfilUsuarioBinding
import com.example.iaa_project.exceptions.*
import java.sql.DriverManager
import java.util.concurrent.Executors
import kotlin.random.Random


class PerfilUsuarioActivity : AppCompatActivity() {

    var variables: ActivityPerfilUsuarioBinding? = null
    var idUsuarioPac = ""
    var dniUsuarioPac = ""
    var nombUsuarioPac = ""
    var apellUsuarioPac = ""
    var fechaUsuarioPac = ""
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var estadoEdicion = false; var estadoEdicion2 = false;
    var editNombre: EditText? = null;
    var editApel: EditText? = null;
    var editFecha: EditText? = null
    var btnEditarUsuario: Button? = null
    var imgbtnEditNom: ImageButton? = null;
    var imgbtnSaveNom: ImageButton? = null;
    var imgbtnCancelNom: ImageButton? = null
    var imgbtnEditApe: ImageButton? = null;
    var imgbtnSaveApe: ImageButton? = null;
    var imgbtnCancelApe: ImageButton? = null
    var imgbtnEditFN: ImageButton? = null;
    var imgbtnSaveFN: ImageButton? = null;
    var imgbtnCancelFN: ImageButton? = null
    var errorProvocadoFecha = ""
    var tablaSesiones: TableLayout? = null
    var contadorSesiones = 0
    var listaSesionID: ArrayList<String> = ArrayList()
    var listaSesionOrg: ArrayList<String> = ArrayList()
    var listaSesionInv: ArrayList<String> = ArrayList()
    var listaSesionUsu: ArrayList<String> = ArrayList()
    var listaSesionFecha: ArrayList<String> = ArrayList()
    var listaSesionRes: ArrayList<String> = ArrayList()
    var filas: ArrayList<TableRow> = ArrayList()
    var textoSesiones = ""

    private companion object {
        private const val CHANNEL_ID = "channel01"
    }

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
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        listaSesionID.addAll(bundle?.getStringArrayList("listaSesionID")!!)
        listaSesionOrg.addAll(bundle.getStringArrayList("listaSesionOrg")!!)
        listaSesionInv.addAll(bundle.getStringArrayList("listaSesionInv")!!)
        listaSesionUsu.addAll(bundle.getStringArrayList("listaSesionUsu")!!)
        listaSesionFecha.addAll(bundle.getStringArrayList("listaSesionFecha")!!)
        listaSesionRes.addAll(bundle.getStringArrayList("listaSesionRes")!!)
        contadorSesiones = bundle.getInt("contadorSesiones")

        val textID = variables!!.textView12
        textID.text = idUsuarioPac

        val editDNI = variables!!.editTextUsuarioDNI
        editDNI.setText(dniUsuarioPac)

        editNombre = variables!!.editTextUsuarioNombre
        editNombre?.setText(nombUsuarioPac)

        editApel = variables!!.editTextUsuarioApellidos
        editApel?.setText(apellUsuarioPac)

        editFecha = variables!!.editTextUsuarioFecha
        editFecha?.setText(fechaUsuarioPac)

        val btnBorradoUsu = variables!!.btnBorrarUsuarioFull
        //TODO: No se han implementado aún las sesiones de mediciones
        var btnBorraSesiones = variables!!.btnBorraSesionesUsuarioFull
        btnEditarUsuario = variables!!.btnEditUsuario
        val btnCancelar = variables!!.btnCancReg

        imgbtnEditNom = variables!!.imgbtnNomUsu
        imgbtnSaveNom = variables!!.ibSaveNomUsu
        imgbtnCancelNom = variables!!.ibCancelNomUsu

        imgbtnEditApe = variables!!.imgbtnApeUsu
        imgbtnSaveApe = variables!!.ibSaveApeUsu
        imgbtnCancelApe = variables!!.ibCancelApeUsu

        imgbtnEditFN = variables!!.imgbtnfnUsu
        imgbtnSaveFN = variables!!.ibSaveFNUsu
        imgbtnCancelFN = variables!!.ibCancelFNUsu

        textoSesiones = variables!!.textView13.text.toString()
        variables!!.textView13.text = "$textoSesiones $contadorSesiones"

        tablaSesiones = variables!!.sesionesUsuario

        añadeFilaTabla(listaSesionFecha)

        val myExecutor = Executors.newSingleThreadExecutor()

        btnBorradoUsu.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle(R.string.app_name)
            //set message for alert dialog
            builder.setMessage("¿Está seguro de querer borrar este usuario? Si lo hace, perderá toda la información que había almacenada en el sistema. Además, lo tendrá que registrar de nuevo en caso de necesitarlo.")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Sí"){dialogInterface, which ->
                myExecutor.execute {
                    borradoUsuario(idUsuarioPac)
                }
            }
            builder.setNegativeButton("No"){dialogInterface, which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        btnBorraSesiones.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle(R.string.app_name)
            //set message for alert dialog
            builder.setMessage("¿Está seguro de querer borrar las sesiones de este usuario? Si lo hace, perderá todas las sesiones que habían registradas en el sistema.")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Sí"){dialogInterface, which ->
                myExecutor.execute {
                    borradoSesiones(idUsuarioPac)
                }
            }
            builder.setNegativeButton("No"){dialogInterface, which ->

            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
        }

        btnEditarUsuario!!.setOnClickListener {
            if (!estadoEdicion2 || estadoEdicion) {
                editNombre?.isEnabled = true
                editApel?.isEnabled = true
                editFecha?.isEnabled = true
                btnEditarUsuario!!.text = "Guardar Usuario"
                estadoEdicion2 = true
                estadoEdicion = false
                cancelaEdicionNombreIconos()
                cancelaEdicionApellidosIconos()
                cancelaEdicionFechaIconos()
                desaparecerIconosDeEdicion()
            } else {
                var nom = editNombre?.text.toString()
                var apel = editApel?.text.toString()
                var fn = FuncionesAuxiliares().formateaFecha(editFecha?.text.toString())
                myExecutor.execute {
                    actualizaUsuarioTotal(idUsuarioPac, nom, apel, fn)
                }
                mostrarIconosDeEdicion()
            }

        }

        btnCancelar.setOnClickListener {
            if (estadoEdicion || estadoEdicion2) {
                cancelaEdicionGeneral(nombUsuarioPac, apellUsuarioPac, fechaUsuarioPac)
                cancelaEdicionNombreIconos()
                cancelaEdicionApellidosIconos()
                cancelaEdicionFechaIconos()
            } else {
                val intent = Intent(this, PrincipalActivity::class.java)
                intent.putExtra("idUsuDef", idUsuDef)
                intent.putExtra("dniUsuDef", dniUsuDef)
                intent.putExtra("apellUsuDef", apellUsuDef)
                intent.putExtra("nombUsuDef", nombUsuDef)
                intent.putExtra("fechaUsuDef", fechaUsuDef)
                intent.putExtra("pwUsuDef", pwUsuDef)
                intent.putExtra("notifUsuDef", notifUsuDef)
                startActivity(intent)
            }
        }

        imgbtnEditNom!!.setOnClickListener {
            editarNombreIconos()
            editNombre?.isEnabled = true
        }

        imgbtnSaveNom!!.setOnClickListener {
            val nombreNew = editNombre?.text.toString()
            myExecutor.execute {
                actualizaCampoUsuario(idUsuarioPac, "nombre", nombreNew)
            }
            editNombre?.isEnabled = false
        }

        imgbtnCancelNom!!.setOnClickListener {
            cancelaEdicionNombreIconos()
            editNombre?.isEnabled = false
            editNombre?.setText(nombUsuarioPac)
        }

        imgbtnEditApe!!.setOnClickListener {
            editarApellidosIconos()
            editApel?.isEnabled = true
        }

        imgbtnSaveApe!!.setOnClickListener {
            val apellidosNew = editApel?.text.toString()
            myExecutor.execute {
                actualizaCampoUsuario(idUsuarioPac, "apellidos", apellidosNew)
            }
            editApel?.isEnabled = false
        }

        imgbtnCancelApe!!.setOnClickListener {
            cancelaEdicionApellidosIconos()
            editApel?.isEnabled = false
            editApel?.setText(apellUsuarioPac)
        }

        imgbtnEditFN!!.setOnClickListener {
            editarFechaIconos()
            editFecha?.isEnabled = true
        }

        imgbtnSaveFN!!.setOnClickListener {
            val fechaNew = editFecha?.text.toString()
            val fechaForm = FuncionesAuxiliares().formateaFecha(fechaNew)
            myExecutor.execute {
                actualizaCampoUsuario(idUsuarioPac, "fnacimiento", fechaForm)
            }
            editFecha?.isEnabled = false
        }

        imgbtnCancelFN!!.setOnClickListener {
            cancelaEdicionFechaIconos()
            editFecha?.isEnabled = false
            editFecha?.setText(fechaUsuarioPac)
        }

    }

    private fun borradoUsuario(id: String) {
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: DELETE FROM b1l1rb6fzqnrv8549nvi.usuario WHERE idusuario='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 = "DELETE FROM b1l1rb6fzqnrv8549nvi.usuario WHERE idusuario='$id';"
            println(query2)
            statement.executeUpdate(query2)
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(
                    this,
                    "Se ha borrado correctamente el usuario $id",
                    Toast.LENGTH_SHORT
                )
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Usuario eliminado del sistema",
                    "Se ha eliminado del sistema el usuario $nombUsuarioPac $apellUsuarioPac (id: $idUsuarioPac)"
                )
            }
            val intento2 = Intent(this, PrincipalActivity::class.java)
            intento2.putExtra("notifUsuDef", notifUsuDef)
            intento2.putExtra("idUsuDef", idUsuDef)
            intento2.putExtra("dniUsuDef", dniUsuDef)
            intento2.putExtra("apellUsuDef", apellUsuDef)
            intento2.putExtra("nombUsuDef", nombUsuDef)
            intento2.putExtra("fechaUsuDef", fechaUsuDef)
            intento2.putExtra("pwUsuDef", pwUsuDef)
            startActivity(intento2)
            println("El borrado del usuario se ha llevado a cabo con éxito.")
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido eliminar el usuario $id, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Error al actualizar el usuario",
                    mensajeError
                )
            }
        }
    }

    private fun borradoSesiones(id: String) {
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: DELETE FROM b1l1rb6fzqnrv8549nvi.sesion WHERE idusuario='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 = "DELETE FROM b1l1rb6fzqnrv8549nvi.sesion WHERE idusuario='$id';"
            println(query2)
            statement.executeUpdate(query2)
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(
                    this,
                    "Se han borrado correctamente las sesiones del usuario $id",
                    Toast.LENGTH_SHORT
                )
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Sesiones eliminadas del sistema",
                    "Se han eliminado del sistema las sesiones del usuario $nombUsuarioPac $apellUsuarioPac (id: $idUsuarioPac)"
                )
                while(contadorSesiones!=0){
                    eliminaFila(contadorSesiones-1)
                }
                variables!!.textView13.text = "$textoSesiones $contadorSesiones"
            }
            println("El borrado del usuario se ha llevado a cabo con éxito.")
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido eliminar el usuario $id, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Error al actualizar el usuario",
                    mensajeError
                )
            }
        }
    }

    private fun actualizaUsuarioTotal(id: String, nom: String, ape: String, fn: String) {
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: UPDATE b1l1rb6fzqnrv8549nvi.usuario SET nombre='$nom', apellidos='$ape', fnacimiento='$fn' WHERE idusuario='$id';")

            compruebaFormatoFecha(fn)

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 =
                "UPDATE b1l1rb6fzqnrv8549nvi.usuario SET nombre='$nom', apellidos='$ape', fnacimiento='$fn' WHERE idusuario='$id';"
            println(query2)
            statement.executeUpdate(query2)
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(
                    this,
                    "Se ha actualizado correctamente el usuario $id",
                    Toast.LENGTH_SHORT
                )
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Usuario actualizado",
                    "Se han actualizado los datos del usuario $nombUsuarioPac $apellUsuarioPac (id: $idUsuarioPac)"
                )
                nombUsuarioPac = nom; apellUsuarioPac = ape; fechaUsuarioPac = fn
                cancelaEdicionGeneral(nom, ape, fn)
            }
        } catch (e1: InvalidFechaException) {
            println(e1)
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido actualizar el usuario $id: $errorProvocadoFecha"
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Error al actualizar el usuario",
                    mensajeError
                )
                cancelaTodo(nombUsuarioPac, apellUsuarioPac, fechaUsuarioPac)
            }
        } catch (e: Exception) {
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
                    "Error al actualizar el usuario",
                    mensajeError
                )
                cancelaTodo(nombUsuarioPac, apellUsuarioPac, fechaUsuarioPac)
            }
        }
    }

    private fun actualizaCampoUsuario(id: String, campo: String, valor: String) {
        try {
            println("Entro en el try")
            if (campo == "fnacimiento") {
                compruebaFormatoFecha(valor)
            }
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: UPDATE b1l1rb6fzqnrv8549nvi.usuario SET $campo='$valor' WHERE idusuario='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 =
                "UPDATE b1l1rb6fzqnrv8549nvi.usuario SET $campo='$valor' WHERE idusuario='$id';"
            println(query2)
            statement.executeUpdate(query2)
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(
                    this,
                    "Se ha actualizado correctamente el usuario $id",
                    Toast.LENGTH_SHORT
                )
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Usuario actualizado",
                    "Se ha actualizado el campo '$campo' del usuario $nombUsuarioPac $apellUsuarioPac (id: $idUsuarioPac)"
                )
                when (campo) {
                    "nombre" -> {
                        nombUsuarioPac = valor; cancelaEdicionNombreIconos()
                    }
                    "apellidos" -> {
                        apellUsuarioPac = valor; cancelaEdicionApellidosIconos()
                    }
                    "fnacimiento" -> {
                        fechaUsuarioPac = valor; cancelaEdicionFechaIconos()
                    }
                }
            }
        } catch (e1: InvalidFechaException) {
            println(e1)
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido actualizar el usuario $id: $errorProvocadoFecha"
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Error al actualizar el usuario",
                    mensajeError
                )
                cancelaTodo(nombUsuarioPac, apellUsuarioPac, fechaUsuarioPac)
            }

        } catch (e: Exception) {
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
                    "Error al actualizar el usuario",
                    mensajeError
                )
                cancelaTodo(nombUsuarioPac, apellUsuarioPac, fechaUsuarioPac)
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

    private fun cancelaEdicionGeneral(nom: String, ape: String, fn: String) {
        editNombre?.isEnabled = false
        editNombre?.setText(nom)
        editApel?.isEnabled = false
        editApel?.setText(ape)
        editFecha?.isEnabled = false
        editFecha?.setText(fn)
        btnEditarUsuario!!.text = "Editar Usuario"
        estadoEdicion = false
        estadoEdicion2=false
    }

    private fun editarNombreIconos() {
        imgbtnEditNom!!.visibility = INVISIBLE
        imgbtnSaveNom!!.visibility = VISIBLE
        imgbtnCancelNom!!.visibility = VISIBLE
        estadoEdicion=true
    }

    private fun cancelaEdicionNombreIconos() {
        imgbtnEditNom!!.visibility = VISIBLE
        imgbtnSaveNom!!.visibility = INVISIBLE
        imgbtnCancelNom!!.visibility = INVISIBLE
        desactivaBoolean1()
    }

    private fun editarApellidosIconos() {
        imgbtnEditApe!!.visibility = INVISIBLE
        imgbtnSaveApe!!.visibility = VISIBLE
        imgbtnCancelApe!!.visibility = VISIBLE
        estadoEdicion=true
    }

    private fun cancelaEdicionApellidosIconos() {
        imgbtnEditApe!!.visibility = VISIBLE
        imgbtnSaveApe!!.visibility = INVISIBLE
        imgbtnCancelApe!!.visibility = INVISIBLE
        desactivaBoolean1()
    }

    private fun editarFechaIconos() {
        imgbtnEditFN!!.visibility = INVISIBLE
        imgbtnSaveFN!!.visibility = VISIBLE
        imgbtnCancelFN!!.visibility = VISIBLE
        estadoEdicion=true
    }

    private fun cancelaEdicionFechaIconos() {
        imgbtnEditFN!!.visibility = VISIBLE
        imgbtnSaveFN!!.visibility = INVISIBLE
        imgbtnCancelFN!!.visibility = INVISIBLE
        desactivaBoolean1()
    }

    private fun mostrarIconosDeEdicion() {
        imgbtnEditNom!!.visibility = VISIBLE
        imgbtnEditApe!!.visibility = VISIBLE
        imgbtnEditFN!!.visibility = VISIBLE
    }

    private fun desaparecerIconosDeEdicion() {
        imgbtnEditNom!!.visibility = INVISIBLE
        imgbtnEditApe!!.visibility = INVISIBLE
        imgbtnEditFN!!.visibility = INVISIBLE
    }

    private fun desactivaBoolean1(){
        if(imgbtnEditNom!!.visibility== View.VISIBLE && imgbtnEditApe!!.visibility== View.VISIBLE && imgbtnEditFN!!.visibility== View.VISIBLE){
            if(estadoEdicion){ estadoEdicion=false }
        }
    }

    private fun compruebaFormatoFecha(fecha: String) {
        if (!FuncionesAuxiliares().formatoCorrectoAnyoFecha(fecha)) {
            errorProvocadoFecha = errorFecha1
            throw InvalidFechaException(errorFecha1)
        }
        if (!FuncionesAuxiliares().formatoCorrectoMesFecha(fecha)) {
            errorProvocadoFecha = errorFecha2
            throw InvalidFechaException(errorFecha2)
        }
        if (!FuncionesAuxiliares().formatoCorrectoDiaFecha(fecha)) {
            errorProvocadoFecha = errorFecha3
            throw InvalidFechaException(errorFecha3)
        }
    }

    private fun cancelaTodo(n: String, ap: String, fn: String) {
        cancelaEdicionGeneral(n, ap, fn)
        cancelaEdicionNombreIconos()
        cancelaEdicionApellidosIconos()
        cancelaEdicionFechaIconos()
        editFecha?.setText(fn)
        editApel?.setText(ap)
        editNombre?.setText(n)
    }

    private fun añadeFilaTabla(lista: ArrayList<String>) {
        var layoutCelda: TableRow.LayoutParams
        val layoutFila = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )

        var aux = 0
        for (l in lista) {
            val fila = TableRow(this)
            val numero = lista.indexOf(l)
            fila.id=numero
            fila.layoutParams = layoutFila
            fila.textAlignment = TableRow.TEXT_ALIGNMENT_CENTER
            val texto = TextView(this)
            texto.text = "    ${FuncionesAuxiliares().formateaFechaRev(l)}  "
            texto.gravity = Gravity.CENTER_HORIZONTAL
            layoutCelda = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            texto.layoutParams = layoutCelda
            texto.textSize=19.0F
            fila.addView(texto)
            /*var separa1 = TextView(this)
            separa1.text = " | "
            separa1.layoutParams = layoutCelda
            fila.addView(separa1)
            var org = TextView(this)
            org.text = "${listaSesionOrg.get(aux)}"
            texto.layoutParams = layoutCelda
            fila.addView(org)*/
            val boton = Button(this)
            //boton.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            //boton.layoutParams.height=ViewGroup.LayoutParams.MATCH_PARENT
            //TODO: Ver cómo se puede personalizar o modificar un poquito el botón, para meterle una imagen o algo así
            boton.text = "Ver"
            boton.id = lista.indexOf(l)
            fila.addView(boton)
            val boton2 = Button(this)
            //boton.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            //boton.layoutParams.height=ViewGroup.LayoutParams.MATCH_PARENT
            //TODO: Ver cómo se puede personalizar o modificar un poquito el botón, para meterle una imagen o algo así
            boton2.text = "Borrar"
            boton2.id = numero
            fila.addView(boton2)
            aux++
            tablaSesiones!!.addView(fila)

            val executorSesion = Executors.newSingleThreadExecutor()

            boton.setOnClickListener {
                executorSesion.execute {
                    buscaDatosSesion(listaSesionID.get(numero))
                }
            }

            // TODO: BORRA LA SESIÓN DE LA BASE DE DATOS
            boton2.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                //set title for alert dialog
                builder.setTitle(R.string.app_name)
                //set message for alert dialog
                builder.setMessage("¿Está seguro de querer borrar esta sesión? Si lo hace, perderá toda la información de dicha sesión que había almacenada en el sistema.")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Sí"){dialogInterface, which ->
                    executorSesion.execute {
                        executorSesion.execute {
                            eliminaSesion(numero)
                        }
                    }
                }
                builder.setNegativeButton("No"){dialogInterface, which ->

                }
                // Create the AlertDialog
                val alertDialog: AlertDialog = builder.create()
                // Set other dialog properties
                alertDialog.setCancelable(false)
                alertDialog.show()
            }

        }

        tablaSesiones!!.textAlignment = TableLayout.TEXT_ALIGNMENT_CENTER

    }

    private fun eliminaSesion(numero: Int){
        val id = listaSesionID.get(numero)
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: DELETE FROM b1l1rb6fzqnrv8549nvi.sesion WHERE idsesion='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 = "DELETE FROM b1l1rb6fzqnrv8549nvi.sesion WHERE idsesion='$id';"
            println(query2)
            statement.executeUpdate(query2)
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(
                    this,
                    "Se ha borrado correctamente la sesión de $nombUsuarioPac $apellUsuarioPac",
                    Toast.LENGTH_SHORT
                )
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Usuario eliminado del sistema",
                    "Se ha eliminado del sistema la sesión de $nombUsuarioPac $apellUsuarioPac (id: $idUsuarioPac)"
                )
                eliminaFila(numero)
            }
            /*val intento2 = Intent(this, MainActivity::class.java)
            startActivity(intento2)*/
            println("El borrado de la sesión se ha llevado a cabo con éxito.")
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido eliminar la sesión $id, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Error al eliminar la sesión",
                    mensajeError
                )
            }
        }
    }

    private fun eliminaFila(id: Int){
        tablaSesiones?.removeViewAt(id)
        contadorSesiones--
    }

    private fun buscaDatosSesion(id: String){
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: SELECT * FROM b1l1rb6fzqnrv8549nvi.sesion WHERE idsesion='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 = "SELECT * FROM b1l1rb6fzqnrv8549nvi.sesion WHERE idsesion='$id';"
            println(query2)
            var resultSet2 = statement.executeQuery(query2)
            var idSesionMarc = ""
            var idOrgMarc = ""
            var idInvMarc = ""
            var idUsuMarc = ""
            var fechaMarc = ""
            var resumenMarc = ""
            while (resultSet2.next()){
                idSesionMarc = resultSet2.getString(1)
                idOrgMarc = resultSet2.getString(2)
                idInvMarc = resultSet2.getString(3)
                idUsuMarc = resultSet2.getString(4)
                fechaMarc = resultSet2.getString(5)
                resumenMarc = resultSet2.getString(6)
            }
            val intento2 = Intent(this, DatosSesionActivity::class.java)
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
            intento2.putExtra("idSesionMarc",idSesionMarc)
            intento2.putExtra("idOrgMarc",idOrgMarc)
            intento2.putExtra("idInvMarc",idInvMarc)
            intento2.putExtra("idUsuMarc",idUsuMarc)
            intento2.putExtra("fechaMarc",FuncionesAuxiliares().formateaFechaRev(fechaMarc))
            intento2.putExtra("resumenMarc",resumenMarc)
            startActivity(intento2)
            println("La búsqueda se ha llevado a cabo con éxito.")
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido buscar los datos de la sesión $id, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Error al buscar la sesión",
                    mensajeError
                )
            }
        }
    }

}