package com.example.serenart

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.serenart.views.DrawingView
import java.io.File
import java.io.FileOutputStream

class DrawingCanvasActivity : AppCompatActivity() {

    private lateinit var drawingView: DrawingView
    private lateinit var toolbar: Toolbar
    private lateinit var btnInstructions: ImageButton
    private lateinit var btnUndo: ImageButton
    private lateinit var btnRedo: ImageButton
    private lateinit var btnClear: ImageButton
    private lateinit var btnSave: ImageButton
    private lateinit var layoutBrushSize: LinearLayout
    private lateinit var seekbarBrushSize: SeekBar
    private lateinit var tvBrushSize: TextView
    private lateinit var scrollColors: HorizontalScrollView
    private lateinit var layoutColors: LinearLayout
    private lateinit var btnToolBrush: LinearLayout
    private lateinit var btnToolEraser: LinearLayout
    private lateinit var btnToolColors: LinearLayout
    private lateinit var btnToolShapes: LinearLayout
    private lateinit var iconBrush: ImageView
    private lateinit var iconEraser: ImageView
    private lateinit var iconColors: ImageView
    private lateinit var iconShapes: ImageView

    // Paleta de colores terapéuticos
    private val therapeuticColors = arrayOf(
        Color.parseColor("#FF6B6B"), // Rojo cálido
        Color.parseColor("#4ECDC4"), // Turquesa
        Color.parseColor("#45B7D1"), // Azul cielo
        Color.parseColor("#FFA07A"), // Salmón
        Color.parseColor("#98D8C8"), // Verde menta
        Color.parseColor("#F7DC6F"), // Amarillo suave
        Color.parseColor("#BB8FCE"), // Lavanda
        Color.parseColor("#F8B88B"), // Durazno
        Color.parseColor("#85C1E2"), // Azul pastel
        Color.parseColor("#F06292"), // Rosa
        Color.parseColor("#AED581"), // Verde lima
        Color.parseColor("#FFB74D"), // Naranja
        Color.BLACK,
        Color.DKGRAY,
        Color.GRAY,
        Color.WHITE
    )

    private var currentColor = Color.BLACK
    private var currentTool = Tool.BRUSH

    enum class Tool {
        BRUSH, ERASER, COLORS, SHAPES
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing_canvas)

        initViews()
        setupToolbar()
        setupBrushSizeControl()
        setupColorPalette()
        setupTools()
        setupClickListeners()

        // Establecer color inicial
        drawingView.setColor(currentColor)

