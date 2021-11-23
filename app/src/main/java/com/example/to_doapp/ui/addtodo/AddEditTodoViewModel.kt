package com.example.to_doapp.ui.addtodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.db.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(private val todoRepository: TodoRepository): ViewModel() {

    fun getTodoById(todoItemId: Int) = todoRepository.getTodoById(todoItemId)

    fun getTodoList(todoItemId: Int) = getTodoById(todoItemId).flatMapLatest {
        val taskFlow = MutableStateFlow(it.tasks)
        taskFlow
    }.asLiveData()

    fun updateTodoTasks(todoItem: TodoItem, subTasks: List<Task>) = viewModelScope.launch {
        todoRepository.updateTodoTasks(todoItem.id, subTasks)
    }

    fun updateTodoTime(todoItemId: Int, remainderTime: Long) = viewModelScope.launch {
        todoRepository.updateTodoTime(todoItemId, remainderTime)
    }

    fun updateTodoDueDate(todoItemId: Int, dueDate: Date) = viewModelScope.launch {
        todoRepository.updateTodoDueDate(todoItemId, dueDate)
    }

    fun onTaskCheckedChanged(todoItem: TodoItem, position: Int, isChecked: Boolean) =
        viewModelScope.launch {
            val tasks = todoItem.tasks
            tasks[position].isCompleted = isChecked
            todoRepository.updateTodoTasks(todoItemId = todoItem.id, tasks = tasks)
        }

    fun addTodo(todoItem: TodoItem) = viewModelScope.launch {
        todoRepository.addTodo(todoItem)
    }

}