
package com.example.clearn

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

private val LoginNavy = Color(0xFF080F22)
private val LoginSurface = Color(0xFF10192D)
private val LoginBorder = Color(0xFF334155)
private val LoginBlue = Color(0xFF1E5BFF)
private val LoginMuted = Color(0xFF98A7C0)

@Composable
fun LoginScreen(
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var identifier by rememberSaveable {
        mutableStateOf("")
    }

    var password by rememberSaveable {
        mutableStateOf("")
    }

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val auth = remember {
        FirebaseAuth.getInstance()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LoginNavy)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 17.dp,
                vertical = 24.dp
            )
    ) {

        // BACK BUTTON
        Text(
            text = "← Back to Welcome",
            color = Color(0xFF4DA3FF),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable(
                enabled = !isLoading
            ) {
                onBackClick()
            }
        )

        Spacer(Modifier.height(20.dp))

        // TITLE
        Text(
            text = "Student Log In",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = "Access your personalized C++ learning library",
            color = LoginMuted,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(34.dp))

        // EMAIL FIELD
        LoginLabel("University Email")

        OutlinedTextField(
            value = identifier,
            onValueChange = {
                identifier = it
                errorMessage = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp),
            placeholder = {
                Text(
                    text = "student@university.edu",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp
                )
            },
            singleLine = true,
            enabled = !isLoading,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 15.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LoginSurface,
                unfocusedContainerColor = LoginSurface,
                focusedBorderColor = LoginBlue,
                unfocusedBorderColor = LoginBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = LoginBlue
            )
        )

        Spacer(Modifier.height(22.dp))

        // PASSWORD LABEL AND FORGOT LINK
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LoginLabel("Password")

            Text(
                text = "Forgot?",
                color = Color(0xFF4DA3FF),
                fontSize = 13.sp,
                modifier = Modifier.clickable(
                    enabled = !isLoading
                ) {
                    onForgotPasswordClick()
                }
            )
        }

        // PASSWORD FIELD
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp),
            placeholder = {
                Text(
                    text = "Enter password",
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            },
            singleLine = true,
            enabled = !isLoading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.White,
                fontSize = 15.sp
            ),
            shape = RoundedCornerShape(15.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = LoginSurface,
                unfocusedContainerColor = LoginSurface,
                focusedBorderColor = LoginBlue,
                unfocusedBorderColor = LoginBorder,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = LoginBlue
            )
        )

        Spacer(Modifier.height(18.dp))

        // ERROR MESSAGE
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color(0xFFFCA5A5),
                fontSize = 13.sp
            )

            Spacer(Modifier.height(12.dp))
        }

        // LOGIN BUTTON
        Button(
            onClick = {
                val email = identifier.trim()

                errorMessage = when {
                    email.isBlank() ->
                        "Please enter your University Email."

                    !android.util.Patterns.EMAIL_ADDRESS
                        .matcher(email).matches() ->
                        "Please enter a valid email address."

                    password.isBlank() ->
                        "Please enter your password."

                    else -> ""
                }

                if (errorMessage.isEmpty()) {

                    isLoading = true

                    auth.signInWithEmailAndPassword(
                        email,
                        password
                    ).addOnCompleteListener { task ->

                        isLoading = false

                        if (task.isSuccessful) {

                            // Firebase login successful.
                            // MainActivity opens Student Dashboard.
                            onLoginSuccess()

                        } else {

                            errorMessage = when (task.exception) {

                                is FirebaseAuthInvalidUserException ->
                                    "Account not found. Please register first."

                                is FirebaseAuthInvalidCredentialsException ->
                                    "Incorrect email or password."

                                else ->
                                    task.exception?.localizedMessage
                                        ?: "Login failed. Please try again."
                            }
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LoginBlue
            )
        ) {

            if (isLoading) {

                CircularProgressIndicator(
                    modifier = Modifier.size(23.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "Logging in...",
                    color = Color.White,
                    fontSize = 15.sp
                )

            } else {

                Text(
                    text = "Login to Dashboard   →",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(30.dp))

        // REGISTER LINK
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Don't have an account yet? ",
                color = LoginMuted,
                fontSize = 14.sp
            )

            Text(
                text = "Register here",
                color = Color(0xFF4DA3FF),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(
                    enabled = !isLoading
                ) {
                    onRegisterClick()
                }
            )
        }
    }
}

@Composable
private fun LoginLabel(text: String) {
    Text(
        text = text,
        color = Color(0xFFBAC7DA),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
