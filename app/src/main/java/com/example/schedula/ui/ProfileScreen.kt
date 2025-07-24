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

@Composable
fun ProfileScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF0E7F4)
    val leaderboardColor = Color(0xFFE6DEF6)
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isEditingName by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("Alex") }
    var selectedTab by remember { mutableStateOf("Global") }

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
                        singleLine = true
                    )
                    IconButton(onClick = { isEditingName = false }) {
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

            Text("test@uwaterloo.ca", fontSize = 14.sp, color = Color.Gray)
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
                        LeaderboardEntry(imageUri, "Rank #2,339", "Top 1%", "88,242 XP", "Out of 3,376,487 users")
                        Divider()
                        LeaderboardRow(Icons.Default.AccountCircle, "Amy Winnar", "88242")
                        Divider()
                        LeaderboardRow(Icons.Default.AccountCircle, "CarsonP", "88230")
                        Divider()
                        LeaderboardRow(Icons.Default.AccountCircle, "Shannanisthebest", "88205")
                        Divider()
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
fun LeaderboardRow(icon: ImageVector, name: String, score: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(name)
        }
        Text(score, fontWeight = FontWeight.Bold)
    }
}
