package com.AyoubTarik.PatientArzt.patient_section.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.AyoubTarik.PatientArzt.R
import com.AyoubTarik.PatientArzt.databinding.VisitExpandBinding
import kotlinx.android.synthetic.main.calendarbooking_fragment.*

class VisitsExpand() : Fragment(R.layout.visit_expand) {
    private var _binding: VisitExpandBinding? = null
    private val binding get() = _binding
    private var visit: String? = null
    private var db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var bundle = this.arguments
        if (bundle != null) {
            visit = bundle.getString("visit")
            println("$visit Got")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = VisitExpandBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // GettingData()

        updateUi()
    }

    private fun GettingData() {
        db = FirebaseFirestore.getInstance()
        db!!.collection("visits").document(visit!!).addSnapshotListener { value, error ->
            if (error != null) {
                println(error.message)
            } else {
                binding?.BookedDate?.text = value?.getString("date")
                binding?.BookedBy?.text = "Booked by ${value?.getString("Booked_by")}"
                var status = value?.getString("status")
                if (status.equals("completed")) {
                    binding?.done1?.visibility = View.VISIBLE
                    binding?.done2?.visibility = View.VISIBLE
                    binding?.done3?.visibility = View.VISIBLE
                    binding?.visitMed?.text = value?.getString("pre")
                    binding?.visitAdvice?.text = value?.getString("ins")
                    binding?.visitReq?.text = value?.getString("req")

                } else {
                    binding?.statusL?.visibility = View.VISIBLE
                    binding?.bookingStatus?.text = status

                }
            }
        }

    }

    private fun updateUi() {
        val item = requireArguments().getSerializable("item") as Visits
        println(item.toString())
        binding?.BookedDate?.text = item.date
        binding?.BookedBy?.text = item.Booked_by
        if (item.status.equals("completed")) {
            binding?.done1?.visibility = View.VISIBLE
            binding?.done2?.visibility = View.VISIBLE
            binding?.done3?.visibility = View.VISIBLE
            binding?.visitMed?.text = item.pre
            binding?.visitAdvice?.text = item.ins
            binding?.visitReq?.text = item.req

        } else {
            binding?.statusL?.visibility = View.VISIBLE
            binding?.bookingStatus?.text = item.status

        }
    }
}