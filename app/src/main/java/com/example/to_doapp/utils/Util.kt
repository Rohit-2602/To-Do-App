package com.example.to_doapp.utils

import android.annotation.SuppressLint
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Util {

    // For 12 hour format
    @SuppressLint("SimpleDateFormat")
    fun formatTime(time: Long): String {
        val dateFormat = SimpleDateFormat("hh:mm a")
        return dateFormat.format(time)
    }

    // For 24 hour format
    @SuppressLint("SimpleDateFormat")
    fun formatTimePicker(time: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm")
        return dateFormat.format(time)
    }

    fun formatDate(dueDate: Long): String {
        val newDay = DateFormat.format("dd", dueDate)
        val monthNumber = DateFormat.format("MM", dueDate)
        val newYear = DateFormat.format("yyyy", dueDate)

        return "$newDay/$monthNumber/$newYear"
    }

    fun isTodoDateLessOrEqual(dueDate: Long): Boolean {
        val calendar = Calendar.getInstance()
        val alarm = Calendar.getInstance()
        alarm.timeInMillis = dueDate

        return calendar.timeInMillis >= alarm.timeInMillis
    }

}