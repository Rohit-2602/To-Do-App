package com.example.to_doapp.ui.editTodo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_doapp.R
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.FragmentEditTodoBinding
import com.example.to_doapp.utils.Util
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class EditTodoFragment : Fragment(R.layout.fragment_edit_todo), OnTaskChanged {

    private var _binding: FragmentEditTodoBinding? = null
    private val binding get() = _binding!!
    private val editTodoViewModel by viewModels<EditTodoViewModel>()
    private val editTodoFragmentArgs: EditTodoFragmentArgs by navArgs()
    private lateinit var editSubTaskAdapter: EditSubTaskAdapter

    private lateinit var todoItem: TodoItem
    private val tasks: MutableList<Task> = ArrayList()
    private val alarmCalendar = Calendar.getInstance()

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentEditTodoBinding.bind(view)
        setUpTasksRecyclerView()

        todoItem = editTodoFragmentArgs.todoItem
        tasks.addAll(todoItem.tasks)

        alarmCalendar.timeInMillis = todoItem.dueDate

        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.addTodoToolbar)

        editTodoViewModel.getTodoById(todoItem.id).asLiveData().observe(viewLifecycleOwner) {
            todoItem = it
            tasks.clear()
            tasks.addAll(it.tasks)
        }

        editTodoViewModel.getTodoList(todoItem.id).observe(viewLifecycleOwner) {
            editSubTaskAdapter.submitList(it)
        }

        binding.apply {

            todoDateTextview.text = Util.formatDate(dueDate = todoItem.dueDate)
            todoTimeTextview.text = Util.formatTime(todoItem.remainderTime)

            todoDate.setOnClickListener {
                setDueDate()
                editTodoViewModel.updateTodoTasks(todoItem.id, tasks)
            }

            todoTime.setOnClickListener {
                setRemainderTime()
                editTodoViewModel.updateTodoTasks(todoItem.id, tasks)
            }

            backButton.setOnClickListener {
                updateTodoTasks()
                findNavController().navigateUp()
            }

            saveTodoButton.setOnClickListener {
                // Nothing Changed because updating item happen in realtime
                updateTodoTasks()
                findNavController().navigateUp()
            }

            addSubTaskButton.setOnClickListener {
                tasks.add(Task(id = tasks.size, title = ""))
                editTodoViewModel.updateTodoTasks(todoItem.id, tasks)
            }
            todoTitle.setText(todoItem.title)
        }

        // Back Button Pressed
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    updateTodoTasks()
                    if (isEnabled) {
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            }
            )

    }

    private fun setUpTasksRecyclerView() {
        editSubTaskAdapter = EditSubTaskAdapter(this)
        binding.subTaskRecyclerview.apply {
            adapter = editSubTaskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onSubTaskTitleChanged(position: Int, newTitle: String) {
        tasks[position].title = newTitle
    }

    override fun updateSubTaskCompletion(position: Int, isCompleted: Boolean) {
        tasks[position].isCompleted = isCompleted
        editTodoViewModel.updateTodoTasks(todoItem.id, tasks)
    }

    override fun removeSubTask(position: Int) {
        tasks.removeAt(position)
        editTodoViewModel.updateTodoTasks(todoItem.id, tasks)
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
        editTodoViewModel.updateTodoTasks(todoItem.id, subTasks)
    }

    private fun setDueDate() {
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, day)

            alarmCalendar.set(Calendar.YEAR, year)
            alarmCalendar.set(Calendar.MONTH, month)
            alarmCalendar.set(Calendar.DAY_OF_MONTH, day)

            todoItem.dueDate = alarmCalendar.timeInMillis

            binding.todoDateTextview.text = Util.formatDate(dueDate = todoItem.dueDate)
            editTodoViewModel.updateTodoDueDate(todoItem.id, todoItem.dueDate)
        }
        Util.showDatePicker(
            todoItem = todoItem, context = requireContext(),
            datePickerListener = datePickerListener
        )
    }

    private fun setRemainderTime() {
        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            alarmCalendar.set(Calendar.MINUTE, minute)
            alarmCalendar.set(Calendar.SECOND, 0)

            todoItem.remainderTime = alarmCalendar.timeInMillis
            todoItem.dueDate = alarmCalendar.timeInMillis

            binding.todoTimeTextview.text = Util.formatTime(todoItem.remainderTime)
            editTodoViewModel.updateTodoDueDateTime(
                todoItem.id, todoItem.dueDate, todoItem.remainderTime
            )
            setAlarm()
        }
        Util.showTimePicker(
            todoItem = todoItem, context = requireContext(),
            timePickerListener = timePickerListener
        )
    }

    private fun setAlarm() {
        Util.setAlarm(
            todoItem = todoItem, context = requireContext(),
            alarmCalendar = alarmCalendar, view = binding.root
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}