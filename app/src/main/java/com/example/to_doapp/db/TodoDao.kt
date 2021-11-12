package com.example.to_doapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodo(todoItem: TodoItem)

    @Update
    suspend fun updateTodo(todoItem: TodoItem)

    @Query("UPDATE todo_table SET tasks =:tasks WHERE id = :todoItemId")
    suspend fun updateTodoTasks(todoItemId: Int, tasks: List<Task>)

    @Delete
    suspend fun removeTodo(todoItem: TodoItem)

    @Query("SELECT * from todo_table WHERE id = :todoId")
    fun getTodoById(todoId: Int): LiveData<TodoItem>

    @Query("SELECT * from todo_table")
    fun getAllTodos(): LiveData<List<TodoItem>>

}