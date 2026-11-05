
package com.example.clearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

// APP COLORS
private val Navy = Color(0xFF080F22)
private val Blue = Color(0xFF2563FF)
private val Purple = Color(0xFF5936F5)
private val Cyan = Color(0xFF22D3EE)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {

                // Check whether a student is already logged in.
                val auth = remember {
                    FirebaseAuth.getInstance()
                }

                var currentScreen by rememberSaveable {
                    mutableStateOf(
                        if (auth.currentUser != null) {
                            "dashboard"
                        } else {
                            "welcome"
                        }
                    )
                }

                // Handle the Android back button.
                when (currentScreen) {
                    "login", "register" -> {
                        BackHandler {
                            currentScreen = "welcome"
                        }
                    }

                    "dashboard" -> {
                        BackHandler {
                            // Stay on dashboard while logged in.
                            // Use Log Out to return to login.
                        }
                    }
                }

                when (currentScreen) {

                    // WELCOME SCREEN
                    "welcome" -> {
                        WelcomeScreen(
                            onLoginClick = {
                                currentScreen = "login"
                            },
                            onRegisterClick = {
                                currentScreen = "register"
                            }
                        )
                    }

                    // REGISTRATION SCREEN
                    "register" -> {
                        RegistrationScreen(
                            onBackClick = {
                                currentScreen = "welcome"
                            },
                            onLoginClick = {
                                currentScreen = "login"
                            },
                            onRegistrationComplete = {
                                // Registration successful.
                                // Continue to the login screen.
                                auth.signOut()
                                currentScreen = "login"
                            }
                        )
                    }

                    // LOGIN SCREEN
                    "login" -> {
                        LoginScreen(
                            onBackClick = {
                                currentScreen = "welcome"
                            },
                            onRegisterClick = {
                                currentScreen = "register"
                            },
                            onForgotPasswordClick = {
                                // Forgot Password screen
                                // will be added later.
                            },
                            onLoginSuccess = {
                                // Called after Firebase login succeeds.
                                currentScreen = "dashboard"
                            }
                        )
                    }

                    // STUDENT DASHBOARD
                    "dashboard" -> {
                        StudentDashboard(
                            onLogout = {
                                // Return to login after logout.
                                currentScreen = "login"
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 32.dp,
                    vertical = 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // C++ LOGO
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color(0xFF091329))
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            listOf(Blue, Cyan)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "</>",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4DA3FF)
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "C++",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Cyan
                    )
                }
            }

            Spacer(Modifier.height(34.dp))

            // APP TITLE
            Text(
                text = "C++ Learn",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(Modifier.height(12.dp))

            // SUBTITLE
            Text(
                text = "Learn C++. Read. Practice. Improve.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4DA3FF),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(18.dp))

            // DESCRIPTION CARD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF0D172B))
                    .border(
                        width = 1.dp,
                        color = Color(0xFF1E293B),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Intelligent e-book recommendation system " +
                            "powered by TF-IDF and Cosine Similarity, " +
                            "customized for Computer Science undergraduate curricula.",
                    fontSize = 14.sp,
                    lineHeight = 24.sp,
                    color = Color(0xFFA5B4CC),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(40.dp))

            // STUDENT LOGIN BUTTON
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Blue
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Blue, Purple)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Student Login   →",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // CREATE STUDENT ACCOUNT BUTTON
            OutlinedButton(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color(0xFF334155)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(0xFF1D293D)
                )
            ) {
                Text(
                    text = "Create Student Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE5E7EB)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(
            onLoginClick = {},
            onRegisterClick = {}
        )
    }
}
