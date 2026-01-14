package com.example.serenart.data.repository

import android.content.Context
import com.example.serenart.models.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repositorio híbrido que coordina Firestore (metadatos) y SQLite (contenido)
 */
class SesionesRepository(
    private val context: Context,
    private val firebaseRepository: FirebaseRepository
) {
    private val localRepository = LocalRepository(context)

    /**
     * Guarda una sesión completa (metadatos en Firestore + contenido en SQLite)
     */
    suspend fun guardarSesionCompleta(
        uid: String,
        tipo: TipoSesion,
        ejercicioId: String,
        ejercicioNombre: String,
        emocionRegistrada: String,
        emocionTag: String,
        duracionSegundos: Int,
        paletaColores: List<String>,
        imagenPath: String? = null,
        textoDiario: String? = null
    ): Result<String> {
        return try {
            // 1. Crear metadatos para Firestore (SIN contenido sensible)
            val metadatos = SesionMeta(
                id = "", // Se asignará automáticamente
                fecha_creacion = obtenerFechaISO(),
                tipo = tipo,
                ejercicio_id = ejercicioId,
                ejercicio_nombre = ejercicioNombre,
                emocion_registrada = emocionRegistrada,
                emocion_tag = emocionTag,
                duracion_segundos = duracionSegundos,
                paleta_colores = paletaColores,
                tiene_imagen = imagenPath != null,
                tiene_texto = textoDiario != null,
                dispositivo_origen = android.os.Build.MODEL,
                version_app = "1.0.0" // TODO: Obtener de BuildConfig
            )

            // 2. Guardar metadatos en Firestore
            val firestoreId = firebaseRepository.guardarSesionMeta(uid, metadatos).getOrThrow()

            // 3. Guardar contenido en SQLite local
            localRepository.guardarSesion(
                firestoreMetaId = firestoreId,
                tipo = tipo,
                imagenPath = imagenPath,
                textoDiario = textoDiario
            )

            // 4. Marcar como sincronizado
            localRepository.marcarComoSincronizado(firestoreId)

            Result.success(firestoreId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las sesiones completas (metadatos + contenido local)
     */
    suspend fun obtenerSesionesCompletas(uid: String): Result<List<SesionCompleta>> {
        return try {
            // 1. Obtener metadatos desde Firestore
            val metadatos = firebaseRepository.obtenerSesionesMeta(uid).getOrThrow()

            // 2. Para cada metadato, buscar contenido local
            val sesionesCompletas = metadatos.map { meta ->
                val contenidoLocal = localRepository.buscarPorFirestoreId(meta.id)
                SesionCompleta(
                    metadatos = meta,
                    contenidoLocal = contenidoLocal
                )
            }

            Result.success(sesionesCompletas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene una sesión completa específica
     */
    suspend fun obtenerSesionCompleta(uid: String, sessionId: String): Result<SesionCompleta> {
        return try {
            // 1. Obtener metadatos desde Firestore
            val metadatos = firebaseRepository.obtenerSesionMeta(uid, sessionId).getOrThrow()

            // 2. Buscar contenido local
            val contenidoLocal = localRepository.buscarPorFirestoreId(sessionId)

            Result.success(
                SesionCompleta(
                    metadatos = metadatos,
                    contenidoLocal = contenidoLocal
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una sesión completa (Firestore + SQLite)
     */
    suspend fun eliminarSesionCompleta(uid: String, sessionId: String): Result<Unit> {
        return try {
            // 1. Eliminar de Firestore
            firebaseRepository.eliminarSesionMeta(uid, sessionId).getOrThrow()

            // 2. Eliminar de SQLite
            localRepository.eliminarSesion(sessionId)

            // 3. TODO: Eliminar archivo de imagen del almacenamiento local

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Adjunta una imagen a una sesión existente
     */
    suspend fun adjuntarImagenASesion(
        uid: String,
        sessionId: String,
        imagenPath: String,
        paletaColores: List<String>
    ): Result<Unit> {
        return try {
            // 1. Actualizar metadatos en Firestore
            val metaActualizados = mapOf(
                "tiene_imagen" to true,
                "paleta_colores" to paletaColores
            )
            firebaseRepository.actualizarUsuario(uid, metaActualizados).getOrThrow()

            // 2. Actualizar SQLite local
            localRepository.actualizarImagen(sessionId, imagenPath)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sincroniza sesiones locales pendientes con Firestore
     */
    suspend fun sincronizarSesionesPendientes(uid: String): Result<Int> {
        return try {
            val sesionesPendientes = localRepository.obtenerTodasLasSesiones()
                .filter { !it.sincronizado }

            var sincronizadas = 0

            sesionesPendientes.forEach { sesionLocal ->
                // Crear metadatos y subir a Firestore
                // ... lógica de sincronización
                sincronizadas++
            }

            Result.success(sincronizadas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun obtenerFechaISO(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
}