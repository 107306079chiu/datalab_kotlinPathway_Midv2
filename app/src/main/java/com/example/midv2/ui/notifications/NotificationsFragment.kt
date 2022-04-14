package com.example.midv2.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.midv2.R
import com.example.midv2.databinding.FragmentNotificationsBinding
import com.example.midv2.ui.home.HomeViewModel

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val model: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.button3.setOnClickListener { view->
            view.findNavController().navigate(R.id.navigation_dashboard)
        }

        if (model.getIsSuccess()) {
            // succeed
            binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_sentiment_very_satisfied_24))
        } else {
            // failed
            binding.imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_sentiment_dissatisfied_24))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}