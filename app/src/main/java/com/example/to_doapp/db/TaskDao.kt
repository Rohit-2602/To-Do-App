package com.example.to_doapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.to_doapp.data.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * from task_table")
    fun getAllTasks(): LiveData<List<Task>>

}