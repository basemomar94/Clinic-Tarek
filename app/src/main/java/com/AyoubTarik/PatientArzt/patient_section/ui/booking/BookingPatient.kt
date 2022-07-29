package com.AyoubTarik.PatientArzt.patient_section.ui.booking

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.AyoubTarik.PatientArzt.R
import com.AyoubTarik.PatientArzt.databinding.CalendarbookingFragmentBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.calendarbooking_fragment.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.HashMap

class BookingPatient : Fragment(R.layout.calendarbooking_fragment) {
    private var _binding: CalendarbookingFragmentBinding? = null
    private val binding get() = _binding
    private var date: String? = null
    private var db: FirebaseFirestore? = null
    private var id: String? = null
    private var visit: String? = null
    private var name: String? = null
    private var complain: String? = null
    private var token: String? = null
    private var turn: String? = null
    private lateinit var estimatedTime: String
    private var book = false
    private var open: String? = null
    private var waiting: Int? = null
    private var holiDay: String? = null
    private var maxPatiens: Int? = null
    private var isFull: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = activity?.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        id = sharedPreferences?.getString("id", "")
        name = sharedPreferences?.getString("name", "")
        complain = sharedPreferences?.getString("complain", "")
        token = sharedPreferences?.getString("token", token)
        GetSettings()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CalendarbookingFragmentBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding?.calendarView?.setOnDateChangeListener { calendarView, year, month, dayofMonth ->
            val realmonth: Int = month + 1
            date = "$dayofMonth-$realmonth-$year"
            if (ValidBooking(date!!)) {
                BookingAvailable()

            } else {
                BookingUnavaiable("the visit should be in the future")
            }

        }
        binding?.confirm?.setOnClickListener {
            book = true
            binding!!.confirm.text = ""
            binding!!.loading.visibility = View.VISIBLE
            binding!!.confirm.isClickable = false
            confirm.alpha = .5F
            Book()
            try {
                Book()
                println("Workingggg")

            } catch (E: Exception) {
                println(E.message)
                binding!!.confirm.text = "Confirm"
                binding!!.loading.visibility = View.INVISIBLE
                binding!!.confirm.isClickable = true
                binding!!.confirm.alpha = 1F

            }


        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.supportFragmentManager?.isDestroyed

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Book() {
        db = FirebaseFirestore.getInstance()
        var note: String = binding?.note?.text.toString()
        var data = HashMap<String, Any>()
        data.put("date", date!!)
        data.put("note", note)
        data.put("id", id!!)
        data.put("status", "Pending")
        data.put("Booked_by", "You")
        data.put("name", name!!)
        data.put("complain", complain!!)
        data["visit_time"] = estimatedTime
        data.put("bookingtime", FieldValue.serverTimestamp())
        db?.collection("visits")?.add(data)?.addOnCompleteListener {
            if (it.isSuccessful) {
                visit = it.result?.id

                VisitTurn()
            }

        }

    }


    private fun HasVisit() {
        db = FirebaseFirestore.getInstance()
        var update = HashMap<String, Any>()
        update["next_visit"] = date!!
        update["IsVisit"] = true
        update["visit_id"] = visit!!
        update["turn"] = turn!!
        update["visit_time"] = estimatedTime!!
        db!!.collection("patiens_info").document(id!!).update(update)
            .addOnCompleteListener {
                if (it.isSuccessful) {

                    findNavController().navigate(R.id.action_global_home)
                    //db!!.collection("visits").document(visit!!).update("visit", visit)
                    //findNavController().navigate(R.id.action_bookingPatient_to_home)
                    // BookingUnavaiable("you already have an appointment")
                    //Toast.makeText(applicationContext,"Termin wurde erfolgreich gebucht",Toast.LENGTH_SHORT).show()
                    //  startActivity(Intent(this, MainActivity::class.java))
                    // findNavController().navigate(R.id.action_loginFragment_to_home2)

                    //println("nadddddddddiiiiiiiiiiiiiiiiiiiiiiiiii kanadi")
                    //    var fr = getFragmentManager()?.beginTransaction()
                    //  fr?.replace(R.layout.activity_patient, this)
                    // fr?.commit()

                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun VisitTurn() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("visits").whereEqualTo("date", date).whereEqualTo("status", "Pending").get()
            .addOnCompleteListener {
                turn = it.result?.size().toString()
                val locale = Locale.ENGLISH
                if (!book) {
                    val sdf = DateTimeFormatter.ofPattern("hh:mm a", locale)
                    //Booking on the same day problem
                    val dateNow = LocalDate.now()
                    val locale = Locale.US
                    val sdate = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
                    val visitDate = LocalDate.parse(date, sdate)
                    val workTime: LocalTime = if (visitDate == dateNow) {
                        val timeNow = sdf.format(LocalTime.now())
                        LocalTime.parse(timeNow.toString(), sdf)

                    } else {
                        LocalTime.parse(open, sdf)
                    }
                    val waitingTime = waiting!! * turn!!.toInt()
                    estimatedTime = sdf.format(workTime.plusMinutes(waitingTime.toLong()))
                    binding?.time!!.text = estimatedTime
                }

                if (book) {
                    HasVisit()
                }


            }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun ValidBooking(date: String): Boolean {
        val locale = Locale.ENGLISH
        val sdf = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
        val visitDate: LocalDate = LocalDate.parse(date, sdf)
        val dateNow = LocalDate.now()
        return visitDate > dateNow
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun AfterDays(date: String): String {
        val result: String
        val dateNow = LocalDate.now()
        val locale = Locale.US
        val sdf = DateTimeFormatter.ofPattern("d-M-yyyy", locale)
        val visitDate = LocalDate.parse(date, sdf)
        result = if (visitDate == dateNow) {
            "Your visit will be today"
        } else {
            var differnt = ChronoUnit.DAYS.between(dateNow, visitDate)
            "Your visit will be after $differnt days"
        }
        return result

    }

    private fun GetSettings() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("settings").document("settings").addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                open = value?.getString("open")
                waiting = value!!.getString("average")?.toInt()
                holiDay = value.getString("holiday")?.trim()
                maxPatiens = value.getString("max")?.trim()?.toInt()


            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun BookingAvailable() {
        binding?.nextvisit?.setTextColor(Color.GREEN)
        binding?.nextvisit?.text = AfterDays(date!!)
        binding?.card?.visibility = View.VISIBLE
        binding?.confrimC?.visibility = View.VISIBLE
        binding?.note?.visibility = View.VISIBLE
        binding?.time?.visibility = View.VISIBLE
        binding?.time2?.visibility = View.VISIBLE
        binding?.textView9?.visibility = View.VISIBLE
        VisitTurn()
    }

    private fun BookingUnavaiable(errorMessage: String) {
        binding?.nextvisit?.setTextColor(Color.RED)
        binding?.card?.visibility = View.VISIBLE
        binding?.confrimC?.visibility = View.GONE
        binding?.note?.visibility = View.GONE
        binding?.time?.visibility = View.GONE
        binding?.time2?.visibility = View.GONE
        binding?.textView9?.visibility = View.GONE
        binding?.nextvisit?.text = errorMessage


    }


}