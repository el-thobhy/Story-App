package com.elthobhy.storyapp.core.utils

import com.elthobhy.storyapp.core.data.remote.model.response.ListStoryItem
import com.elthobhy.storyapp.core.domain.model.Story

object DataMapper {
    fun mapResponseToDomain(input: List<ListStoryItem>): List<Story>{
        val mapTo = ArrayList<Story>()
        input.map{
            val listTo = Story(
                name = it.name,
                createdAt = it.createdAt,
                description = it.description,
                id = it.id,
                lat = it.lat,
                lon = it.lon,
                photoUrl = it.photoUrl
            )
            mapTo.add(listTo)
        }
        return mapTo
    }
}