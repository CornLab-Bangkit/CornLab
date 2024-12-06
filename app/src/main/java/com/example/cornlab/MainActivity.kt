package com.example.cornlab

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.cornlab.databinding.ActivityMainBinding
import com.example.cornlab.ui.analyze.AnalyzeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        // Inflate layout with ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

        if (navHostFragment != null) {
            navController = navHostFragment.navController

            val navView: BottomNavigationView = binding.bottomNavView
            navView.setupWithNavController(navController)
        } else {
            Log.e("MainActivity", "NavHostFragment not found")
        }

        binding.fab.setOnClickListener {
            val analyzeFragment = AnalyzeFragment()
            val transaction : FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, analyzeFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    // Ensure proper navigation behavior for the up button
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
