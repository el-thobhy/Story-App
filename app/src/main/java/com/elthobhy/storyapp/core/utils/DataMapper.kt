package com.elthobhy.storyapp.core.utils

import androidx.paging.PagingData
import androidx.paging.map
import com.elthobhy.storyapp.core.data.local.entity.StoryEntity
import com.elthobhy.storyapp.core.data.remote.model.response.ListStoryItem
import com.elthobhy.storyapp.core.domain.model.Story

object DataMapper {
    fun mapEntityToDomain(input: List<StoryEntity>): List<Story> {
        val mapTo = ArrayList<Story>()
        input.map {
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

    fun mapPagingEntityToDomain(input: PagingData<StoryEntity>): PagingData<Story> {
        return input.map {
            Story(
                name = it.name,
                createdAt = it.createdAt,
                description = it.description,
                id = it.id,
                lat = it.lat,
                lon = it.lon,
                photoUrl = it.photoUrl
            )
        }
    }

    fun mapResponseToEntity(input: List<ListStoryItem>): List<StoryEntity>{
        val mapTo = ArrayList<StoryEntity>()
        input.map {
            val listTo = StoryEntity(
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

    fun mapDomainToResponse(input: List<Story>): List<ListStoryItem>{
        val mapTo = ArrayList<ListStoryItem>()
        input.map {
            val listTo = it.id?.let { it1 ->
                ListStoryItem(
                    name = it.name,
                    createdAt = it.createdAt,
                    description = it.description,
                    id = it1,
                    lat = it.lat,
                    lon = it.lon,
                    photoUrl = it.photoUrl
                )
            }
            if (listTo != null) {
                mapTo.add(listTo)
            }
        }
        return mapTo
    }

}