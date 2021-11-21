package com.example.to_doapp.utils

import android.text.format.DateFormat
import java.util.*

object Util {

    fun formattedTime(hourOfDay: Int, minute: Int) : String {
        val formattedTime: String = when {
            hourOfDay == 0 -> {
                if (minute < 10) {
                    "${hourOfDay + 12}:0${minute} am"
                } else {
                    "${hourOfDay + 12}:${minute} am"
                }
            }
            hourOfDay > 12 -> {
                if (minute < 10) {
                    "${hourOfDay - 12}:0${minute} pm"
                } else {
                    "${hourOfDay - 12}:${minute} pm"
                }
            }
            hourOfDay == 12 -> {
                if (minute < 10) {
                    "${hourOfDay}:0${minute} pm"
                } else {
                    "${hourOfDay}:${minute} pm"
                }
            }
            else -> {
                if (minute < 10) {
                    "${hourOfDay}:${minute} am"
                } else {
                    "${hourOfDay}:${minute} am"
                }
            }
        }
        return formattedTime
    }

    fun formatDate(dueDate: Date): String {
        val newDay = DateFormat.format("dd", dueDate)
        val monthNumber = DateFormat.format("MM", dueDate)
        val newYear = DateFormat.format("yyyy", dueDate)

        return "$newDay/$monthNumber/$newYear"
    }

}