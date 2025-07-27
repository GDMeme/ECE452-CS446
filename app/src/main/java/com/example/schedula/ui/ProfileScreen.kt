package com.example.schedula.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.schedula.ui.components.BottomNavBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlin.math.roundToInt

@Composable
fun ProfileScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF0E7F4)
    val leaderboardColor = Color(0xFFE6DEF6)
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isEditingName by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("Alex") } // This should ideally be fetched from Firestore
    var currentUserEmail by remember { mutableStateOf("test@uwaterloo.ca") } // This should also be dynamic
    var selectedTab by remember { mutableStateOf("Global") }

    val globalLeaderboardUsers = remember { mutableStateListOf<Pair<String, Long>>() }
    var currentUserRank by remember { mutableStateOf<Int?>(null) }
    var currentUserXp by remember { mutableStateOf<Long?>(null) }
    var totalUsers by remember { mutableStateOf<Int?>(null) } // New state for total users

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            currentUserEmail = user.email ?: "N/A"
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    username = document.getString("username") ?: "Alex"
                    currentUserXp = document.getLong("userXP")
                }
                .addOnFailureListener { exception ->
                    println("Error getting current user data: $exception")
                }
        }

        db.collection("users")
            .orderBy("userXP", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                globalLeaderboardUsers.clear()
                val allUsers = mutableListOf<Pair<String, Long>>()
                totalUsers = result.size() // Get the total number of users

                for (document in result) {
                    val name = document.getString("username") ?: "Unknown"
                    val xp = document.getLong("userXP") ?: 0L
                    allUsers.add(name to xp)

                    if (document.id == currentUser?.uid) {
                        currentUserRank = allUsers.size
                        currentUserXp = xp
                    }
                }
                globalLeaderboardUsers.addAll(allUsers.take(3)) // Display top 3
            }
            .addOnFailureListener { exception ->
                println("Error getting leaderboard documents: $exception")
            }
    }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    Scaffold(
        bottomBar = { BottomNavBar(currentScreen = "profile", navController = navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Default profile",
                        tint = Color.Gray,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isEditingName) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = username,
                        onValueChange = { username = it },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        isEditingName = false
                        auth.currentUser?.uid?.let { uid ->
                            db.collection("users").document(uid)
                                .update("username", username)
                                .addOnSuccessListener { println("Username updated successfully!") }
                                .addOnFailureListener { e -> println("Error updating username: $e") }
                        }
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            } else {
                Text(
                    text = username,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { isEditingName = true }
                )
            }

            Text(currentUserEmail, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))

            SettingsMenu(
                onEditUsername = { isEditingName = true },
                onPrivacySettings = {
                    // TODO: implement action
                }
            )

            SettingsItem(Icons.Default.Edit, "Edit Questionnaire", "View & Edit Answers") {
                navController.navigate("questionsMenu")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Leaderboard
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = leaderboardColor),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Leaderboard", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TabChip("Friends", selectedTab == "Friends") { selectedTab = "Friends" }
                            TabChip("Global", selectedTab == "Global") { selectedTab = "Global" }
                        }
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }

                    if (selectedTab == "Global") {
                        val rankText = currentUserRank?.let { "Rank #$it" } ?: "Fetching Rank..."
                        val xpText = currentUserXp?.let { "$it XP" } ?: "Fetching XP..."

                        val topPercentage = if (currentUserRank != null && totalUsers != null && totalUsers != 0) {
                            val percentage = (currentUserRank!!.toDouble() / totalUsers!!.toDouble()) * 100
                            "${percentage.roundToInt()}%" // You might want to format this differently for "Top N%"
                        } else {
                            "N/A"
                        }

                        val userCountText = totalUsers?.let { "Out of $it users" } ?: "Out of All Users"

                        LeaderboardEntry(
                            profileImageUri = imageUri,
                            rank = rankText,
                            top = "Top ${topPercentage}", // Use the calculated percentage here
                            xp = xpText,
                            userCount = userCountText // Use the dynamic total user count here
                        )
                        Divider()
                        if (globalLeaderboardUsers.isNotEmpty()) {
                            globalLeaderboardUsers.forEachIndexed { index, (name, xp) ->
                                LeaderboardRow(
                                    icon = Icons.Default.AccountCircle,
                                    name = name,
                                    score = xp.toString(),
                                    rank = index + 1
                                )
                                Divider()
                            }
                        } else {
                            Text("Loading leaderboard...", modifier = Modifier.padding(top = 16.dp), color = Color.Gray)
                        }
                    } else {
                        Text("No friends yet!", modifier = Modifier.padding(top = 16.dp), color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Show more", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun SettingsMenu(
    onEditUsername: () -> Unit,
    onPrivacySettings: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { expanded = true },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Settings", fontWeight = FontWeight.Medium)
                    Text("Security, Privacy", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Edit Username") },
                onClick = {
                    expanded = false
                    onEditUsername()
                }
            )
            DropdownMenuItem(
                text = { Text("Privacy Settings") },
                onClick = {
                    expanded = false
                    onPrivacySettings()
                }
            )
        }
    }
}

@Composable
fun TabChip(text: String, selected: Boolean, onClick: () -> Unit) {
    val bgColor = if (selected) Color(0xFF9C89B8) else Color(0xFFE0E0E0)
    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = Color.White
    )
}

@Composable
fun LeaderboardEntry(profileImageUri: Uri?, rank: String, top: String, xp: String, userCount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (profileImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(profileImageUri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(rank, fontWeight = FontWeight.Bold)
                Text(xp, fontSize = 12.sp, color = Color.Gray)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(top, fontWeight = FontWeight.Bold)
            Text(userCount, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun LeaderboardRow(icon: ImageVector, name: String, score: String, rank: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$rank.", fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp))
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(name)
        }
        Text(score, fontWeight = FontWeight.Bold)
    }
}