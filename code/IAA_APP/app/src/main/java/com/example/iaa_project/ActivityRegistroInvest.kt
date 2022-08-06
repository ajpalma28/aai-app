package com.example.iaa_project

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.iaa_project.databinding.ActivityRegistroInvestBinding
import com.example.iaa_project.databinding.ActivityRegistroInvestBinding.inflate
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.sql.DriverManager
import java.util.concurrent.Executors
import javax.net.ssl.*

class ActivityRegistroInvest : AppCompatActivity() {
    var variables: ActivityRegistroInvestBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_invest)

        try {
            ProviderInstaller.installIfNeeded(applicationContext)
            val sslContext: SSLContext = SSLContext.getInstance("TLSv1.3")
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
        }

        variables = inflate(layoutInflater)
        setContentView(variables!!.root)

        val botonAceptar = variables!!.btnAceptReg
        val botonCancelar = variables!!.btnCancReg

        val myExecutor = Executors.newSingleThreadExecutor()


        botonAceptar.setOnClickListener{
            myExecutor.execute{
                registerInvest()
            }
        }

        botonCancelar.setOnClickListener{
            super.onBackPressed()
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

        val dni = entradaDNI.toString()
        val nombre = entradaNombre.toString()
        val apellidos = entradaApellidos.toString()
        val fecha = entradaFecha.toString()
        val pw1 = entradaPW1.toString()
        val pw2 = entradaPW2.toString()
        val term = entradaTC.isChecked
        var contra: String? = null

        val id = generaIdInvestigador(nombre, apellidos, dni, fecha)
        var tyc: String = "false"
        if(term){
            tyc = "true"
        }

        val fechaDef = formateaFecha(fecha)

        if(pw1==pw2){
            contra = pw1
        }

        //try{
            println("Entro en el try")
            Class.forName("com.mysql.cj.jdbc.Driver")
            //Configuracion de la conexión
            //Configuracion de la conexión
            println("Hola 1")

            val connection = DriverManager.getConnection(
                "jdbc:mysql://tpfrgiw79q39.eu-west-3.psdb.cloud:3306/iaadb?sslMode=VERIFY_IDENTITY",
                "wq2es46v0vv7",
                "pscale_pw_u-YgidCTseLQ0tzJ8c6HThAEjIfcdwZNL6wqk_lImpE"
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
            val resultSet =
                statement.executeQuery("\"INSERT INTO `db-tfg`.`investigador` (`idinvestigador`, `dni`, `apellidos`, `nombre`, `fnacimiento`, `contrasena`, `notificaciones`, `terminoscondiciones`) VALUES ('$id', '$dni', '$apellidos', '$nombre', '$fechaDef', '$contra', 'true', '$tyc');")

            Toast.makeText(this,"¡REGISTRADO CORRECTAMENTE! Su identificador es $id", Toast.LENGTH_LONG).show()

        /*} catch (e: Exception) {
            println(e.toString())
        }*/

    }

    private fun generaIdInvestigador(nombre: String, apellidos: String, dni: String, fecha: String){
        var identificador = ""
        for(i in nombre.indices){
            if(i<4){
                identificador += nombre[i]
            }else{
                break
            }
        }
        for(j in dni.indices){
            if(j>5){
                identificador += dni[j]
            }
        }
        for(i in apellidos.indices){
            if(i<4){
                identificador += apellidos[i]
            }else{
                break
            }
        }
        var partes = fecha.split("/")
        var ano = partes[2]
        for(j in ano.indices){
            if(j>1){
                identificador += ano[j]
            }
        }
    }

    private fun formateaFecha(fecha: String): String {
        var partes = fecha.split("/")
        return partes[2]+"-"+partes[1]+"-"+partes[0]
    }

}