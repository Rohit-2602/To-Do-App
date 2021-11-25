package com.example.to_doapp.ui.alltodos

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.to_doapp.R
import com.example.to_doapp.data.Filter
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.FragmentAllTodoBinding
import com.example.to_doapp.receiver.AlarmReceiver
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AllTodoFragment : Fragment(R.layout.fragment_all_todo), AddEditTask {

    private var _binding: FragmentAllTodoBinding? = null
    private val binding get() = _binding!!
    private val allTodoViewModel by viewModels<AllTodoViewModel>()
    private lateinit var allTodoAdapter: AllTodoAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    private val calendar = Calendar.getInstance()
    private var alarmCalendar = Calendar.getInstance()
    private var dueDate = calendar.time
    private var remainderTime = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllTodoBinding.bind(view)

        allTodoAdapter = AllTodoAdapter(this)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.isDraggable = false

        allTodoViewModel.todoList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.noTaskTextview.visibility = View.VISIBLE
                binding.allTodoRecyclerview.visibility = View.GONE
            }
            else {
                binding.noTaskTextview.visibility = View.GONE
                binding.allTodoRecyclerview.visibility = View.VISIBLE
                allTodoAdapter.submitList(it)
            }
        }

        allTodoViewModel.todoFilter.asLiveData().observe(viewLifecycleOwner) { filter ->
            when(filter) {
                Filter.ALL -> binding.noTaskTextview.text = requireContext().resources.getString(R.string.no_task_all)
                Filter.COMPLETED -> binding.noTaskTextview.text = requireContext().resources.getString(R.string.no_task_completed)
                Filter.IMPORTANT -> binding.noTaskTextview.text = requireContext().resources.getString(R.string.no_task_important)
                else -> requireContext().resources.getString(R.string.no_task_completed)
            }
        }

        binding.apply {
            addTodoButton.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    showBottomSheet()
                } else {
                    if(todoTitle.text.toString().trim().isNotEmpty()) {
                        val todoItem = TodoItem(title = todoTitle.text.trim().toString(), dueDate = dueDate, remainderTime = remainderTime)
                        allTodoViewModel.addTodo(todoItem)
                        setAlarm(todoItem)
                        todoTitle.setText("")
                        dueDate = Calendar.getInstance().time
                        remainderTime = System.currentTimeMillis()
                    }
                    else {
                        Snackbar.make(view, "Task can't be Empty!!", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            todoCalendar.setOnClickListener {
                setDueDate(null)
            }
            todoAlarm.setOnClickListener {
                setTimeField(null)
            }
            screen.setOnClickListener {
                hideBottomSheet()
            }

            binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.all_todo_chip -> {
                        allTodoViewModel.todoFilter.value = Filter.ALL
                    }
                    R.id.completed_todo_chip -> {
                        allTodoViewModel.todoFilter.value = Filter.COMPLETED
                    }
                    R.id.important_todo_chip -> {
                        allTodoViewModel.todoFilter.value = Filter.IMPORTANT
                    }
                }
            }

        }

        binding.allTodoRecyclerview.apply {
            adapter = allTodoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val todoItem = allTodoAdapter.currentList[viewHolder.adapterPosition]
                allTodoViewModel.removeTodo(todoItem)
                showUndoSnackBar(todoItem)
            }
        }).attachToRecyclerView(binding.allTodoRecyclerview)

        // Using BackPressed in Fragment
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        hideBottomSheet()
                        return
                    }
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )

    }

    private fun showUndoSnackBar(todoItem: TodoItem) {
        val snackBar = Snackbar.make(binding.addTodoButton, "${todoItem.title} Removed", Snackbar.LENGTH_SHORT)
            .setAction("UNDO") {
                allTodoViewModel.addTodo(todoItem)
            }
        snackBar.anchorView = binding.addTodoButton
        snackBar.show()
    }

    private fun setDueDate(todoItem: TodoItem?) {
        var mDay = calendar.get(Calendar.DAY_OF_MONTH)
        var mMonth = calendar.get(Calendar.MONTH)
        var mYear = calendar.get(Calendar.YEAR)

        if (todoItem != null) {
            mYear = DateFormat.format("yyyy", todoItem.dueDate).toString().toInt()
            mMonth = DateFormat.format("MM", todoItem.dueDate).toString().toInt()-1
            mDay = DateFormat.format("dd", todoItem.dueDate).toString().toInt()
        }
        val datePickerDialog = DatePickerDialog(requireContext(), R.style.MyDatePickerLight,
            { _, year, month, day ->
                val newDate = Calendar.getInstance()
                newDate.set(year, month, day)

                dueDate = Date(newDate.timeInMillis)
                alarmCalendar.set(Calendar.YEAR, year)
                alarmCalendar.set(Calendar.MONTH, month)
                alarmCalendar.set(Calendar.DAY_OF_MONTH, day)
                if (todoItem != null) {
                    allTodoViewModel.updateTodoDueDate(todoItem.id, dueDate)
                    setAlarm(todoItem)
                }
            },
            mYear, mMonth, mDay
        )
        datePickerDialog.datePicker.minDate = calendar.timeInMillis
        datePickerDialog.show()
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.dark_gray))
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.dark_gray))
    }

    private fun setTimeField(todoItem: TodoItem?) {

        val mCalendar = Calendar.getInstance()
        var pickerHour = mCalendar.get(Calendar.HOUR_OF_DAY)
        var pickerMinute = mCalendar.get(Calendar.MINUTE)
        if (todoItem != null) {
            pickerHour = todoItem.remainderTime.toInt() / 3600
            pickerMinute = todoItem.remainderTime.toInt() - pickerHour * 3600
        }
        val timePickerListener =
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                alarmCalendar.set(Calendar.MINUTE, minute)
                alarmCalendar.set(Calendar.SECOND, 0)

                remainderTime = alarmCalendar.timeInMillis
                if (todoItem != null) {
                    allTodoViewModel.updateTodoTime(todoItem.id, remainderTime)
                    setAlarm(todoItem)
                }

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

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun setAlarm(todoItem: TodoItem) {
        val new = Calendar.getInstance()
        if (alarmCalendar.timeInMillis < new.timeInMillis) {
            Toast.makeText(requireContext(), "Alarm Calendar Less", Toast.LENGTH_SHORT).show()
            return
        }
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("todoTitle", todoItem.title)
        intent.putExtra("todoId", todoItem.id)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), System.currentTimeMillis().toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmCalendar.timeInMillis, pendingIntent)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun cancelAlarm(todoItem: TodoItem) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        intent.putExtra("todoTitle", todoItem.title)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), todoItem.id, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    override fun updateTodoDate(todoItem: TodoItem) {
        setDueDate(todoItem)
    }

    override fun updateTodoTime(todoItem: TodoItem) {
        setTimeField(todoItem)
    }

    override fun updateSubTaskCompletion(todoItem: TodoItem, position: Int, isChecked: Boolean) {
        allTodoViewModel.onTaskCheckedChanged(todoItem, position, isChecked)
    }

    override fun removeSubTask(todoItemId: Int, tasks: List<Task>) {
        allTodoViewModel.updateTodoTasks(todoItemId, tasks)
    }

    override fun removeTodo(todoItem: TodoItem) {
        allTodoViewModel.removeTodo(todoItem)
        cancelAlarm(todoItem)
    }

    override fun editTodo(todoItem: TodoItem) {
        val action = AllTodoFragmentDirections.actionAllTodoFragmentToAddEditTodoFragment(todoItem)
        findNavController().navigate(action)
    }

    override fun completeTodo(todoItem: TodoItem, completed: Boolean) {
        allTodoViewModel.updateTodoChecked(todoItem.id, completed)
        cancelAlarm(todoItem)
    }

    override fun importantTodo(todoItem: TodoItem, important: Boolean) {
        allTodoViewModel.updateTodoImportant(todoItem.id, important)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showBottomSheet() {
        binding.apply {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            addTodoButton.text = requireContext().resources.getString(R.string.create)
            addTodoButton.gravity = Gravity.END
            addTodoButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_submit, 0, 0, 0)
            screen.visibility = View.VISIBLE
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun hideBottomSheet() {
        binding.apply {
            screen.visibility = View.GONE
            addTodoButton.text = requireContext().resources.getString(R.string.add_task)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            addTodoButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add, 0, 0, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}