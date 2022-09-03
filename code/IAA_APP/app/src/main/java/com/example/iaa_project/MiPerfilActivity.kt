package com.example.iaa_project

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.NestedScrollView
import com.example.iaa_project.databinding.ActivityMiPerfilBinding
import com.example.iaa_project.exceptions.InvalidFechaException
import com.example.iaa_project.exceptions.errorFecha1
import com.example.iaa_project.exceptions.errorFecha2
import com.example.iaa_project.exceptions.errorFecha3
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.sql.DriverManager
import java.util.concurrent.Executors
import kotlin.random.Random


class MiPerfilActivity : AppCompatActivity() {

    var variables: ActivityMiPerfilBinding? = null
    var notifUsuDef = false
    var idUsuDef = ""
    var dniUsuDef = ""
    var apellUsuDef = ""
    var nombUsuDef = ""
    var fechaUsuDef = ""
    var pwUsuDef = ""
    var correoInvest = ""
    var estadoEdicion = false
    var editNombre: EditText? = null
    var editApel: EditText? = null
    var editFecha: EditText? = null
    var editCorreo: EditText? = null
    var btnEditarInvestigador: Button? = null
    var imgbtnEditNom: ImageButton? = null;
    var imgbtnSaveNom: ImageButton? = null;
    var imgbtnCancelNom: ImageButton? = null
    var imgbtnEditApe: ImageButton? = null;
    var imgbtnSaveApe: ImageButton? = null;
    var imgbtnCancelApe: ImageButton? = null
    var imgbtnEditFN: ImageButton? = null;
    var imgbtnSaveFN: ImageButton? = null;
    var imgbtnCancelFN: ImageButton? = null
    var btnMover : ExtendedFloatingActionButton? = null
    var errorProvocadoFecha = ""
    var estadoEdicion2 = false
    var contadorSesiones = 0
    var contadorOrganizaciones = 0
    var listaSesionID : ArrayList<String> = ArrayList()
    var listaSesionOrg : ArrayList<String> = ArrayList()
    var listaSesionInv : ArrayList<String> = ArrayList()
    var listaSesionUsu : ArrayList<String> = ArrayList()
    var listaSesionFecha : ArrayList<String> = ArrayList()
    var listaSesionRes : ArrayList<String> = ArrayList()
    var listaAsocID : ArrayList<String> = ArrayList()
    var listaAsocOrganizacion : ArrayList<String> = ArrayList()
    var listaAsocInv : ArrayList<String> = ArrayList()
    var textoSesiones = ""
    var textoOrg = ""
    var tablaOrganizaciones: TableLayout? = null
    var tablaSesiones: TableLayout? = null
    var conectados = ArrayList<BluetoothDevice>()
    var abajo = false
    var scroll : NestedScrollView? = null
    var actividad = true

    private companion object {
        private const val CHANNEL_ID = "channel01"
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_perfil)

        variables = ActivityMiPerfilBinding.inflate(layoutInflater)
        setContentView(variables!!.root)

        val bundle = intent.extras
        notifUsuDef = bundle?.getBoolean("notifUsuDef") == true
        idUsuDef = bundle?.getString("idUsuDef").toString()
        dniUsuDef = bundle?.getString("dniUsuDef").toString()
        apellUsuDef = bundle?.getString("apellUsuDef").toString()
        nombUsuDef = bundle?.getString("nombUsuDef").toString()
        fechaUsuDef = bundle?.getString("fechaUsuDef").toString()
        correoInvest = bundle?.getString("correoInvest").toString()
        pwUsuDef = bundle?.getString("pwUsuDef").toString()
        listaSesionID = bundle?.getStringArrayList("listaSesionID")!!
        listaSesionOrg = bundle.getStringArrayList("listaSesionOrg")!!
        listaSesionInv = bundle.getStringArrayList("listaSesionInv")!!
        listaSesionUsu = bundle.getStringArrayList("listaSesionUsu")!!
        listaSesionFecha = bundle.getStringArrayList("listaSesionFecha")!!
        listaSesionRes = bundle.getStringArrayList("listaSesionRes")!!
        contadorSesiones = bundle.getInt("contadorSesiones")
        listaAsocID = bundle.getStringArrayList("listaAsocID")!!
        listaAsocOrganizacion = bundle.getStringArrayList("listaAsocOrganizacion")!!
        contadorOrganizaciones = bundle.getInt("contadorOrganizaciones")
        if(bundle.getParcelableArrayList<BluetoothDevice>("conectados")!!.isNotEmpty()){
            conectados.addAll(bundle.getParcelableArrayList("conectados")!!)
        }

