package com.example.to_doapp.ui.alltodo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.R
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.ItemTodoBinding
import com.example.to_doapp.utils.Comparators
import com.example.to_doapp.utils.Util

class TodoAdapter(private val listener: AddEditTask):
    ListAdapter<TodoItem, TodoAdapter.TodoViewHolder>(Comparators.TODO_COMPARATOR) {

    private var subTaskAdapter: SubTaskAdapter?= null

    inner class TodoViewHolder(val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindTodo(todoItem: TodoItem) {
            binding.apply {
                todoTitle.text = todoItem.title
                todoDateTextview.text = Util.formatDate(todoItem.dueDate)
                todoTimeTextview.text = Util.formatTime(todoItem.remainderTime)
                todoCompletedCheckbox.isChecked = todoItem.completed
                todoImportantCheckbox.isChecked = todoItem.important

                // If todoItem remainderTime is less than actual time and date is equal (less isn't possible)
                if (Util.isTodoDateLessOrEqual(todoItem.dueDate)) {
                    todoTimeTextview.setTextColor(binding.root.context.resources.getColor(R.color.red))
                }
                else {
                    todoTimeTextview.setTextColor(Color.WHITE)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val addTodoViewHolder = TodoViewHolder(binding)

        binding.todoEdit.setOnClickListener {
            listener.editTodo(getItem(addTodoViewHolder.adapterPosition))
        }
        binding.todoCompletedCheckbox.setOnCheckedChangeListener { _, checked ->
            listener.updateTodoCompletion(getItem(addTodoViewHolder.adapterPosition), checked)
        }
        binding.todoImportantCheckbox.setOnCheckedChangeListener { _, checked ->
            listener.updateTodoImportance(getItem(addTodoViewHolder.adapterPosition), checked)
        }
        binding.todoConstraint.setOnClickListener {
            val visibility = (binding.subTaskRecyclerview.visibility == View.VISIBLE)
            if (visibility) {
                binding.subTaskRecyclerview.visibility = View.GONE
            }
            else {
                binding.subTaskRecyclerview.visibility = View.VISIBLE
            }
        }
        binding.todoDateTextview.setOnClickListener {
            listener.updateTodoDate(getItem(addTodoViewHolder.adapterPosition))
        }
        binding.todoTimeTextview.setOnClickListener {
            listener.updateTodoTime(getItem(addTodoViewHolder.adapterPosition))
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
    fun editTodo(todoItem: TodoItem)
    fun updateSubTaskCompletion(todoItem: TodoItem, position: Int, isChecked: Boolean)
    fun removeSubTask(todoItemId: Int, tasks: List<Task>)
    fun removeTodo(todoItem: TodoItem)
    fun updateTodoCompletion(todoItem: TodoItem, completed: Boolean)
    fun updateTodoImportance(todoItem: TodoItem, important: Boolean)
    fun updateTodoDate(todoItem: TodoItem)
    fun updateTodoTime(todoItem: TodoItem)
}