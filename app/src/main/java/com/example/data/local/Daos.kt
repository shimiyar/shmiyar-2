package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY id ASC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE leitnerBox = :box")
    fun getFlashcardsByBox(box: Int): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE grade = :grade")
    fun getFlashcardsByGrade(grade: Int): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE nextReviewTimestamp <= :currentTimestamp AND isMastered = 0")
    fun getDueFlashcards(currentTimestamp: Long): Flow<List<FlashcardEntity>>

    @Query("SELECT COUNT(*) FROM flashcards WHERE nextReviewTimestamp <= :currentTimestamp AND isMastered = 0")
    fun getDueFlashcardsCount(currentTimestamp: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards WHERE leitnerBox = :box")
    fun getBoxCount(box: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM flashcards WHERE isMastered = 1")
    fun getMasteredCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<FlashcardEntity>)

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Query("UPDATE flashcards SET leitnerBox = :newBox, nextReviewTimestamp = :nextReview, reviewCount = reviewCount + 1, lastReviewedTimestamp = :now, isMastered = :isMastered WHERE id = :id")
    suspend fun updateBoxProgress(id: Int, newBox: Int, nextReview: Long, now: Long, isMastered: Boolean)

    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun getCount(): Int
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY id ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE grade = :grade")
    fun getQuestionsByGrade(grade: Int): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE isMistake = 1")
    fun getMistakeQuestions(): Flow<List<QuestionEntity>>

    @Query("SELECT COUNT(*) FROM questions WHERE isMistake = 1")
    fun getMistakeCount(): Flow<Int>

    @Query("SELECT * FROM questions WHERE isBookmarked = 1")
    fun getBookmarkedQuestions(): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("UPDATE questions SET isMistake = :isMistake, userSelectedOption = :option WHERE id = :id")
    suspend fun recordAnswer(id: Int, option: Int, isMistake: Boolean)

    @Query("UPDATE questions SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun setBookmarked(id: Int, isBookmarked: Boolean)

    @Query("UPDATE questions SET isMistake = 0 WHERE id = :id")
    suspend fun clearMistake(id: Int)

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getCount(): Int
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY id ASC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE grade = :grade")
    fun getNotesByGrade(grade: Int): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE isBookmarked = 1")
    fun getBookmarkedNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET isBookmarked = :isBookmarked WHERE id = :id")
    suspend fun setBookmarked(id: Int, isBookmarked: Boolean)

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun getCount(): Int
}

@Dao
interface PackageDao {
    @Query("SELECT * FROM packages")
    fun getAllPackages(): Flow<List<PackageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackages(packages: List<PackageEntity>)

    @Query("UPDATE packages SET isDownloaded = :isDownloaded, downloadProgress = :progress WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, isDownloaded: Boolean, progress: Int)
}

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1")
    fun getUserStats(): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: UserStatsEntity)

    @Query("UPDATE user_stats SET xp = xp + :xpGain WHERE id = 1")
    suspend fun addXp(xpGain: Int)

    @Query("UPDATE user_stats SET streakDays = :streak, lastStudyDate = :date WHERE id = 1")
    suspend fun updateStreak(streak: Int, date: String)

    @Query("UPDATE user_stats SET isProUser = :isPro WHERE id = 1")
    suspend fun setProUser(isPro: Boolean)

    @Query("UPDATE user_stats SET reminderHour = :hour, reminderMinute = :minute, isReminderEnabled = :enabled WHERE id = 1")
    suspend fun updateReminder(hour: Int, minute: Int, enabled: Boolean)

    @Query("UPDATE user_stats SET selectedGrade = :grade, selectedMajor = :major WHERE id = 1")
    suspend fun updateGradeAndMajor(grade: String, major: String)
}

@Dao
interface ElementDao {
    @Query("SELECT * FROM elements ORDER BY atomicNumber ASC")
    fun getAllElements(): Flow<List<ElementEntity>>

    @Query("SELECT * FROM elements ORDER BY atomicNumber ASC")
    suspend fun getAllElementsList(): List<ElementEntity>

    @Query("SELECT * FROM elements WHERE atomicNumber = :atomicNumber")
    suspend fun getElementByNumber(atomicNumber: Int): ElementEntity?

    @Query("SELECT * FROM elements WHERE atomicNumber = :atomicNumber")
    fun getElementByNumberFlow(atomicNumber: Int): Flow<ElementEntity?>

    @Query("SELECT * FROM elements WHERE symbol = :symbol COLLATE NOCASE")
    suspend fun getElementBySymbol(symbol: String): ElementEntity?

    @Query("SELECT * FROM elements WHERE category = :category ORDER BY atomicNumber ASC")
    fun getElementsByCategory(category: String): Flow<List<ElementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertElements(elements: List<ElementEntity>)

    @Query("SELECT COUNT(*) FROM elements")
    suspend fun getCount(): Int
}
