package com.example.to_doapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
@Entity(tableName = "todo_table")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var title: String? = "",
    var tasks: List<Task> = ArrayList(),
    var dueDate: Date
): Parcelable