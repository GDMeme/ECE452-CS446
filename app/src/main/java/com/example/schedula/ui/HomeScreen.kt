package com.example.schedula.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.schedula.ui.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random



data class HomeEvent(
    val title: String,
    val startTime: String,
    val endTime: String,
    val date: String
)

@Composable
fun HomeScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF0E7F4)
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "User"

    val showNotifications = remember { mutableStateOf(false) }
    val notifications = remember {
        mutableStateListOf(
            "💡 Study Session at 5:00 PM",
            "🎯 2 out of 4 daily goals completed",
            "⏰ Timer running for 30 mins"
        )
    }

    val eventList = remember {
        mutableStateListOf(
            HomeEvent("ECE 452 @ MC 2017", "13:00", "14:20", todayDate),
            HomeEvent("MSE 452 @ CPH 3681", "11:30", "12:50", todayDate),
            HomeEvent("🏃 Exercise", "15:00", "16:00", todayDate),
            HomeEvent("💡 Study Session", "17:00", "18:00", todayDate),
            HomeEvent("🍽️ Dinner", "18:00", "19:00", todayDate),
            HomeEvent("💡 Study Session", "20:00", "21:00", todayDate)
        )
    }

    // --- Persistence Changes Start Here ---
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // State flow to hold the daily goals fetched from Firestore
    val _dailyGoalsState = remember { MutableStateFlow<List<DailyGoals>>(emptyList()) }
    val goalsToday by _dailyGoalsState.collectAsState() // Observe changes to _dailyGoalsState

    // All possible daily goals (can be static or fetched from a global config)
    val allPossibleGoals = remember {
        listOf(
            DailyGoals(description = "Complete all classes", xp = 15),
            DailyGoals(description = "Do 30 mins of focused study", xp = 10),
            DailyGoals(description = "Plan tomorrow’s schedule", xp = 5),
            DailyGoals(description = "Drink 8 glasses of water", xp = 5),
            DailyGoals(description = "Sleep before 11 PM", xp = 20)
        )
    }

    // Function to initialize daily goals in Firestore if they don't exist for the current day
    fun initializeDailyGoalsForUser(userId: String, firestore: FirebaseFirestore, allGoals: List<DailyGoals>, onComplete: (Boolean) -> Unit) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val userGoalsRef = firestore.collection("users").document(userId).collection("dailyGoals")

        userGoalsRef.whereEqualTo("date", today).get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // No goals for today, so add them
                    val random = Random(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) // Seed with day of year for consistent daily goals
                    val selectedGoals = allGoals.shuffled(random).take(4) // Take 4 random goals

                    scope.launch {
                        val batch = firestore.batch()
                        selectedGoals.forEach { goal ->
                            val newGoalRef = userGoalsRef.document() // Firestore generates ID
                            // Ensure date and initial completed status are set when creating
                            batch.set(newGoalRef, goal.copy(date = today, completed = false))
                        }
                        batch.commit()
                            .addOnSuccessListener {
                                Log.d("Firestore", "Successfully added daily goals for $today.")
                                onComplete(true)
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error adding daily goals: ${e.message}", e)
                                onComplete(false)
                            }
                    }
                } else {
                    Log.d("Firestore", "Daily goals for $today already exist.")
                    onComplete(true) // Goals already exist, no need to initialize
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error checking daily goals: ${e.message}", e)
                onComplete(false)
            }
    }


    // Fetch daily goals from Firestore when the screen is composed
    val userId = currentUser?.uid
    LaunchedEffect(userId) {
        if (userId != null) {
            val userDailyGoalsRef = db.collection("users").document(userId).collection("dailyGoals")

            userDailyGoalsRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // Check if there are documents for today
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val fetchedGoalsForToday = snapshot.documents.mapNotNull { document ->
                        document.toObject(DailyGoals::class.java)?.copy(goalId = document.id)
                    }.filter { it.date == today } // Filter to only show goals for today

                    if (fetchedGoalsForToday.isEmpty()) {
                        Log.d("Firestore", "No daily goals for $today, initializing...")
                        initializeDailyGoalsForUser(userId, db, allPossibleGoals) { initialized ->
                            if (initialized) {
                                // Re-fetch or let the listener update if initialization adds data
                                Log.d("Firestore", "Daily goals initialized successfully, listener should update.")
                            } else {
                                Log.e("Firestore", "Failed to initialize daily goals.")
                            }
                        }
                    } else {
                        _dailyGoalsState.value = fetchedGoalsForToday
                        Log.d("Firestore", "Fetched daily goals: ${fetchedGoalsForToday.size}")
                    }
                } else {
                    Log.d("Firestore", "Snapshot is null.")
                }
            }
        }
    }


    val now = Calendar.getInstance()
    val totalMinutes = 10 * 60  // From 09:00 to 19:00
    val minutesPassed = (now.get(Calendar.HOUR_OF_DAY) - 9) * 60 + now.get(Calendar.MINUTE)
    val percentOfDay = (minutesPassed.toFloat() / totalMinutes).coerceIn(0f, 1f)
    val percentageText = "${(percentOfDay * 100).toInt()}%"

    val message = when {
        percentOfDay >= 1f -> "Well done! You've completed today's plan 🎉"
        percentOfDay < 0.5f -> "Let’s get started! You’ve got this 💪"
        else -> "Excellent! Your today's plan is almost done 🥰"
    }

    Scaffold(
        bottomBar = { BottomNavBar(currentScreen = "home", navController = navController) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding),
            contentPadding = PaddingValues(bottom = 50.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { showNotifications.value = !showNotifications.value }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification",
                            tint = Color.Gray
                        )
                    }

                    Text(
                        text = "Good Morning,\n$userName!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            if (showNotifications.value) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Notifications", fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            if (notifications.isEmpty()) {
                                Text("No new notifications", color = Color.Gray, fontSize = 14.sp)
                            } else {
                                notifications.forEach { note ->
                                    Text("• $note", fontSize = 14.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(140.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1BCE3))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = percentOfDay,
                                modifier = Modifier.size(64.dp),
                                strokeWidth = 6.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = percentageText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            item {
                DailyGoalsSection(goals = goalsToday) { goal ->
                    // This lambda is called when a checkbox is toggled in DailyGoalsSection
                    // Update the 'completed' status in Firestore
                    currentUser?.let { user ->
                        val userGoalRef = db.collection("users").document(user.uid)
                            .collection("dailyGoals").document(goal.goalId)

                        val newCompletedStatus = !goal.completed // The new state after toggle

                        userGoalRef.update("completed", newCompletedStatus)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Goal '${goal.description}' completion status updated to $newCompletedStatus")
                                val message = if (newCompletedStatus) {
                                    "Gained ${goal.xp} XP for completing \"${goal.description}\"!"
                                } else {
                                    "Removed ${goal.xp} XP for unchecking \"${goal.description}\""
                                }
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                                // Update XP in Firestore
                                val xpChange = if (newCompletedStatus) goal.xp else -goal.xp
                                updateUserXP(xpChange) { success ->
                                    if (!success) {
                                        Toast.makeText(context, "Failed to update XP", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                // Add notification
                                notifications.add(if (newCompletedStatus) "✅ Completed: ${goal.description}" else "❌ Uncompleted: ${goal.description}")

                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error updating goal completion: ${e.localizedMessage}")
                                Toast.makeText(context, "Failed to update goal completion", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }

            item {
                val totalXP = goalsToday.filter { it.completed }.sumOf { it.xp }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("XP earned today: $totalXP", fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Today's Schedule",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(eventList.size) { index ->
                val event = eventList[index]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (index % 4) {
                            0 -> Color(0xFFE6DEF6)
                            1 -> Color(0xFFFFE3D9)
                            2 -> Color(0xFFCCE5FF)
                            else -> Color(0xFFFFF6CC)
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(event.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${event.startTime} - ${event.endTime}", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// Function to update user XP in Firestore
private fun updateUserXP(xpDelta: Int, onComplete: ((Boolean) -> Unit)? = null) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val db = FirebaseFirestore.getInstance()

    if (currentUser != null) {
        val userRef = db.collection("users").document(currentUser.uid)

        userRef.update("userXP", FieldValue.increment(xpDelta.toLong()))
            .addOnSuccessListener {
                Log.d("XP_UPDATE", "Incremented XP by $xpDelta for user ${currentUser.uid}")
                onComplete?.invoke(true)
            }
            .addOnFailureListener { e ->
                Log.e("XP_UPDATE", "Failed to increment XP: ${e.localizedMessage}")
                onComplete?.invoke(false)
            }
    } else {
        Log.e("XP_UPDATE", "No authenticated user.")
        onComplete?.invoke(false)
    }
}

@Composable
fun DailyGoalsSection(goals: List<DailyGoals>, onToggle: (DailyGoals) -> Unit) {

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Daily Goals", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (goals.isEmpty()) {
            Text(
                "Loading daily goals or no goals for today...",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            goals.forEach { goal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (goal.completed) Color(0xFFD1E7DD) else Color(0xFFF8D7DA)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(goal.description, fontWeight = FontWeight.SemiBold)
                            Text("XP: ${goal.xp}", fontSize = 12.sp, color = Color.Gray)
                        }
                        Checkbox(
                            checked = goal.completed,
                            onCheckedChange = {
                                // Delegate the actual update to the onToggle lambda provided by the HomeScreen
                                onToggle(goal)
                            }
                        )
                    }
                }
            }
        }
    }
}