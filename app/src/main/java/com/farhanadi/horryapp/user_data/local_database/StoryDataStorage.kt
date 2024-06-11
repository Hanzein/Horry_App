package com.farhanadi.horryapp.user_data.local_database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.farhanadi.horryapp.user_data.api.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemotePaginationKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDataStorage : RoomDatabase() {

    abstract fun storyDao(): StoryDataAccess
    abstract fun remoteKeysDao(): RemotePaginationKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDataStorage? = null

        fun getDatabase(context: Context): StoryDataStorage {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also {
                    INSTANCE = it
                }
            }
        }

        private fun buildDatabase(context: Context): StoryDataStorage {
            return Room.databaseBuilder(
                context.applicationContext,
                StoryDataStorage::class.java, "story_database"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
