package com.elthobhy.storyapp.core.data.local.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.elthobhy.storyapp.core.data.local.entity.StoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(story: List<StoryEntity>)

    @Query("SELECT * FROM story")
    fun getStories(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM story")
    fun getStoriesLocation(): Flow<List<StoryEntity>>

    @Query("DELETE FROM story")
    suspend fun deleteAll()


    @Query("DELETE FROM story")
    suspend fun deleteSung()
}