package com.elthobhy.storyapp.core.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatter {
    fun formatDate(dateString: String, targetDate: String): String {
        val instant = Instant.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
            .withZone(ZoneId.of(targetDate))
        return formatter.format(instant)
    }
}