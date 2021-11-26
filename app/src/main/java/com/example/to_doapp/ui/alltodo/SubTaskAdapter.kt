package com.example.to_doapp.ui.alltodo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.ItemSubTaskBinding

class SubTaskAdapter(private val todoItem: TodoItem, private val listener: AddEditTask) :
    RecyclerView.Adapter<SubTaskAdapter.SubTaskViewHolder>() {

    inner class SubTaskViewHolder(private val binding: ItemSubTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindSubTask(subTask: Task) {
            binding.apply {
                taskTitle.isChecked = subTask.isCompleted
                taskTitle.text = subTask.title
                taskTitle.paint.isStrikeThruText = subTask.isCompleted
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubTaskViewHolder {
        val binding = ItemSubTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val subTaskViewHolder = SubTaskViewHolder(binding)
        binding.taskTitle.setOnClickListener {
            listener.updateSubTaskCompletion(todoItem, subTaskViewHolder.adapterPosition, binding.taskTitle.isChecked)
            notifyItemChanged(subTaskViewHolder.adapterPosition)
        }
        binding.taskRemove.setOnClickListener {
            val tasks = todoItem.tasks.toMutableList()
            tasks.removeAt(subTaskViewHolder.adapterPosition)
            listener.removeSubTask(todoItem.id, tasks)
            notifyItemChanged(subTaskViewHolder.adapterPosition)
        }
        return subTaskViewHolder
    }

    override fun onBindViewHolder(holder: SubTaskViewHolder, position: Int) {
        val subTask = todoItem.tasks[position]
        holder.bindSubTask(subTask)
    }

    override fun getItemCount(): Int {
        return todoItem.tasks.size
    }
}