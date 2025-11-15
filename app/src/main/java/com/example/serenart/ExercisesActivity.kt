package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class ExercisesActivity : AppCompatActivity() {

    private lateinit var btnFilter: ImageButton
    private lateinit var rvRecommended: RecyclerView
    private lateinit var rvCalmStorm: RecyclerView
    private lateinit var rvPresentAnchor: RecyclerView
    private lateinit var rvExploringEmotions: RecyclerView
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercises)

        initViews()
        setupRecyclerViews()
        setupClickListeners()
        setupBottomNavigation()
        loadExercises()
    }

    private fun initViews() {
        btnFilter = findViewById(R.id.btn_filter)
        rvRecommended = findViewById(R.id.rv_recommended)
        rvCalmStorm = findViewById(R.id.rv_calm_storm)
        rvPresentAnchor = findViewById(R.id.rv_present_anchor)
        rvExploringEmotions = findViewById(R.id.rv_exploring_emotions)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setupRecyclerViews() {
        // Configurar RecyclerViews horizontales
        rvRecommended.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        rvCalmStorm.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        rvPresentAnchor.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        rvExploringEmotions.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        // TODO: Configurar adapters
    }

    private fun setupClickListeners() {
        btnFilter.setOnClickListener {
            // TODO: Mostrar dialog de filtros
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_exercises

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_exercises -> {
                    // Ya estamos en Ejercicios
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

    private fun loadExercises() {
        // TODO: Cargar ejercicios desde Firebase/Base de datos local
        // TODO: Aplicar filtros según el estado de ánimo del usuario
    }
}