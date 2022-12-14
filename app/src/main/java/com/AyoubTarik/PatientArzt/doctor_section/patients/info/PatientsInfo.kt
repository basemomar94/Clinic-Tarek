package com.AyoubTarik.PatientArzt.doctor_section.patients.info
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.okhttp.*
import com.AyoubTarik.PatientArzt.R
import com.AyoubTarik.PatientArzt.databinding.PatientinfoFragmentBinding
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar


class PatientsInfo() : Fragment(R.layout.patientinfo_fragment) {
    lateinit var id: String
    lateinit var db: FirebaseFirestore
    private var _binding: PatientinfoFragmentBinding? = null
    val binding get() = _binding
    var mobile: String? = null
    private var fullname: String? = null
    private var handler: String? = null
    var visit_ID: String? = null
    var complain: String? = null
    var token: String? = null
    var today: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GetToday()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PatientinfoFragmentBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            id = bundle.getString("id", "null")
            println("$id=========id")
            GettingData()

        }


        binding?.bookBu?.setOnClickListener {
            GotoBooking()
        }
        binding?.cancel?.setOnClickListener {
            val builder= AlertDialog.Builder(context)
            builder.setMessage("Are you sure to cancel this booking")
            builder.setPositiveButton("yes"){ builder, _ ->Cancel()}
            builder.setNegativeButton("No"){ builder, _ ->builder.dismiss()}
            builder.setTitle("Cancel booking")
            builder.show()

        }

    }

    fun GettingData() {
        println("data")
        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id).addSnapshotListener { value, error ->

            if (error != null) {
                println("Firebase error ${error.message}")
            } else {
                token = value?.getString("token")
                fullname = value?.getString("fullname")

                binding!!.fullnameInfo.text = fullname

                binding!!.ageInfo.text = value?.getDouble("age")?.toInt().toString()
                binding!!.jobInfo.text = value?.getString("insurance")
                complain = value?.getString("complain")
                binding!!.complainInfo.text = complain
                binding!!.mailInfo.text = value?.getString("mail")
                mobile = value?.getString("phone")

                binding!!.notesInfo.text = value?.getString("note")
                val sex = value?.getString("sex")
                binding!!.sexInfo.text = sex
                handler = if (sex.equals("male")) {
                    "Mr"
                } else {
                    "Miss"
                }
                if (value?.getBoolean("IsVisit") == true) {
                    visit_ID = value.getString("visit_id")
                    binding?.next?.text = value.getString("next_visit")
                    binding?.bookBu?.visibility = View.GONE
                    binding?.cancel?.visibility = View.VISIBLE
                    binding?.nextlinear?.visibility = View.VISIBLE
                } else {
                    binding?.bookBu?.visibility = View.VISIBLE
                    binding?.cancel?.visibility = View.GONE
                    binding?.nextlinear?.visibility = View.GONE


                }


            }
        }
    }




    private fun GotoPrescription() {
        val bundle = Bundle()
        bundle.putString("id", id)
        bundle.putString("token", token)
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_doctor)
        navController.navigate(R.id.action_patientsInfo_to_prescription2, bundle)
    }

    private fun GotoBooking() {
        val bundle = Bundle()
        bundle.putString("id", id)
        bundle.putString("complain", complain)
        bundle.putString("name", fullname)
        bundle.putString("token", token)
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_doctor)
        val navBuilder = NavOptions.Builder()
        val navOptions: NavOptions = navBuilder.setLaunchSingleTop(true).build()
        navController.navigate(R.id.action_patientsInfo_to_booking2, bundle, navOptions)

    }

    private fun Cancel() {
        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id).update("IsVisit", false)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    binding?.bookBu?.visibility = View.VISIBLE
                    binding?.cancel?.visibility = View.GONE
                    binding?.nextlinear?.visibility = View.GONE
                    CancelOnVisit()
                }

            }
    }

    private fun CancelOnVisit() {
        db = FirebaseFirestore.getInstance()
        var cancelHashMap = HashMap<String, Any>()
        cancelHashMap["status"] = "cancelled by clinic"
        cancelHashMap["date"] = today!!
        db.collection("visits").document(visit_ID!!).update(cancelHashMap)
            .addOnCompleteListener {
                SendCancelNotification()

            }

    }

    private fun GetToday() {
        val calendar: Calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        today = "$day-$month-$year"
    }

    private fun SendCancelNotification() {
        val servertoken: String =
            "key=AAAA8wp6gvE:APA91bGkhZC4jPFfmqTiExrbYIi8-hdgqq1W9cC7EC0CMGRUM37o0a36nez9cQI4LKgNQ2Pc1VrBhL9Y04koZsZ97JCXnrctVYmYiI3LUYWZ2egnLHoxgnOGVn2wJmv_Xv0VU2ynnvGN"
        val jsonObject: JSONObject = JSONObject()
        try {
            jsonObject.put("to", token)
            val notification: JSONObject = JSONObject()
            notification.put("title", "Dr Ayoub's clinc")
            notification.put("body", "Your appointment has been canceled")
            jsonObject.put("notification", notification)
        } catch (e: JSONException) {
            println(e.message)
        }

        val mediaType: MediaType = MediaType.parse("application/json")
        val client: OkHttpClient = OkHttpClient()
        var body: RequestBody = RequestBody.create(mediaType, jsonObject.toString())
        val request: Request? =
            Request.Builder().url("https://fcm.googleapis.com/fcm/send").method("POST", body)
                .addHeader("Authorization", servertoken)
                .addHeader("Content-Type", "application/json").build()
        Thread(Runnable {
            val response: Response = client.newCall(request).execute()
            println("response=========================================${response.message()}")
        }).start()


    }




}