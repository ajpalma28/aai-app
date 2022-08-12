package com.example.iaa_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.updateLayoutParams
import com.example.iaa_project.databinding.ActivityPerfilUsuarioBinding
import com.example.iaa_project.exceptions.*
import java.sql.DriverManager
import java.util.concurrent.Executors


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
    var estadoEdicion = false
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
    var listaSesionID : ArrayList<String> = ArrayList()
    var listaSesionOrg : ArrayList<String> = ArrayList()
    var listaSesionInv : ArrayList<String> = ArrayList()
    var listaSesionUsu : ArrayList<String> = ArrayList()
    var listaSesionFecha : ArrayList<String> = ArrayList()
    var listaSesionRes : ArrayList<String> = ArrayList()

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
        listaSesionOrg.addAll(bundle?.getStringArrayList("listaSesionOrg")!!)
        listaSesionInv.addAll(bundle?.getStringArrayList("listaSesionInv")!!)
        listaSesionUsu.addAll(bundle?.getStringArrayList("listaSesionUsu")!!)
        listaSesionFecha.addAll(bundle?.getStringArrayList("listaSesionFecha")!!)
        listaSesionRes.addAll(bundle?.getStringArrayList("listaSesionRes")!!)
        contadorSesiones = bundle?.getInt("contadorSesiones")!!

        var textID = variables!!.textView12
        textID.text = idUsuarioPac

        var editDNI = variables!!.editTextUsuarioDNI
        editDNI.setText(dniUsuarioPac)

        editNombre = variables!!.editTextUsuarioNombre
        editNombre?.setText(nombUsuarioPac)

        editApel = variables!!.editTextUsuarioApellidos
        editApel?.setText(apellUsuarioPac)

        editFecha = variables!!.editTextUsuarioFecha
        editFecha?.setText(fechaUsuarioPac)

        var btnBorradoUsu = variables!!.btnBorrarUsuarioFull
        //TODO: No se han implementado aún las sesiones de mediciones
        var btnBorraSesiones = variables!!.btnBorraSesionesUsuarioFull
        btnEditarUsuario = variables!!.btnEditUsuario
        var btnCancelar = variables!!.btnCancReg

        imgbtnEditNom = variables!!.imgbtnNomUsu
        imgbtnSaveNom = variables!!.ibSaveNomUsu
        imgbtnCancelNom = variables!!.ibCancelNomUsu

        imgbtnEditApe = variables!!.imgbtnApeUsu
        imgbtnSaveApe = variables!!.ibSaveApeUsu
        imgbtnCancelApe = variables!!.ibCancelApeUsu

        imgbtnEditFN = variables!!.imgbtnfnUsu
        imgbtnSaveFN = variables!!.ibSaveFNUsu
        imgbtnCancelFN = variables!!.ibCancelFNUsu

        val textoSesiones = variables!!.textView13.text.toString()
        variables!!.textView13.text="$textoSesiones $contadorSesiones"

        tablaSesiones = variables!!.sesionesUsuario

        añadeFilaTabla(listaSesionID)

        val myExecutor = Executors.newSingleThreadExecutor()

        btnBorradoUsu.setOnClickListener {
            myExecutor.execute {
                borradoUsuario(idUsuarioPac)
            }
        }

        btnEditarUsuario!!.setOnClickListener {
            if (!estadoEdicion) {
                editNombre?.isEnabled = true
                editApel?.isEnabled = true
                editFecha?.isEnabled = true
                btnEditarUsuario!!.text = "Guardar Usuario"
                estadoEdicion = true
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
            if (estadoEdicion) {
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
            var nombreNew = editNombre?.text.toString()
            myExecutor.execute {
                actualizaCampoUsuario(idUsuarioPac,"nombre",nombreNew)
            }
            editNombre?.isEnabled=false
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
            var apellidosNew = editApel?.text.toString()
            myExecutor.execute {
                actualizaCampoUsuario(idUsuarioPac, "apellidos", apellidosNew)
            }
            editApel?.isEnabled=false
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
            var fechaNew = editFecha?.text.toString()
            var fechaForm = FuncionesAuxiliares().formateaFecha(fechaNew)
            myExecutor.execute {
                actualizaCampoUsuario(idUsuarioPac,"fnacimiento", fechaForm)
            }
            editFecha?.isEnabled=false
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
                when(campo){
                    "nombre" -> { nombUsuarioPac=valor; cancelaEdicionNombreIconos() }
                    "apellidos" -> { apellUsuarioPac=valor; cancelaEdicionApellidosIconos() }
                    "fnacimiento" -> { fechaUsuarioPac=valor; cancelaEdicionFechaIconos() }
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
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            val notifationManagerCompat = NotificationManagerCompat.from(this)
            notifationManagerCompat.notify(123456, builder.build())
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
    }

    private fun editarNombreIconos() {
        imgbtnEditNom!!.visibility = INVISIBLE
        imgbtnSaveNom!!.visibility = VISIBLE
        imgbtnCancelNom!!.visibility = VISIBLE
    }

    private fun cancelaEdicionNombreIconos() {
        imgbtnEditNom!!.visibility = VISIBLE
        imgbtnSaveNom!!.visibility = INVISIBLE
        imgbtnCancelNom!!.visibility = INVISIBLE
    }

    private fun editarApellidosIconos() {
        imgbtnEditApe!!.visibility = INVISIBLE
        imgbtnSaveApe!!.visibility = VISIBLE
        imgbtnCancelApe!!.visibility = VISIBLE
    }

    private fun cancelaEdicionApellidosIconos() {
        imgbtnEditApe!!.visibility = VISIBLE
        imgbtnSaveApe!!.visibility = INVISIBLE
        imgbtnCancelApe!!.visibility = INVISIBLE
    }

    private fun editarFechaIconos() {
        imgbtnEditFN!!.visibility = INVISIBLE
        imgbtnSaveFN!!.visibility = VISIBLE
        imgbtnCancelFN!!.visibility = VISIBLE
    }

    private fun cancelaEdicionFechaIconos() {
        imgbtnEditFN!!.visibility = VISIBLE
        imgbtnSaveFN!!.visibility = INVISIBLE
        imgbtnCancelFN!!.visibility = INVISIBLE
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

    private fun cancelaTodo(n: String, ap: String, fn: String ){
        cancelaEdicionGeneral(n, ap, fn)
        cancelaEdicionNombreIconos()
        cancelaEdicionApellidosIconos()
        cancelaEdicionFechaIconos()
        editFecha?.setText(fn)
        editApel?.setText(ap)
        editNombre?.setText(n)
    }

    private fun añadeFilaTabla(lista: ArrayList<String>){
        var layoutCelda: TableRow.LayoutParams
        val layoutFila = TableRow.LayoutParams(
            TableRow.LayoutParams.WRAP_CONTENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        val fila = TableRow(this)
        fila.layoutParams = layoutFila

        for (l in lista) {
            var texto = TextView(this)
            texto.text = "$l"
            texto.gravity = Gravity.CENTER_HORIZONTAL
            layoutCelda = TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            texto.layoutParams = layoutCelda
            fila.addView(texto)
            val boton = Button(this)
            //boton.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            //boton.layoutParams.height=ViewGroup.LayoutParams.MATCH_PARENT
            //TODO: Ver cómo se puede personalizar o modificar un poquito el botón, para meterle una imagen o algo así
            boton.text = "Ver"
            boton.id=lista.indexOf(l)
            fila.addView(boton)

            // TODO: Me falta añadir la funcionalidad a los botones de las sesiones
        }

        tablaSesiones!!.addView(fila)

    }

}