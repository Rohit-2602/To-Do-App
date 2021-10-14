package com.example.to_doapp.ui.alltodos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.db.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllTodoViewModel @Inject constructor(private val repository: TodoRepository) : ViewModel() {

    fun addTodo(todoItem: TodoItem) = viewModelScope.launch {
        repository.addTodo(todoItem)
    }

    val allTodos = repository.getAllTodos()

    fun onTaskCheckedChanged(todoItem: TodoItem, position: Int, isChecked: Boolean) =
        viewModelScope.launch {
            val tasks = todoItem.tasks
            tasks[position].isCompleted = isChecked
            todoItem.tasks = tasks
            repository.updateTodo(todoItem)
        }

    fun updateTodoTasks(todoItemId: Int, tasks: List<Task>) =
        viewModelScope.launch {
            repository.updateTodoTasks(todoItemId, tasks)
        }

    fun removeTodo(todoItem: TodoItem) = viewModelScope.launch {
        repository.removeTodo(todoItem)
    }

}