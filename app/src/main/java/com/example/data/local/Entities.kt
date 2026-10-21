package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val grade: Int, // 10, 11, 12 or 0 for calculations
    val chapter: String,
    val frontQuestion: String,
    val backAnswer: String,
    val category: String, // "رنگ رسوب", "انحلال‌پذیری", "فرمول", "تعریف", "محاسبات"
    val leitnerBox: Int = 1, // 1 to 5
    val nextReviewTimestamp: Long = 0L,
    val lastReviewedTimestamp: Long = 0L,
    val reviewCount: Int = 0,
    val isMastered: Boolean = false,
    val isPro: Boolean = false
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val grade: Int,
    val chapter: String,
    val examSource: String, // e.g. "کنکور داخل ۱۴۰۲", "کنکور تجربی ۱۴۰۱"
    val questionText: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val option4: String,
    val correctOption: Int, // 1..4
    val explanation: String,
    val difficulty: String = "متوسط", // آسان, متوسط, دشوار
    val isPro: Boolean = false,
    val userSelectedOption: Int = 0, // 0 = unanswered, 1..4
    val isMistake: Boolean = false,
    val isBookmarked: Boolean = false
)

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val grade: Int,
    val chapter: String,
    val title: String,
    val content: String,
    val formulaOrRule: String = "",
    val category: String = "نکته طلایی", // نکته طلایی, فرمول و روابط, دام تستی
    val isBookmarked: Boolean = false,
    val isPro: Boolean = false
)

@Entity(tableName = "packages")
data class PackageEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String,
    val itemCount: Int,
    val sizeMb: Float,
    val isPro: Boolean = false,
    val isDownloaded: Boolean = false,
    val downloadProgress: Int = 0 // 0 to 100
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val streakDays: Int = 1,
    val lastStudyDate: String = "",
    val xp: Int = 50,
    val level: Int = 1,
    val testsCompleted: Int = 0,
    val cardsReviewed: Int = 0,
    val isProUser: Boolean = false,
    val selectedGrade: String = "دوازدهم",
    val selectedMajor: String = "تجربی",
    val dailyCardsToday: Int = 0,
    val dailyQuestionsToday: Int = 0,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val isReminderEnabled: Boolean = true
)

@Entity(tableName = "elements")
data class ElementEntity(
    @PrimaryKey
    val atomicNumber: Int,
    val symbol: String,
    val persianName: String,
    val englishName: String,
    val atomicMass: Double,
    val category: String, // e.g. فلز قلیایی, گاز نجیب, etc.
    val period: Int,
    val group: Int,
    val block: String = "s",
    val electronConfiguration: String = "",
    val electronegativity: Double? = null,
    val atomicRadiusPm: Int? = null,
    val meltingPointK: Double? = null,
    val boilingPointK: Double? = null,
    val stateAt298K: String = "جامد",
    val valenceElectrons: Int = 1,
    val oxidationStates: String = "",
    val konkurTipsAndUses: String = "",
    val gridX: Int = 1,
    val gridY: Int = 1
)
