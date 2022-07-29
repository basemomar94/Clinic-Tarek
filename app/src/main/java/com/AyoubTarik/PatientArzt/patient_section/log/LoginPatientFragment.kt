package com.AyoubTarik.PatientArzt.patient_section.log

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
   import com.AyoubTarik.PatientArzt.R
import com.AyoubTarik.PatientArzt.databinding.FragmentLoginPatientBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_login_doctort.*

class LoginPatientFragment : Fragment(R.layout.fragment_login_patient) {
    var binding: FragmentLoginPatientBinding? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var token: String? = null
    private var id: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser?.uid

        if (currentUser != null) {
            saveId(currentUser)
            gotohome()
            println("==USER========================$currentUser")
        } else {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginPatientBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visibilityBottomBar(false)

        binding?.confirmBu?.setOnClickListener {
            binding?.apply {
                confirmBu.text = ""
                progressLogin.visibility = View.VISIBLE
                progressLogin.isClickable = false
                confirmBu.alpha = .5F
            }
            Signin()
        }

        binding?.signupBu?.setOnClickListener {
            findNavController().navigate(R.id.action_loginPatientFragment_to_signupFragment)
        }

    }

    private fun gotohome() {
        findNavController().navigate(R.id.action_loginPatientFragment_to_home)
    }

    private fun Signin() {
        if (binding?.mailLog?.text!!.isNotEmpty() && binding?.mailLog?.text!!.isNotEmpty()) {
            auth.signInWithEmailAndPassword(
                binding?.mailLog?.text.toString().trim(),
                binding?.passwordLog?.text.toString().trim()
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    id = auth.currentUser?.uid
                    //    GetToken()
                    findNavController().navigate(R.id.action_loginPatientFragment_to_home)


                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_LONG).show()
                binding?.apply {
                    confirmBu.text = "Login"
                    progressLogin.visibility = View.INVISIBLE
                    confirmBu.isClickable = true
                    confirmBu.alpha = 1F
                }

            }
        } else {
            Toast.makeText(
                requireContext(),
                "Please enter your mail and password ",
                Toast.LENGTH_LONG
            ).show()
            binding?.apply {
                confirmBu.text = "Login"
                progressLogin.visibility = View.INVISIBLE
                confirmBu.isClickable = true
                confirmBu.alpha = 1F
            }

        }


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

    private fun saveId(userID: String) {
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("PREF", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("id", userID)
        editor.apply()
    }

}