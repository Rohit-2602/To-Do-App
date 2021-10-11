package com.example.to_doapp.ui.addtask

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.to_doapp.R
import com.example.to_doapp.databinding.FragmentAddTaskBinding
import com.example.to_doapp.databinding.FragmentAllTodoBinding

class AddTaskFragment : Fragment(R.layout.fragment_add_task) {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTaskBinding.bind(view)



    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}