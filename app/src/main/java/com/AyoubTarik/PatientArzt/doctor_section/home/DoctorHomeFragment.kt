package com.AyoubTarik.PatientArzt.doctor_section.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bassem.clinicadminapp.schedule.history.VisitsAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.AyoubTarik.PatientArzt.MainActivity
import com.AyoubTarik.PatientArzt.R
import com.AyoubTarik.PatientArzt.databinding.HomeDoctorFragmentBinding
import com.AyoubTarik.PatientArzt.patient_section.ui.booking.Visits
import java.util.*
import kotlin.collections.ArrayList

class DoctorHomeFragment() : Fragment(com.AyoubTarik.PatientArzt.R.layout.home_doctor_fragment), VisitsAdapter.Myclicklisener {
    private var _binding: HomeDoctorFragmentBinding? = null
    private val binding get() = _binding
    private var db: FirebaseFirestore? = null
    private var today: String? = null
    lateinit var visitsArrayList: ArrayList<Visits>
    private var adapter: VisitsAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        visitsArrayList = arrayListOf()

        GetToday()

    }

    override fun onCreateView( // dokumuntieren override bitte !!
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = HomeDoctorFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showNavigation()
        GetNewPatient()
        GetBookedToday()

        binding?.bookedCard?.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_schedule_Container)
        }

        binding?.cancelCard?.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_schedule_Container)
        }

        binding?.completeCard?.setOnClickListener {
            findNavController().navigate(R.id.action_home2_to_schedule_Container)
        }

        binding?.button2?.setOnClickListener {
            val i = Intent(requireActivity(), MainActivity::class.java)
            startActivity(i)
            requireActivity().finish()
            removeLogin()
        }
    }

    private fun GetToday() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        today = "$day-$month-$year"
        println("$today============today")
    }

    fun Filter() {
        val AllList: ArrayList<Visits> = arrayListOf()
        val cancelList: ArrayList<Visits> = arrayListOf()
        val completList: ArrayList<Visits> = arrayListOf()
        Thread(Runnable {
            for (visit: Visits in visitsArrayList) {
                val status = visit.status

                if (status == "completed") {
                    AllList.add(visit)
                }
                if (status == "cancelled by clinic" || status == "cancelled by You") {
                    cancelList.add(visit)
                }
                if (status == "completed") {
                    completList.add(visit)
                }
            }
            activity?.runOnUiThread {

                binding?.cancelHome?.text = cancelList.size.toString()
                binding?.doneHome?.text = completList.size.toString()
                visitsArrayList.clear()
            }
        }).start()
    }

    private fun GetBookedToday() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("visits").whereEqualTo("booking_date", today)
            .whereEqualTo("status", "Pending").get().addOnCompleteListener {
                if (it.isSuccessful) {
                    binding?.bookedToday?.text = it.result?.size().toString()
                    binding?.shimmer?.apply {
                        visibility = View.GONE
                        stopShimmer()
                    }
                    binding?.cards?.visibility = View.VISIBLE
                }
            }
    }

    private fun GetNewPatient() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("patiens_info").whereEqualTo("registered_date", today).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result?.size() != null) {
                        binding?.newPatients!!.text = it.result?.size().toString()
                    }
                }
            }
    }

    override fun onClick(position: Int, vist: Visits) { // dokummentieren, wenn den sceen erstes mal angeklickt wird, machste mir was, registrier
    }

    private fun showNavigation() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomAppBar).apply {
            visibility = View.VISIBLE
        }
    }

    private fun removeLogin() { // diese machen nix lassen ;) login daten werden gespeichert // getsharPreferences
        val pref = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("key", false)
        editor.apply()
    }
}