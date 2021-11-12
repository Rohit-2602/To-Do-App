package com.example.to_doapp.db

import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import javax.inject.Inject

class TodoRepository @Inject constructor(private val todoDao: TodoDao) {

    suspend fun addTodo(todoItem: TodoItem) = todoDao.addTodo(todoItem)

    suspend fun updateTodo(todoItem: TodoItem) = todoDao.updateTodo(todoItem)

    suspend fun updateTodoTasks(todoItemId: Int, tasks: List<Task>) =
        todoDao.updateTodoTasks(todoItemId, tasks)

    suspend fun removeTodo(todoItem: TodoItem) = todoDao.removeTodo(todoItem)

    fun getTodoById(todoItemId: Int) = todoDao.getTodoById(todoItemId)

    fun getAllTodos() = todoDao.getAllTodos()

}