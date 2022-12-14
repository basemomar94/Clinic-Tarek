package com.bassem.clinicadminapp.schedule.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.AyoubTarik.PatientArzt.R
import com.AyoubTarik.PatientArzt.patient_section.ui.booking.Visits

class VisitsAdapter(
    val visitsList: ArrayList<Visits>,
    val listner: Myclicklisener
) :
    RecyclerView.Adapter<VisitsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.visit, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val visit: Visits = visitsList[position]
        holder.name.text = visit.name
        // holder.status.text = visit.status
        holder.complain.text = visit.complain
    }

    override fun getItemCount(): Int {
        return visitsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name_visit)
        val complain: TextView = itemView.findViewById(R.id.complain_visit)

        init {
            itemView.setOnClickListener {
                val position: Int = absoluteAdapterPosition
                val visit = visitsList[position]
                listner.onClick(position, visit)


            }
        }
    }

    interface Myclicklisener {
        fun onClick(position: Int, vist: Visits)

    }


}