package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FlashcardEntity::class,
        QuestionEntity::class,
        NoteEntity::class,
        PackageEntity::class,
        UserStatsEntity::class,
        ElementEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
    abstract fun questionDao(): QuestionDao
    abstract fun noteDao(): NoteDao
    abstract fun packageDao(): PackageDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun elementDao(): ElementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shimiyar_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
