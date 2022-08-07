package com.example.iaa_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.iaa_project.databinding.ActivityRegistroInvestBinding
import java.util.concurrent.Executors

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var variables: LoginActivityBinding? = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val myExecutor = Executors.newSingleThreadExecutor()


    }
}