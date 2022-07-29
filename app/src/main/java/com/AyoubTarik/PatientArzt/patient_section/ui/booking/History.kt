package com.bassem.clinic_userapp.ui.booking

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.AyoubTarik.PatientArzt.R
import com.AyoubTarik.PatientArzt.databinding.HistoryFragmentBinding
import com.AyoubTarik.PatientArzt.patient_section.ui.booking.HistoryAdapter
import com.AyoubTarik.PatientArzt.patient_section.ui.booking.Visits
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class History() : Fragment(R.layout.history_fragment), HistoryAdapter.Myclicklisener {
    private var _binding: HistoryFragmentBinding? = null
    private val binding get() = _binding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var visitsArrayList: ArrayList<Visits>
    private lateinit var id: String
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var sharedPreferences = activity?.getSharedPreferences("PREF", Context.MODE_PRIVATE)
        id = sharedPreferences?.getString("id", "")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = HistoryFragmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        visitsArrayList = arrayListOf()
        Recycle_Setup(visitsArrayList)

        EventChangeListner()
    }

    private fun EventChangeListner() {
        db = FirebaseFirestore.getInstance()
        db.collection("visits").whereEqualTo("id", id)
            .orderBy("bookingtime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    println(error.message)
                } else {
                    Thread(Runnable {
                        for (dc: DocumentChange in value!!.documentChanges) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                visitsArrayList.add(dc.document.toObject(Visits::class.java))

                            }

                        }
                        activity?.runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }
                    }).start()


                }
            }


    }

    override fun onClick(position: Int, visit: Visits) {
      //  val visit = visitsArrayList[position].visit
        val bundle = Bundle()
        bundle.putSerializable("item", visit)
       // bundle.putString("visit", visit)
        findNavController().navigate(R.id.action_bookingPatient_to_visitsExpand,bundle)
    }

    fun Recycle_Setup(list: ArrayList<Visits>) {
        recyclerView = requireView().findViewById(R.id.historyRV)
        adapter = HistoryAdapter(list, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

}


