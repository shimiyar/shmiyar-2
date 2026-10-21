package com.example.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.data.local.DataLoader
import com.example.data.local.PreferencesHelper
import com.example.receiver.LeitnerNotificationReceiver
import java.util.Calendar

data class DueLeitnerInfo(
    val dueBoxes: List<Int>,
    val dueCategories: List<String>,
    val dueCardsCount: Int,
    val totalCardsCount: Int,
    val summaryText: String
)

object LeitnerReminderManager {

    const val CHANNEL_ID = "leitner_daily_reminder_channel"
    const val NOTIFICATION_ID = 2001
    const val ACTION_REMINDER = "com.shimiyar.action.LEITNER_REMINDER"

    /**
     * Creates the notification channel for Android 8.0+
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "یادآور روزانه جعبه لایتنر شیمی"
            val descriptionText = "اعلان‌های هوشمند مرور دوره‌ای فلش‌کارت‌های شیمی کنکور بر اساس الگوریتم لایتنر"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Calculates which boxes and categories are due for review today according to the Leitner algorithm.
     */
    fun calculateDueCards(context: Context): DueLeitnerInfo {
        val prefs = PreferencesHelper(context)
        val allCards = DataLoader.loadFlashcards(context)
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L

        // Day index in the 30-day Leitner cycle
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val cycleDay = (dayOfYear % 30).coerceAtLeast(1)

        // Leitner algorithm calendar schedule:
        // Box 1: Everyday
        // Box 2: Every 2 days (2, 4, 6, 8...)
        // Box 3: Every 4 days (4, 8, 12, 16...)
        // Box 4: Every 8 days (8, 16, 24...)
        // Box 5: Every 16 days (16, 30...)
        val scheduledBoxes = mutableListOf(1)
        if (cycleDay % 2 == 0) scheduledBoxes.add(2)
        if (cycleDay % 4 == 0) scheduledBoxes.add(3)
        if (cycleDay % 8 == 0) scheduledBoxes.add(4)
        if (cycleDay % 16 == 0 || cycleDay == 30) scheduledBoxes.add(5)

        val dueCategoriesSet = mutableSetOf<String>()
        var dueCount = 0

        for (card in allCards) {
            val box = prefs.getCardBox(card.id) // 0 or 1 = Box 1, up to 5
            val effectiveBox = if (box in 1..5) box else 1
            val lastReview = prefs.getCardLastReview(card.id)

            val intervalDays = when (effectiveBox) {
                1 -> 1
                2 -> 2
                3 -> 4
                4 -> 8
                5 -> 16
                else -> 1
            }

            val isDueByTime = (now - lastReview) >= (intervalDays * oneDayMs)
            val isDueBySchedule = scheduledBoxes.contains(effectiveBox)

            // A card is due if it has never been reviewed, or meets time interval or schedule
            if (lastReview == 0L || isDueByTime || isDueBySchedule) {
                dueCount++
                if (card.category.isNotBlank() && card.category != "همه") {
                    dueCategoriesSet.add(card.category)
                }
            }
        }

        val dueCategoriesList = dueCategoriesSet.toList()
        val dueBoxesSorted = scheduledBoxes.sorted()

        val summary = if (dueCategoriesList.isNotEmpty()) {
            val sampleCategories = dueCategoriesList.take(3).joinToString("، ")
            "امروز نوبت مرور جعبه‌های ${dueBoxesSorted.joinToString(" و ")} است. دسته‌های آماده: $sampleCategories"
        } else {
            "امروز نوبت مرور جعبه‌های ${dueBoxesSorted.joinToString(" و ")} است."
        }

        return DueLeitnerInfo(
            dueBoxes = dueBoxesSorted,
            dueCategories = dueCategoriesList,
            dueCardsCount = if (dueCount > 0) dueCount else allCards.size.coerceAtMost(15),
            totalCardsCount = allCards.size,
            summaryText = summary
        )
    }

    /**
     * Schedules the daily alarm to trigger the Leitner notification.
     */
    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        createNotificationChannel(context)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, LeitnerNotificationReceiver::class.java).apply {
            action = ACTION_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            // If the selected time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            // Fallback for devices restricting alarms
            try {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } catch (ignored: Exception) {}
        }
    }

    /**
     * Cancels the daily alarm.
     */
    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, LeitnerNotificationReceiver::class.java).apply {
            action = ACTION_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Builds and posts the Leitner notification immediately.
     */
    fun sendNotification(context: Context, isTest: Boolean = false) {
        createNotificationChannel(context)

        // Permission check for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionStatus = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val dueInfo = calculateDueCards(context)
        val prefs = PreferencesHelper(context)
        val streak = prefs.streakDays

        val primaryCategory = dueInfo.dueCategories.firstOrNull() ?: "مرورنامه دوازدهم (درست/نادرست)"

        // Tap action: launches MainActivity directly to Flashcards tab with category selected
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_NAVIGATE_TAB", "FLASHCARDS")
            putExtra("EXTRA_CATEGORY", primaryCategory)
        }

        val clickPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isTest) {
            "⚡ تست اعلان هوشمند جعبه لایتنر شیمی"
        } else {
            "⚛️ وقت مرور جعبه لایتنر شیمی کنکور رسید!"
        }

        val shortMessage = "امروز نوبت جعبه‌های ${dueInfo.dueBoxes.joinToString(" و ")} است (${dueInfo.dueCardsCount} کارت آماده مرور)"

        val bigText = buildString {
            append("📅 برنامه مرور لایتنر امروز:\n")
            append("• جعبه‌های نوبت امروز: ${dueInfo.dueBoxes.joinToString(" و ")}\n")
            if (dueInfo.dueCategories.isNotEmpty()) {
                val catText = dueInfo.dueCategories.take(4).joinToString("، ")
                append("• دسته‌های پیشنهادی: $catText\n")
            }
            append("• کارت‌های در انتظار مرور: ${dueInfo.dueCardsCount} کارت\n")
            append("• زنجیره مطالعه شما: $streak روز متوالی 🔥\n")
            append("برای جلوگیری از فراموشی فرمول‌ها و نکات، همین حالا مرور کنید!")
        }

        val largeIconBitmap = try {
            BitmapFactory.decodeResource(context.resources, R.drawable.app_icon_fg)
        } catch (e: Exception) {
            null
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_leitner)
            .setContentTitle(title)
            .setContentText(shortMessage)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
                    .setBigContentTitle(title)
                    .setSummaryText("الگوریتم لایتنر شیمی")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(clickPendingIntent)
            .addAction(
                R.drawable.ic_notification_leitner,
                "📖 شروع مرور کارت‌ها",
                clickPendingIntent
            )

        if (largeIconBitmap != null) {
            notificationBuilder.setLargeIcon(largeIconBitmap)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
