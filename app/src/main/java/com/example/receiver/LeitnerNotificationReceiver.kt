package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.PreferencesHelper
import com.example.reminder.LeitnerReminderManager

class LeitnerNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = PreferencesHelper(context)
        if (!prefs.leitnerNotificationEnabled) return

        when (intent?.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            "android.intent.action.QUICKBOOT_POWERON" -> {
                // Re-schedule daily alarm after device reboot
                LeitnerReminderManager.scheduleDailyReminder(
                    context,
                    prefs.leitnerNotificationHour,
                    prefs.leitnerNotificationMinute
                )
            }

            LeitnerReminderManager.ACTION_REMINDER -> {
                // Trigger daily notification
                LeitnerReminderManager.sendNotification(context, isTest = false)

                // Reschedule for next day
                LeitnerReminderManager.scheduleDailyReminder(
                    context,
                    prefs.leitnerNotificationHour,
                    prefs.leitnerNotificationMinute
                )
            }
        }
    }
}
