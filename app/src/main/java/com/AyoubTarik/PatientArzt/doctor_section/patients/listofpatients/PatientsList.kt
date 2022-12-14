package com.AyoubTarik.PatientArzt.doctor_section.patients.listofpatients

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.AyoubTarik.PatientArzt.R
import com.google.firebase.firestore.*

class PatientsList() : Fragment(R.layout.patients_fragment), patientsadapter.Myclicklisener,

    SearchView.OnQueryTextListener {

    lateinit var recyclerView: RecyclerView
    lateinit var patientsArrayList: ArrayList<Patientsclass>
    lateinit var myAdapter: patientsadapter
    lateinit var db: FirebaseFirestore
    lateinit var id: String
    var IsSearch = false
    var filterArrayList: ArrayList<Patientsclass>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search, menu)
        val search = menu.findItem(R.id.app_bar_search)
        val SearchView = search.actionView as SearchView
        SearchView.setOnQueryTextListener(this)
    }

    override fun onResume() {
        super.onResume()

        // EventChangedListner()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        patientsArrayList = arrayListOf()
        recyclerView = view.findViewById(R.id.patientsRV)
        //  RecycleSetup(patientsArrayList)
        EventChangedListner()


    }

    private fun RecycleSetup(list: ArrayList<Patientsclass>) {

        myAdapter = patientsadapter(list, this)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = myAdapter

    }


    private fun EventChangedListner() {

        db = FirebaseFirestore.getInstance()
        db.collection("patiens_info").orderBy("first_visit", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {

                    if (error != null) {
                        println("Firestore error ${error.message}")
                        return
                    }
                    Thread(Runnable {
                        for (dc: DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                patientsArrayList.add(dc.document.toObject(Patientsclass::class.java))
                            }

                        }
                        activity?.runOnUiThread {
                            RecycleSetup(patientsArrayList)
                            myAdapter.notifyDataSetChanged()

                        }


                    }).start()


                }


            })
    }

    override fun onClick(position: Int) {
        var patient: Patientsclass = if (IsSearch) {
            filterArrayList?.get(position)!!
        } else {
            patientsArrayList[position]
        }

        id = patient.id!!
        var bundle: Bundle = Bundle()
        bundle.putString("id", id)
        println(id)
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_doctor)
        navController.navigate(R.id.action_patientsList_to_patientsInfo, bundle)


    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(search: String?): Boolean {
        IsSearch = true
        filterArrayList = arrayListOf()
        var input: String = search!!.lowercase()
        for (patient: Patientsclass in patientsArrayList) {
            var name = patient.fullname!!.lowercase()

            println(name)
            if (name.contains(input) ) {
                filterArrayList!!.add(patient)

            }


        }
        RecycleSetup(filterArrayList!!)
        myAdapter.notifyDataSetChanged()
        println("final")

        return true

    }







}