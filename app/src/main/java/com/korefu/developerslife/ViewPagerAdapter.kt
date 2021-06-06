package com.korefu.developerslife

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt("index", position)
        val fragment = EntryFragment()
        fragment.arguments = bundle
        return fragment
    }

}