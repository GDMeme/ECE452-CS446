package com.example.schedula.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun SuccessScreen(navController: NavController) {
    val backgroundColor = Color(0xFFF0E7F4)
    val accentPurple = Color(0xFFE6DEF6)
    val textColor = Color(0xFF3B3B3B)
    val context = LocalContext.current

    var visible by remember { mutableStateOf(false) }

    fun updateUserXP(xpDelta: Int, onComplete: ((Boolean) -> Unit)? = null) {
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

    LaunchedEffect(Unit) {
        delay(150)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = accentPurple,
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Success!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Schedule successfully uploaded!",
                fontSize = 16.sp,
                color = textColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    updateUserXP(30) { success ->
                        if (success) {
                            Toast.makeText(context, "Gained 30 XP for signing up!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to update XP", Toast.LENGTH_SHORT).show()
                        }
                    }
                    navController.navigate("calendar") {
                        popUpTo("success") { inclusive = true }
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = accentPurple),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Continue", color = textColor, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
