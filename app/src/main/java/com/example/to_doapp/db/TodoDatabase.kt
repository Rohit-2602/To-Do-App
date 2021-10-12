package com.example.to_doapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.to_doapp.data.Converters
import com.example.to_doapp.data.TodoItem

@Database(entities = [TodoItem::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TodoDatabase: RoomDatabase() {

    abstract fun getTodoDao(): TodoDao

}