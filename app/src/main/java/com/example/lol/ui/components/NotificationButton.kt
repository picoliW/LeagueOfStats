package com.example.lol.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lol.ui.activities.MainActivity
import com.example.lol.viewModel.NotificationViewModel


@Composable
fun NotificationButton() {
    val context = LocalContext.current

    Button(onClick = {
        if (checkNotificationPermission(context)) {
            scheduleNotification(context)
        } else {
            requestNotificationPermission(context as MainActivity)
        }
    }) {
        Text(text = "Send notification")
    }
}

fun checkNotificationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED
}

fun requestNotificationPermission(activity: ComponentActivity) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
        0
    )
}

fun scheduleNotification(context: Context) {
    val notificationRequest = OneTimeWorkRequestBuilder<NotificationViewModel>().build()
    WorkManager.getInstance(context).enqueue(notificationRequest)
}
