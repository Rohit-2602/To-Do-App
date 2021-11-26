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
    var id: Int = 0,
    var title: String? = "",
    var createdAt: Long = System.currentTimeMillis(),
    var tasks: MutableList<Task> = ArrayList(),
    var dueDate: Long = Calendar.getInstance().timeInMillis,
    var remainderTime: Long = System.currentTimeMillis(),
    var completed: Boolean = false,
    var important: Boolean = false
): Parcelable

// Used in AllTodoFragment to filter TodoItem
enum class Filter {
    ALL,
    COMPLETED,
    IMPORTANT
}