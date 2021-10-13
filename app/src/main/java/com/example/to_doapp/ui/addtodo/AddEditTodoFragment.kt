package com.example.to_doapp.ui.addtodo

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.to_doapp.R
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.FragmentAddTodoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditTodoFragment : Fragment(R.layout.fragment_add_todo) {

    private var _binding: FragmentAddTodoBinding? = null
    private val binding get() = _binding!!
    private val addTodoViewModel by viewModels<AddEditTodoViewModel>()
    private val addEditTodoFragmentArgs : AddEditTodoFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTodoBinding.bind(view)

        val todoItem = addEditTodoFragmentArgs.todoItem

        getSubTasks(todoItem)

        binding.apply {
            val subTasks = ArrayList<Task>()
            saveTodoButton.setOnClickListener {
                for (i in 0 until binding.subTaskRoot.childCount) {
                    val subTaskLayout = binding.subTaskRoot.getChildAt(i)
                    val editText = subTaskLayout.findViewById<EditText>(R.id.sub_task_title)
                    val taskTitle = editText.text.trim().toString()
                    if (taskTitle.isNotEmpty()) {
                        val task = Task(editText.text.toString())
                        subTasks.add(task)
                    }
                }
                todoItem.title = todoTitle.text.toString()
                todoItem.tasks = subTasks
                addTodoViewModel.addTodo(todoItem)
                findNavController().navigateUp()
            }
            addSubTaskButton.setOnClickListener {

                val inflater = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_add_sub_task, null)
                binding.subTaskRoot.addView(inflater)

                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
            todoTitle.setText(todoItem.title)
        }

    }

    @SuppressLint("InflateParams")
    private fun getSubTasks(todoItem: TodoItem) {
        for (i in todoItem.tasks.indices) {

            val subTask = todoItem.tasks[i]

            val inflater = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_add_sub_task, null)
            binding.subTaskRoot.addView(inflater)

            val view = binding.subTaskRoot.getChildAt(i)

            val subTaskTitle = view?.findViewById<EditText>(R.id.sub_task_title)
            val subTaskCheckbox = view?.findViewById<CheckBox>(R.id.sub_task_checkbox)
            val subTaskSort = view?.findViewById<ImageView>(R.id.sub_task_sort)

            subTaskTitle?.setText(subTask.title)
            subTaskCheckbox?.isChecked = subTask.isCompleted
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}