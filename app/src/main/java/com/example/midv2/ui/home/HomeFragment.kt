package com.example.midv2.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.midv2.R
import com.example.midv2.TimerApplication
import com.example.midv2.database.History
import com.example.midv2.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.time.Clock
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val model: HomeViewModel by activityViewModels {
            HomeViewModelFactory(
                (activity?.application as TimerApplication).database.historyDao()
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.button.setOnClickListener { view->
            model.goalTime.value = binding.editInput.text.toString().toInt()

            /*
            Insert a new countdown data into room db.
            ---
            Bug: Cannot distinguish between am and pm. (huh?)
             */
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())
            val tempHistory = History(
                null,
                currentDate,
                binding.editInput.text.toString().toInt(),
                false, System.currentTimeMillis())
            model.insertNew(tempHistory)

            view.findNavController().navigate(R.id.countdownFragment)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}