package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DiaryActivity : AppCompatActivity() {

    private lateinit var btnFilter: ImageButton
    private lateinit var rvDiaryEntries: RecyclerView
    private lateinit var layoutEmptyState: LinearLayout
    private lateinit var fabNewEntry: FloatingActionButton
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
        btnFilter = findViewById(R.id.btn_filter)
        rvDiaryEntries = findViewById(R.id.rv_diary_entries)
        layoutEmptyState = findViewById(R.id.layout_empty_state)
        fabNewEntry = findViewById(R.id.fab_new_entry)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setupRecyclerView() {
        // Grid de 2 columnas para las entradas
        rvDiaryEntries.layoutManager = GridLayoutManager(this, 2)

        // TODO: Configurar adapter
    }

    private fun setupClickListeners() {
        btnFilter.setOnClickListener {
            // TODO: Mostrar dialog de filtros (por fecha, emoción, etc.)
        }

        fabNewEntry.setOnClickListener {
            // TODO: Navegar a pantalla de nueva entrada
            // startActivity(Intent(this, DiaryEntryActivity::class.java))
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
                    // Ya estamos en Diario
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
        // TODO: Cargar entradas desde Firebase/Base de datos local
        val entries = emptyList<Any>() // Placeholder

        if (entries.isEmpty()) {
            // Mostrar estado vacío
            rvDiaryEntries.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
        } else {
            // Mostrar entradas
            rvDiaryEntries.visibility = View.VISIBLE
            layoutEmptyState.visibility = View.GONE
            // TODO: Configurar adapter con las entradas
        }
    }
}