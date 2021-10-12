package com.example.to_doapp.ui.alltodos

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.ItemTodoBinding
import com.example.to_doapp.utils.Comparators

class AllTodoAdapter(private val listener: AddEditTask, private val subTaskCompleteListener: SubTaskComplete):
    ListAdapter<TodoItem, AllTodoAdapter.AllTodoViewHolder>(Comparators.TODO_COMPARATOR) {

    val viewPool = RecyclerView.RecycledViewPool()
    private var subTaskAdapter: SubTaskAdapter?= null

    inner class AllTodoViewHolder(private val binding: ItemTodoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindTodo(todoItem: TodoItem, position: Int) {
            binding.apply {
                todoTitle.text = todoItem.title

                subTaskAdapter = SubTaskAdapter(todoItem, subTaskCompleteListener)
                subTaskRecyclerview.apply {
                    adapter = subTaskAdapter
                    layoutManager = LinearLayoutManager(binding.root.context)
                    setRecycledViewPool(viewPool)
                }
                subTaskAdapter!!.submitList(todoItem.tasks)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllTodoViewHolder {
        val binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val addTodoViewHolder = AllTodoViewHolder(binding)
        binding.todoTitle.setOnClickListener {
            listener.addEditTask(getItem(addTodoViewHolder.adapterPosition))
        }
        return addTodoViewHolder
    }

    override fun onBindViewHolder(holder: AllTodoViewHolder, position: Int) {
        val todoItem = getItem(position)
        holder.bindTodo(todoItem, position)
    }
}

interface AddEditTask {
    fun addEditTask(todoItem: TodoItem)
//    fun updateSubTaskCompletion(todoItem: TodoItem, position: Int, isChecked: Boolean)
}