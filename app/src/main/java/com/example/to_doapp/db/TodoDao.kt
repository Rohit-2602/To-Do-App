package com.example.to_doapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.to_doapp.data.TodoItem

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodo(todoItem: TodoItem)

    @Delete
    suspend fun deleteTodo(todoItem: TodoItem)

    @Query("SELECT * from todo_table")
    fun getAllTodos(): LiveData<List<TodoItem>>

}