package com.example.to_doapp.db

import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import javax.inject.Inject

class TodoRepository @Inject constructor(private val todoDao: TodoDao) {

    suspend fun addTodo(todoItem: TodoItem) = todoDao.addTodo(todoItem)

    suspend fun updateTodo(todoItem: TodoItem) = todoDao.updateTodo(todoItem)

    suspend fun updateTodoChecked(todoItemId: Int, completed: Boolean) =
        todoDao.updateTodoChecked(todoItemId, completed)

    suspend fun updateTodoImportant(todoItemId: Int, important: Boolean) =
        todoDao.updateTodoImportant(todoItemId, important)

    suspend fun updateTodoTasks(todoItemId: Int, tasks: List<Task>) =
        todoDao.updateTodoTasks(todoItemId, tasks)

    suspend fun updateTodoTime(todoItemId: Int, remainderTime: Long) =
        todoDao.updateTodoTime(todoItemId, remainderTime)

    suspend fun updateTodoDueDate(todoItemId: Int, dueDate: Long) =
        todoDao.updateTodoDueDate(todoItemId, dueDate)

    suspend fun updateTodoDueDateTime(todoItemId: Int, dueDate: Long, remainderTime: Long) =
        todoDao.updateTodoDueDateTime(todoItemId, dueDate, remainderTime)

    suspend fun removeTodo(todoItem: TodoItem) = todoDao.removeTodo(todoItem)

    fun getTodoById(todoItemId: Int) = todoDao.getTodoById(todoItemId)

    fun getAllTodos() = todoDao.getAllTodos()

}