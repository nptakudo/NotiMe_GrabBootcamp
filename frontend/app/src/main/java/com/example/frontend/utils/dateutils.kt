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
    // If less than 14 days, show "Xd ago"
    // If less than 2 months, show "Xw ago"
    // If less than 2 years, show "Xm ago"
    // Else, show "Xy ago"
    val givenLocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    val daysDifference = ChronoUnit.DAYS.between(givenLocalDate, today)
    if (daysDifference < 14) {
        return "${daysDifference}d ago"
    } else if (daysDifference < 60) {
        val weeksDifference = daysDifference / 7
        return "${weeksDifference}w ago"
    } else if (daysDifference < 730) {
        val monthsDifference = daysDifference / 30
        return "$monthsDifference months ago"
    } else {
        val yearsDifference = daysDifference / 365
        return "$yearsDifference years ago"
    }
}