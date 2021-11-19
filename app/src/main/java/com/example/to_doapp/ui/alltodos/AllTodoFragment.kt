package com.example.to_doapp.ui.alltodos

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_doapp.R
import com.example.to_doapp.data.Task
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.FragmentAllTodoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.sql.Time
import java.util.*

@AndroidEntryPoint
class AllTodoFragment : Fragment(R.layout.fragment_all_todo), AddEditTask {

    private var _binding: FragmentAllTodoBinding? = null
    private val binding get() = _binding!!
    private val allTodoViewModel by viewModels<AllTodoViewModel>()
    private lateinit var allTodoAdapter: AllTodoAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    private val calendar = Calendar.getInstance()

    private var mDay = calendar.get(Calendar.DAY_OF_MONTH)
    private var mMonth = calendar.get(Calendar.MONTH)
    private var mYear = calendar.get(Calendar.YEAR)

    private var dueDate = calendar.time

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllTodoBinding.bind(view)

        allTodoAdapter = AllTodoAdapter(this)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.isDraggable = false

        binding.apply {
            addTodoButton.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    showBottomSheet()
                } else {
                    if(todoTitle.text.toString().trim().isNotEmpty()) {
                        val todoItem = TodoItem(title = todoTitle.text.trim().toString(), dueDate = dueDate, remainderTime = Time(System.currentTimeMillis()))
                        allTodoViewModel.addTodo(todoItem)
                        todoTitle.setText("")
                    }
                    else {
                        Snackbar.make(view, "Task can't be Empty!!", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            todoCalendar.setOnClickListener {
                showDialogPicker()
            }
            screen.setOnClickListener {
                hideBottomSheet()
            }
        }

        binding.allTodoRecyclerview.apply {
            adapter = allTodoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        allTodoViewModel.allTodos.observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                binding.noTaskTextview.visibility = View.VISIBLE
            }
            else {
                binding.noTaskTextview.visibility = View.GONE
            }
            allTodoAdapter.submitList(it)
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
                        requireActivity().onBackPressed()
                    }
                }
            }
            )

    }

    private fun showDialogPicker() {
        val dialogPicker = DatePickerDialog(requireContext(),
            { _, year, month, day ->
                val newDate = Calendar.getInstance()
                newDate.set(year, month, day)
                dueDate = Date(newDate.timeInMillis)
            },
        mYear, mMonth, mDay)
        dialogPicker.datePicker.minDate = calendar.timeInMillis
        dialogPicker.show()
    }

    override fun updateSubTaskCompletion(todoItem: TodoItem, position: Int, isChecked: Boolean) {
        allTodoViewModel.onTaskCheckedChanged(todoItem, position, isChecked)
    }

    override fun removeSubTask(todoItemId: Int, tasks: List<Task>) {
        allTodoViewModel.updateTodoTasks(todoItemId, tasks)
    }

    override fun removeTodo(todoItem: TodoItem) {
        allTodoViewModel.removeTodo(todoItem)
    }

    override fun addEditTask(todoItem: TodoItem) {
        val action = AllTodoFragmentDirections.actionAllTodoFragmentToAddTodoFragment(todoItem)
        findNavController().navigate(action)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showBottomSheet() {
        binding.apply {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            addTodoButton.setImageDrawable(requireContext().getDrawable(R.drawable.ic_submit))
            screen.visibility = View.VISIBLE
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun hideBottomSheet() {
        binding.apply {
            screen.visibility = View.GONE
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            addTodoButton.setImageDrawable(requireContext().getDrawable(R.drawable.ic_add))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}