package com.AyoubTarik.PatientArzt.doctor_section.schedule

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.AyoubTarik.PatientArzt.doctor_section.schedule.history.HistoryOfvisits
import com.AyoubTarik.PatientArzt.doctor_section.schedule.history.today_visitors.Today_Visitor

open class PageViewerAdapter (fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                Today_Visitor()
            }
            else -> {
                HistoryOfvisits()
            }
        }
    }
}