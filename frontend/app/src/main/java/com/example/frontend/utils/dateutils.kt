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

fun dateToStringAgoFormat(date: Date): String {
    // If less than 14 days, show "Xd ago"
    // If less than 2 months, show "Xw ago"
    // If less than 2 years, show "Xm ago"
    // Else, show "Xy ago"
    val givenLocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val today = LocalDate.now()
    val daysDifference = ChronoUnit.DAYS.between(givenLocalDate, today)
    return if (daysDifference < 14) {
        "${daysDifference}d ago"
    } else if (daysDifference < 60) {
        val weeksDifference = daysDifference / 7
        "${weeksDifference}w ago"
    } else if (daysDifference < 730) {
        val monthsDifference = daysDifference / 30
        "$monthsDifference months ago"
    } else {
        val yearsDifference = daysDifference / 365
        "$yearsDifference years ago"
    }
}