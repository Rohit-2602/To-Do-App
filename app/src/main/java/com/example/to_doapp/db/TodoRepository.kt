package com.example.to_doapp.db

import com.example.to_doapp.data.TodoItem
import javax.inject.Inject

class TodoRepository @Inject constructor(private val todoDao: TodoDao) {

    suspend fun addTodo(todoItem: TodoItem) = todoDao.addTodo(todoItem)

    suspend fun updateTodo(todoItem: TodoItem) = todoDao.updateTodo(todoItem)

    suspend fun deleteTodo(todoItem: TodoItem) = todoDao.deleteTodo(todoItem)

    fun getAllTodos() = todoDao.getAllTodos()

}