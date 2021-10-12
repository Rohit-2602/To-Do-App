package com.example.to_doapp.ui.alltodos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.db.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllTodoViewModel @Inject constructor(private val repository: AppRepository) : ViewModel() {

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

}