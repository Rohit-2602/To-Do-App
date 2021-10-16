package com.example.to_doapp.ui.addtodo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.to_doapp.R
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.FragmentAddTodoBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddEditTodoFragment : Fragment(R.layout.fragment_add_todo) {

    private var _binding: FragmentAddTodoBinding? = null
    private val binding get() = _binding!!
    private val addTodoViewModel by viewModels<AddEditTodoViewModel>()
    private val addEditTodoFragmentArgs : AddEditTodoFragmentArgs by navArgs()

    private val calendar = Calendar.getInstance()

    private var mDay = calendar.get(Calendar.DAY_OF_MONTH)
    private var mMonth = calendar.get(Calendar.MONTH)
    private var mYear = calendar.get(Calendar.YEAR)
    private lateinit var dueDate: Date

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTodoBinding.bind(view)

        val todoItem = addEditTodoFragmentArgs.todoItem
        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.addTodoToolbar)

        getSubTasks(todoItem)

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
                findNavController().navigateUp()
            }

            saveTodoButton.setOnClickListener {
                val subTasks = ArrayList<Task>()
                for (i in 0 until binding.subTaskRoot.childCount) {
                    val subTaskLayout = binding.subTaskRoot.getChildAt(i)

                    val editText = subTaskLayout.findViewById<EditText>(R.id.sub_task_title)
                    val subTaskCheckbox = subTaskLayout.findViewById<CheckBox>(R.id.sub_task_checkbox)

                    val taskTitle = editText.text.trim().toString()
                    if (taskTitle.isNotEmpty()) {
                        val task = Task(title = taskTitle, isCompleted = subTaskCheckbox.isChecked)
                        subTasks.add(task)
                    }
                }
                todoItem.title = todoTitle.text.toString()
                todoItem.tasks = subTasks
                todoItem.dueDate = dueDate
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

    @SuppressLint("SetTextI18n")
    private fun setDateTimeField() {
        val datePickerDialog = DatePickerDialog(requireContext(),
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
        mYear, mMonth, mDay)
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
    }

    @SuppressLint("InflateParams")
    private fun getSubTasks(todoItem: TodoItem) {
        for (i in todoItem.tasks.indices) {

            val subTask = todoItem.tasks[i]

            val inflater = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_add_sub_task, null)
            binding.subTaskRoot.addView(inflater)

            val view = binding.subTaskRoot.getChildAt(i)!!

            val subTaskTitle = view.findViewById<EditText>(R.id.sub_task_title)
            val subTaskCheckbox = view.findViewById<CheckBox>(R.id.sub_task_checkbox)
//            val subTaskSort = view.findViewById<ImageView>(R.id.sub_task_sort)

            subTaskTitle.setText(subTask.title)
            subTaskTitle.paint.isStrikeThruText = subTask.isCompleted
            subTaskCheckbox.isChecked = subTask.isCompleted

//            subTaskCheckbox.setOnCheckedChangeListener { compoundButton, isChecked ->
//                subTaskTitle.paint.isStrikeThruText = isChecked
//            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}