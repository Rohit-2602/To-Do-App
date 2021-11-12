package com.example.to_doapp.ui.addtodo

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.data.Task
import com.example.to_doapp.databinding.ItemAddSubTaskBinding

class AddTodoAdapter(private val listener: OnTaskChanged) :
    ListAdapter<Task, AddTodoAdapter.AddTodoViewHolder>(subTaskComparator) {

    companion object {
        val subTaskComparator = object : DiffUtil.ItemCallback<Task?>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class AddTodoViewHolder(private val binding: ItemAddSubTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindSubTask(subTask: Task, position: Int) {
            binding.apply {
                subTaskCheckbox.isChecked = subTask.isCompleted
                subTaskTitle.setText(subTask.title)
                subTaskTitle.paint.isStrikeThruText = subTask.isCompleted
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddTodoViewHolder {
        val binding =
            ItemAddSubTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val addTodoViewHolder = AddTodoViewHolder(binding)
        binding.apply {
            subTaskCheckbox.setOnCheckedChangeListener { _, isChecked ->
                listener.onCompletedChanged(addTodoViewHolder.adapterPosition, isChecked)
            }
            subTaskTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    currentList[addTodoViewHolder.adapterPosition].title = newText.toString()
                    listener.onTitleChanged(getItem(addTodoViewHolder.adapterPosition), currentList)
                }

                override fun afterTextChanged(p0: Editable?) {

                }
            })
        }
        return addTodoViewHolder
    }

    override fun onBindViewHolder(holder: AddTodoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindSubTask(item, position)
    }
}

interface OnTaskChanged {
    fun onTitleChanged(subTask: Task, subTaskList: List<Task>)
    fun onCompletedChanged(position: Int, isCompleted: Boolean)

}
