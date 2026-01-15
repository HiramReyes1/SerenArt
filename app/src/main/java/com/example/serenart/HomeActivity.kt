package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.serenart.data.repository.FirebaseRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var btnAdd: ImageButton
    private lateinit var cardExerciseDay: MaterialCardView
    private lateinit var btnStartExercise: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView

    private val firebaseRepository = FirebaseRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar autenticación
        if (!firebaseRepository.estaLogueado()) {
            navigateToLogin()
            return
        }

        setContentView(R.layout.activity_home)

        initViews()
        setupClickListeners()
        setupBottomNavigation()
        loadUserData()
    }

    private fun initViews() {
        tvGreeting = findViewById(R.id.tv_greeting)
        btnAdd = findViewById(R.id.btn_add)
        cardExerciseDay = findViewById(R.id.card_exercise_day)
        btnStartExercise = findViewById(R.id.btn_start_exercise)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setupClickListeners() {
        btnAdd.setOnClickListener {
            // TODO: Mostrar opciones de creación rápida
            Toast.makeText(this, "Crear nuevo ejercicio", Toast.LENGTH_SHORT).show()
        }

        btnStartExercise.setOnClickListener {
            // TODO: Navegar a detalle del ejercicio
            Toast.makeText(this, "Iniciar ejercicio del día", Toast.LENGTH_SHORT).show()
        }

        cardExerciseDay.setOnClickListener {
            // TODO: Navegar a detalle del ejercicio
            Toast.makeText(this, "Ver ejercicio del día", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_home

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Ya estamos en Home
                    true
                }
                R.id.nav_exercises -> {
                    startActivity(Intent(this, ExercisesActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_diary -> {
                    startActivity(Intent(this, DiaryActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadUserData() {
        val firebaseUser = firebaseRepository.obtenerUsuarioActual()

        if (firebaseUser == null) {
            navigateToLogin()
            return
        }

        // Cargar datos básicos del usuario de Firebase Auth
        val email = firebaseUser.email ?: "Usuario"
        val nombreTemporal = email.substringBefore("@").capitalize()
        tvGreeting.text = "Hola, $nombreTemporal"

        // Cargar datos completos desde Firestore
        lifecycleScope.launch {
            val resultado = firebaseRepository.obtenerDatosUsuario(firebaseUser.uid)

            resultado.onSuccess { usuario ->
                // Actualizar saludo con el nombre completo
                val nombreCompleto = usuario.nombre_completo.split(" ").firstOrNull() ?: nombreTemporal
                tvGreeting.text = "Hola, $nombreCompleto"

                // Aquí puedes cargar más datos del usuario como estadísticas, etc.
                // TODO: Cargar estadísticas, ejercicio del día recomendado, etc.

            }.onFailure { excepcion ->
                Toast.makeText(
                    this@HomeActivity,
                    "Error al cargar datos del usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}