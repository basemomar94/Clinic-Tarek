package com.AyoubTarik.PatientArzt.patient_section.ui.booking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.AyoubTarik.PatientArzt.R

class HistoryAdapter(
    val visitsList: ArrayList<Visits>,
    val listner: Myclicklisener
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.visit_patient, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val visit: Visits = visitsList[position]
        holder.dateTv.text = visit.date
        holder.status.text = visit.status
    }

    override fun getItemCount(): Int {
        return visitsList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTv: TextView = itemView.findViewById(R.id.dateTV)
        val status: TextView = itemView.findViewById(R.id.status)

        init {
            itemView.setOnClickListener {
                val position: Int = absoluteAdapterPosition
                val visit = visitsList[position]
                listner.onClick(position, visit)


            }
        }
    }

    interface Myclicklisener {
        fun onClick(position: Int, visit: Visits)

    }


}