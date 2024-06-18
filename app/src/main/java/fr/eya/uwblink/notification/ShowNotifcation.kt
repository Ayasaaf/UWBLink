package fr.eya.uwblink.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import fr.eya.uwblink.R
import fr.eya.uwblink.ui.MainActivity


fun showImageReceivedNotification(context: Context, imageUri: Uri) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra("image_uri", imageUri.toString())
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle("Image Received")
        .setContentText("You have received a new image.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    if (ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    ) {
        with(NotificationManagerCompat.from(context)) {
            notify(NOTIFICATION_ID, builder.build())
        }
    } else {
        Log.e("Notification", "Permission not granted for posting notifications")
    }
}

const val NOTIFICATION_ID = 1