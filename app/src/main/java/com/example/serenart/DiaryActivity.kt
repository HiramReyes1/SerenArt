package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.serenart.adapters.DiaryAdapter
import com.example.serenart.models.DiaryEntry
import com.google.android.material.bottomnavigation.BottomNavigationView

class DiaryActivity : AppCompatActivity() {

    private lateinit var btnAdd: ImageButton
    private lateinit var btnFilter: ImageButton
    private lateinit var rvDiaryEntries: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        setupBottomNavigation()
        loadDiaryEntries()
    }

    private fun initViews() {
        btnAdd = findViewById(R.id.btn_add)
        btnFilter = findViewById(R.id.btn_filter)
        rvDiaryEntries = findViewById(R.id.rv_diary_entries)
        layoutEmptyState = findViewById(R.id.layout_empty_state)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setupRecyclerView() {
        // Grid de 2 columnas para las entradas
        rvDiaryEntries.layoutManager = GridLayoutManager(this, 2)
    }

    private fun setupClickListeners() {
        btnAdd.setOnClickListener {
            // Navegar a crear nueva entrada
            val intent = Intent(this, DiaryEntryActivity::class.java)
            startActivity(intent)
        }

        btnFilter.setOnClickListener {
            Toast.makeText(this, "Filtros próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_diary

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_exercises -> {
                    startActivity(Intent(this, ExercisesActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_diary -> {
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

    private fun loadDiaryEntries() {
        // Datos de ejemplo - reemplazar con Firebase
        val entries = createSampleEntries()

        if (entries.isEmpty()) {
            // Mostrar estado vacío
            rvDiaryEntries.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        } else {
            // Mostrar entradas
            rvDiaryEntries.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE

            // Configurar adapter
            rvDiaryEntries.adapter = DiaryAdapter(entries) { entry ->
                openEntryDetail(entry)
            }
        }
    }

    private fun createSampleEntries(): List<DiaryEntry> {
        return listOf(
            DiaryEntry(
                id = "1",
                date = "20 Nov 2024",
                entryText = "Hoy me sentí muy tranquilo después del ejercicio de mandala. Los colores me ayudaron a expresar mi calma interior.",
                mood = "😊"
            ),
            DiaryEntry(
                id = "2",
                date = "19 Nov 2024",
                entryText = "Día complicado, pero el dibujo me ayudó a procesar mis emociones. Me di cuenta de que necesito más tiempo para mí.",
                mood = "😐"
            ),
            DiaryEntry(
                id = "3",
                date = "18 Nov 2024",
                entryText = "Increíble cómo el arte puede cambiar mi perspectiva. Hoy dibujé un paisaje y me sentí en paz.",
                mood = "😊"
            ),
            DiaryEntry(
                id = "4",
                date = "17 Nov 2024",
                entryText = "Me costó concentrarme hoy, pero el ejercicio de respiración mientras dibujaba me ayudó mucho.",
                mood = "😐"
            ),
            DiaryEntry(
                id = "5",
                date = "16 Nov 2024",
                entryText = "Primera vez usando la app. Me gusta poder expresarme sin palabras, solo con colores y formas.",
                mood = "😊"
            ),
            DiaryEntry(
                id = "6",
                date = "15 Nov 2024",
                entryText = "Día difícil en el trabajo. El dibujo libre me permitió soltar el estrés acumulado.",
                mood = "😔"
            )
        )
    }

    private fun openEntryDetail(entry: DiaryEntry) {
        val intent = Intent(this, DiaryEntryDetailActivity::class.java).apply {
            putExtra("ENTRY_ID", entry.id)
            putExtra("ENTRY_DATE", entry.date)
            putExtra("ENTRY_TEXT", entry.entryText)
            putExtra("ENTRY_MOOD", entry.mood)
        }
        startActivity(intent)
    }
}