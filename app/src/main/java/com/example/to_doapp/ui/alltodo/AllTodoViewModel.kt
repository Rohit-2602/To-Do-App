package com.example.to_doapp.ui.alltodo

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
import javax.inject.Inject

@HiltViewModel
class AllTodoViewModel @Inject constructor(private val todoRepository: TodoRepository) : ViewModel() {

    fun addTodo(todoItem: TodoItem) = viewModelScope.launch {
        todoRepository.addTodo(todoItem)
    }

    private val allTodos = todoRepository.getAllTodos()
    private val completedTodo = todoRepository.getAllTodos().map { list ->
        list.filter { todoItem -> todoItem.completed }
    }
    private val importantTodo = todoRepository.getAllTodos().map { list ->
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
        todoRepository.updateTodoTime(todoItemId, remainderTime)
    }

    fun updateTodoDueDate(todoItemId: Int, dueDate: Long) = viewModelScope.launch {
        todoRepository.updateTodoDueDate(todoItemId, dueDate)
    }

    fun updateTodoDueDateTime(todoItemId: Int, dueDate: Long, remainderTime: Long) = viewModelScope.launch {
        todoRepository.updateTodoDueDateTime(todoItemId, dueDate, remainderTime)
    }

    fun updateTodoCompletion(todoItemId: Int, completed: Boolean) =
        viewModelScope.launch {
            todoRepository.updateTodoChecked(todoItemId, completed)
        }

    fun updateTodoImportance(todoItemId: Int, important: Boolean) =
        viewModelScope.launch {
            todoRepository.updateTodoImportant(todoItemId, important)
        }

    fun updateSubTaskCompletion(todoItem: TodoItem, position: Int, isChecked: Boolean) =
        viewModelScope.launch {
            val tasks = todoItem.tasks
            tasks[position].isCompleted = isChecked
            todoItem.tasks = tasks
            todoRepository.updateTodo(todoItem)
        }

    fun updateTodoTasks(todoItemId: Int, tasks: List<Task>) =
        viewModelScope.launch {
            todoRepository.updateTodoTasks(todoItemId, tasks)
        }

    fun removeTodo(todoItem: TodoItem) = viewModelScope.launch {
        todoRepository.removeTodo(todoItem)
    }

}