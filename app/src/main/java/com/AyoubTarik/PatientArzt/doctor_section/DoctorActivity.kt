package com.AyoubTarik.PatientArzt.doctor_section

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.AyoubTarik.PatientArzt.R

class DoctorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomAppBar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_doctor) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNavigationView.setupWithNavController(navController)
    }


}