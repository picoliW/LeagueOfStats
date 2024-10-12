package com.example.lol

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME = "champions.db"
private const val DATABASE_VERSION = 1
private const val TABLE_CHAMPIONS = "champions"

private const val COLUMN_ID = "id"
private const val COLUMN_NAME = "name"
private const val COLUMN_TITLE = "title"
private const val COLUMN_ICON = "icon"

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_CHAMPIONS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_NAME TEXT,
                $COLUMN_TITLE TEXT,
                $COLUMN_ICON TEXT
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHAMPIONS")
        onCreate(db)
    }

    fun insertChampion(champion: ChampionStats) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ID, champion.id)
            put(COLUMN_NAME, champion.name)
            put(COLUMN_TITLE, champion.title)
            put(COLUMN_ICON, champion.icon)
        }
        db.insert(TABLE_CHAMPIONS, null, values)
    }

    fun getAllChampions(): List<ChampionStats> {
        val champions = mutableListOf<ChampionStats>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_CHAMPIONS, null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndexOrThrow(COLUMN_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                val icon = getString(getColumnIndexOrThrow(COLUMN_ICON))

                champions.add(ChampionStats(id, "", name, title, listOf(), Stats(0, 0, 0, 0, 0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0), icon, Sprite("", 0, 0), ""))
            }
        }
        cursor.close()
        return champions
    }
}
