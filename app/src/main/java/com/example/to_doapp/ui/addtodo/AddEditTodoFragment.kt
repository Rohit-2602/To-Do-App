package com.example.to_doapp.ui.addtodo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_doapp.R
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.FragmentAddTodoBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddEditTodoFragment : Fragment(R.layout.fragment_add_todo), OnTaskChanged {

    private var _binding: FragmentAddTodoBinding? = null
    private val binding get() = _binding!!
    private val addTodoViewModel by viewModels<AddEditTodoViewModel>()
    private val addEditTodoFragmentArgs: AddEditTodoFragmentArgs by navArgs()
    private lateinit var addEditTodoAdapter: AddTodoAdapter

    private lateinit var todoItem: TodoItem
    val tasks: MutableList<Task> = ArrayList()

    private val calendar = Calendar.getInstance()

    private var mDay = calendar.get(Calendar.DAY_OF_MONTH)
    private var mMonth = calendar.get(Calendar.MONTH)
    private var mYear = calendar.get(Calendar.YEAR)
    private lateinit var dueDate: Date

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTodoBinding.bind(view)

        todoItem = addEditTodoFragmentArgs.todoItem
        tasks.addAll(todoItem.tasks)

        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.addTodoToolbar)

        addEditTodoAdapter = AddTodoAdapter(this)

        binding.subTaskRecyclerview.apply {
            adapter = addEditTodoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        addTodoViewModel.getTodoById(todoItem.id!!).observe(viewLifecycleOwner) {
            addEditTodoAdapter.submitList(it.tasks as List<Task>)
        }

        binding.apply {

            val day = DateFormat.format("dd", todoItem.dueDate)
            val monthNumber = DateFormat.format("MM", todoItem.dueDate)
            val year = DateFormat.format("yyyy", todoItem.dueDate)

            dueDate = todoItem.dueDate

            todoDateTextview.text = "$day/$monthNumber/$year"

            todoDate.setOnClickListener {
                setDateTimeField()
            }

            backButton.setOnClickListener {
                updateTodoTasks()
                findNavController().navigateUp()
            }

            saveTodoButton.setOnClickListener {
                // Nothing Changed bcoz updating item happen in realtime
                updateTodoTasks()
                findNavController().navigateUp()
            }

            addSubTaskButton.setOnClickListener {
                tasks.add(Task(id = tasks.size, title = ""))
                addTodoViewModel.updateTodoItem(todoItem, tasks)
            }
            todoTitle.setText(todoItem.title)
        }

        // Back Button Pressed
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    var index = 0
                    val subTasks: MutableList<Task> = ArrayList()
                    subTasks.addAll(tasks)
                    tasks.forEach { task ->
                        if (task.title == "") {
                            subTasks.remove(task)
                        } else {
                            task.id = index++
                        }
                    }
                    addTodoViewModel.updateTodoItem(todoItem, subTasks)
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )

    }

    override fun onTitleChanged(subTask: Task, subTaskList: List<Task>) {
        tasks.clear()
        tasks.addAll(subTaskList)
        addTodoViewModel.updateTodoItem(todoItem = addEditTodoFragmentArgs.todoItem, subTaskList)
    }

    override fun onCompletedChanged(position: Int, isCompleted: Boolean) {
        addTodoViewModel.onTaskCheckedChanged(todoItem, position, isCompleted)
    }

    private fun updateTodoTasks() {
        var index = 0
        val subTasks: MutableList<Task> = ArrayList()
        subTasks.addAll(tasks)
        tasks.forEach { task ->
            if (task.title == "") {
                subTasks.remove(task)
            } else {
                task.id = index++
            }
        }
        addTodoViewModel.updateTodoItem(todoItem, subTasks)
    }

    @SuppressLint("SetTextI18n")
    private fun setDateTimeField() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val newDate = Calendar.getInstance()
                newDate.set(year, month, day)

                mDay = newDate.get(Calendar.DAY_OF_MONTH)
                mMonth = newDate.get(Calendar.MONTH)
                mYear = newDate.get(Calendar.YEAR)

                dueDate = Date(newDate.timeInMillis)
                val newDay = DateFormat.format("dd", dueDate)
                val monthNumber = DateFormat.format("MM", dueDate)
                val newYear = DateFormat.format("yyyy", dueDate)

                binding.todoDateTextview.text = "$newDay/$monthNumber/$newYear"
            },
            mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}