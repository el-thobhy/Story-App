package com.elthobhy.storyapp.core.data.local

import androidx.paging.PagingSource
import com.elthobhy.storyapp.core.data.local.entity.StoryEntity
import com.elthobhy.storyapp.core.data.local.room.StoryDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val dao: StoryDao) {
    fun getStories(): PagingSource<Int, StoryEntity> = dao.getStories()
    fun getStoriesTest(): PagingSource<Int, StoryEntity> = dao.getStories()
    fun getStoriesLocation(): Flow<List<StoryEntity>> = dao.getStoriesLocation()
    suspend fun insertStories(story: List<StoryEntity>) = dao.insertData(story)
}