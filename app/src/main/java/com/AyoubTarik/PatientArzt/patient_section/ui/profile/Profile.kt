package com.bassem.clinic_userapp.ui.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.AyoubTarik.PatientArzt.MainActivity
import com.AyoubTarik.PatientArzt.R
import com.AyoubTarik.PatientArzt.databinding.ProfileFragmentBinding
import java.util.*

class Profile() : Fragment(R.layout.profile_fragment) {
    private var _binding: ProfileFragmentBinding? = null
    private  val binding get() = _binding
    private lateinit var db: FirebaseFirestore



    private var regesited_date: String? = null
    private var fileName: String? = null
    private var id: String? = null
    private var auth:FirebaseAuth?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences: SharedPreferences =
            requireActivity().getSharedPreferences("PREF", Context.MODE_PRIVATE)
        id = sharedPreferences.getString("id", "noo")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        GettingData()


        binding?.logout?.setOnClickListener {
            Logout()
        }
    }

    private fun GettingData() {

        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id!!).addSnapshotListener { value, error ->

            if (error != null) {
                println("majab walo")
            } else {
                binding!!.fullnameInfo.text = value?.getString("fullname")
                binding!!.ageInfo.text = value?.getDouble("age")?.toInt().toString()
                binding!!.jobInfo.text = value?.getString("insurance")
                binding!!.complainInfo.text = value?.getString("complain")
                binding!!.mailInfo.text = value?.getString("mail")

                //binding!!.phoneInfo.text = value?.getString("phone")

                binding!!.notesInfo.text = value?.getString("note")
                val sex = value?.getString("sex")
                binding!!.sexInfo.text = sex
                println("jab l9lawi")

            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*
        if (resultCode == Activity.RESULT_OK && requestCode == pickImage) {
            imageuri = data?.data
            binding?.profileimage?.setImageURI(imageuri)
            UploadtoFirebase(imageuri!!)
        }*/
    }
/*
    private fun UploadtoFirebase(image: Uri) {
        var progressIndicator = ProgressDialog.show(context, "Uploading..", "please wait")
        fileName = UUID.randomUUID().toString() + ".jpg"
        val storage = FirebaseStorage.getInstance().reference.child("profile/$fileName")
        storage.putFile(image).addOnSuccessListener { it ->
            progressIndicator.dismiss()
            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener {

                imageLink = it.toString()
                AddPhotoNametoFireStore(imageLink!!)
            }


        }


    }
*/
    /*
    private  fun AddPhotoNametoFireStore(link: String) {
        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").document(id!!).update("image", link).addOnCompleteListener {


        }
    }*/


    private  fun Logout(){
        val sharedPreferences:SharedPreferences= activity?.getSharedPreferences("PREF",Context.MODE_PRIVATE)!!
        sharedPreferences.edit().clear().apply()
        auth= FirebaseAuth.getInstance()
        auth!!.signOut()
        val i = Intent(requireActivity(), MainActivity::class.java)
        startActivity(i)
        requireActivity().finish()


    }


}