package com.example.to_doapp.db

import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val todoDao: TodoDao
) {

    suspend fun addTodo(todoItem: TodoItem) = todoDao.addTodo(todoItem)

    suspend fun addTask(task: Task) = taskDao.addTask(task)

    suspend fun deleteTodo(todoItem: TodoItem) = todoDao.deleteTodo(todoItem)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    fun getAllTodos() = todoDao.getAllTodos()

    fun getAllTasks() = taskDao.getAllTasks()

}