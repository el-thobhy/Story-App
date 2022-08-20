package com.elthobhy.storyapp.core.data.local

import com.elthobhy.storyapp.core.data.local.entity.StoryEntity
import com.elthobhy.storyapp.core.data.local.room.StoryDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val dao: StoryDao) {
    fun getStories(): Flow<List<StoryEntity>> = dao.getStories()
    suspend fun insertStories(story: List<StoryEntity>) = dao.saveStory(story)
}