        val tvID = variables!!.textView12
        tvID.text = idUsuDef

        val editDNI = variables!!.editTextInvestigadorDNI
        editDNI.setText(dniUsuDef)

        editNombre = variables!!.editTextInvestigadorNombre
        editNombre!!.setText(nombUsuDef)

        editApel = variables!!.editTextInvestigadorApellidos
        editApel!!.setText(apellUsuDef)

        editFecha = variables!!.editTextInvestigadorFecha
        editFecha!!.setText(fechaUsuDef)

        editCorreo = variables!!.editTextInvestigadorCorreo
        editCorreo!!.setText(correoInvest)

        btnEditarInvestigador = variables!!.btnEditInvestigador
        val cancelar = variables!!.btnCancReg
        val cambioPW = variables!!.btnCambioPW
        val deleteInvest = variables!!.btnDeletePerfil

        imgbtnEditNom = variables!!.imgbtnNomInv
        imgbtnSaveNom = variables!!.ibSaveNomInv
        imgbtnCancelNom = variables!!.ibCancelNomInv

        imgbtnEditApe = variables!!.imgbtnApeInv
        imgbtnSaveApe = variables!!.ibSaveApeInv
        imgbtnCancelApe = variables!!.ibCancelApeInv

        imgbtnEditFN = variables!!.imgbtnfnInv
        imgbtnSaveFN = variables!!.ibSaveFNInv
        imgbtnCancelFN = variables!!.ibCancelFNInv

        val myExecutor = Executors.newSingleThreadExecutor()

        // TODO: TODA LA PARTE DE LAS ORGANIZACIONES, NO SE HA HECHO AÚN NADA
        // TODO: TODA LA PARTE DE LAS SESIONES, NO SE HA HECHO AÚN NADA NI AQUÍ NI EN EL USUARIO

        textoOrg = variables!!.textView14.text.toString()
        variables!!.textView14.text = "$textoOrg $contadorOrganizaciones"

        textoSesiones = variables!!.textView15.text.toString()
        variables!!.textView15.text = "$textoSesiones $contadorSesiones"

        tablaOrganizaciones = variables!!.organizacionesInv

        tablaSesiones = variables!!.sesionesInv

        añadeFilaTabla(listaSesionFecha)
        cargaOrganizaciones(listaAsocOrganizacion)

        imgbtnEditNom!!.setOnClickListener {
            editarNombreIconos()
        }

        imgbtnSaveNom!!.setOnClickListener {
            var nombreNew = editNombre?.text.toString()
            myExecutor.execute {
                actualizaCampoInvestigador(idUsuDef,"nombre",nombreNew)
            }
            editNombre?.isEnabled=false
        }

        imgbtnCancelNom!!.setOnClickListener {
            cancelaEdicionNombreIconos()
            editNombre?.isEnabled=false
            editNombre?.setText(nombUsuDef)
        }

        imgbtnEditApe!!.setOnClickListener {
            editarApellidosIconos()
        }

        imgbtnSaveApe!!.setOnClickListener {
            var apellidosNew = editApel?.text.toString()
            myExecutor.execute {
                actualizaCampoInvestigador(idUsuDef, "apellidos", apellidosNew)
            }
            editApel?.isEnabled=false
        }

        imgbtnCancelApe!!.setOnClickListener {
            cancelaEdicionApellidosIconos()
            editApel?.isEnabled=false
            editApel?.setText(apellUsuDef)
        }

