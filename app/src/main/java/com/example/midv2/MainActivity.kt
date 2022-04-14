package com.example.midv2

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.midv2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        Set up navigation bar between home fragment and dashboard fragment.
         */
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // make bar not visible in countdown and result fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_notifications || destination.id == R.id.countdownFragment) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }

        // Passing each menu ID as a set of Ids because each menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.countdownFragment, R.id.navigation_notifications, R.id.navigation_dashboard
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        /*
        Get user permission for getting activity usage.
        If not yet granted, settings app will be opened automatically; else, nothing will happen.
        ---
        Bug: Settings will be opened whether permission is granted or not.
        Uncomment the following paragraph to get permission when needed.
         */
        /*
        val requestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
            }
        }
        requestLauncher.launch(Manifest.permission.PACKAGE_USAGE_STATS)
         */
    }

}