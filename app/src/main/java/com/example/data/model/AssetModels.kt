package com.example.data.model

data class ElementItem(
    val number: Int,
    val symbol: String,
    val name: String,
    val category: String,
    val mass: Double,
    val electron_config: String,
    val group: Int,
    val period: Int,
    val x: Int,
    val y: Int,
    val konkur_tip: String = ""
)

data class FlashcardItem(
    val id: Int,
    val category: String,
    val question: String,
    val answer: String
)

data class EducationChapter(
    val id: String,
    val grade: Int,
    val gradeTitle: String,
    val chapterNumber: Int,
    val title: String,
    val summary: String,
    val formulas: String,
    val goldenTip: String,
    val konkurTip: String,
    val memorizeTrick: String,
    val commonMistakes: String,
    val example: String,
    val examQuestion: String
)

data class GlossaryItem(
    val id: Int,
    val category: String,
    val term: String,
    val definition: String
)

data class BadgeItem(
    val id: String,
    val title: String,
    val description: String,
    val iconName: String,
    val isUnlocked: Boolean
)

data class DailyMission(
    val id: String,
    val title: String,
    val xpReward: Int,
    val isDone: Boolean
)

data class DailyTip(
    val id: Int,
    val title: String,
    val content: String
)