        imgbtnEditFN!!.setOnClickListener {
            editarFechaIconos()
        }

        imgbtnSaveFN!!.setOnClickListener {
            var fechaNew = editFecha?.text.toString()
            var fechaForm = FuncionesAuxiliares().formateaFecha(fechaNew)
            myExecutor.execute {
                actualizaCampoInvestigador(idUsuDef,"fnacimiento", fechaForm)
            }
            editFecha?.isEnabled=false
        }

        imgbtnCancelFN!!.setOnClickListener {
            cancelaEdicionFechaIconos()
            editFecha?.isEnabled=false
            editFecha?.setText(fechaUsuDef)
        }

        btnEditarInvestigador!!.setOnClickListener {
            if (!estadoEdicion2 || estadoEdicion) {
                editNombre?.isEnabled = true
                editApel?.isEnabled = true
                editFecha?.isEnabled = true
                btnEditarInvestigador!!.text = "Guardar Investigador"
                estadoEdicion2 = true
                estadoEdicion=false
                cancelaEdicionNombreIconos()
                cancelaEdicionApellidosIconos()
                cancelaEdicionFechaIconos()
                desaparecerIconosDeEdicion()
            }else{
                var nom = editNombre?.text.toString()
                var apel = editApel?.text.toString()
                var fn = FuncionesAuxiliares().formateaFecha(editFecha?.text.toString())
                myExecutor.execute {
                    actualizaInvestigadorTotal(idUsuDef, nom, apel, fn)
                }
                mostrarIconosDeEdicion()
            }
        }

        cancelar.setOnClickListener {
            if (estadoEdicion || estadoEdicion2) {
                cancelaEdicionGeneral(nombUsuDef, apellUsuDef, fechaUsuDef)
                cancelaEdicionNombreIconos()
                cancelaEdicionApellidosIconos()
                cancelaEdicionFechaIconos()
            } else {
                onBackPressed()
            }
        }

        btnMover = variables!!.botonMover
        scroll = variables!!.scrollView

        btnMover!!.setOnClickListener{
            if(!abajo){
                scroll?.fullScroll(View.FOCUS_DOWN)
                btnMover?.icon=this.getDrawable(R.drawable.ic_iaa_top)
                abajo = true
            }else{
                scroll?.fullScroll(View.FOCUS_UP)
                btnMover?.icon=this.getDrawable(R.drawable.ic_iaa_down)
                abajo = false
            }
        }

        // TODO: Hacer toda la parte del cambio de contraseña del investigador
        cambioPW.setOnClickListener {
            val intent = Intent(this, CambioPwActivity::class.java)
            intent.putExtra("idUsuDef", idUsuDef)
            intent.putExtra("dniUsuDef", dniUsuDef)
            intent.putExtra("apellUsuDef", apellUsuDef)
            intent.putExtra("nombUsuDef", nombUsuDef)
            intent.putExtra("fechaUsuDef", fechaUsuDef)
            intent.putExtra("correoInvest",correoInvest)
            intent.putExtra("pwUsuDef", pwUsuDef)
            intent.putExtra("notifUsuDef", notifUsuDef)
            startActivity(intent)
        }

