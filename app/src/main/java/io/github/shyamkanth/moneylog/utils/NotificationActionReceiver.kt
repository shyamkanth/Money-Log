package io.github.shyamkanth.moneylog.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "MARK_AS_DONE") {
            Toast.makeText(context, "Marked as Done!", Toast.LENGTH_SHORT).show()

            // OPTIONAL: Dismiss notification when action is clicked
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }
    }
}
