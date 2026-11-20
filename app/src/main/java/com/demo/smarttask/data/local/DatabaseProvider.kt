package com.demo.smarttask.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    @Volatile
    private var INSTANCE: TaskDatabase? = null
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE tasks ADD COLUMN remoteId INTEGER")
        }
    }

    fun getDatabase(context: Context): TaskDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance = Room.databaseBuilder(
                context.applicationContext,
                TaskDatabase::class.java,
                "smart_task_database"
            ).addMigrations(MIGRATION_1_2)
                .build()

            INSTANCE = instance
            instance
        }
    }
}