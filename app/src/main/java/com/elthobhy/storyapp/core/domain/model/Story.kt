package com.elthobhy.storyapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val photoUrl: String? = null,
    val createdAt: String? = null,
    val name: String? = null,
    val description: String? = null,
    val lon: Double? = null,
    val id: String? = null,
    val lat: Double? = null
) : Parcelable