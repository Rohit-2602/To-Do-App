package com.example.to_doapp.ui.addtodo

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.to_doapp.R
import com.example.to_doapp.databinding.FragmentAddTodoBinding
import com.example.to_doapp.databinding.FragmentAllTodoBinding

class AddTodoFragment : Fragment(R.layout.fragment_add_todo) {

    private var _binding: FragmentAddTodoBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddTodoBinding.bind(view)



    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}