package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.serenart.data.repository.FirebaseRepository
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import android.widget.TextView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvRegister: TextView
    private lateinit var tvForgotPassword: TextView
    private lateinit var progressBar: ProgressBar

    private val firebaseRepository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Verificar si el usuario ya está logueado
        checkUserSession()

        initViews()
        setupClickListeners()
    }

    private fun checkUserSession() {
        // Verificar si hay sesión activa
        if (firebaseRepository.estaLogueado()) {
            // Si ya hay sesión, ir directo a Home
            navigateToHome()
        }
    }

    private fun initViews() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        tvRegister = findViewById(R.id.tv_register)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        progressBar = findViewById(R.id.progress_bar)
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                performLogin(email, password)
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgotPassword.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(
                    this,
                    "Ingresa tu correo electrónico para recuperar tu contraseña",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            recuperarContrasena(email)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(
                this,
                "La contraseña debe tener al menos 6 caracteres",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String) {
        showLoading(true)

        lifecycleScope.launch {
            val resultado = firebaseRepository.iniciarSesion(email, password)

            resultado.onSuccess { firebaseUser ->
                Toast.makeText(
                    this@MainActivity,
                    "Bienvenido ${firebaseUser.email}",
                    Toast.LENGTH_SHORT
                ).show()
                navigateToHome()
            }.onFailure { excepcion ->
                showLoading(false)
                val mensajeError = when {
                    excepcion.message?.contains("password") == true ->
                        "Correo electrónico o contraseña incorrectos"
                    excepcion.message?.contains("network") == true ->
                        "Error de conexión. Verifica tu internet"
                    excepcion.message?.contains("user") == true ->
                        "No existe una cuenta con este correo"
                    else -> "Error al iniciar sesión: ${excepcion.message}"
                }
                Toast.makeText(this@MainActivity, mensajeError, Toast.LENGTH_LONG).show()

                // Limpiar campos en caso de error
                etPassword.setText("")
            }
        }
    }

    private fun recuperarContrasena(email: String) {
        showLoading(true)

        lifecycleScope.launch {
            val resultado = firebaseRepository.recuperarContrasena(email)

            showLoading(false)

            resultado.onSuccess {
                Toast.makeText(
                    this@MainActivity,
                    "Se ha enviado un correo de recuperación a $email",
                    Toast.LENGTH_LONG
                ).show()
            }.onFailure { excepcion ->
                val mensajeError = when {
                    excepcion.message?.contains("user") == true ->
                        "No existe una cuenta con este correo"
                    else -> "Error: ${excepcion.message}"
                }
                Toast.makeText(this@MainActivity, mensajeError, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(mostrar: Boolean) {
        progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        btnLogin.isEnabled = !mostrar
        etEmail.isEnabled = !mostrar
        etPassword.isEnabled = !mostrar
    }
}