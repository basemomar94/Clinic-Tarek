package com.AyoubTarik.PatientArzt.patient_section.ui.booking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.AyoubTarik.PatientArzt.R


class BookingDashboard : Fragment(R.layout.booking_dashboard) {

    var tabtitle = arrayOf("Upcoming", "History")
    private lateinit var pager2: ViewPager2
    private lateinit var tabl:TabLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         pager2 = view.findViewById(R.id.ViewPager)
         tabl = view.findViewById(R.id.tablayout)

        pager2.adapter= PageViewerAdapter(requireActivity().supportFragmentManager,requireActivity().lifecycle)

        TabLayoutMediator(tabl, pager2) {

                tab, position ->
            tab.text = tabtitle[position]

        }.attach()
    }

}





