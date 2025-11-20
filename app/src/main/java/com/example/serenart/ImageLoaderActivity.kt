package com.example.serenart

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.io.File
import java.io.IOException

class ImageLoaderActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var cardCamera: MaterialCardView
    private lateinit var cardGallery: MaterialCardView
    private lateinit var cardPreview: MaterialCardView
    private lateinit var ivPreview: ImageView
    private lateinit var btnRetake: MaterialButton
    private lateinit var btnUseImage: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProcessing: TextView

    private var currentImageUri: Uri? = null

    private var currentImageBitmap: Bitmap? = null
    private var photoFile: File? = null

    // Activity Result Launchers
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentImageUri?.let { uri ->
                processImage(uri)
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            currentImageUri = it
            processImage(it)
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Se necesita permiso de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_loader)

        initViews()
        setupToolbar()
        setupClickListeners()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        cardCamera = findViewById(R.id.card_camera)
        cardGallery = findViewById(R.id.card_gallery)
        cardPreview = findViewById(R.id.card_preview)
        ivPreview = findViewById(R.id.iv_preview)
        btnRetake = findViewById(R.id.btn_retake)
        btnUseImage = findViewById(R.id.btn_use_image)
        progressBar = findViewById(R.id.progress_bar)
        tvProcessing = findViewById(R.id.tv_processing)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupClickListeners() {
        cardCamera.setOnClickListener {
            checkCameraPermission()
        }

        cardGallery.setOnClickListener {
            openGallery()
        }

        btnRetake.setOnClickListener {
            resetView()
        }

        btnUseImage.setOnClickListener {
            useImage()
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        try {
            photoFile = createImageFile()

            val file = photoFile ?: throw IOException("No se pudo crear el archivo")

            // Crear el URI (ya NO es null)
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            // Guardamos el URI
            currentImageUri = uri

            // Lanzamos cámara con un Uri NO nulo
            cameraLauncher.launch(uri)

        } catch (e: IOException) {
            Toast.makeText(this, "Error al crear archivo: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun createImageFile(): File {
        val timestamp = System.currentTimeMillis()
        val storageDir = getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        )
    }

    private fun processImage(uri: Uri) {
        showLoading(true)

        try {
            // Cargar bitmap desde URI
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                // TODO: Integrar OpenCV aquí
                // 1. Corrección de perspectiva
                // 2. Mejora tonal y balance de blancos
                // 3. Extracción de colores y formas

                // Por ahora, solo procesamiento básico
                currentImageBitmap = processImageBasic(bitmap)

                // Mostrar vista previa
                showPreview(currentImageBitmap!!)
            } else {
                Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show()
                showLoading(false)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            showLoading(false)
        }
    }

    private fun processImageBasic(bitmap: Bitmap): Bitmap {
        // Procesamiento básico: redimensionar si es muy grande
        val maxSize = 2048
        val width = bitmap.width
        val height = bitmap.height

        return if (width > maxSize || height > maxSize) {
            val scale = if (width > height) {
                maxSize.toFloat() / width
            } else {
                maxSize.toFloat() / height
            }

            val newWidth = (width * scale).toInt()
            val newHeight = (height * scale).toInt()

            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
    }

    private fun showPreview(bitmap: Bitmap) {
        ivPreview.setImageBitmap(bitmap)
        cardPreview.visibility = View.VISIBLE
        cardCamera.visibility = View.GONE
        cardGallery.visibility = View.GONE
        showLoading(false)
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        tvProcessing.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun resetView() {
        cardPreview.visibility = View.GONE
        cardCamera.visibility = View.VISIBLE
        cardGallery.visibility = View.VISIBLE
        currentImageBitmap = null
        currentImageUri = null
    }

    private fun useImage() {
        currentImageBitmap?.let { bitmap ->
            // Navegar a DrawingCanvasActivity con la imagen
            val intent = Intent(this, DrawingCanvasActivity::class.java)

            // TODO: Pasar el bitmap a la siguiente activity
            // Opción 1: Guardar en archivo temporal y pasar la ruta
            // Opción 2: Usar un singleton o ViewModel compartido

            startActivity(intent)
            finish()
        }
    }

    // TODO: Métodos de OpenCV para procesamiento avanzado
    /*
    private fun applyPerspectiveCorrection(bitmap: Bitmap): Bitmap {
        // Implementar con OpenCV:
        // - Detección de bordes
        // - Transformación de perspectiva
        // - Corrección de distorsión
        return bitmap
    }

    private fun enhanceToneAndWhiteBalance(bitmap: Bitmap): Bitmap {
        // Implementar con OpenCV:
        // - Corrección de histograma
        // - Balance de blancos automático
        // - Mejora de contraste
        return bitmap
    }

    private fun extractColorsAndShapes(bitmap: Bitmap): Map<String, Any> {
        // Implementar con OpenCV:
        // - Detección de colores dominantes
        // - Segmentación de formas
        // - Análisis de composición
        return mapOf()
    }
    */
}