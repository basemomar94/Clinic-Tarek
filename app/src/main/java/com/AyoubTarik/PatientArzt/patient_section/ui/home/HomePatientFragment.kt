package com.bassem.clinic_userapp.ui.home

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.AyoubTarik.PatientArzt.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.AyoubTarik.PatientArzt.patient_section.ui.booking.Visits
import com.AyoubTarik.PatientArzt.databinding.HomePatientFragmentBinding
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class HomePatientFragment() : Fragment(R.layout.home_patient_fragment) {
    private var _binding: HomePatientFragmentBinding? = null
    private val binding get() = _binding
    private var db: FirebaseFirestore? = null
    private var name: String? = null
    private var complain: String? = null
    private var today: String? = null
    private var pendingList: ArrayList<Visits>? = null
    private var turn: String? = null
    private var nextDate: String? = null
    private var nextTime: String? = null
    private var waiting: Int? = null
    private var userID: String? = null
    private var auth: FirebaseAuth? = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        GetToday()
        GetSettings()

//
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HomePatientFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)// doku
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visibilityBottomBar(true)
        GettingData()
        binding?.upcomingCard?.setOnClickListener {
            findNavController().navigate(R.id.action_homePatientFragment_to_bookingPatient)

        }
        binding?.bookHome?.setOnClickListener {
            findNavController().navigate(R.id.action_homePatientFragment_to_bookingPatient)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun GettingData() {
        val sharedPreferences = activity?.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        userID = auth?.currentUser?.uid
        db = FirebaseFirestore.getInstance()
        db?.collection("patiens_info")?.document(userID!!)?.addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                if (value?.getString("pre") != null) {
                    binding?.welcome?.visibility = View.GONE
                    //binding?.roshta?.visibility = View.VISIBLE
                   // binding?.preTV?.text = value.getString("pre")
                }
                if (value?.getString("ins") != null) {
                    binding?.welcome?.visibility = View.GONE
                  //  binding?.roshta?.visibility = View.VISIBLE
                  //  binding?.insTV?.text = value.getString("ins")
                }
                val sex = value?.getString("sex")
                if (sex.equals("male")) {
                    binding?.name?.text = "Hello Mr ${value?.getString("fullname")}"

                } else {
                    binding?.name?.text = "Hello Miss ${value?.getString("fullname")}"
                }
                nextDate = value?.getString("next_visit")

                nextTime = value?.getString("visit_time")
                if (value?.getBoolean("IsVisit") == true && IsBookDatePassed(nextDate!!)) {
                    binding?.welcome?.visibility = View.GONE
                    binding?.upcomingCard?.visibility = View.VISIBLE

                    binding?.next?.text = nextDate
                    binding?.timeHome?.text = nextTime
                   // IsClinicOpen()


                }
                name = value?.getString("fullname")
                complain = value?.getString("complain")
                turn = value?.getString("turn")
                //binding?.patientNumber?.text = turn
                val editor = sharedPreferences!!.edit()
                editor.putString("name", name)
                editor.putString("complain", complain)
                editor.apply()

            }
        }

    }

    private fun GetToday() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        today = "$day-$month-$year"
    }




/**
    @RequiresApi(Build.VERSION_CODES.O)
    private fun IsClinicOpen() {
        var open: String? = null
        var close: String? = null
        val locale = Locale.ENGLISH
        var available: Boolean? = null

        val sdf = DateTimeFormatter.ofPattern("hh:mm a", locale)
        val timeNow = LocalTime.now()
        db = FirebaseFirestore.getInstance()
        db?.collection("settings")?.document("settings")?.addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                open = value!!.getString("open")!!
                close = value.getString("close")!!
                println("$close=============close")
                val openTime = LocalTime.parse(open!!.trim(), sdf)
                val closeTime = LocalTime.parse(close!!.trim(), sdf)
                available = timeNow > openTime && timeNow < closeTime

                if (available == true) {


                }


            }
        }


    }
*/
    private fun GetSettings() {
        db = FirebaseFirestore.getInstance()
        db?.collection("settings")?.document("settings")?.addSnapshotListener { value, error ->
            waiting = value?.getString("average")?.toInt() // average doku
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun IsBookDatePassed(visit: String): Boolean {
        val locale = Locale.ENGLISH
        val sdf = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
        val visitDate: LocalDate = LocalDate.parse(visit, sdf)
        val dateNow = LocalDate.now()
        return visitDate >= dateNow
    }



    fun visibilityBottomBar(isvisible: Boolean) {
        requireActivity().findViewById<BottomNavigationView>(R.id.patient_bottom_bar).apply {
            visibility = if (isvisible) {
                View.VISIBLE

            } else {
                View.GONE

            }

        }
    }

}