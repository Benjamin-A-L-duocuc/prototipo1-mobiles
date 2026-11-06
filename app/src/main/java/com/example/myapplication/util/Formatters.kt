package com.example.myapplication.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
private val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
private val shortDateTimeFormat = SimpleDateFormat("dd/MM · HH:mm", Locale.getDefault())

fun formatTime(timestamp: Long?): String =
    timestamp?.let { timeFormat.format(Date(it)) } ?: "--:--"

fun formatDate(timestamp: Long?): String =
    timestamp?.let { dateFormat.format(Date(it)) } ?: "Sin fecha"

fun formatDateTime(timestamp: Long?): String =
    timestamp?.let { dateTimeFormat.format(Date(it)) } ?: "Sin programar"

fun formatShortDateTime(timestamp: Long?): String =
    timestamp?.let { shortDateTimeFormat.format(Date(it)) } ?: "Sin programar"

fun formatCoordinate(value: Double?): String =
    value?.let { String.format(Locale.getDefault(), "%.6f", it) } ?: "--"

fun formatAccuracy(value: Float?): String =
    if (value == null || value <= 0f) "No informada" else "±${value.toInt()} m"

fun initials(name: String): String =
    name.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }

fun startOfDay(timestamp: Long): Long =
    Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

fun endOfDay(timestamp: Long): Long = startOfDay(timestamp) + 24 * 60 * 60 * 1000L - 1

fun isToday(timestamp: Long?): Boolean =
    timestamp != null && startOfDay(timestamp) == startOfDay(System.currentTimeMillis())

fun startOfWeek(timestamp: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val day = calendar.get(Calendar.DAY_OF_WEEK)
    val diff = if (day == Calendar.SUNDAY) -6 else Calendar.MONDAY - day
    calendar.add(Calendar.DAY_OF_MONTH, diff)
    return calendar.timeInMillis
}

fun addDays(timestamp: Long, days: Int): Long =
    Calendar.getInstance().apply {
        timeInMillis = timestamp
        add(Calendar.DAY_OF_MONTH, days)
    }.timeInMillis

fun technicianInitials(name: String): String = initials(name)
