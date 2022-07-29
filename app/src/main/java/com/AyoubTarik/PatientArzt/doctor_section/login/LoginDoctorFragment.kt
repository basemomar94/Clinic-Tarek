package com.AyoubTarik.PatientArzt.doctor_section.login

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.AyoubTarik.PatientArzt.R
import java.util.*

class LoginDoctorFragment : Fragment(R.layout.fragment_login_doctort) {
    lateinit var db: FirebaseFirestore
    lateinit var mail: EditText
    lateinit var password: EditText
    lateinit var loginBu: Button
    lateinit var loading: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLogin()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigation()
        mail = requireActivity().findViewById(R.id.mail_log)
        password = requireActivity().findViewById(R.id.password_log)
        loginBu = requireActivity().findViewById(R.id.confirmBu)
        loading = requireActivity().findViewById(R.id.progressLogin)
        loginBu.setOnClickListener {
            if (mail.text.isNotEmpty() && password.text.isNotEmpty()) {
                checkDoctor()
            }

        }
    }

    private fun checkDoctor() {
        showLoading()
        db = FirebaseFirestore.getInstance()
        db.collection("doctors")
            .document(mail?.text.toString().lowercase(Locale.getDefault()).trim())
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Snackbar.make(
                        requireActivity().findViewById(android.R.id.content),
                        "please check your username",
                        Snackbar.LENGTH_LONG
                    ).show()
                    showButton()
                } else {
                    val firebaseKey = value?.getString("key")
                    if (firebaseKey == password.text.toString().lowercase(Locale.getDefault())
                            .trim()
                    ) {
                        saveLogin()
                        goToDashboard()
                    } else {
                        Snackbar.make(
                            requireActivity().findViewById(android.R.id.content),
                            "please check your password",
                            Snackbar.LENGTH_LONG
                        ).show()
                        showButton()

                    }


                }
            }
    }

    private fun goToDashboard() {
        findNavController().navigate(R.id.action_loginFragment_to_home2)
    }

    private fun showLoading() {
        loginBu.visibility = View.GONE
        loading.visibility = View.VISIBLE
    }

    private fun showButton() {
        loginBu.visibility = View.VISIBLE
        loading.visibility = View.GONE
    }

    private fun saveLogin() {
        val pref = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putBoolean("key", true)
        editor.apply()
    } //

    private fun checkLogin() {
        val pref = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE)
        val key = pref.getBoolean("key", false)
        if (key) {
            goToDashboard()
        }

    }

    private fun hideNavigation() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomAppBar).apply {
            visibility = View.GONE
        }
    }

}