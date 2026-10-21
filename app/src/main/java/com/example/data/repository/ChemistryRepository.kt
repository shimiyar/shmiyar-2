package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.InitialFlashcards
import com.example.data.model.InitialNotes
import com.example.data.model.InitialQuestions
import com.example.data.model.PackagesData
import com.example.data.model.PeriodicTableData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class ChemistryRepository(
    private val database: AppDatabase,
    private val billingRepository: BillingRepository
) {
    val flashcardDao = database.flashcardDao()
    val questionDao = database.questionDao()
    val noteDao = database.noteDao()
    val packageDao = database.packageDao()
    val userStatsDao = database.userStatsDao()
    val elementDao = database.elementDao()

    val allFlashcards: Flow<List<FlashcardEntity>> = flashcardDao.getAllFlashcards()
    val allQuestions: Flow<List<QuestionEntity>> = questionDao.getAllQuestions()
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    val allPackages: Flow<List<PackageEntity>> = packageDao.getAllPackages()
    val userStats: Flow<UserStatsEntity?> = userStatsDao.getUserStats()
    val allElements: Flow<List<ElementEntity>> = elementDao.getAllElements()

    suspend fun initializeDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        if (elementDao.getCount() == 0) {
            val elementEntities = PeriodicTableData.elements.map { el ->
                ElementEntity(
                    atomicNumber = el.atomicNumber,
                    symbol = el.symbol,
                    persianName = el.persianName,
                    englishName = el.englishName,
                    atomicMass = el.atomicMass,
                    category = el.category.persianTitle,
                    period = el.period,
                    group = el.group,
                    block = el.block,
                    electronConfiguration = el.electronConfiguration,
                    electronegativity = el.electronegativity,
                    atomicRadiusPm = el.atomicRadiusPm,
                    meltingPointK = el.meltingPointK,
                    boilingPointK = el.boilingPointK,
                    stateAt298K = el.stateAt298K.persianTitle,
                    valenceElectrons = el.valenceElectrons,
                    oxidationStates = el.oxidationStates,
                    konkurTipsAndUses = el.konkurTipsAndUses,
                    gridX = el.group,
                    gridY = el.period
                )
            }
            elementDao.insertElements(elementEntities)
        }
        if (flashcardDao.getCount() == 0) {
            flashcardDao.insertFlashcards(InitialFlashcards.list)
        }
        if (questionDao.getCount() == 0) {
            questionDao.insertQuestions(InitialQuestions.list)
        }
        if (noteDao.getCount() == 0) {
            noteDao.insertNotes(InitialNotes.list)
        }
        val existingPackages = packageDao.getAllPackages().first()
        if (existingPackages.isEmpty()) {
            packageDao.insertPackages(PackagesData.initialPackages)
        }
        val stats = userStatsDao.getUserStats().first()
        if (stats == null) {
            userStatsDao.insertOrUpdate(
                UserStatsEntity(
                    id = 1,
                    streakDays = 3,
                    lastStudyDate = getTodayDateString(),
                    xp = 180,
                    level = 2,
                    testsCompleted = 4,
                    cardsReviewed = 12,
                    isProUser = false
                )
            )
        }
    }

    // Leitner Box progression
    suspend fun answerFlashcard(card: FlashcardEntity, knewIt: Boolean) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L

        if (knewIt) {
            val newBox = card.leitnerBox + 1
            if (newBox > 5) {
                // Card is Mastered!
                flashcardDao.updateBoxProgress(
                    id = card.id,
                    newBox = 5,
                    nextReview = now + 30 * oneDayMs,
                    now = now,
                    isMastered = true
                )
            } else {
                val intervalDays = when (newBox) {
                    2 -> 2
                    3 -> 4
                    4 -> 7
                    5 -> 14
                    else -> 1
                }
                flashcardDao.updateBoxProgress(
                    id = card.id,
                    newBox = newBox,
                    nextReview = now + (intervalDays * oneDayMs),
                    now = now,
                    isMastered = false
                )
            }
            recordStudyActivity(xpEarned = 15, isCard = true)
        } else {
            // Failed -> Return to Box 1 for immediate review tomorrow
            flashcardDao.updateBoxProgress(
                id = card.id,
                newBox = 1,
                nextReview = now + oneDayMs,
                now = now,
                isMastered = false
            )
            recordStudyActivity(xpEarned = 5, isCard = true)
        }
    }

    suspend fun recordQuestionAnswer(questionId: Int, selectedOption: Int, isCorrect: Boolean) = withContext(Dispatchers.IO) {
        questionDao.recordAnswer(
            id = questionId,
            option = selectedOption,
            isMistake = !isCorrect
        )
        val xp = if (isCorrect) 25 else 5
        recordStudyActivity(xpEarned = xp, isCard = false)
    }

    suspend fun toggleNoteBookmark(noteId: Int, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        noteDao.setBookmarked(noteId, !currentStatus)
    }

    suspend fun toggleQuestionBookmark(questionId: Int, currentStatus: Boolean) = withContext(Dispatchers.IO) {
        questionDao.setBookmarked(questionId, !currentStatus)
    }

    suspend fun removeMistake(questionId: Int) = withContext(Dispatchers.IO) {
        questionDao.clearMistake(questionId)
    }

    suspend fun downloadPackage(packageId: String) = withContext(Dispatchers.IO) {
        // Simulates network chunk download with progress updates
        for (p in 10..100 step 15) {
            packageDao.updateDownloadStatus(packageId, isDownloaded = false, progress = p)
            delay(150)
        }
        packageDao.updateDownloadStatus(packageId, isDownloaded = true, progress = 100)
    }

    private suspend fun recordStudyActivity(xpEarned: Int, isCard: Boolean) {
        val today = getTodayDateString()
        val currentStats = userStatsDao.getUserStats().first() ?: UserStatsEntity()
        val newXp = currentStats.xp + xpEarned
        val newLevel = 1 + (newXp / 150)

        var streak = currentStats.streakDays
        if (currentStats.lastStudyDate != today) {
            // New day
            streak += 1
        }

        userStatsDao.insertOrUpdate(
            currentStats.copy(
                streakDays = streak,
                lastStudyDate = today,
                xp = newXp,
                level = newLevel,
                cardsReviewed = if (isCard) currentStats.cardsReviewed + 1 else currentStats.cardsReviewed,
                testsCompleted = if (!isCard) currentStats.testsCompleted + 1 else currentStats.testsCompleted
            )
        )
    }

    suspend fun updateReminderSettings(hour: Int, minute: Int, enabled: Boolean) = withContext(Dispatchers.IO) {
        userStatsDao.updateReminder(hour, minute, enabled)
    }

    suspend fun updateGradeAndMajor(grade: String, major: String) = withContext(Dispatchers.IO) {
        userStatsDao.updateGradeAndMajor(grade, major)
    }

    suspend fun setProUser(isPro: Boolean) = withContext(Dispatchers.IO) {
        userStatsDao.setProUser(isPro)
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }
}
