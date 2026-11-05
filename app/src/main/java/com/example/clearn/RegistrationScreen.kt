
package com.example.clearn

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val RegBackground = Color(0xFF080F22)
private val RegSurface = Color(0xFF10192D)
private val RegBorder = Color(0xFF334155)
private val RegBlue = Color(0xFF1E5BFF)
private val RegText = Color(0xFFE8EDF7)
private val RegMuted = Color(0xFF98A7C0)

private val cppTopics = listOf(
    "C++ Fundamentals",
    "Variables and Data Types",
    "Functions",
    "Arrays",
    "Pointers",
    "Object-Oriented Programming",
    "Classes",
    "Inheritance",
    "Polymorphism",
    "Templates",
    "STL",
    "File Handling",
    "Exception Handling",
    "Memory Management"
)

@Composable
fun RegistrationScreen(
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationComplete: () -> Unit
) {
    var studentId by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    var selectedTopics by rememberSaveable {
        mutableStateOf(listOf<String>())
    }

    var selectedDifficulty by rememberSaveable {
        mutableStateOf("Beginner")
    }

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RegBackground)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 18.dp)
    ) {

        Text(
            text = "← Back",
            color = Color(0xFF4DA3FF),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(
                enabled = !isLoading
            ) { onBackClick() }
        )

        Spacer(Modifier.height(18.dp))

        Text(
            text = "Student Registration",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Initialize your C++ e-book recommendation profile",
            color = RegMuted,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(24.dp))

        // STUDENT ID AND FULL NAME
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                RegLabel("Student ID")
                RegTextField(
                    value = studentId,
                    onValueChange = { studentId = it },
                    placeholder = "CS-2024-9102",
                    enabled = !isLoading
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                RegLabel("Full Name")
                RegTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = "Jordan Lee",
                    enabled = !isLoading
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // EMAIL
        RegLabel("University Email")
        RegTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "student@university.edu",
            keyboardType = KeyboardType.Email,
            enabled = !isLoading
        )

        Spacer(Modifier.height(20.dp))

        // PASSWORD FIELDS
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                RegLabel("Password")
                RegTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Password",
                    isPassword = true,
                    enabled = !isLoading
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                RegLabel("Confirm Password")
                RegTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Confirm",
                    isPassword = true,
                    enabled = !isLoading
                )
            }
        }

        Spacer(Modifier.height(26.dp))

        // C++ TOPICS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select Your C++ Topics of Interest",
                color = Color(0xFF93C5FD),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "For TF-IDF matching",
                color = RegMuted,
                fontSize = 11.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    Color(0xFF1E293B),
                    RoundedCornerShape(12.dp)
                )
                .padding(10.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cppTopics.forEach { topic ->
                    val selected = topic in selectedTopics

                    TopicChip(
                        text = topic,
                        selected = selected,
                        enabled = !isLoading,
                        onClick = {
                            selectedTopics = if (selected) {
                                selectedTopics - topic
                            } else {
                                selectedTopics + topic
                            }
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // DIFFICULTY LEVEL
        Text(
            text = "Your Preferred Difficulty Level",
            color = Color(0xFF93C5FD),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(9.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                "Beginner",
                "Intermediate",
                "Advanced"
            ).forEach { level ->

                val selected = selectedDifficulty == level

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .background(
                            if (selected) Color(0xFF102C62)
                            else RegSurface,
                            RoundedCornerShape(10.dp)
                        )
                        .border(
                            1.dp,
                            if (selected) RegBlue
                            else Color(0xFF1E293B),
                            RoundedCornerShape(10.dp)
                        )
                        .clickable(enabled = !isLoading) {
                            selectedDifficulty = level
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level,
                        color = if (selected) {
                            Color(0xFFBFDBFE)
                        } else {
                            RegMuted
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ERROR MESSAGE
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color(0xFFFCA5A5),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(12.dp))
        }

        // FIREBASE REGISTRATION BUTTON
        Button(
            onClick = {
                if (isLoading) return@Button

                val cleanId = studentId.trim()
                val cleanName = fullName.trim()
                val cleanEmail = email.trim()

                errorMessage = when {
                    cleanId.isBlank() ||
                            cleanName.isBlank() ||
                            cleanEmail.isBlank() ||
                            password.isBlank() ||
                            confirmPassword.isBlank() ->
                        "Please complete all required fields."

                    !Patterns.EMAIL_ADDRESS
                        .matcher(cleanEmail).matches() ->
                        "Please enter a valid email address."

                    password.length < 8 ->
                        "Password must contain at least 8 characters."

                    password != confirmPassword ->
                        "Passwords do not match."

                    selectedTopics.isEmpty() ->
                        "Please select at least one C++ topic."

                    else -> ""
                }

                if (errorMessage.isEmpty()) {
                    isLoading = true

                    // STEP 1: CREATE FIREBASE AUTH ACCOUNT
                    auth.createUserWithEmailAndPassword(
                        cleanEmail,
                        password
                    ).addOnCompleteListener { authTask ->

                        if (authTask.isSuccessful) {
                            val user = authTask.result?.user

                            if (user == null) {
                                isLoading = false
                                errorMessage =
                                    "Unable to retrieve the new account."
                                return@addOnCompleteListener
                            }

                            // STEP 2: PREPARE STUDENT PROFILE
                            val studentProfile = hashMapOf(
                                "studentId" to cleanId,
                                "fullName" to cleanName,
                                "email" to cleanEmail,
                                "topics" to selectedTopics.toList(),
                                "difficulty" to selectedDifficulty
                            )

                            // STEP 3: SAVE PROFILE IN FIRESTORE
                            firestore.collection("students")
                                .document(user.uid)
                                .set(studentProfile)
                                .addOnSuccessListener {

                                    isLoading = false

                                    // STEP 4: SIGN OUT
                                    auth.signOut()

                                    // STEP 5: GO TO LOGIN SCREEN
                                    onRegistrationComplete()
                                }
                                .addOnFailureListener { exception ->

                                    isLoading = false

                                    errorMessage =
                                        "Account created, but the profile " +
                                                "could not be saved: " +
                                                (exception.localizedMessage
                                                    ?: "Unknown error") +
                                                ". Please contact support " +
                                                "before trying again."
                                }

                        } else {
                            isLoading = false

                            errorMessage =
                                authTask.exception?.localizedMessage
                                    ?: "Registration failed. Please try again."
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(13.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = RegBlue
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(23.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )

                Spacer(Modifier.width(12.dp))

                Text("Creating Account...")
            } else {
                Text(
                    text = "Register & Start Learning",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // LOGIN LINK
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already registered? ",
                color = RegMuted,
                fontSize = 14.sp
            )

            Text(
                text = "Login",
                color = Color(0xFF4DA3FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(
                    enabled = !isLoading
                ) {
                    onLoginClick()
                }
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun RegLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFFBAC7DA),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 7.dp)
    )
}

@Composable
private fun RegTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = androidx.compose.ui.text.TextStyle(
            color = RegText,
            fontSize = 13.sp
        ),
        placeholder = {
            Text(
                text = placeholder,
                color = RegMuted,
                fontSize = 12.sp
            )
        },
        visualTransformation = if (isPassword) {
            PasswordVisualTransformation()
        } else {
            androidx.compose.ui.text.input.VisualTransformation.None
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) {
                KeyboardType.Password
            } else {
                keyboardType
            }
        ),
        shape = RoundedCornerShape(11.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = RegText,
            unfocusedTextColor = RegText,
            focusedContainerColor = RegSurface,
            unfocusedContainerColor = RegSurface,
            focusedBorderColor = RegBlue,
            unfocusedBorderColor = RegBorder,
            cursorColor = RegBlue
        )
    )
}

@Composable
private fun TopicChip(
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                if (selected) RegBlue
                else Color(0xFF1E293B),
                RoundedCornerShape(8.dp)
            )
            .clickable(enabled = enabled) {
                onClick()
            }
            .padding(
                horizontal = 11.dp,
                vertical = 9.dp
            )
    ) {
        Text(
            text = if (selected) "✓ $text" else text,
            color = if (selected) Color.White
            else Color(0xFFCBD5E1),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
