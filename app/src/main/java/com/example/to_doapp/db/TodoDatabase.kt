package com.example.to_doapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.to_doapp.data.Converters
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem

@Database(entities = [TodoItem::class, Task::class], version = 1)
@TypeConverters(Converters::class)
abstract class TodoDatabase: RoomDatabase() {

    abstract fun getTodoDao(): TodoDao

    abstract fun getTaskDao(): TaskDao

}