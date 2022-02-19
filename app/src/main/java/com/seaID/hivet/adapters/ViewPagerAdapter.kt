package com.seaID.hivet.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.seaID.hivet.RiwayatFragment
import com.seaID.hivet.RiwayatFragment2

class ViewPagerAdapter(var context: Context, fm: FragmentManager, var totalTabs: Int ) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        return when(position){
            0 -> {
                RiwayatFragment()
            }

            1 -> {
                RiwayatFragment2()
            }

            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }





}