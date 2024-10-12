package database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [ChampionStatsEntity::class], version = 1)
abstract class ChampionDatabase : RoomDatabase() {
    abstract fun championDao(): ChampionDao

    companion object {
        @Volatile
        private var INSTANCE: ChampionDatabase? = null

        fun getDatabase(context: Context): ChampionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChampionDatabase::class.java,
                    "champion_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
