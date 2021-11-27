package com.example.to_doapp.ui.editTodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.to_doapp.data.Task
import com.example.to_doapp.db.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTodoViewModel @Inject constructor(private val todoRepository: TodoRepository): ViewModel() {

    fun getTodoById(todoItemId: Int) = todoRepository.getTodoById(todoItemId)

    fun getTodoList(todoItemId: Int) = getTodoById(todoItemId).flatMapLatest {
        val taskFlow = MutableStateFlow(it.tasks)
        taskFlow
    }.asLiveData()

    fun updateTodoTasks(todoItemId: Int, subTasks: List<Task>) = viewModelScope.launch {
        todoRepository.updateTodoTasks(todoItemId, subTasks)
    }

    fun updateTodoTime(todoItemId: Int, remainderTime: Long) = viewModelScope.launch {
        todoRepository.updateTodoTime(todoItemId, remainderTime)
    }

    fun updateTodoDueDate(todoItemId: Int, dueDate: Long) = viewModelScope.launch {
        todoRepository.updateTodoDueDate(todoItemId, dueDate)
    }

    fun updateTodoDueDateTime(todoItemId: Int, dueDate: Long, remainderTime: Long) = viewModelScope.launch {
        todoRepository.updateTodoDueDateTime(todoItemId, dueDate, remainderTime)
    }

    fun updateSubTaskCompletion(todoItemId: Int, position: Int, isChecked: Boolean, tasks: List<Task>) =
        viewModelScope.launch {
            tasks[position].isCompleted = isChecked
            todoRepository.updateTodoTasks(todoItemId = todoItemId, tasks = tasks)
        }

}