package com.AyoubTarik.PatientArzt.doctor_section.patients.info

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
import com.squareup.okhttp.*
import com.AyoubTarik.PatientArzt.R
import com.AyoubTarik.PatientArzt.databinding.CalendarbookingFragmentBinding
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.Calendar
import kotlin.collections.HashMap


class BookingDoctor : Fragment(R.layout.calendarbooking_fragment) {
    private var _binding: CalendarbookingFragmentBinding? = null
    private val binding get() = _binding
    private var date: String? = null
    private var db: FirebaseFirestore? = null
    private var id: String? = null
    private var visit: String? = null
    private var complain: String? = null
    private var fullname: String? = null
    private var token: String? = null
    private var turn: String? = null
    private lateinit var estimatedTime: String
    private var book = false
    private var today: String? = null
    private var open: String? = null
    private var waiting: Int? = null
    private var holiDay: String? = null
    private var maxPatiens: Int? = null
    private var isFull: Boolean? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            id = bundle.getString("id")
            complain = bundle.getString("complain")
            fullname = bundle.getString("name")
            token = bundle.getString("token")
            GetToday()
            GetSettings()
        }
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

            IsDayFull(date!!)


        }
        binding?.confirm?.setOnClickListener {
            book = true
            binding?.confirm?.text = ""
            binding?.loading?.visibility = View.VISIBLE
            binding?.confirm?.alpha = .5F
            binding?.confirm?.isClickable = false
            try {
                Book()


            } catch (E: Exception) {
                println(E.message)
                binding?.confirm!!.text = "Confirm"
                binding?.loading!!.visibility = View.INVISIBLE
                binding?.confirm!!.alpha = 1F
                binding?.confirm!!.isClickable = true
            }


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Book() {
        db = FirebaseFirestore.getInstance()
        var note: String = binding?.note?.text.toString()
        var data = HashMap<String, Any>()
        data["date"] = date!!
        data["note"] = note
        data["bookingtime"] = FieldValue.serverTimestamp()
        data["id"] = id!!
        data["Booked_by"] = "Clinic"
        data["status"] = "Pending"
        data["complain"] = complain!!
        data["name"] = fullname!!
        data["booking_date"] = today!!

        db?.collection("visits")?.add(data)?.addOnCompleteListener {
            if (it.isSuccessful) {
                visit = it.result?.id
                VisitTurn()
                return@addOnCompleteListener

            }

        }


    }


    private fun Addvisit() {
        val updates = HashMap<String, Any>()
        updates["visit_id"] = visit!!
        updates["IsVisit"] = true
        updates["next_visit"] = date!!
        updates["turn"] = turn!!


        db!!.collection("patiens_info").document(id!!)
            .update(updates)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    SendBookingNotification()
                    println("Done")
                    db!!.collection("visits").document(visit!!).update("visit", visit)
                        .addOnSuccessListener {
                            findNavController().navigateUp()

                        }

                }


            }
    }

    private fun SendBookingNotification() {
        val servertoken: String =
            "key=AAAA8wp6gvE:APA91bGkhZC4jPFfmqTiExrbYIi8-hdgqq1W9cC7EC0CMGRUM37o0a36nez9cQI4LKgNQ2Pc1VrBhL9Y04koZsZ97JCXnrctVYmYiI3LUYWZ2egnLHoxgnOGVn2wJmv_Xv0VU2ynnvGN"
        val jsonObject: JSONObject = JSONObject()
        try {
            jsonObject.put("to", token)
            val notification: JSONObject = JSONObject()

            notification.put("title", "Dr Ayoub's clinc")
            notification.put("body", "We have booked you an appointment on $date")
            jsonObject.put("notification", notification)
        } catch (e: JSONException) {
            println(e.message)
        }

        val mediaType: MediaType = MediaType.parse("application/json")
        val client: OkHttpClient = OkHttpClient()
        val body: RequestBody = RequestBody.create(mediaType, jsonObject.toString())
        val request: Request? =
            Request.Builder().url("https://fcm.googleapis.com/fcm/send").method("POST", body)
                .addHeader("Authorization", servertoken)
                .addHeader("Content-Type", "application/json").build()
        Thread(Runnable {
            val response: Response = client.newCall(request).execute()
            println("response=========================================${response.message()}")
        }).start()


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
                    var workTime: LocalTime = if (visitDate == dateNow) {
                        var timeNow = sdf.format(LocalTime.now())
                        LocalTime.parse(timeNow.toString(), sdf)

                    } else {
                        LocalTime.parse(open, sdf)
                    }

                    val waitingTime = waiting!! * turn!!.toInt()
                    println(waitingTime)
                    estimatedTime = sdf.format(workTime.plusMinutes(waitingTime.toLong()))
                    println(estimatedTime)
                    binding?.time!!.text = estimatedTime
                }

                if (book) {
                    Addvisit()
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun IsValidBooking(date: String): Boolean {
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
            val differnt = ChronoUnit.DAYS.between(dateNow, visitDate)
            "Your visit will be after $differnt days"
        }
        return result

    }

    private fun GetToday() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        today = "$day-$month-$year"
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
    private fun IsHoliday(): Boolean {
        val holiday: Boolean
        val cal = Calendar.getInstance()
        val locale = Locale.US
        val sdf = SimpleDateFormat("d-m-yyyy", locale)
        val calDate: Date = sdf.parse(date)
        cal.time = calDate
        val dayNumber: Int = cal.get(Calendar.DAY_OF_WEEK)
        println(dayNumber)
        val daysList = listOf<String>(
            "SUNDAY",
            "MONDAY",
            "TUESDAY",
            "WEDNESDAY",
            "THURSDAY",
            "FRIDAY",
            "SATURDAY"
        )
        val dayName = daysList[dayNumber - 1]
        println(dayName)

        holiday = dayName == holiDay

        return holiday


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun IsDayFull(date: String) {
        var currentPatients: Int? = null
        db = FirebaseFirestore.getInstance()
        db!!.collection("visits").whereEqualTo("date", date).whereEqualTo("status", "Pending").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentPatients = it.result?.size()
                    println(currentPatients)
                    println(maxPatiens)
                    isFull = maxPatiens!! <= currentPatients!!
                }
                if (isFull!!) {

                    BookingUnavaiable(
                        "We are sorry, we have reached maximum patients for these day, check another date"
                    )
                } else {
                    if (IsValidBooking(date)) {
                        if (IsHoliday()) {
                            BookingUnavaiable("We are sorry it is our holiday")

                        } else {
                            BookingAvailable()
                        }

                    } else {
                        BookingUnavaiable("the visit should be in the future")


                    }

                }
            }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun BookingAvailable() {
        binding?.nextvisit?.setTextColor(Color.GREEN)
        binding?.nextvisit?.text = AfterDays(date!!)
        binding?.card?.visibility = View.VISIBLE
        binding?.confrimC?.visibility = View.VISIBLE
        binding?.confirm?.visibility = View.VISIBLE
        binding?.note?.visibility = View.VISIBLE
        binding?.time?.visibility = View.VISIBLE
        binding?.time2?.visibility = View.VISIBLE
        binding?.textView9?.visibility = View.VISIBLE
        VisitTurn()
    }

    private fun BookingUnavaiable(errorMessage: String) {
        binding?.nextvisit?.setTextColor(Color.RED)
        binding?.card?.visibility = View.VISIBLE
        binding?.confirm?.visibility = View.GONE
        binding?.note?.visibility = View.GONE
        binding?.time?.visibility = View.GONE
        binding?.time2?.visibility = View.GONE
        binding?.textView9?.visibility = View.GONE
        binding?.nextvisit?.text = errorMessage


    }


}