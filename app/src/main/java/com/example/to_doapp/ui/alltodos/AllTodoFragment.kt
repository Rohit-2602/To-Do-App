package com.example.to_doapp.ui.alltodos

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.to_doapp.R
import com.example.to_doapp.data.TodoItem
import com.example.to_doapp.databinding.FragmentAllTodoBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllTodoFragment : Fragment(R.layout.fragment_all_todo), AddEditTask, SubTaskComplete {

    private var _binding: FragmentAllTodoBinding? = null
    private val binding get() = _binding!!
    private val allTodoViewModel by viewModels<AllTodoViewModel>()
    private lateinit var allTodoAdapter: AllTodoAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllTodoBinding.bind(view)

        allTodoAdapter = AllTodoAdapter(this, this)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.isDraggable = false

        binding.apply {
            addTodoButton.setOnClickListener {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    showBottomSheet()
                } else {
                    val todoItem = TodoItem(title = todoTitle.text.trim().toString())
                    allTodoViewModel.addTodo(todoItem)
                }
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
            allTodoAdapter.submitList(it)
        }

        // Using BackPressed in Fragment
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                        binding.apply {
                            hideBottomSheet()
                        }
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

    override fun checkboxClicked(todoItem: TodoItem, position: Int, isChecked: Boolean) {
        allTodoViewModel.onTaskCheckedChanged(todoItem, position, isChecked)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}