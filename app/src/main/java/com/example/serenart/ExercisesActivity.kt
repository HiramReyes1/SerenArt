package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serenart.adapters.ExerciseAdapter
import com.example.serenart.models.Exercise
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
    }

    private fun setupClickListeners() {
        btnFilter.setOnClickListener {
            Toast.makeText(this, "Filtros próximamente", Toast.LENGTH_SHORT).show()
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
        // Ejercicios de muestra - reemplazar con datos de Firebase
        val recommendedExercises = createSampleExercises("recomendado")
        val calmStormExercises = createSampleExercises("calma")
        val presentAnchorExercises = createSampleExercises("anclaje")
        val exploringEmotionsExercises = createSampleExercises("emociones")

        // Configurar adaptadores con callback para navegar
        rvRecommended.adapter = ExerciseAdapter(recommendedExercises) { exercise ->
            navigateToDrawing(exercise)
        }

        rvCalmStorm.adapter = ExerciseAdapter(calmStormExercises) { exercise ->
            navigateToDrawing(exercise)
        }

        rvPresentAnchor.adapter = ExerciseAdapter(presentAnchorExercises) { exercise ->
            navigateToDrawing(exercise)
        }

        rvExploringEmotions.adapter = ExerciseAdapter(exploringEmotionsExercises) { exercise ->
            navigateToDrawing(exercise)
        }
    }

    private fun createSampleExercises(category: String): List<Exercise> {
        // Datos de muestra - reemplazar con Firebase
        return listOf(
            Exercise(
                id = "1_$category",
                title = "Mandala de la Calma",
                description = "Dibuja patrones repetitivos",
                duration = "15 min",
                category = category,
                imageRes = R.drawable.placeholder_exercise
            ),
            Exercise(
                id = "2_$category",
                title = "Expresión Libre",
                description = "Deja fluir tus emociones",
                duration = "20 min",
                category = category,
                imageRes = R.drawable.placeholder_exercise
            ),
            Exercise(
                id = "3_$category",
                title = "Garabatos Conscientes",
                description = "Dibuja sin pensar",
                duration = "10 min",
                category = category,
                imageRes = R.drawable.placeholder_exercise
            ),
            Exercise(
                id = "4_$category",
                title = "Paisaje Emocional",
                description = "Representa tu estado actual",
                duration = "25 min",
                category = category,
                imageRes = R.drawable.placeholder_exercise
            )
        )
    }

    private fun navigateToDrawing(exercise: Exercise) {
        val intent = Intent(this, DrawingCanvasActivity::class.java).apply {
            putExtra("EXERCISE_ID", exercise.id)
            putExtra("EXERCISE_TITLE", exercise.title)
            putExtra("EXERCISE_CATEGORY", exercise.category)
        }
        startActivity(intent)
    }
}