package com.example.serenart.data.repository

import com.example.serenart.models.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FirebaseRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Referencia a la colección de usuarios
    private val usuariosCollection = firestore.collection("usuarios")

    /**
     * Registra un nuevo usuario con Firebase Auth y crea su documento en Firestore
     */
    suspend fun registrarUsuario(
        nombreCompleto: String,
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            // 1. Crear usuario en Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Error al crear usuario")

            // 2. Crear documento en Firestore con el mismo UID
            val usuario = Usuario(
                id = firebaseUser.uid,
                nombre_completo = nombreCompleto,
                email = email,
                fecha_registro = obtenerFechaActual()
            )

            usuariosCollection.document(firebaseUser.uid)
                .set(usuario)
                .await()

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con email y contraseña
     */
    suspend fun iniciarSesion(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Usuario no encontrado")
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión del usuario actual
     */
    fun cerrarSesion() {
        auth.signOut()
    }

    /**
     * Obtiene el usuario actualmente autenticado
     */
    fun obtenerUsuarioActual(): FirebaseUser? {
        return auth.currentUser
    }

    /**
     * Verifica si hay un usuario logueado
     */
    fun estaLogueado(): Boolean {
        return auth.currentUser != null
    }

    /**
     * Obtiene los datos del usuario desde Firestore
     */
    suspend fun obtenerDatosUsuario(uid: String): Result<Usuario> {
        return try {
            val documento = usuariosCollection.document(uid).get().await()
            val usuario = documento.toObject(Usuario::class.java)
                ?: throw Exception("Usuario no encontrado en Firestore")
            Result.success(usuario)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza los datos del usuario en Firestore
     */
    suspend fun actualizarUsuario(uid: String, datos: Map<String, Any>): Result<Unit> {
        return try {
            usuariosCollection.document(uid).update(datos).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza el nombre completo del usuario
     */
    suspend fun actualizarNombre(uid: String, nombreCompleto: String): Result<Unit> {
        return actualizarUsuario(uid, mapOf("nombre_completo" to nombreCompleto))
    }

    /**
     * Actualiza las estadísticas del usuario
     */
    suspend fun actualizarEstadisticas(
        uid: String,
        sesionesCompletadas: Int? = null,
        rachaActual: Int? = null,
        emocionMasFrecuente: String? = null,
        minutosCreativos: Int? = null,
        ejercicioFavoritoId: String? = null
    ): Result<Unit> {
        return try {
            val updates = mutableMapOf<String, Any>()

            sesionesCompletadas?.let {
                updates["estadisticas_agregadas.total_sesiones_completadas"] = it
            }
            rachaActual?.let {
                updates["estadisticas_agregadas.racha_actual_dias"] = it
            }
            emocionMasFrecuente?.let {
                updates["estadisticas_agregadas.emocion_mas_frecuente"] = it
            }
            minutosCreativos?.let {
                updates["estadisticas_agregadas.total_minutos_creativos"] = it
            }
            ejercicioFavoritoId?.let {
                updates["estadisticas_agregadas.ejercicio_favorito_id"] = it
            }

            if (updates.isNotEmpty()) {
                usuariosCollection.document(uid).update(updates).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Incrementa el contador de sesiones completadas
     */
    suspend fun incrementarSesionesCompletadas(uid: String): Result<Unit> {
        return try {
            val usuario = obtenerDatosUsuario(uid).getOrThrow()
            val nuevasCompletadas = usuario.estadisticas_agregadas.total_sesiones_completadas + 1
            actualizarEstadisticas(uid, sesionesCompletadas = nuevasCompletadas)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza las preferencias de notificaciones
     */
    suspend fun actualizarNotificaciones(uid: String, activas: Boolean): Result<Unit> {
        return actualizarUsuario(
            uid,
            mapOf("preferencias.notificaciones_activas" to activas)
        )
    }

    /**
     * Envía email de recuperación de contraseña
     */
    suspend fun recuperarContrasena(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene la fecha actual en formato ISO 8601
     */
    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }

    // ========== MÉTODOS PARA SESIONES META (Solo Metadatos) ==========

    /**
     * Guarda SOLO los metadatos de una sesión en Firestore
     */
    suspend fun guardarSesionMeta(uid: String, sesionMeta: com.example.serenart.models.SesionMeta): Result<String> {
        return try {
            val sesionRef = usuariosCollection
                .document(uid)
                .collection("sesiones_meta")
                .document()

            val metaConId = sesionMeta.copy(id = sesionRef.id)
            sesionRef.set(metaConId).await()

            Result.success(sesionRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todos los metadatos de sesiones de un usuario
     */
    suspend fun obtenerSesionesMeta(uid: String): Result<List<com.example.serenart.models.SesionMeta>> {
        return try {
            val snapshot = usuariosCollection
                .document(uid)
                .collection("sesiones_meta")
                .orderBy("fecha_creacion", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val sesiones = snapshot.documents.mapNotNull {
                it.toObject(com.example.serenart.models.SesionMeta::class.java)
            }
            Result.success(sesiones)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene sesiones meta filtradas por tipo
     */
    suspend fun obtenerSesionesMetaPorTipo(
        uid: String,
        tipo: com.example.serenart.models.TipoSesion
    ): Result<List<com.example.serenart.models.SesionMeta>> {
        return try {
            val tipoStr = tipo.name
            val snapshot = usuariosCollection
                .document(uid)
                .collection("sesiones_meta")
                .whereEqualTo("tipo", tipoStr)
                .orderBy("fecha_creacion", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val sesiones = snapshot.documents.mapNotNull {
                it.toObject(com.example.serenart.models.SesionMeta::class.java)
            }
            Result.success(sesiones)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene los metadatos de una sesión específica
     */
    suspend fun obtenerSesionMeta(uid: String, sessionId: String): Result<com.example.serenart.models.SesionMeta> {
        return try {
            val documento = usuariosCollection
                .document(uid)
                .collection("sesiones_meta")
                .document(sessionId)
                .get()
                .await()

            val sesion = documento.toObject(com.example.serenart.models.SesionMeta::class.java)
                ?: throw Exception("Sesión no encontrada")
            Result.success(sesion)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina los metadatos de una sesión
     */
    suspend fun eliminarSesionMeta(uid: String, sessionId: String): Result<Unit> {
        return try {
            usuariosCollection
                .document(uid)
                .collection("sesiones_meta")
                .document(sessionId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}