package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var btnNotifications: ImageButton
    private lateinit var cardExerciseDay: MaterialCardView
    private lateinit var btnStartExercise: MaterialButton
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupClickListeners()
        setupBottomNavigation()
        loadUserData()
    }

    private fun initViews() {
        tvGreeting = findViewById(R.id.tv_greeting)
        btnNotifications = findViewById(R.id.btn_notifications)
        cardExerciseDay = findViewById(R.id.card_exercise_day)
        btnStartExercise = findViewById(R.id.btn_start_exercise)
        fabAdd = findViewById(R.id.fab_add)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setupClickListeners() {
        btnNotifications.setOnClickListener {
            // TODO: Abrir pantalla de notificaciones
        }

        btnStartExercise.setOnClickListener {
            // TODO: Navegar a detalle del ejercicio
        }

        cardExerciseDay.setOnClickListener {
            // TODO: Navegar a detalle del ejercicio
        }

        fabAdd.setOnClickListener {
            // TODO: Mostrar opciones de creación rápida
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
        // TODO: Cargar datos del usuario desde Firebase/Preferencias
        val userName = "Usuario" // Placeholder
        tvGreeting.text = getString(R.string.hello, userName)

        // TODO: Cargar gráfico de progreso emocional
        // TODO: Cargar ejercicio del día
    }

    override fun onBackPressed() {
        // Preguntar si desea salir de la aplicación
        super.onBackPressed()
    }
}