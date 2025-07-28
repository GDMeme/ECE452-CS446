package com.example.schedula.ui

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScheduleViewModel : ViewModel() {
    // Observable list of events for Compose
    val events: SnapshotStateList<Event> = mutableStateListOf()

    // Helper to update all events at once
    fun setEvents(newEvents: List<Event>) {
        events.clear()
        events.addAll(newEvents)
    }

    fun addEvent(event: Event) {
        events.add(event)
    }

    fun clearEvents() {
        events.clear()
    }
}
