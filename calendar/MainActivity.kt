package com.example.calendarlist

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        eventAdapter = EventAdapter(mutableListOf())

        rvEvents.adapter = eventAdapter
        rvEvents.LayoutManager = LinearLayoutManager(this)

        buttonNewEvent.setOnClickListener {
            val eventTitle = etEventTitle.text.toString()
            if (eventTitle.isNotEmpty()) {
                val event = Event(eventTitle)
                eventAdapter.addEvent(event)
                etEventTitle.text.clear()
            }
        }

        buttonDelEvents.setOnClickListener{
            eventAdapter.deleteEvents()
        }
    }
}
