package com.example.to_doapp.ui.alltodos

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.to_doapp.R
import com.example.to_doapp.databinding.FragmentAllTodoBinding

class AllTodoFragment : Fragment(R.layout.fragment_all_todo) {

    private var _binding: FragmentAllTodoBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllTodoBinding.bind(view)



    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}