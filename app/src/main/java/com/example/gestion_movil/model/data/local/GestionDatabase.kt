package com.example.gestion_movil.model.data.local


import android.content.Context
import android.content.pm.ApplicationInfo
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration

// exportSchema = true guarda cada versión del esquema en app/schemas/ (versionar en git).
// Al cambiar una entidad: subir version, agregar un Migration en MIGRACIONES y commitear el JSON nuevo.
@Database(
    entities = [
        ProductoEntity::class,
        MovimientoEntity::class,
        UbicacionEntity::class,
        ProveedorEntity::class,
        SolicitudEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class GestionDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun movimientoDao(): MovimientoDao
    abstract fun ubicacionDao(): UbicacionDao
    abstract fun proveedorDao(): ProveedorDao
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        @Volatile
        private var INSTANCE: GestionDatabase? = null

        // Migraciones entre versiones, en orden. Ejemplo para cuando exista la versión 2:
        // val MIGRACION_1_2 = object : Migration(1, 2) {
        //     override fun migrate(db: SupportSQLiteDatabase) {
        //         db.execSQL("ALTER TABLE productos ADD COLUMN notas TEXT NOT NULL DEFAULT ''")
        //     }
        // }
        private val MIGRACIONES = emptyArray<Migration>()

        fun getDatabase(context: Context): GestionDatabase {
            return INSTANCE ?: synchronized(this) {
                val app = context.applicationContext
                val esDebug = (app.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0

                Room.databaseBuilder(app, GestionDatabase::class.java, "gestion_database")
                    .addMigrations(*MIGRACIONES)
                    .apply {
                        // Solo en debug: si falta una migración, recrea la base (y vuelve a sembrar
                        // los datos de ejemplo) en vez de cerrar la app. En release nunca borra datos.
                        if (esDebug) fallbackToDestructiveMigration(dropAllTables = true)
                    }
                    .build().also { INSTANCE = it }
            }
        }
    }
}
