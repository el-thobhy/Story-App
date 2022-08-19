package com.elthobhy.storyapp.core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormatter {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String, targetDate: String): String {
        val instant = Instant.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
            .withZone(ZoneId.of(targetDate))
        return formatter.format(instant)
    }
}