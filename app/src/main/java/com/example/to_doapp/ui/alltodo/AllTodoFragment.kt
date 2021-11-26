package com.example.to_doapp.ui.alltodo

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
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
import com.example.to_doapp.utils.Util
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AllTodoFragment : Fragment(R.layout.fragment_all_todo), AddEditTask {

    private var _binding: FragmentAllTodoBinding? = null
    private val binding get() = _binding!!
    private val allTodoViewModel by viewModels<AllTodoViewModel>()
    private lateinit var allTodoAdapter: TodoAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    private var alarmCalendar = Calendar.getInstance()
    private var dueDate = Calendar.getInstance().timeInMillis
    private var remainderTime = System.currentTimeMillis()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllTodoBinding.bind(view)

        setUpTodoRecyclerview()
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.isDraggable = false

        allTodoViewModel.todoList.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.noTaskTextview.visibility = View.VISIBLE
                binding.allTodoRecyclerview.visibility = View.GONE
            } else {
                binding.noTaskTextview.visibility = View.GONE
                binding.allTodoRecyclerview.visibility = View.VISIBLE
                allTodoAdapter.submitList(it)
            }
        }

        allTodoViewModel.todoFilter.asLiveData().observe(viewLifecycleOwner) { filter ->
            when (filter) {
                Filter.ALL -> binding.noTaskTextview.text =
                    requireContext().resources.getString(R.string.no_task_all)
                Filter.COMPLETED -> binding.noTaskTextview.text =
                    requireContext().resources.getString(R.string.no_task_completed)
                Filter.IMPORTANT -> binding.noTaskTextview.text =
                    requireContext().resources.getString(R.string.no_task_important)
                else -> requireContext().resources.getString(R.string.no_task_completed)
            }
        }

        binding.apply {
            addTodoButton.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    showBottomSheet()
                } else {
                    if (todoTitle.text.toString().trim().isNotEmpty()) {
                        val todoItem = TodoItem(
                            title = todoTitle.text.trim().toString(),
                            dueDate = dueDate, remainderTime = remainderTime
                        )
                        allTodoViewModel.addTodo(todoItem)
                        setAlarm(todoItem)
                        todoTitle.setText("")
                        dueDate = Calendar.getInstance().timeInMillis
                        remainderTime = System.currentTimeMillis()
                    } else {
                        Snackbar.make(view, "Task can't be Empty!!", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            todoCalendar.setOnClickListener {
                setDueDate(null)
            }
            todoAlarm.setOnClickListener {
                setRemainderTime(null)
            }
            screen.setOnClickListener {
                hideBottomSheet()
            }

            chipGroup.setOnCheckedChangeListener { _, checkedId ->
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
                        activity?.onBackPressed()
                    }
                }
            }
            )

    }

    private fun setUpTodoRecyclerview() {
        allTodoAdapter = TodoAdapter(this)
        binding.allTodoRecyclerview.apply {
            adapter = allTodoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
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
    }

    private fun showUndoSnackBar(todoItem: TodoItem) {
        val snackBar =
            Snackbar.make(binding.addTodoButton, "${todoItem.title} Removed", Snackbar.LENGTH_SHORT)
                .setAction("UNDO") {
                    allTodoViewModel.addTodo(todoItem)
                }
        snackBar.anchorView = binding.addTodoButton
        snackBar.show()
    }

    private fun setDueDate(todoItem: TodoItem?) {
        val datePickerListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, day)

            alarmCalendar.set(Calendar.YEAR, year)
            alarmCalendar.set(Calendar.MONTH, month)
            alarmCalendar.set(Calendar.DAY_OF_MONTH, day)
            dueDate = alarmCalendar.timeInMillis
            if (todoItem != null) {
                allTodoViewModel.updateTodoDueDate(todoItem.id, dueDate)
                setAlarm(todoItem)
            }
        }
        Util.showDatePicker(
            todoItem = todoItem, context = requireContext(),
            datePickerListener = datePickerListener
        )
    }

    private fun setRemainderTime(todoItem: TodoItem?) {
        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            alarmCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            alarmCalendar.set(Calendar.MINUTE, minute)
            alarmCalendar.set(Calendar.SECOND, 0)

            remainderTime = alarmCalendar.timeInMillis
            dueDate = alarmCalendar.timeInMillis

            if (todoItem != null) {
                allTodoViewModel.updateTodoDueDateTime(todoItem.id, dueDate, remainderTime)
                if (!todoItem.completed) {
                    setAlarm(todoItem)
                }
            }
        }
        Util.showTimePicker(
            todoItem = todoItem, context = requireContext(),
            timePickerListener = timePickerListener
        )
    }

    private fun setAlarm(todoItem: TodoItem) {
        Util.setAlarm(
            todoItem = todoItem, context = requireContext(),
            alarmCalendar = alarmCalendar, view = binding.root
        )
    }

    private fun cancelAlarm(todoItem: TodoItem) {
        Util.cancelAlarm(todoItem = todoItem, context = requireContext())
    }

    override fun updateTodoDate(todoItem: TodoItem) {
        setDueDate(todoItem)
    }

    override fun updateTodoTime(todoItem: TodoItem) {
        setRemainderTime(todoItem)
    }

    override fun updateTodoCompletion(todoItem: TodoItem, completed: Boolean) {
        allTodoViewModel.updateTodoCompletion(todoItem.id, completed)
        if (completed) {
            cancelAlarm(todoItem)
        } else {
            setAlarm(todoItem)
        }
    }

    override fun updateTodoImportance(todoItem: TodoItem, important: Boolean) {
        allTodoViewModel.updateTodoImportance(todoItem.id, important)
    }

    override fun removeTodo(todoItem: TodoItem) {
        allTodoViewModel.removeTodo(todoItem)
        cancelAlarm(todoItem)
    }

    override fun editTodo(todoItem: TodoItem) {
        val action = AllTodoFragmentDirections.actionAllTodoFragmentToAddEditTodoFragment(todoItem)
        findNavController().navigate(action)
    }

    override fun updateSubTaskCompletion(todoItem: TodoItem, position: Int, isChecked: Boolean) {
        allTodoViewModel.updateSubTaskCompletion(todoItem, position, isChecked)
    }

    override fun removeSubTask(todoItemId: Int, tasks: List<Task>) {
        allTodoViewModel.updateTodoTasks(todoItemId, tasks)
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