package com.example.schedula.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.schedula.ui.components.BottomNavBar
import androidx.compose.foundation.clickable

data class LeaderboardUser(val rank: Int, val username: String, val xp: Int)
data class DailyMission(val title: String, val description: String, val xp: Int = 20)

fun getLeaderboardUsers() = listOf(
    LeaderboardUser(1, "Test", 3090),
    LeaderboardUser(2, "Mark", 2750),
    LeaderboardUser(3, "Kate", 1680),
    LeaderboardUser(4, "Leah", 1660)
)

fun getDailyMissions() = listOf(
    DailyMission("Master of Focus", "Use the Pomodoro timer for a total of 60 minutes."),
    DailyMission("Active Engagement", "Attend one exercise event today."),
    DailyMission("Event Enthusiast", "Attend more than 3 scheduled events."),
    DailyMission("Early Riser", "Wake up on time."),
    DailyMission("Momentum Keeper", "Complete two scheduled tasks with no delay in between."),
    DailyMission("Climb the Leaderboard", "Gain at least 100 XP in one day.")
)

fun getBadgeAchievements() = listOf(true, true, true, false, false, false)
fun getUserXP(): Int = 60

@Composable
fun BadgeItemSmall(badge: DailyMission, achieved: Boolean) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achieved) Color(0xFF5B5F9D) else Color(0xFFE0E0E0)
        ),
        modifier = Modifier
            .padding(4.dp)
            .size(90.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (achieved) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = null,
                tint = if (achieved) Color.Yellow else Color.Gray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = badge.title,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                maxLines = 2,
                color = if (achieved) Color.White else Color.DarkGray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LeaderboardScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }

    val leaderboardUsers by remember { mutableStateOf(getLeaderboardUsers()) }
    val allMissions by remember { mutableStateOf(getDailyMissions()) }
    val badgeAchievements by remember { mutableStateOf(getBadgeAchievements()) }
    val currentXP = remember { getUserXP() }

    val randomMissions = remember { allMissions.shuffled().take(2) }
    val missionChecks = remember { mutableStateListOf(false, false) }
    var totalXP by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = { BottomNavBar(currentScreen = "leaderboard", navController = navController) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Leaderboard",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Profile Card
            Card(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = Color(0xFF5B5F9D)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Alex", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Level 2 Productive Scholar", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = currentXP / 100f,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF5B5F9D)
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text("$currentXP / 100 XP", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Daily Missions
            Text("Daily Missions", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF6F2FF), RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                randomMissions.forEachIndexed { index, mission ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(mission.title, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            Text("+${mission.xp} XP", fontSize = 10.sp, color = Color.Gray)
                        }
                        Checkbox(
                            checked = missionChecks[index],
                            onCheckedChange = {
                                missionChecks[index] = it
                                totalXP = missionChecks.mapIndexed { i, checked ->
                                    if (checked) randomMissions[i].xp else 0
                                }.sum()
                            },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF5B5F9D))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("DAILY XP goal", fontSize = 10.sp, color = Color.Gray)
                Text(
                    "$totalXP / 100 XP",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.End)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Leaderboard
            Text("Leaderboard", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    Triple("Global", Icons.Default.Public, 0),
                    Triple("Friends", Icons.Default.Group, 1),
                    Triple("You", Icons.Default.Person, 2)
                ).forEach { (label, icon, index) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedTab = index }
                            .padding(horizontal = 4.dp)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = "$label Icon",
                            tint = if (selectedTab == index) Color(0xFF5B5F9D) else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = label,
                            color = if (selectedTab == index) Color(0xFF5B5F9D) else Color.Gray,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            leaderboardUsers.forEach {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("#${it.rank} ${it.username}", fontSize = 12.sp)
                        Text("${it.xp} XP", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Badges
            Text("Badges", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.heightIn(max = 180.dp),
                content = {
                    itemsIndexed(allMissions) { index, badge ->
                        BadgeItemSmall(badge = badge, achieved = badgeAchievements.getOrNull(index) ?: false)
                    }
                }
            )

            Spacer(modifier = Modifier.height(64.dp)) // space above nav bar
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderboardScreenPreview() {
    LeaderboardScreen(navController = rememberNavController())
}