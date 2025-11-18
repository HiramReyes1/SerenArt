package com.example.serenart.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.*

class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Configuración del pincel
    private var drawPaint: Paint = Paint().apply {
        color = Color.BLACK
        isAntiAlias = true
        strokeWidth = 25f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    // Canvas y bitmap para dibujo
    private var canvasBitmap: Bitmap? = null
    private var drawCanvas: Canvas? = null
    private val canvasPaint = Paint(Paint.DITHER_FLAG)

    // Path actual y historial para deshacer/rehacer
    private var drawPath = Path()
    private val paths = ArrayList<PathData>()
    private val undoPaths = ArrayList<PathData>()

    // Estado de la herramienta
    private var currentTool = Tool.BRUSH
    private var isEraser = false

    enum class Tool {
        BRUSH, ERASER, SHAPE_CIRCLE, SHAPE_RECTANGLE, SHAPE_LINE
    }

    // Clase para almacenar path con su configuración
    private data class PathData(
        val path: Path,
        val paint: Paint,
        val isEraser: Boolean = false
    )

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        drawPaint.isAntiAlias = true
        drawPaint.strokeWidth = 25f
        drawPaint.style = Paint.Style.STROKE
        drawPaint.strokeJoin = Paint.Join.ROUND
        drawPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Dibujar bitmap con todos los paths anteriores
        canvasBitmap?.let { canvas.drawBitmap(it, 0f, 0f, canvasPaint) }

        // Dibujar path actual
        canvas.drawPath(drawPath, drawPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                drawPath.moveTo(touchX, touchY)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                drawPath.lineTo(touchX, touchY)
            }
            MotionEvent.ACTION_UP -> {
                // Guardar el path actual
                val newPaint = Paint(drawPaint)
                paths.add(PathData(Path(drawPath), newPaint, isEraser))

                // Dibujar en el canvas permanente
                drawCanvas?.drawPath(drawPath, drawPaint)

                // Reiniciar path para el siguiente trazo
                drawPath.reset()

                // Limpiar redo stack al hacer un nuevo trazo
                undoPaths.clear()
            }
            else -> return false
        }

        invalidate()
        return true
    }

    // Métodos públicos para controlar el dibujo

    fun setBrushSize(size: Float) {
        drawPaint.strokeWidth = size
    }

    fun getBrushSize(): Float = drawPaint.strokeWidth

    fun setColor(color: Int) {
        drawPaint.color = color
        isEraser = false
        currentTool = Tool.BRUSH
    }

    fun enableEraser() {
        isEraser = true
        currentTool = Tool.ERASER
        drawPaint.color = Color.WHITE
    }

    fun enableBrush() {
        isEraser = false
        currentTool = Tool.BRUSH
    }

    fun getCurrentColor(): Int = drawPaint.color

    fun undo() {
        if (paths.isNotEmpty()) {
            val lastPath = paths.removeAt(paths.size - 1)
            undoPaths.add(lastPath)
            redrawCanvas()
        }
    }

    fun redo() {
        if (undoPaths.isNotEmpty()) {
            val redoPath = undoPaths.removeAt(undoPaths.size - 1)
            paths.add(redoPath)
            redrawCanvas()
        }
    }

    fun canUndo(): Boolean = paths.isNotEmpty()

    fun canRedo(): Boolean = undoPaths.isNotEmpty()

    fun clearCanvas() {
        paths.clear()
        undoPaths.clear()
        drawPath.reset()

        // Limpiar el bitmap
        canvasBitmap?.eraseColor(Color.TRANSPARENT)
        drawCanvas = canvasBitmap?.let { Canvas(it) }

        invalidate()
    }

    private fun redrawCanvas() {
        // Limpiar el canvas
        canvasBitmap?.eraseColor(Color.TRANSPARENT)
        drawCanvas = canvasBitmap?.let { Canvas(it) }

        // Redibujar todos los paths
        paths.forEach { pathData ->
            drawCanvas?.drawPath(pathData.path, pathData.paint)
        }

        invalidate()
    }

    fun getBitmap(): Bitmap? {
        // Crear un bitmap con fondo blanco para guardar
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        // Dibujar todos los paths
        paths.forEach { pathData ->
            canvas.drawPath(pathData.path, pathData.paint)
        }

        return bitmap
    }

    fun loadBitmap(bitmap: Bitmap) {
        // Cargar una imagen existente en el canvas
        canvasBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        drawCanvas = Canvas(canvasBitmap!!)
        invalidate()
    }
}