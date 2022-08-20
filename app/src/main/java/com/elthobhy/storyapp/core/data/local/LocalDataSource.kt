package com.elthobhy.storyapp.core.data.local

import androidx.paging.PagingSource
import com.elthobhy.storyapp.core.data.local.entity.StoryEntity
import com.elthobhy.storyapp.core.data.local.room.StoryDao

class LocalDataSource(private val dao: StoryDao) {
    fun getStories(): PagingSource<Int, StoryEntity> = dao.getStories()
    suspend fun insertStories(story: List<StoryEntity>) = dao.insertData(story)
}