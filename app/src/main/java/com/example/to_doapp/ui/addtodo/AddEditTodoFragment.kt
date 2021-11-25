package com.example.to_doapp.ui.addtodo

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
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
import com.example.to_doapp.databinding.FragmentAddTodoBinding
import com.example.to_doapp.receiver.AlarmReceiver
import com.example.to_doapp.utils.Util
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

    private lateinit var dueDate: Date
    private val alarmCalendar = Calendar.getInstance()

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTodoBinding.bind(view)

        todoItem = addEditTodoFragmentArgs.todoItem
        tasks.addAll(todoItem.tasks)

        dueDate = todoItem.dueDate
        alarmCalendar.time = dueDate

        val mainActivity = activity as AppCompatActivity
        mainActivity.setSupportActionBar(binding.addTodoToolbar)

        addEditTodoAdapter = AddTodoAdapter(this)

        binding.subTaskRecyclerview.apply {
            adapter = addEditTodoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        addTodoViewModel.getTodoById(todoItem.id).asLiveData().observe(viewLifecycleOwner) {
            todoItem = it
            tasks.clear()
            tasks.addAll(it.tasks)
        }

        addTodoViewModel.getTodoList(todoItem.id).observe(viewLifecycleOwner) {
            addEditTodoAdapter.submitList(it)
        }

        binding.apply {

            todoDateTextview.text = Util.formatDate(dueDate = dueDate)
            todoTimeTextview.text = Util.formatTime(todoItem.remainderTime)

            todoDate.setOnClickListener {
                addTodoViewModel.updateTodoTasks(todoItem, tasks)
                setDateField()
            }

            todoTime.setOnClickListener {
                addTodoViewModel.updateTodoTasks(todoItem, tasks)
                setTimeField()
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
                addTodoViewModel.updateTodoTasks(todoItem, tasks)
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

    override fun onTitleChanged(position: Int, newTitle: String) {
        tasks[position].title = newTitle
    }

    override fun onCompletedChanged(position: Int, isCompleted: Boolean) {
        addTodoViewModel.onTaskCheckedChanged(todoItem, position, isCompleted, tasks)
    }

    override fun removeSubTask(position: Int) {
        tasks.removeAt(position)
        addTodoViewModel.updateTodoTasks(todoItem, tasks)
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
        addTodoViewModel.updateTodoTasks(todoItem, subTasks)
    }

    private fun setTimeField() {

        val pickerHour = todoItem.remainderTime.toInt() / 3600
        val pickerMinute = todoItem.remainderTime.toInt() - pickerHour * 3600

        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                alarmCalendar.set(Calendar.MINUTE, minute)
                alarmCalendar.set(Calendar.SECOND, 0)

                todoItem.remainderTime = alarmCalendar.timeInMillis

                binding.todoTimeTextview.text = Util.formatTime(todoItem.remainderTime)
                addTodoViewModel.updateTodoTime(todoItem.id, todoItem.remainderTime)
                setAlarm(alarmCalendar)
            }

        val timePicker = TimePickerDialog(
            requireContext(),
            timePickerListener,
            pickerHour, pickerMinute, true
        )
        timePicker.show()
        timePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.light_blue))
        timePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.light_blue))
    }

    @SuppressLint("SetTextI18n")
    private fun setDateField() {

        val calendar = Calendar.getInstance()

        val mYear = DateFormat.format("yyyy", todoItem.dueDate).toString().toInt()
        val mMonth = DateFormat.format("MM", todoItem.dueDate).toString().toInt()-1
        val mDay = DateFormat.format("dd", todoItem.dueDate).toString().toInt()

        val datePickerDialog = DatePickerDialog(
            requireContext(), R.style.MyDatePickerLight,
            { _, year, month, day ->
                val newDate = Calendar.getInstance()
                newDate.set(year, month, day)

                dueDate = Date(newDate.timeInMillis)
                alarmCalendar.set(Calendar.YEAR, year)
                alarmCalendar.set(Calendar.MONTH, month)
                alarmCalendar.set(Calendar.DAY_OF_MONTH, day)

                binding.todoDateTextview.text = Util.formatDate(dueDate = dueDate)
                addTodoViewModel.updateTodoDueDate(todoItem.id, dueDate)
            },
            mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.dark_gray))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.dark_gray))
    }

    private fun setAlarm(calendar: Calendar) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("todoTitle", todoItem.title)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), todoItem.id, intent, 0)
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}