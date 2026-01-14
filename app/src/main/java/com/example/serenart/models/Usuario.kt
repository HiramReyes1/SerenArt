package com.example.serenart.models

data class Usuario(
    val id: String = "",
    val nombre_completo: String = "",
    val email: String = "",
    val fecha_registro: String = "",
    val preferencias: Preferencias = Preferencias(),
    val estadisticas_agregadas: EstadisticasAgregadas = EstadisticasAgregadas(),
)

data class Preferencias(
    val notificaciones_activas: Boolean = true,
)

data class EstadisticasAgregadas(
    val total_sesiones_completadas: Int = 0,
    val racha_actual_dias: Int = 0,
    val emocion_mas_frecuente: String = "",
    val total_minutos_creativos: Int = 0,
    val ejercicio_favorito_id: String = ""
)