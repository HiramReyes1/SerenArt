package com.example.serenart

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DiaryEntryDetailActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvDate: TextView
    private lateinit var tvMood: TextView
    private lateinit var ivDrawing: ImageView
    private lateinit var tvEntryText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_entry_detail)

        initViews()
        loadEntryData()
        setupClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back)
        tvDate = findViewById(R.id.tv_date)
        tvMood = findViewById(R.id.tv_mood)
        ivDrawing = findViewById(R.id.iv_drawing)
        tvEntryText = findViewById(R.id.tv_entry_text)
    }

    private fun loadEntryData() {
        // Obtener datos del Intent
        val date = intent.getStringExtra("ENTRY_DATE") ?: ""
        val mood = intent.getStringExtra("ENTRY_MOOD") ?: "😐"
        val text = intent.getStringExtra("ENTRY_TEXT") ?: ""

        tvDate.text = date
        tvMood.text = mood
        tvEntryText.text = text

        // TODO: Cargar imagen del dibujo desde path
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }
}