package com.example.gestion_movil.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.gestion_movil.MainActivity

// Avisos locales del teléfono cuando un producto queda con existencia baja
object NotificacionHelper {

    private const val CANAL_ID = "stock_bajo"

    // Android 13+ pide permiso para mostrar notificaciones
    fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Crea el canal "Stock bajo" (obligatorio desde Android 8)
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_ID,
                "Stock bajo",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisos de productos agotados o bajo su existencia mínima"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }

    // Muestra el aviso de un producto agotado o bajo el mínimo
    fun mostrarStockBajo(
        context: Context,
        productoId: Long,
        nombre: String,
        cantidad: Int,
        minimo: Int
    ) {
        if (!hasNotificationPermission(context)) return

        val titulo = if (cantidad == 0) "Producto agotado" else "Stock bajo"
        val texto = if (cantidad == 0) "$nombre se agotó (mínimo $minimo)"
        else "$nombre: quedan $cantidad (mínimo $minimo)"

        // Al tocar la notificación se abre la app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            // Un aviso por producto (si vuelve a bajar, se reemplaza)
            NotificationManagerCompat.from(context).notify(productoId.toInt(), notificacion)
        } catch (e: SecurityException) {
            // Sin permiso de notificaciones: no se muestra nada
        }
    }
}
