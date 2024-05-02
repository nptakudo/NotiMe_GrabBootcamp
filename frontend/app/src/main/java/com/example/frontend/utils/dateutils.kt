package com.example.frontend.utils

import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

// Returns the difference between the given date and the current date in days.
fun dateDifferenceFromNow(date: Date): Long {
    val givenLocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    return ChronoUnit.DAYS.between(givenLocalDate, today)
}

fun dateToStringExactDateFormat(date: Date): String {
    // Format example: "January 1, 2022"
    val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val month = localDate.month.toString().lowercase().replaceFirstChar { it.uppercase() }
    val day = localDate.dayOfMonth
    val year = localDate.year
    return "$month $day, $year"
}

fun dateToStringAgoFormat(date: Date): String {
    // If less than 100 minutes, show "X minutes ago"
    // If less than 24 hours, show "X hours ago"
    // If yesterday, show "Yesterday"
    // If less than 14 days, show "X days ago"
    // If less than 2 months, show "X weeks ago"
    // If less than 2 years, show "X months ago"
    // Else, show "X years ago"
    val givenLocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    val daysDifference = ChronoUnit.DAYS.between(givenLocalDate, today)
    if (daysDifference == 0L) {
        val minutesDifference = ChronoUnit.MINUTES.between(date.toInstant(), Date().toInstant())
        return if (minutesDifference < 100) {
            "$minutesDifference minutes ago"
        } else {
            val hoursDifference = ChronoUnit.HOURS.between(date.toInstant(), Date().toInstant())
            "$hoursDifference hours ago"
        }
    } else if (daysDifference == 1L) {
        return "Yesterday"
    } else if (daysDifference < 14) {
        return "$daysDifference days ago"
    } else if (daysDifference < 60) {
        val weeksDifference = daysDifference / 7
        return "$weeksDifference weeks ago"
    } else if (daysDifference < 730) {
        val monthsDifference = daysDifference / 30
        return "$monthsDifference months ago"
    } else {
        val yearsDifference = daysDifference / 365
        return "$yearsDifference years ago"
    }
}