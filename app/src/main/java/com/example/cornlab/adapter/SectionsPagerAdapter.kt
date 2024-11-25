package com.example.cornlab.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cornlab.ui.home.corn.CornFragment
import com.example.cornlab.ui.home.husk.HuskFragment

class SectionsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2 // Corn and Husk

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CornFragment()
            1 -> HuskFragment()
            else -> throw IllegalStateException("Invalid position: $position")
        }
    }
}
