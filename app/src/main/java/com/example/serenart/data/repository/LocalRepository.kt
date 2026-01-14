package com.example.serenart.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.serenart.models.SesionLocal
import com.example.serenart.models.TipoSesion
import java.text.SimpleDateFormat
import java.util.*

class LocalRepository(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "serenart_local.db"
        private const val DATABASE_VERSION = 1

        // Tabla sesiones_locales
        private const val TABLE_SESIONES = "sesiones_locales"
        private const val COL_ID = "id"
        private const val COL_FIRESTORE_META_ID = "firestore_meta_id"
        private const val COL_TIPO = "tipo"
        private const val COL_IMAGEN_PATH = "imagen_path"
        private const val COL_TEXTO_DIARIO = "texto_diario"
        private const val COL_FECHA_CREACION = "fecha_creacion_local"
        private const val COL_SINCRONIZADO = "sincronizado"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSesiones = """
            CREATE TABLE $TABLE_SESIONES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FIRESTORE_META_ID TEXT NOT NULL UNIQUE,
                $COL_TIPO TEXT NOT NULL,
                $COL_IMAGEN_PATH TEXT,
                $COL_TEXTO_DIARIO TEXT,
                $COL_FECHA_CREACION DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COL_SINCRONIZADO INTEGER DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createTableSesiones)

        // Índices para mejorar rendimiento
        db.execSQL("CREATE INDEX idx_firestore_meta ON $TABLE_SESIONES($COL_FIRESTORE_META_ID)")
        db.execSQL("CREATE INDEX idx_fecha ON $TABLE_SESIONES($COL_FECHA_CREACION DESC)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SESIONES")
        onCreate(db)
    }

    /**
     * Guarda una sesión local en SQLite
     * @return ID de la sesión insertada
     */
    fun guardarSesion(
        firestoreMetaId: String,
        tipo: TipoSesion,
        imagenPath: String? = null,
        textoDiario: String? = null
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_FIRESTORE_META_ID, firestoreMetaId)
            put(COL_TIPO, tipo.name)
            put(COL_IMAGEN_PATH, imagenPath)
            put(COL_TEXTO_DIARIO, textoDiario)
            put(COL_FECHA_CREACION, obtenerFechaActual())
            put(COL_SINCRONIZADO, 0)
        }

        return db.insert(TABLE_SESIONES, null, values)
    }

    /**
     * Busca una sesión local por su ID de Firestore
     */
    fun buscarPorFirestoreId(firestoreMetaId: String): SesionLocal? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_SESIONES,
            null,
            "$COL_FIRESTORE_META_ID = ?",
            arrayOf(firestoreMetaId),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val sesion = SesionLocal(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                firestore_meta_id = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRESTORE_META_ID)),
                tipo = TipoSesion.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPO))),
                imagen_path = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGEN_PATH)),
                texto_diario = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEXTO_DIARIO)),
                fecha_creacion_local = cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA_CREACION)),
                sincronizado = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SINCRONIZADO)) == 1
            )
            cursor.close()
            sesion
        } else {
            cursor.close()
            null
        }
    }

    /**
     * Obtiene todas las sesiones locales
     */
    fun obtenerTodasLasSesiones(): List<SesionLocal> {
        val db = readableDatabase
        val sesiones = mutableListOf<SesionLocal>()
        val cursor = db.query(
            TABLE_SESIONES,
            null,
            null,
            null,
            null,
            null,
            "$COL_FECHA_CREACION DESC"
        )

        while (cursor.moveToNext()) {
            sesiones.add(
                SesionLocal(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)),
                    firestore_meta_id = cursor.getString(cursor.getColumnIndexOrThrow(COL_FIRESTORE_META_ID)),
                    tipo = TipoSesion.valueOf(cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPO))),
                    imagen_path = cursor.getString(cursor.getColumnIndexOrThrow(COL_IMAGEN_PATH)),
                    texto_diario = cursor.getString(cursor.getColumnIndexOrThrow(COL_TEXTO_DIARIO)),
                    fecha_creacion_local = cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA_CREACION)),
                    sincronizado = cursor.getInt(cursor.getColumnIndexOrThrow(COL_SINCRONIZADO)) == 1
                )
            )
        }
        cursor.close()
        return sesiones
    }

    /**
     * Actualiza el estado de sincronización
     */
    fun marcarComoSincronizado(firestoreMetaId: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_SINCRONIZADO, 1)
        }
        db.update(
            TABLE_SESIONES,
            values,
            "$COL_FIRESTORE_META_ID = ?",
            arrayOf(firestoreMetaId)
        )
    }

    /**
     * Elimina una sesión local
     */
    fun eliminarSesion(firestoreMetaId: String): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_SESIONES,
            "$COL_FIRESTORE_META_ID = ?",
            arrayOf(firestoreMetaId)
        )
    }

    /**
     * Actualiza la imagen de una sesión
     */
    fun actualizarImagen(firestoreMetaId: String, imagenPath: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_IMAGEN_PATH, imagenPath)
        }
        db.update(
            TABLE_SESIONES,
            values,
            "$COL_FIRESTORE_META_ID = ?",
            arrayOf(firestoreMetaId)
        )
    }

    /**
     * Actualiza el texto del diario
     */
    fun actualizarTexto(firestoreMetaId: String, textoDiario: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TEXTO_DIARIO, textoDiario)
        }
        db.update(
            TABLE_SESIONES,
            values,
            "$COL_FIRESTORE_META_ID = ?",
            arrayOf(firestoreMetaId)
        )
    }

    private fun obtenerFechaActual(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}