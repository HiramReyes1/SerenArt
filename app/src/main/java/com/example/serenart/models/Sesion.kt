package com.example.serenart.models

/**
 * Modelo para METADATOS almacenados en Firestore
 * NO contiene imágenes ni textos reales
 */
data class SesionMeta(
    val id: String = "",
    val fecha_creacion: String = "",
    val tipo: TipoSesion = TipoSesion.DIBUJO,
    val ejercicio_id: String = "",
    val ejercicio_nombre: String = "",
    val emocion_registrada: String = "", // Emoji: "😔", "😐", "😊"
    val emocion_tag: String = "", // "tristeza", "neutral", "alegria", "calma"
    val duracion_segundos: Int = 0,
    val paleta_colores: List<String> = emptyList(), // ["#2E5077", "#5D8BF4"]
    val tiene_imagen: Boolean = false,
    val tiene_texto: Boolean = false,
    val dispositivo_origen: String = "",
    val version_app: String = ""
)

/**
 * Modelo para CONTENIDO LOCAL almacenado en SQLite
 * Contiene las imágenes y textos reales
 */
data class SesionLocal(
    val id: Long = 0,
    val firestore_meta_id: String = "",
    val tipo: TipoSesion = TipoSesion.DIBUJO,
    val imagen_path: String? = null, // Ruta local del archivo
    val texto_diario: String? = null, // Contenido del diario
    val fecha_creacion_local: String = "",
    val sincronizado: Boolean = false
)

/**
 * Modelo completo para la UI (combina metadatos + contenido local)
 */
data class SesionCompleta(
    val metadatos: SesionMeta,
    val contenidoLocal: SesionLocal?
)

enum class TipoSesion {
    DIBUJO,
    DIARIO,
    DIBUJO_CON_DIARIO
}