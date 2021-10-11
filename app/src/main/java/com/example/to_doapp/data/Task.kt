package com.example.to_doapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
class Task(
    @PrimaryKey
    var id: Int? = 0,
    var title: String
)