package com.example.to_doapp.ui.alltodos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.ItemTodoBinding
import com.example.to_doapp.utils.Comparators

class AllTodoAdapter(private val listener: AddEditTask):
    ListAdapter<TodoItem, AllTodoAdapter.TodoViewHolder>(Comparators.TODO_COMPARATOR) {

    private var subTaskAdapter: SubTaskAdapter?= null

    inner class TodoViewHolder(val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindTodo(todoItem: TodoItem) {
            binding.apply {
                todoTitle.text = todoItem.title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val addTodoViewHolder = TodoViewHolder(binding)
        binding.todoTitle.setOnClickListener {
            listener.addEditTask(getItem(addTodoViewHolder.adapterPosition))
        }
        return addTodoViewHolder
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todoItem = getItem(position)
        holder.bindTodo(todoItem)

        subTaskAdapter = SubTaskAdapter(todoItem, listener)
        holder.binding.subTaskRecyclerview.apply {
            adapter = subTaskAdapter
            layoutManager = LinearLayoutManager(holder.binding.root.context)
        }

    }
}

interface AddEditTask {
    fun addEditTask(todoItem: TodoItem)
    fun updateSubTaskCompletion(todoItem: TodoItem, position: Int, isChecked: Boolean)
}