package com.example.calendarlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class EventAdapter (
    private val events: MutableList<Event>
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>(){

    class EventViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_event,
                parent,
                false
            )
        )
    }

    fun addEvent(event: Event){
        events.add(event)
        notifyItemInserted(events.size-1)
    }

    fun deleteEvents(){
        events.removeAll(events)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val curEvent = events[position]
        holder.itemView.apply {
            tvEventTitle.text = curEvent.title
            tvStartTime.text = curEvent.startTime
            tvEndTime.text = curEvent.endTime
        }
    }

    override fun getItemCount(): Int {
        return events.size
    }
}