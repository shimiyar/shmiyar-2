package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

class PreferencesHelper(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("shimiyar_prefs", Context.MODE_PRIVATE)

    var onboardingDone: Boolean
        get() = prefs.getBoolean("onboarding_done", false)
        set(value) = prefs.edit().putBoolean("onboarding_done", value).apply()

    var isPro: Boolean
        get() {
            if (prefs.getBoolean("is_pro_purchased", false)) return true
            // بررسی فعال بودن دسترسی رایگان ۳ روزه کافه‌بازار
            if (isTrialActive) {
                if (System.currentTimeMillis() < trialEndTime) {
                    return true
                }
            }
            return false
        }
        set(value) = prefs.edit().putBoolean("is_pro_purchased", value).apply()

    var isTrialActive: Boolean
        get() = prefs.getBoolean("is_trial_active", false)
        set(value) = prefs.edit().putBoolean("is_trial_active", value).apply()

    var trialEndTime: Long
        get() = prefs.getLong("trial_end_time", 0L)
        set(value) = prefs.edit().putLong("trial_end_time", value).apply()

    var hasClaimedBazaarTrial: Boolean
        get() = prefs.getBoolean("has_claimed_bazaar_trial", false)
        set(value) = prefs.edit().putBoolean("has_claimed_bazaar_trial", value).apply()

    var hasNotifiedExpired: Boolean
        get() = prefs.getBoolean("has_notified_expired", false)
        set(value) = prefs.edit().putBoolean("has_notified_expired", value).apply()

    /**
     * آیا مهلت دسترسی آزمایشی کاربر به پایان رسیده و کاربر مطلع شده یا خیر
     */
    val isSubscriptionExpired: Boolean
        get() {
            if (prefs.getBoolean("is_pro_purchased", false)) return false
            if (isTrialActive && System.currentTimeMillis() >= trialEndTime) {
                return true
            }
            return false
        }

    /**
     * فعال‌سازی ۳ روز دسترسی رایگان ویژه ورود با کافه‌بازار
     */
    fun startThreeDayTrialForBazaar() {
        val now = System.currentTimeMillis()
        val threeDaysMs = 3 * 24 * 60 * 60 * 1000L
        prefs.edit()
            .putBoolean("is_trial_active", true)
            .putBoolean("has_claimed_bazaar_trial", true)
            .putLong("trial_started_time", now)
            .putLong("trial_end_time", now + threeDaysMs)
            .putBoolean("has_notified_expired", false)
            .apply()
    }

    /**
     * متد قبلی جهت حفظ سازگاری (به ۳ روز تغییر یافت)
     */
    fun startSevenDayTrial() {
        startThreeDayTrialForBazaar()
    }

    var xp: Int
        get() = prefs.getInt("user_xp", 50)
        set(value) {
            prefs.edit().putInt("user_xp", value).apply()
            // check level
            val lvl = 1 + (value / 100)
            level = lvl
        }

    var level: Int
        get() = prefs.getInt("user_level", 1)
        set(value) = prefs.edit().putInt("user_level", value).apply()

    var streakDays: Int
        get() = prefs.getInt("streak_days", 1)
        set(value) = prefs.edit().putInt("streak_days", value).apply()

    var lastVisitDate: String
        get() = prefs.getString("last_visit_date", "") ?: ""
        set(value) = prefs.edit().putString("last_visit_date", value).apply()

    fun checkAndUpdateStreak(): Int {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val last = lastVisitDate
        if (last.isEmpty()) {
            lastVisitDate = today
            streakDays = 1
            return 1
        }
        if (last == today) {
            return streakDays
        }

        // Check if yesterday
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)

        val newStreak = if (last == yesterday) streakDays + 1 else 1
        streakDays = newStreak
        lastVisitDate = today
        return newStreak
    }

    var elementsViewedCount: Int
        get() = prefs.getInt("el_viewed", 0)
        set(value) = prefs.edit().putInt("el_viewed", value).apply()

    var lockedContentClicks: Int
        get() = prefs.getInt("locked_clicks", 0)
        set(value) = prefs.edit().putInt("locked_clicks", value).apply()

    var flashcardsKnownCount: Int
        get() = prefs.getInt("fc_known", 0)
        set(value) = prefs.edit().putInt("fc_known", value).apply()

    var flashcardsLearningCount: Int
        get() = prefs.getInt("fc_learning", 0)
        set(value) = prefs.edit().putInt("fc_learning", value).apply()

    var flashcardsTodayCount: Int
        get() = prefs.getInt("fc_today", 0)
        set(value) = prefs.edit().putInt("fc_today", value).apply()

    fun getCardBox(cardId: Int): Int {
        return prefs.getInt("fc_box_$cardId", 0)
    }

    fun setCardBox(cardId: Int, box: Int) {
        prefs.edit().putInt("fc_box_$cardId", box).apply()
    }

    fun getCardLastReview(cardId: Int): Long {
        return prefs.getLong("fc_last_review_$cardId", 0L)
    }

    fun setCardLastReview(cardId: Int, time: Long) {
        prefs.edit().putLong("fc_last_review_$cardId", time).apply()
    }

    var leitnerNotificationEnabled: Boolean
        get() = prefs.getBoolean("leitner_notif_enabled", true)
        set(value) = prefs.edit().putBoolean("leitner_notif_enabled", value).apply()

    var leitnerNotificationHour: Int
        get() = prefs.getInt("leitner_notif_hour", 18) // 18:00 (6 PM) default
        set(value) = prefs.edit().putInt("leitner_notif_hour", value).apply()

    var leitnerNotificationMinute: Int
        get() = prefs.getInt("leitner_notif_minute", 0)
        set(value) = prefs.edit().putInt("leitner_notif_minute", value).apply()

    var lastLeitnerNotificationDay: String
        get() = prefs.getString("last_leitner_notif_day", "") ?: ""
        set(value) = prefs.edit().putString("last_leitner_notif_day", value).apply()

    fun getElementNote(elementNumber: Int): String {
        return prefs.getString("el_note_$elementNumber", "") ?: ""
    }

    fun saveElementNote(elementNumber: Int, note: String) {
        prefs.edit().putString("el_note_$elementNumber", note).apply()
    }

    fun isBadgeUnlocked(badgeId: String): Boolean {
        return prefs.getBoolean("badge_$badgeId", false)
    }

    fun unlockBadge(badgeId: String) {
        prefs.edit().putBoolean("badge_$badgeId", true).apply()
    }

    var eyeCareMode: Boolean
        get() = prefs.getBoolean("eye_care_mode", false)
        set(value) = prefs.edit().putBoolean("eye_care_mode", value).apply()
}
