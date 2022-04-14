package com.example.midv2.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.midv2.HistoryAdapter
import com.example.midv2.TimerApplication
import com.example.midv2.databinding.FragmentDashboardBinding
import com.example.midv2.ui.home.HomeViewModel
import com.example.midv2.ui.home.HomeViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val model: HomeViewModel by activityViewModels {
        HomeViewModelFactory(
            (activity?.application as TimerApplication).database.historyDao()
        )
    }

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // displaying all history data
        recyclerView = binding.recyclerHistory
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val historyAdapter = HistoryAdapter()
        recyclerView.adapter = historyAdapter

        // retrieving data from db needs not to be executed in the ui thread
        lifecycle.coroutineScope.launch {
            model.fullHistory().collect { historyAdapter.submitList(it) }
        }
        // old way to do so is through the following code
        /*
        GlobalScope.launch(Dispatchers.IO) {
            historyAdapter.submitList(model.fullHistory())
        }
         */
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}