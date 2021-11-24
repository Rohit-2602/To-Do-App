package com.example.to_doapp.ui.addtodo

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.R
import com.example.to_doapp.data.Task
import com.example.to_doapp.databinding.ItemAddSubTaskBinding
import com.example.to_doapp.utils.Comparators.SUBTASK_COMPARATOR

class AddTodoAdapter(private val listener: OnTaskChanged) :
    ListAdapter<Task, AddTodoAdapter.AddTodoViewHolder>(SUBTASK_COMPARATOR) {

    inner class AddTodoViewHolder(private val binding: ItemAddSubTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindSubTask(subTask: Task) {
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
            subTaskTitle.setOnFocusChangeListener { _, focused ->
                if (focused) {
                    subTaskSort.background = null
                    subTaskSort.setImageResource(R.drawable.ic_close)
                    subTaskSort.setOnClickListener {
                        listener.removeSubTask(addTodoViewHolder.adapterPosition)
                    }
                }
                else {
                    subTaskSort.background = null
                    subTaskSort.setImageResource(R.drawable.ic_sort)
                }
            }
            subTaskTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(newText: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    currentList[addTodoViewHolder.adapterPosition].title = newText.toString()
                }
                override fun afterTextChanged(newText: Editable?) {
                    listener.onTitleChanged(addTodoViewHolder.adapterPosition, newText.toString())
                }
            })
        }
        return addTodoViewHolder
    }

    override fun onBindViewHolder(holder: AddTodoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bindSubTask(item)
    }
}

interface OnTaskChanged {
    fun onTitleChanged(position: Int, newTitle: String)
    fun onCompletedChanged(position: Int, isCompleted: Boolean)
    fun removeSubTask(position: Int)
}
