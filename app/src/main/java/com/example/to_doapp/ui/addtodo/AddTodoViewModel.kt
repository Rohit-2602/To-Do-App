package com.example.to_doapp.ui.addtodo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.db.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTodoViewModel @Inject constructor(private val repository: AppRepository): ViewModel() {

    fun addTodo(todoItem: TodoItem) = viewModelScope.launch {
        repository.addTodo(todoItem)
    }

}