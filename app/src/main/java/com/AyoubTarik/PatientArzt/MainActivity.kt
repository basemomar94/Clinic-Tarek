package com.AyoubTarik.PatientArzt

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.AyoubTarik.PatientArzt.databinding.ActivityMainBinding
import com.AyoubTarik.PatientArzt.doctor_section.DoctorActivity
import com.AyoubTarik.PatientArzt.patient_section.PatientActivity

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.apply {
            doctorCard.setOnClickListener {
                gotTo(DoctorActivity())
            }
            patientCard.setOnClickListener {
                gotTo(PatientActivity())
            }
        }
    }


    private fun gotTo(destination: Activity) {
        val intent = Intent(this, destination::class.java)
        startActivity(intent)
        finish()
    }


}