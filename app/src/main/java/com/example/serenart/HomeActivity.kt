package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class HomeActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var btnAdd: ImageButton
    private lateinit var btnNotifications: ImageButton
    private lateinit var cardExerciseDay: MaterialCardView
    private lateinit var btnStartExercise: MaterialButton
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
        btnAdd = findViewById(R.id.btn_add)
        btnNotifications = findViewById(R.id.btn_notifications)
        cardExerciseDay = findViewById(R.id.card_exercise_day)
        btnStartExercise = findViewById(R.id.btn_start_exercise)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setupClickListeners() {
        btnAdd.setOnClickListener {
            // TODO: Mostrar opciones de creación rápida
        }

        btnNotifications.setOnClickListener {
            // TODO: Abrir pantalla de notificaciones
        }

        btnStartExercise.setOnClickListener {
            // TODO: Navegar a detalle del ejercicio
        }

        cardExerciseDay.setOnClickListener {
            // TODO: Navegar a detalle del ejercicio
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
        val userName = "Usuario"
        tvGreeting.text = "Hola, $userName"
    }
}