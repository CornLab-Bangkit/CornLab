package com.example.cornlab.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.cornlab.ui.home.corn.CornFragment
import com.example.cornlab.ui.home.husk.HuskFragment

class TabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CornFragment()
            1 -> HuskFragment()
            else -> CornFragment()
        }
    }
}
