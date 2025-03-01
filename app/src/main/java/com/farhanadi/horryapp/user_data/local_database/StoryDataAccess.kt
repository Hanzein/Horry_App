package com.farhanadi.horryapp.user_data.local_database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.farhanadi.horryapp.user_data.api.response.ListStoryItem

@Dao
interface StoryDataAccess {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStories(stories: List<ListStoryItem>)

    @Query("SELECT * FROM story")
    fun getStories(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}