        //salida
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                mostrarDialogoSalida()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)

    }

    private fun initViews() {
        drawingView = findViewById(R.id.drawing_view)
        toolbar = findViewById(R.id.toolbar)
        btnInstructions = findViewById(R.id.btn_instructions)
        btnUndo = findViewById(R.id.btn_undo)
        btnRedo = findViewById(R.id.btn_redo)
        btnClear = findViewById(R.id.btn_clear)
        btnSave = findViewById(R.id.btn_save)
        layoutBrushSize = findViewById(R.id.layout_brush_size)
        seekbarBrushSize = findViewById(R.id.seekbar_brush_size)
        tvBrushSize = findViewById(R.id.tv_brush_size)
        scrollColors = findViewById(R.id.scroll_colors)
        layoutColors = findViewById(R.id.layout_colors)
        btnToolBrush = findViewById(R.id.btn_tool_brush)
        btnToolEraser = findViewById(R.id.btn_tool_eraser)
        btnToolColors = findViewById(R.id.btn_tool_colors)
        btnToolShapes = findViewById(R.id.btn_tool_shapes)
        iconBrush = findViewById(R.id.icon_brush)
        iconEraser = findViewById(R.id.icon_eraser)
        iconColors = findViewById(R.id.icon_colors)
        iconShapes = findViewById(R.id.icon_shapes)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupBrushSizeControl() {
        val initialSize = drawingView.getBrushSize().toInt()
        seekbarBrushSize.progress = initialSize
        tvBrushSize.text = initialSize.toString()

        seekbarBrushSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val size = maxOf(5, progress) // Mínimo 5
                drawingView.setBrushSize(size.toFloat())
                tvBrushSize.text = size.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupColorPalette() {
        therapeuticColors.forEach { color ->
            val colorView = createColorView(color)
            layoutColors.addView(colorView)
        }
    }

    private fun createColorView(color: Int): View {
        val size = resources.getDimensionPixelSize(R.dimen.icon_size_large)
        val margin = resources.getDimensionPixelSize(R.dimen.padding_small)

        val view = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                setMargins(margin, margin, margin, margin)
            }

            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
                setStroke(4, Color.GRAY)
            }

            setOnClickListener {
                selectColor(color)
            }
        }

        return view
    }

    private fun selectColor(color: Int) {
        currentColor = color
        drawingView.setColor(color)
        updateToolSelection(Tool.BRUSH)
    }

    private fun setupTools() {
        updateToolSelection(Tool.BRUSH)
    }

    private fun setupClickListeners() {
        btnInstructions.setOnClickListener {
            showInstructionsDialog()
        }

        btnUndo.setOnClickListener {
            drawingView.undo()
            updateUndoRedoButtons()
        }

        btnRedo.setOnClickListener {
            drawingView.redo()
            updateUndoRedoButtons()
        }

        btnClear.setOnClickListener {
            showClearConfirmationDialog()
        }

        btnSave.setOnClickListener {
            saveDrawing()
        }

        btnToolBrush.setOnClickListener {
            selectTool(Tool.BRUSH)
        }

        btnToolEraser.setOnClickListener {
            selectTool(Tool.ERASER)
        }

        btnToolColors.setOnClickListener {
            selectTool(Tool.COLORS)
        }

        btnToolShapes.setOnClickListener {
            selectTool(Tool.SHAPES)
        }
    }

    private fun selectTool(tool: Tool) {
        currentTool = tool
        updateToolSelection(tool)

        when (tool) {
            Tool.BRUSH -> {
                drawingView.enableBrush()
                drawingView.setColor(currentColor)
                layoutBrushSize.visibility = View.VISIBLE
                scrollColors.visibility = View.GONE
            }
            Tool.ERASER -> {
                drawingView.enableEraser()
                layoutBrushSize.visibility = View.VISIBLE
                scrollColors.visibility = View.GONE
            }
            Tool.COLORS -> {
                layoutBrushSize.visibility = View.GONE
                scrollColors.visibility = View.VISIBLE
            }
            Tool.SHAPES -> {
                showShapesDialog()
                layoutBrushSize.visibility = View.GONE
                scrollColors.visibility = View.GONE
            }
        }
    }

    private fun updateToolSelection(tool: Tool) {
        val activeColor = ContextCompat.getColor(this, R.color.button_primary)
        val inactiveColor = ContextCompat.getColor(this, R.color.text_secondary)

        // Resetear todos los iconos
        iconBrush.setColorFilter(inactiveColor)
        iconEraser.setColorFilter(inactiveColor)
        iconColors.setColorFilter(inactiveColor)
        iconShapes.setColorFilter(inactiveColor)

        // Activar el icono seleccionado
        when (tool) {
            Tool.BRUSH -> iconBrush.setColorFilter(activeColor)
            Tool.ERASER -> iconEraser.setColorFilter(activeColor)
            Tool.COLORS -> iconColors.setColorFilter(activeColor)
            Tool.SHAPES -> iconShapes.setColorFilter(activeColor)
        }
    }

    private fun updateUndoRedoButtons() {
        btnUndo.isEnabled = drawingView.canUndo()
        btnRedo.isEnabled = drawingView.canRedo()

        btnUndo.alpha = if (drawingView.canUndo()) 1.0f else 0.5f
        btnRedo.alpha = if (drawingView.canRedo()) 1.0f else 0.5f
    }

    private fun showInstructionsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Instrucciones")
            .setMessage("""
                🎨 Usa el pincel para dibujar libremente
                🧹 La goma te permite borrar trazos
                🎨 Selecciona colores de la paleta
                ↩️ Usa deshacer/rehacer para corregir
                💾 Guarda tu creación cuando termines
                
                Recuerda: No hay trazos incorrectos, solo expresión libre.
            """.trimIndent())
            .setPositiveButton("Entendido", null)
            .show()
    }

    private fun showClearConfirmationDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Limpiar lienzo")
            .setMessage("¿Estás seguro de que deseas borrar todo el dibujo?")
            .setPositiveButton("Sí") { _, _ ->
                drawingView.clearCanvas()
                updateUndoRedoButtons()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showShapesDialog() {
        val shapes = arrayOf("Círculo", "Rectángulo", "Línea")
        MaterialAlertDialogBuilder(this)
            .setTitle("Selecciona una forma")
            .setItems(shapes) { _, which ->
                // TODO: Implementar dibujo de formas
                Toast.makeText(this, "Forma ${shapes[which]} seleccionada", Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun saveDrawing() {
        val bitmap = drawingView.getBitmap()
        if (bitmap != null) {
            try {
                // Guardar en directorio interno de la app
                val filename = "drawing_${System.currentTimeMillis()}.png"
                val file = File(filesDir, filename)

                FileOutputStream(file).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                }

                Toast.makeText(this, "Dibujo guardado exitosamente", Toast.LENGTH_SHORT).show()

                // TODO: Guardar en Firebase Storage
                // TODO: Guardar referencia en Firestore

                // Volver a la pantalla anterior
                finish()

            } catch (e: Exception) {
                Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoSalida() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Salir sin guardar")
            .setMessage("¿Deseas salir sin guardar tu dibujo?")
            .setPositiveButton("Salir") { _, _ ->
                // Importante: desactivar el callback para permitir el back normal
                this.onBackPressedDispatcher.onBackPressed()
            }
            .setNegativeButton("Continuar dibujando", null)
            .show()
    }
}