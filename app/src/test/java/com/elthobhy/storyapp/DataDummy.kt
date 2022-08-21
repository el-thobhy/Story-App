package com.elthobhy.storyapp

import com.elthobhy.storyapp.core.data.remote.model.response.LoginResponse
import com.elthobhy.storyapp.core.data.remote.model.response.LoginResult
import com.elthobhy.storyapp.core.domain.model.Story

object DataDummy {
    fun generateDummy(): List<Story> {
        val list = ArrayList<Story>()
        for (i in 0..10) {
            val listStory = Story(
                name = "Budi",
                createdAt = "2022-01-08T06:34:18.598Z",
                description = "Lorem Ipsum",
                id = "story-FvU4u0Vp2S3PMsFg",
                lat = -10.212,
                lon = -16.002,
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png"
            )
            list.add(listStory)
        }
        return list
    }

    fun generateDummyLoginResponse(): LoginResponse {
        val loginResult = LoginResult(
            userId = "user-yj5pc_LARC_AgK61",
            name = "Arif Faizin",
            token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXlqNXBjX0xBUkNfQWdLNjEiLCJpYXQiOjE2NDE3OTk5NDl9.flEMaQ7zsdYkxuyGbiXjEDXO8kuDTcI__3UjCwt6R_I"
        )

        return LoginResponse(
            loginResult = loginResult,
            error = false,
            message = "success"
        )
    }
}