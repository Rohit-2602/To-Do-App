package com.example.to_doapp.utils

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Util {

    fun formatTime(time: Long): String {
        val dateFormat = SimpleDateFormat("hh:mm a")
        return dateFormat.format(time)
    }

    fun formatDate(dueDate: Date): String {
        val newDay = DateFormat.format("dd", dueDate)
        val monthNumber = DateFormat.format("MM", dueDate)
        val newYear = DateFormat.format("yyyy", dueDate)

        return "$newDay/$monthNumber/$newYear"
    }

    fun isTodoDateLessOrEqual(dueDate: Date): Boolean {
        val calendar = Calendar.getInstance()
        val alarm = Calendar.getInstance()
        alarm.time = dueDate
        return calendar.timeInMillis >= alarm.timeInMillis
    }

}