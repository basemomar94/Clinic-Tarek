package com.AyoubTarik.PatientArzt.patient_section

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.AyoubTarik.PatientArzt.R

class PatientActivity : AppCompatActivity() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

        val bottomnaviagation = findViewById<BottomNavigationView>(R.id.patient_bottom_bar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_patient) as NavHostFragment
        val navController = navHostFragment.navController
        bottomnaviagation.setupWithNavController(navController)
    }


    fun isSigned() = auth.currentUser?.uid.isNullOrEmpty()
}
