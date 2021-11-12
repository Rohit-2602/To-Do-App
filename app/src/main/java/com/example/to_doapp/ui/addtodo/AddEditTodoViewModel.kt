package com.example.to_doapp.ui.addtodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.db.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTodoViewModel @Inject constructor(private val todoRepository: TodoRepository): ViewModel() {

    fun getTodoById(todoItemId: Int) = todoRepository.getTodoById(todoItemId)

    fun updateTodoItem(todoItem: TodoItem, subTasks: List<Task>) = viewModelScope.launch {
        todoRepository.updateTodoTasks(todoItem.id!!, subTasks)
    }

    fun onTaskCheckedChanged(todoItem: TodoItem, position: Int, isChecked: Boolean) =
        viewModelScope.launch {
            val tasks = todoItem.tasks
            tasks[position].isCompleted = isChecked
            todoItem.tasks = tasks
            todoRepository.updateTodo(todoItem)
        }

    fun addTodo(todoItem: TodoItem) = viewModelScope.launch {
        todoRepository.addTodo(todoItem)
    }

}