package com.example.alinadiplom.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.alinadiplom.database.Analysis
import com.example.alinadiplom.kvector.KVectorFragment
import com.example.alinadiplom.kvector.KVectorGraphFragment
import com.example.alinadiplom.pojo.MyEntry

class FragmentsAdapter(
    activity: AppCompatActivity,
    var myEntries1: ArrayList<MyEntry>,
    var myEntries2: ArrayList<MyEntry>,
    private val analyzes: ArrayList<Analysis>
    ) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            KVectorFragment.instance(myEntries1, analyzes)
        } else {
            KVectorGraphFragment.instance(myEntries2, analyzes)
        }
    }
}