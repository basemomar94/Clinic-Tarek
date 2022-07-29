package com.AyoubTarik.PatientArzt.doctor_section.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.google.firebase.firestore.FirebaseFirestore
import com.AyoubTarik.PatientArzt.R
import com.AyoubTarik.PatientArzt.databinding.VisitExpandBinding
import com.AyoubTarik.PatientArzt.patient_section.ui.booking.Visits

class Expand_visit() : Fragment(R.layout.visit_expand) {
    var _binding: VisitExpandBinding? = null
    val binding get() = _binding
    var visit: String? = null
    var db: FirebaseFirestore? = null
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

        updateUi()
    }
    private fun updateUi() {
        val item = requireArguments().getSerializable("visit") as Visits
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