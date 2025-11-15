package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var ivProfilePicture: ShapeableImageView
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var btnEditProfile: MaterialButton
    private lateinit var tvConsecutiveDays: TextView
    private lateinit var tvCompletedExercises: TextView
    private lateinit var tvMostEmotion: TextView
    private lateinit var layoutNotifications: LinearLayout
    private lateinit var layoutHelp: LinearLayout
    private lateinit var btnLogout: MaterialButton
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        setupClickListeners()
        setupBottomNavigation()
        loadUserData()
    }

    private fun initViews() {
        ivProfilePicture = findViewById(R.id.iv_profile_picture)
        tvUserName = findViewById(R.id.tv_user_name)
        tvUserEmail = findViewById(R.id.tv_user_email)
        btnEditProfile = findViewById(R.id.btn_edit_profile)
        tvConsecutiveDays = findViewById(R.id.tv_consecutive_days)
        tvCompletedExercises = findViewById(R.id.tv_completed_exercises)
        tvMostEmotion = findViewById(R.id.tv_most_emotion)
        layoutNotifications = findViewById(R.id.layout_notifications)
        layoutHelp = findViewById(R.id.layout_help)
        btnLogout = findViewById(R.id.btn_logout)
        bottomNavigation = findViewById(R.id.bottom_navigation)
    }

    private fun setupClickListeners() {
        btnEditProfile.setOnClickListener {
            // TODO: Navegar a pantalla de editar perfil
        }

        ivProfilePicture.setOnClickListener {
            // TODO: Cambiar foto de perfil
        }

        layoutNotifications.setOnClickListener {
            // TODO: Navegar a configuración de notificaciones
        }

        layoutHelp.setOnClickListener {
            // TODO: Navegar a ayuda especializada
        }

        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.selectedItemId = R.id.nav_profile

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
                    startActivity(Intent(this, DiaryActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_profile -> {
                    // Ya estamos en Perfil
                    true
                }
                else -> false
            }
        }
    }

    private fun loadUserData() {
        // TODO: Cargar datos del usuario desde Firebase/Preferencias
        tvUserName.text = "Nombre Usuario"
        tvUserEmail.text = "usuario@email.com"

        // TODO: Cargar estadísticas desde la base de datos
        tvConsecutiveDays.text = "7"
        tvCompletedExercises.text = "23"
        tvMostEmotion.text = "😊"
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun performLogout() {
        // TODO: Cerrar sesión en Firebase
        // TODO: Limpiar preferencias locales

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}