package com.example.to_doapp.ui.alltodos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.to_doapp.data.Filter
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.db.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AllTodoViewModel @Inject constructor(private val repository: TodoRepository) : ViewModel() {

    fun addTodo(todoItem: TodoItem) = viewModelScope.launch {
        repository.addTodo(todoItem)
    }

    private val allTodos = repository.getAllTodos()
    private val completedTodo = repository.getAllTodos().map { list ->
        list.filter { todoItem -> todoItem.completed }
    }
    private val importantTodo = repository.getAllTodos().map { list ->
        list.filter { todoItem -> todoItem.important }
    }
    val todoFilter = MutableStateFlow(Filter.ALL)

    private val todoListFlow = todoFilter.flatMapLatest { filter ->
        when (filter) {
            Filter.ALL -> allTodos
            Filter.COMPLETED -> completedTodo
            Filter.IMPORTANT -> importantTodo
        }
    }

    val todoList = todoListFlow.asLiveData()

    fun updateTodoTime(todoItemId: Int, remainderTime: Long) = viewModelScope.launch {
        repository.updateTodoTime(todoItemId, remainderTime)
    }

    fun updateTodoDueDate(todoItemId: Int, dueDate: Date) = viewModelScope.launch {
        repository.updateTodoDueDate(todoItemId, dueDate)
    }

    fun updateTodoChecked(todoItemId: Int, completed: Boolean) =
        viewModelScope.launch {
            repository.updateTodoChecked(todoItemId, completed)
        }

    fun updateTodoImportant(todoItemId: Int, important: Boolean) =
        viewModelScope.launch {
            repository.updateTodoImportant(todoItemId, important)
        }

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