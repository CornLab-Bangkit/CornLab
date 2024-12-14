package com.example.cornlab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.cornlab.R
import com.example.cornlab.adapter.TabAdapter
import com.example.cornlab.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val tabTitles = arrayListOf(
        "Corn",
        "Husk"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        val toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.home_menu)
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    view?.findNavController()?.navigate(R.id.action_home_to_settings)
                    true
                }
                else -> false
            }
        }
        setUpTabLayoutWithPager()
        return binding.root
    }

    private fun setUpTabLayoutWithPager() {
        binding.viewPager.adapter = TabAdapter (this)
        TabLayoutMediator(binding.tabs, binding.viewPager){ tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

}