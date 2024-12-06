package com.example.cornlab.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cornlab.R
import com.example.cornlab.databinding.FragmentInfoBinding

class InfoFragment : Fragment(R.layout.fragment_info) {

    private lateinit var binding : FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInfoBinding.inflate(inflater, container, false)

        val toolbar = binding.toolbar

        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        activity?.findViewById<CoordinatorLayout>(R.id.setBottomNav)?.visibility = View.GONE

        return binding.root
    }

    override fun onDestroyView(){
        super.onDestroyView()

        activity?.findViewById<CoordinatorLayout>(R.id.setBottomNav)?.visibility = View.VISIBLE

    }
}