package com.example.schedula.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.schedula.AuthenticationRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

@Composable
fun SignUpScreen(navController: NavController) {
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var pwVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEFF4F9))
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Schedula",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Create An Account",
                fontSize = 20.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Uploads course times for efficient scheduling",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                placeholder = { Text("Your Name") },
                trailingIcon = {
                    if (name.text.isNotEmpty()) {
                        IconButton(onClick = { name = TextFieldValue("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("School email") },
                placeholder = { Text("yourwatid@uwaterloo.ca") },
                trailingIcon = {
                    if (email.text.isNotEmpty()) {
                        IconButton(onClick = { email = TextFieldValue("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (pwVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (pwVisible) Icons.Filled.VisibilityOff else Icons.Default.Visibility
                    val description = if (pwVisible) "Hide password" else "Show password"
                    IconButton(onClick = { pwVisible = !pwVisible }) {
                        Icon(imageVector = icon, contentDescription = description)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color.Black,
                    fontSize = 13.sp
                )
                Text(
                    text = "Login",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("login")
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val emailText = email.text.trim()
                    val passwordText = password.text.trim()
                    val nameText = name.text.trim()

                    if (emailText.isNotBlank() && passwordText.isNotBlank()) {
                        AuthenticationRepo.register(nameText, emailText, passwordText) { success, error ->
                            if (success) {
                                val user = FirebaseAuth.getInstance().currentUser
                                if (user != null) {
                                    // Optionally set display name in Firebase Auth
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(nameText)
                                        .build()
                                    user.updateProfile(profileUpdates)

                                    val db = FirebaseFirestore.getInstance()
                                    val userData = hashMapOf(
                                        "userId" to user.uid,
                                        "username" to nameText,
                                        "userXP" to 0,
                                        "email" to user.email,
                                        "createdAt" to Timestamp.now()
                                    )
                                    db.collection("users").document(user.uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Account created!", Toast.LENGTH_SHORT).show()
                                            navController.navigate("login")
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(context, "Firestore error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    Toast.makeText(context, "Error: User is null after sign-up", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, error ?: "Sign up failed", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD7D9F7))
            ) {
                Text(text = "Create Account", color = Color(0xFF5B5F9D))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "By clicking continue, you agree to our Terms of Service\nand Privacy Policy",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Black
            )
        }
    }
}
