package com.elthobhy.storyapp.core.data.local

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.elthobhy.storyapp.core.data.local.entity.StoryEntity
import com.elthobhy.storyapp.core.data.local.room.StoryDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val dao: StoryDao) {
    fun getStories(): PagingSource<Int, StoryEntity> = dao.getStories()
    suspend fun insertStories(story: List<StoryEntity>) = dao.saveStory(story)
}