        deleteInvest.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle(R.string.app_name)
            //set message for alert dialog
            builder.setMessage("¿Está seguro de querer borrar su cuenta? Si lo hace, perderá toda la información que había almacenada en el sistema. Además, se tendrá que registrar de nuevo para volver a usar la aplicación.")
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setPositiveButton("Sí"){dialogInterface, which ->
                myExecutor.execute {
                    borradoInvestigador(idUsuDef)
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

    private fun borradoInvestigador(id: String) {
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: DELETE FROM b1l1rb6fzqnrv8549nvi.investigador WHERE idinvestigador='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 = "DELETE FROM b1l1rb6fzqnrv8549nvi.investigador WHERE idinvestigador='$id';"
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
                    "Se ha eliminado del sistema el investigador $nombUsuDef $apellUsuDef (id: $idUsuDef)"
                )
            }
            val intento2 = Intent(this, MainActivity::class.java)
            startActivity(intento2)
            println("El borrado del perfil se ha llevado a cabo con éxito.")
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido eliminar el investigador $id, ha ocurrido un error con la base de datos."
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

    private fun actualizaInvestigadorTotal(id: String, nom: String, ape: String, fn: String) {
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: UPDATE b1l1rb6fzqnrv8549nvi.investigador SET nombre='$nom', apellidos='$ape', fnacimiento='$fn' WHERE idinvestigador='$id';")

            compruebaFormatoFecha(fn)

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 =
                "UPDATE b1l1rb6fzqnrv8549nvi.investigador SET nombre='$nom', apellidos='$ape', fnacimiento='$fn' WHERE idinvestigador='$id';"
            println(query2)
            statement.executeUpdate(query2)
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(
                    this,
                    "Se ha actualizado correctamente el investigador $id",
                    Toast.LENGTH_SHORT
                )
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Usuario actualizado",
                    "Se han actualizado los datos del usuario $nombUsuDef $apellUsuDef (id: $idUsuDef)"
                )
                nombUsuDef = nom; apellUsuDef = ape; fechaUsuDef = fn
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
                cancelaTodo(nombUsuDef, apellUsuDef, fechaUsuDef)
            }
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido actualizar el investigador $id, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Error al actualizar el usuario",
                    mensajeError
                )
                cancelaTodo(nombUsuDef, apellUsuDef, fechaUsuDef)
            }
        }
    }

    private fun actualizaCampoInvestigador(id: String, campo: String, valor: String) {
        try {
            println("Entro en el try")
            if (campo == "fnacimiento") {
                compruebaFormatoFecha(valor)
            }
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: UPDATE b1l1rb6fzqnrv8549nvi.investigador SET $campo='$valor' WHERE idinvestigador='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 =
                "UPDATE b1l1rb6fzqnrv8549nvi.investigador SET $campo='$valor' WHERE idinvestigador='$id';"
            println(query2)
            statement.executeUpdate(query2)
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(
                    this,
                    "Se ha actualizado correctamente el investigador $id",
                    Toast.LENGTH_SHORT
                )
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Investigador actualizado",
                    "Se ha actualizado el campo '$campo' del investigador $nombUsuDef $apellUsuDef (id: $idUsuDef)"
                )
                when(campo){
                    "nombre" -> { nombUsuDef=valor; cancelaEdicionNombreIconos() }
                    "apellidos" -> { apellUsuDef=valor; cancelaEdicionApellidosIconos() }
                    "fnacimiento" -> { fechaUsuDef=valor; cancelaEdicionFechaIconos() }
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
                cancelaTodo(nombUsuDef, apellUsuDef, fechaUsuDef)
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
                cancelaTodo(nombUsuDef, apellUsuDef, fechaUsuDef)
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
        btnEditarInvestigador!!.text = "Editar Investigador"
        estadoEdicion = false
        estadoEdicion2 = false
    }

    private fun editarNombreIconos() {
        imgbtnEditNom!!.visibility = View.INVISIBLE
        imgbtnSaveNom!!.visibility = View.VISIBLE
        imgbtnCancelNom!!.visibility = View.VISIBLE
        editNombre?.isEnabled=true
        estadoEdicion=true
    }

    private fun cancelaEdicionNombreIconos() {
        imgbtnEditNom!!.visibility = View.VISIBLE
        imgbtnSaveNom!!.visibility = View.INVISIBLE
        imgbtnCancelNom!!.visibility = View.INVISIBLE
        desactivaBoolean1()
    }

    private fun editarApellidosIconos() {
        imgbtnEditApe!!.visibility = View.INVISIBLE
        imgbtnSaveApe!!.visibility = View.VISIBLE
        imgbtnCancelApe!!.visibility = View.VISIBLE
        editApel?.isEnabled=true
        estadoEdicion=true
    }

    private fun cancelaEdicionApellidosIconos() {
        imgbtnEditApe!!.visibility = View.VISIBLE
        imgbtnSaveApe!!.visibility = View.INVISIBLE
        imgbtnCancelApe!!.visibility = View.INVISIBLE
        desactivaBoolean1()
    }

    private fun editarFechaIconos() {
        imgbtnEditFN!!.visibility = View.INVISIBLE
        imgbtnSaveFN!!.visibility = View.VISIBLE
        imgbtnCancelFN!!.visibility = View.VISIBLE
        editFecha?.isEnabled=true
        estadoEdicion=true
    }

    private fun cancelaEdicionFechaIconos() {
        imgbtnEditFN!!.visibility = View.VISIBLE
        imgbtnSaveFN!!.visibility = View.INVISIBLE
        imgbtnCancelFN!!.visibility = View.INVISIBLE
        desactivaBoolean1()
    }

    private fun mostrarIconosDeEdicion() {
        imgbtnEditNom!!.visibility = View.VISIBLE
        imgbtnEditApe!!.visibility = View.VISIBLE
        imgbtnEditFN!!.visibility = View.VISIBLE
    }

    private fun desaparecerIconosDeEdicion() {
        imgbtnEditNom!!.visibility = View.INVISIBLE
        imgbtnEditApe!!.visibility = View.INVISIBLE
        imgbtnEditFN!!.visibility = View.INVISIBLE
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

    // TODO: Esta parte y el segundo boolean estaría bien implementarlo también en la pantalla de datos del usuario
    // TODO: De esa forma, ambas pantallas tendrían un funcionamiento similar y sería mejor
    private fun desactivaBoolean1(){
        if(imgbtnEditNom!!.visibility==View.VISIBLE && imgbtnEditApe!!.visibility==View.VISIBLE && imgbtnEditFN!!.visibility==View.VISIBLE){
            if(estadoEdicion){ estadoEdicion=false }
        }
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
            texto.text = "          ${FuncionesAuxiliares().formateaFechaRev(l)}         "
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
            aux++
            tablaSesiones!!.addView(fila)

            val executorSesion = Executors.newSingleThreadExecutor()

            boton.setOnClickListener {
                executorSesion.execute {
                    buscaDatosSesion(listaSesionID.get(numero))
                }
            }

        }

        tablaSesiones!!.textAlignment = TableLayout.TEXT_ALIGNMENT_CENTER

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

    private fun cargaOrganizaciones(lista: ArrayList<String>) {
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
            texto.text = " $l "
            texto.gravity = Gravity.CENTER_HORIZONTAL
            layoutCelda = TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
            )
            texto.layoutParams = layoutCelda
            texto.textSize=18.0F
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
            boton2.text = "Borrar"
            boton2.id = numero
            fila.addView(boton2)
            aux++
            tablaOrganizaciones!!.addView(fila)

            val executorSesion = Executors.newSingleThreadExecutor()

            boton.setOnClickListener {
                executorSesion.execute {
                    buscaDatosOrg(listaAsocOrganizacion[numero])
                }
            }

            boton2.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                //set title for alert dialog
                builder.setTitle(R.string.app_name)
                //set message for alert dialog
                builder.setMessage("¿Está seguro de querer desvincularse de la organización ${listaAsocOrganizacion[numero]}? Si lo hace, tendrá que vincularse con ella de nuevo en caso de necesitarlo.")
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                builder.setPositiveButton("Sí"){dialogInterface, which ->
                    executorSesion.execute {
                        executorSesion.execute {
                            borraAsociacion(numero)
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

    private fun buscaDatosOrg(id: String){
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: SELECT * FROM b1l1rb6fzqnrv8549nvi.organizacion WHERE idorganizacion='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 = "SELECT * FROM b1l1rb6fzqnrv8549nvi.organizacion WHERE idorganizacion='$id';"
            println(query2)
            var resultSet2 = statement.executeQuery(query2)
            var idOrgMarc = ""
            var nomOrgMarc = ""
            var dirOrgMarc = ""
            var locOrgMarc = ""
            while (resultSet2.next()){
                idOrgMarc = resultSet2.getString(1)
                nomOrgMarc = resultSet2.getString(2)
                dirOrgMarc = resultSet2.getString(3)
                locOrgMarc = resultSet2.getString(4)
            }
            println("Segunda query")
            val query3 = "SELECT investigador FROM b1l1rb6fzqnrv8549nvi.asociacion WHERE organizacion='$id';"
            println(query3)
            var result3 = statement.executeQuery(query3)
            while(result3.next()){
                listaAsocInv.add(result3.getString(1))
            }
            val intento2 = Intent(this, DatosOrganizacionActivity::class.java)
            intento2.putExtra("notifUsuDef", notifUsuDef)
            intento2.putExtra("idUsuDef", idUsuDef)
            intento2.putExtra("dniUsuDef", dniUsuDef)
            intento2.putExtra("apellUsuDef", apellUsuDef)
            intento2.putExtra("nombUsuDef", nombUsuDef)
            intento2.putExtra("fechaUsuDef", fechaUsuDef)
            intento2.putExtra("pwUsuDef", pwUsuDef)
            intento2.putExtra("idOrgMarc",idOrgMarc)
            intento2.putExtra("nomOrgMarc",nomOrgMarc)
            intento2.putExtra("dirOrgMarc",dirOrgMarc)
            intento2.putExtra("locOrgMarc",locOrgMarc)
            intento2.putExtra("listaAsocInv",listaAsocInv)
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

    private fun borraAsociacion(numero: Int){
        var id = listaAsocID[numero]
        try {
            println("Entro en el try")
            Class.forName("com.mysql.jdbc.Driver")
            //Configuracion de la conexión
            println("Query que vamos a ejecutar: DELETE FROM b1l1rb6fzqnrv8549nvi.asociacion WHERE idasociacion='$id';")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://b1l1rb6fzqnrv8549nvi-mysql.services.clever-cloud.com",
                "umk5rnkivqyw4r0m",
                "06pQBYy1mrut9N1Cps1K"
            )

            val statement = connection.createStatement()
            println("Voy a la query")
            val query2 = "DELETE FROM b1l1rb6fzqnrv8549nvi.asociacion WHERE idasociacion='$id';"
            println(query2)
            statement.executeUpdate(query2)
            Handler(Looper.getMainLooper()).post {
                val txtMostrar = Toast.makeText(
                    this,
                    "Se ha borrado correctamente la asociación con la organización $id",
                    Toast.LENGTH_SHORT
                )
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Asociación eliminada del sistema",
                    "Se ha borrado correctamente la asociación con la organización $id"
                )
                eliminaFila(numero)

            }
        } catch (e: Exception) {
            println(e.toString())
            Handler(Looper.getMainLooper()).post {
                // write your code here
                val mensajeError =
                    "No se ha podido desvincular de la organización $id, ha ocurrido un error con la base de datos."
                val txtMostrar = Toast.makeText(this, mensajeError, Toast.LENGTH_LONG)
                txtMostrar.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                txtMostrar.show()
                lanzaNotificacion(
                    notifUsuDef,
                    "Error al desvincular de la organización",
                    mensajeError
                )
            }
        }
    }

    private fun eliminaFila(numero: Int){
        tablaOrganizaciones?.removeViewAt(numero)
        contadorOrganizaciones--
        variables!!.textView14.text = "$textoOrg $contadorOrganizaciones"
    }

    override fun onBackPressed() {
        val intent = Intent(this, PrincipalActivity::class.java)
        intent.putExtra("idUsuDef", idUsuDef)
        intent.putExtra("dniUsuDef", dniUsuDef)
        intent.putExtra("apellUsuDef", apellUsuDef)
        intent.putExtra("nombUsuDef", nombUsuDef)
        intent.putExtra("fechaUsuDef", fechaUsuDef)
        intent.putExtra("pwUsuDef", pwUsuDef)
        intent.putExtra("notifUsuDef", notifUsuDef)
        intent.putParcelableArrayListExtra("conectados",conectados)
        startActivity(intent)
    }

}