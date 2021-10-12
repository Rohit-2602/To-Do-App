package com.example.to_doapp.ui.alltodos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.ItemTaskBinding
import com.example.to_doapp.utils.Comparators

class SubTaskAdapter(private val todoItem: TodoItem, private val listener: SubTaskComplete) :
    ListAdapter<Task, SubTaskAdapter.SubTaskViewHolder>(Comparators.SUBTASK_COMPARATOR) {

    inner class SubTaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindSubTask(subTask: Task) {
            binding.apply {
                taskTitle.isChecked = subTask.isCompleted
                taskTitle.text = subTask.title
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val subTaskViewHolder = SubTaskViewHolder(binding)
        binding.taskTitle.setOnClickListener {
            listener.checkboxClicked(todoItem, subTaskViewHolder.adapterPosition, binding.taskTitle.isChecked)
        }
        return subTaskViewHolder
    }

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        val subTask = getItem(position)
        holder.bindSubTask(subTask)
    }
}

interface SubTaskComplete {
    fun checkboxClicked(todoItem: TodoItem, position: Int, isChecked: Boolean)
}