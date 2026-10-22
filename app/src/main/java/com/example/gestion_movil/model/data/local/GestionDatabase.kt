package com.example.gestion_movil.model.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductoEntity::class,
        MovimientoEntity::class,
        UbicacionEntity::class,
        ProveedorEntity::class,
        SolicitudEntity::class
    ],
    version = 1
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

        fun getDatabase(context: Context): GestionDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GestionDatabase::class.java,
                    "gestion_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
