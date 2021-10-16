package com.example.to_doapp.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import java.util.*

class Converters {

    @TypeConverter
    fun taskToJson(value: List<Task>?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToTask(value: String) = Gson().fromJson(value, Array<Task>::class.java).toList()

    @TypeConverter
    fun dateToJson(value: Date?): String = Gson().toJson(value)

    @TypeConverter
    fun jsonToDate(value: String): Date = Gson().fromJson(value, Date::class.java)

}