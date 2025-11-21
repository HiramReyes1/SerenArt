package com.example.serenart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class DiaryEntryActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnSave: ImageView
    private lateinit var tvDate: TextView
    private lateinit var moodSad: TextView
    private lateinit var moodNeutral: TextView
    private lateinit var moodHappy: TextView
    private lateinit var etEntryText: TextInputEditText
    private lateinit var cardDrawing: MaterialCardView
    private lateinit var ivDrawingPreview: ImageView
    private lateinit var layoutNoDrawing: LinearLayout
    private lateinit var btnAttachDrawing: MaterialButton

    private var selectedMood: String = "😐"
    private var hasDrawing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_entry)

        initViews()
        setupDate()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        btnSave = findViewById(R.id.btn_save)
        tvDate = findViewById(R.id.tv_date)
        moodSad = findViewById(R.id.mood_sad)
        moodNeutral = findViewById(R.id.mood_neutral)
        moodHappy = findViewById(R.id.mood_happy)
        etEntryText = findViewById(R.id.et_entry_text)
        cardDrawing = findViewById(R.id.card_drawing)
        ivDrawingPreview = findViewById(R.id.iv_drawing_preview)
        layoutNoDrawing = findViewById(R.id.layout_no_drawing)
        btnAttachDrawing = findViewById(R.id.btn_attach_drawing)
    }

    private fun setupDate() {
        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES")).format(Date())
        tvDate.text = currentDate
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveEntry()
        }

        moodSad.setOnClickListener {
            selectMood("😔")
        }

        moodNeutral.setOnClickListener {
            selectMood("😐")
        }

        moodHappy.setOnClickListener {
            selectMood("😊")
        }

        btnAttachDrawing.setOnClickListener {
            // Navegar a selección de dibujo (ejercicio o crear nuevo)
            showDrawingOptions()
        }

        cardDrawing.setOnClickListener {
            if (hasDrawing) {
                // Ver dibujo completo
                Toast.makeText(this, "Ver dibujo completo", Toast.LENGTH_SHORT).show()
            } else {
                showDrawingOptions()
            }
        }
    }

    private fun selectMood(mood: String) {
        selectedMood = mood

        // Reset todos los fondos
        moodSad.setBackgroundResource(android.R.color.transparent)
        moodNeutral.setBackgroundResource(android.R.color.transparent)
        moodHappy.setBackgroundResource(android.R.color.transparent)

        // Resaltar el seleccionado
        val selectedBg = android.R.drawable.dialog_holo_light_frame
        when (mood) {
            "😔" -> moodSad.setBackgroundResource(selectedBg)
            "😐" -> moodNeutral.setBackgroundResource(selectedBg)
            "😊" -> moodHappy.setBackgroundResource(selectedBg)
        }
    }

    private fun showDrawingOptions() {
        val options = arrayOf(
            "Crear nuevo dibujo",
            "Seleccionar de ejercicios completados"
        )

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Adjuntar Dibujo")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Crear nuevo dibujo
                        val intent = Intent(this, DrawingCanvasActivity::class.java)
                        intent.putExtra("FROM_DIARY", true)
                        startActivity(intent)
                    }
                    1 -> {
                        // TODO: Mostrar lista de ejercicios completados
                        Toast.makeText(this, "Próximamente: Seleccionar dibujo guardado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun saveEntry() {
        val entryText = etEntryText.text.toString().trim()

        if (entryText.isEmpty()) {
            Toast.makeText(this, "Escribe algo antes de guardar", Toast.LENGTH_SHORT).show()
            return
        }

        // TODO: Guardar en Firebase/Base de datos local

        Toast.makeText(this, "Entrada guardada exitosamente", Toast.LENGTH_SHORT).show()
        finish()
    }
}