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
        // Crear todos los ejercicios por categoría
        val allExercises = createAllExercises()

        // Recomendados: mezcla de todas las categorías
        val recommendedExercises = listOf(
            allExercises["calma"]!![0],
            allExercises["emociones"]!![0],
            allExercises["anclaje"]!![0],
            allExercises["calma"]!![1],
            allExercises["emociones"]!![1]
        )

        // Configurar adaptadores con ejercicios específicos por categoría
        rvRecommended.adapter = ExerciseAdapter(recommendedExercises) { exercise ->
            navigateToDrawing(exercise)
        }

        rvCalmStorm.adapter = ExerciseAdapter(allExercises["calma"]!!) { exercise ->
            navigateToDrawing(exercise)
        }

        rvPresentAnchor.adapter = ExerciseAdapter(allExercises["anclaje"]!!) { exercise ->
            navigateToDrawing(exercise)
        }

        rvExploringEmotions.adapter = ExerciseAdapter(allExercises["emociones"]!!) { exercise ->
            navigateToDrawing(exercise)
        }
    }

    private fun createAllExercises(): Map<String, List<Exercise>> {
        return mapOf(
            "calma" to listOf(
                Exercise(
                    id = "calma_1",
                    title = "Mandala de la Calma",
                    description = "Dibuja patrones repetitivos para encontrar paz interior",
                    duration = "15 min",
                    category = "calma",
                    imageRes = R.drawable.placeholder_exercise
                ),
                Exercise(
                    id = "calma_2",
                    title = "Respiración Cromática",
                    description = "Pinta mientras respiras conscientemente",
                    duration = "20 min",
                    category = "calma",
                    imageRes = R.drawable.placeholder_exercise
                ),
                Exercise(
                    id = "calma_3",
                    title = "Olas de Serenidad",
                    description = "Dibuja líneas fluidas como el agua",
                    duration = "10 min",
                    category = "calma",
                    imageRes = R.drawable.placeholder_exercise
                )
            ),
            "anclaje" to listOf(
                Exercise(
                    id = "anclaje_1",
                    title = "Aquí y Ahora",
                    description = "Dibuja lo que ves en este momento",
                    duration = "15 min",
                    category = "anclaje",
                    imageRes = R.drawable.placeholder_exercise
                ),
                Exercise(
                    id = "anclaje_2",
                    title = "Trazos Conscientes",
                    description = "Cada línea es una conexión con el presente",
                    duration = "12 min",
                    category = "anclaje",
                    imageRes = R.drawable.placeholder_exercise
                ),
                Exercise(
                    id = "anclaje_3",
                    title = "Observación Detallada",
                    description = "Dibuja un objeto con total atención",
                    duration = "20 min",
                    category = "anclaje",
                    imageRes = R.drawable.placeholder_exercise
                )
            ),
            "emociones" to listOf(
                Exercise(
                    id = "emociones_1",
                    title = "Expresión Libre",
                    description = "Deja fluir tus emociones sin juzgar",
                    duration = "20 min",
                    category = "emociones",
                    imageRes = R.drawable.placeholder_exercise
                ),
                Exercise(
                    id = "emociones_2",
                    title = "Mapa Emocional",
                    description = "Representa cómo te sientes con colores",
                    duration = "25 min",
                    category = "emociones",
                    imageRes = R.drawable.placeholder_exercise
                ),
                Exercise(
                    id = "emociones_3",
                    title = "Garabatos Liberadores",
                    description = "Suelta la tensión a través del dibujo",
                    duration = "10 min",
                    category = "emociones",
                    imageRes = R.drawable.placeholder_exercise
                ),
                Exercise(
                    id = "emociones_4",
                    title = "Autorretrato Emocional",
                    description = "Dibuja cómo te ves interiormente hoy",
                    duration = "30 min",
                    category = "emociones",
                    imageRes = R.drawable.placeholder_exercise
                )
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