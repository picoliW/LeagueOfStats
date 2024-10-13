package com.example.lol.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context

@Database(entities = [ChampionStatsEntity::class], version = 3)
abstract class ChampionDatabase : RoomDatabase() {
    abstract fun championDao(): ChampionDao

    companion object {
        @Volatile
        private var INSTANCE: ChampionDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE champions ADD COLUMN new_column_name INTEGER DEFAULT 0 NOT NULL")
            }
        }

        fun getDatabase(context: Context): ChampionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChampionDatabase::class.java,
                    "champion_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
