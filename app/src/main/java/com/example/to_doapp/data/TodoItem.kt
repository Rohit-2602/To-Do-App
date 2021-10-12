package com.example.to_doapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "todo_table")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var title: String? = "",
    var tasks: List<Task> = ArrayList()
): Parcelable