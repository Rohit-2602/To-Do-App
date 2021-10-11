package com.example.to_doapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_table")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = 0,
    var title: String? = "",
    var tasks: List<Task>
)