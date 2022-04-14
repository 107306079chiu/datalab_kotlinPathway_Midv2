package com.example.midv2.ui.countdown

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.midv2.Constant
import com.example.midv2.MyService
import com.example.midv2.R
import com.example.midv2.TimerApplication
import com.example.midv2.databinding.FragmentCountdownBinding
import com.example.midv2.ui.home.HomeViewModel
import com.example.midv2.ui.home.HomeViewModelFactory

class CountdownFragment : Fragment() {

    private var _binding: FragmentCountdownBinding? = null
    private val binding get() = _binding!!

    private val model: HomeViewModel by activityViewModels {
        HomeViewModelFactory(
            (activity?.application as TimerApplication).database.historyDao()
        )
    }

    private lateinit var serviceIntent: Intent

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCountdownBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // start service immediately after this fragment is created and let room know the goal countdown time
        activity?.startService(Intent(requireContext(), MyService::class.java)
            .putExtra(Constant.TIME_EXTRA, model.getLatestGoal().toLong()))
        // Toast.makeText(requireContext(), "hi", Toast.LENGTH_SHORT).show()

        // built to receive broadcast from service
        serviceIntent = Intent(requireContext(), MyService::class.java)
        activity?.registerReceiver(updateTime, IntentFilter(Constant.TIME_UPDATE))

        // set cancel (give up) button
        binding.button2.setOnClickListener { view->
            stopService()
            navToNextFragment()
            // did not change the isSuccess state in room here
            // every data is default failed in room
        }

        return root
    }

    private fun navToNextFragment() {
        view?.findNavController()?.navigate(R.id.navigation_notifications)
    }

    private fun stopService() {
        val intentStop = Intent(requireContext(), MyService::class.java)
        intentStop.action = Constant.ACTION_STOP
        activity?.startService(intentStop)
    }

    /*
    Called whenever this fragment receives broadcast from the service (once every second in this case).
    In every call, this fun update the ui (countdown) and control the nav when timer fail / succeed.
     */
    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // update ui time left
            if (_binding!=null) {
                binding.textTimer.text = intent?.getStringExtra(Constant.TIME_EXTRA)
            }

            // failed due to leaving the app over 10 sec
            if ((intent?.getBooleanExtra(Constant.UI_10SEC, true))==false) {
                navToNextFragment()
            }

            // succeed
            if ((intent?.getBooleanExtra(Constant.TIME_FINISH, false))==true) {
                model.updateSuccess()
                /*
                Bug: In the case when user leave app with <10sec is left and the timer end with success state,
                the service is stop (line 103), but did not nav to next fragment (line 102).
                Temp solu: change button text from 'cancel' into 'ok' and click to nav.
                 */
                if (_binding!=null) {
                    binding.button2.text = "OK"
                }
                navToNextFragment()
                stopService()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}