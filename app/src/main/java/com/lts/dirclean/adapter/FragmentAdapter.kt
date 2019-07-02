package com.lts.dirclean.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class FragmentAdapter(fm: FragmentManager,fragments : ArrayList<Fragment> ,titles : ArrayList<String>) : FragmentStatePagerAdapter(fm) {

    val fragments : ArrayList<Fragment>
    val titls : ArrayList<String>

    init {
        this.fragments = fragments
        this.titls = titles
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titls.get(position)
    }

    override fun getItem(position: Int): Fragment {
        return fragments.get(position)
    }

    override fun getCount(): Int {

        return fragments.size
    }
}