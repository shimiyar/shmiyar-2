package com.example.reminder

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
import com.example.MainActivity
import com.example.R
import com.example.data.local.PreferencesHelper

/**
 * مدیریت بررسی و ارسال اعلان‌های هوشمند انقضای اشتراک و دسترسی ۳ روزه رایگان کافه‌بازار
 */
object SubscriptionReminderManager {

    const val CHANNEL_ID = "subscription_status_channel"
    const val NOTIFICATION_ID = 3001
    const val EXTRA_OPEN_PAYWALL = "EXTRA_OPEN_PAYWALL"

    /**
     * ایجاد کانال اعلان اختصاصی برای اشتراک
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "وضعیت اشتراک شیمی‌یار پرو"
            val descriptionText = "اطلاع‌رسانی وضعیت تمدید و انقضای دسترسی آزمایشی یا اشتراک پرو"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * ارسال فوری اعلان سیستمی در نوار اعلانات گوشی مبنی بر اتمام مهلت اشتراک
     */
    fun notifySubscriptionExpired(context: Context) {
        createNotificationChannel(context)

        // بررسی مجوز ارسال اعلان در اندروید ۱۳ به بالا
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_OPEN_PAYWALL, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("⚠️ پایان مهلت ۳ روزه شیمی‌یار پرو")
            .setContentText("مهلت دسترسی رایگان شما به پایان رسید. جهت تداوم استفاده از پاسخنامه‌ها و بانک تست کنکور، اشتراک خود را تمدید فرمایید.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("مهلت ۳ روزه دسترسی رایگان کافه‌بازار شما به پایان رسید.\nبرای اینکه در روند جمع‌بندی کنکور و دسترسی به پاسخنامه‌های تشریحی وقفه‌ای ایجاد نشود، اشتراک خود را از پلن‌های ماهانه یا سالانه تمدید کنید.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {}
    }

    /**
     * بررسی خودکار آیا اشتراک کاربر منقضی شده و نیاز به ارسال اعلان دارد یا خیر
     * @return true در صورتی که کاربر به تازگی منقضی شده و اعلان ارسال شده است
     */
    fun checkAndNotifyIfExpired(context: Context): Boolean {
        val prefs = PreferencesHelper(context)
        if (prefs.isSubscriptionExpired && !prefs.hasNotifiedExpired) {
            prefs.hasNotifiedExpired = true
            notifySubscriptionExpired(context)
            return true
        }
        return false
    }
}
