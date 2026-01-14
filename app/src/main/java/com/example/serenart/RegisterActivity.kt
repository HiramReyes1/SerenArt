package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.serenart.data.repository.FirebaseRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var etFullname: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnCreateAccount: MaterialButton
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    private val firebaseRepository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        etFullname = findViewById(R.id.et_fullname)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        cbTerms = findViewById(R.id.cb_terms)
        btnCreateAccount = findViewById(R.id.btn_create_account)
        tvLogin = findViewById(R.id.tv_login)
        progressBar = findViewById(R.id.progress_bar)

        // Configurar toolbar
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupClickListeners() {
        btnCreateAccount.setOnClickListener {
            val fullname = etFullname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInputs(fullname, email, password, confirmPassword)) {
                performRegistration(fullname, email, password)
            }
        }

        tvLogin.setOnClickListener {
            finish() // Volver a LoginActivity
        }
    }

    private fun validateInputs(
        fullname: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (fullname.isEmpty()) {
            Toast.makeText(this, "Ingresa tu nombre completo", Toast.LENGTH_SHORT).show()
            return false
        }

        if (fullname.length < 3) {
            Toast.makeText(
                this,
                "El nombre debe tener al menos 3 caracteres",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

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

        // Validación mejorada de contraseña según RF-013
        if (password.length < 8) {
            Toast.makeText(
                this,
                "La contraseña debe tener al menos 8 caracteres",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Verificar mayúsculas
        if (!password.any { it.isUpperCase() }) {
            Toast.makeText(
                this,
                "La contraseña debe contener al menos una letra mayúscula",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Verificar minúsculas
        if (!password.any { it.isLowerCase() }) {
            Toast.makeText(
                this,
                "La contraseña debe contener al menos una letra minúscula",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Verificar números
        if (!password.any { it.isDigit() }) {
            Toast.makeText(
                this,
                "La contraseña debe contener al menos un número",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        // Verificar caracteres especiales
        val caracteresEspeciales = "!@#$%^&*()_+-=[]{}|;:,.<>?"
        if (!password.any { it in caracteresEspeciales }) {
            Toast.makeText(
                this,
                "La contraseña debe contener al menos un carácter especial (!@#$%...)",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!cbTerms.isChecked) {
            Toast.makeText(
                this,
                "Debes aceptar los términos y condiciones",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun performRegistration(fullname: String, email: String, password: String) {
        showLoading(true)

        lifecycleScope.launch {
            val resultado = firebaseRepository.registrarUsuario(fullname, email, password)

            resultado.onSuccess { firebaseUser ->
                Toast.makeText(
                    this@RegisterActivity,
                    "¡Cuenta creada exitosamente!",
                    Toast.LENGTH_SHORT
                ).show()

                // Navegar directamente a Home ya que el usuario está autenticado
                navigateToHome()
            }.onFailure { excepcion ->
                showLoading(false)
                val mensajeError = when {
                    excepcion.message?.contains("already in use") == true ->
                        "Este correo electrónico ya está registrado"
                    excepcion.message?.contains("network") == true ->
                        "Error de conexión. Verifica tu internet"
                    excepcion.message?.contains("weak-password") == true ->
                        "La contraseña es muy débil"
                    else -> "Error al crear cuenta: ${excepcion.message}"
                }
                Toast.makeText(this@RegisterActivity, mensajeError, Toast.LENGTH_LONG).show()
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
        btnCreateAccount.isEnabled = !mostrar
        etFullname.isEnabled = !mostrar
        etEmail.isEnabled = !mostrar
        etPassword.isEnabled = !mostrar
        etConfirmPassword.isEnabled = !mostrar
        cbTerms.isEnabled = !mostrar
    }
}