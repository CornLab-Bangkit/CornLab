package com.example.cornlab.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.cornlab.R
import com.example.cornlab.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupAction()
    }

    private fun setupView() {
        activity?.findViewById<CoordinatorLayout>(R.id.setBottomNav)?.visibility = View.GONE
        val navController = findNavController()
        val toolbar = binding.toolbar

        NavigationUI.setupWithNavController(toolbar, navController)
        toolbar.title = ""
    }

    private fun setupAction() {
        binding.tvInfo.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_info)
        }
        binding.tvFAQ.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_faq)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        activity?.findViewById<CoordinatorLayout>(R.id.setBottomNav)?.visibility = View.VISIBLE
    }
}