package com.example.clearn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

private val profileBackground = Color(0xFF050A1D)
private val profilePanel = Color(0xFF10192E)
private val profileField = Color(0xFF060D20)
private val profileBlue = Color(0xFF195FFF)
private val profileMuted = Color(0xFF9AAEC9)
private val profileLine = Color(0xFF23314B)

private val interestOptions = listOf(
    "C++ Fundamentals", "Variables and Data Types", "Functions", "Arrays",
    "Pointers", "Object-Oriented Programming", "Classes", "Inheritance",
    "Polymorphism", "STL", "Data Structures"
)

data class StudentProfileData(
    val fullName: String = "",
    val studentId: String = "",
    val email: String = "",
    val course: String = "",
    val yearLevel: String = "",
    val difficulty: String = "Beginner",
    val interests: Set<String> = emptySet(),
    val weakTopics: Set<String> = emptySet(),
    val bookmarkedCount: Int = 0,
    val completedCount: Int = 0,
    val quizAverage: Int = 0,
    val inProgressCount: Int = 0
)

/** This screen contains no bottom navigation: keep the existing dashboard navigation. */
@Composable
fun StudentProfileScreen(
    profile: StudentProfileData = StudentProfileData(),
    onProfileSaved: (StudentProfileData) -> Unit = {},
    onBookmarksClick: () -> Unit = {},
    onProgressClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    var current by remember(profile) { mutableStateOf(profile) }
    var editing by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var profileError by remember { mutableStateOf<String?>(null) }
    val auth = remember { FirebaseAuth.getInstance() }
    val db = remember { FirebaseFirestore.getInstance() }
    val uid = auth.currentUser?.uid

    // Listen for profile changes; remove listener when leaving this screen.
    DisposableEffect(uid) {
        if (uid == null) {
            loading = false
            profileError = "Please sign in to view your profile."
            onDispose { }
        } else {
            val registration = db.collection("students").document(uid)
                .addSnapshotListener { snapshot, error ->
                    loading = false
                    if (error != null) {
                        profileError = error.localizedMessage ?: "Could not load profile."
                    } else {
                        profileError = null
                        if (snapshot != null && snapshot.exists()) {
                            current = current.copy(
                                fullName = snapshot.getString("fullName") ?: "",
                                studentId = snapshot.getString("studentId") ?: "",
                                email = auth.currentUser?.email ?: snapshot.getString("email").orEmpty(),
                                course = snapshot.getString("course") ?: "",
                                yearLevel = snapshot.getString("yearLevel") ?: "",
                                difficulty = snapshot.getString("difficulty") ?: "Beginner",
                                interests = (snapshot.get("interests") as? List<*>)
                                    ?.filterIsInstance<String>()?.toSet() ?: emptySet(),
                                weakTopics = (snapshot.get("weakTopics") as? List<*>)
                                    ?.filterIsInstance<String>()?.toSet() ?: emptySet(),
                                bookmarkedCount = (snapshot.getLong("bookmarkedCount") ?: 0L).toInt(),
                                completedCount = (snapshot.getLong("completedCount") ?: 0L).toInt(),
                                quizAverage = (snapshot.getLong("quizAverage") ?: 0L).toInt(),
                                inProgressCount = (snapshot.getLong("inProgressCount") ?: 0L).toInt()
                            )
                        } else {
                            current = StudentProfileData(email = auth.currentUser?.email.orEmpty(),
                                fullName = auth.currentUser?.displayName.orEmpty())
                        }
                    }
                }
            onDispose { registration.remove() }
        }
    }

    Column(Modifier.fillMaxSize().background(profileBackground)) {
        if (loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        if (profileError != null) {
            Text(profileError.orEmpty(), color = Color(0xFFFF9AA7),
                modifier = Modifier.padding(12.dp), fontSize = 12.sp)
        }
        if (saving) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Student Profile", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Surface(
                onClick = { if (!loading && !saving && uid != null) editing = true },
                color = profilePanel,
                border = BorderStroke(1.dp, profileLine),
                shape = RoundedCornerShape(13.dp)
            ) {
                Text("✎  Edit Profile", color = Color(0xFFD6E3F8), fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp))
            }
        }
        HorizontalDivider(color = profileLine, thickness = 0.5.dp)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color = profilePanel,
                border = BorderStroke(1.dp, profileLine),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        Modifier.size(96.dp).background(
                            Brush.linearGradient(listOf(Color(0xFF573BFF), Color(0xFF00C8FF))), CircleShape
                        ).padding(2.dp).background(profilePanel, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(current.fullName.firstOrNull()?.uppercase() ?: "?", color = Color.White,
                            fontWeight = FontWeight.Bold, fontSize = 30.sp)
                        Box(Modifier.align(Alignment.BottomEnd).size(25.dp)
                            .background(profileBlue, CircleShape), contentAlignment = Alignment.Center) {
                            Text("C++", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(current.fullName, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(current.studentId, color = Color(0xFF59A7FF), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("✉  ${current.email}", color = profileMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(12.dp))
                    Surface(color = Color(0xFF202D43), shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, profileLine)) {
                        Text("♧  ${current.course} • ${current.yearLevel}",
                            color = Color(0xFFD5DFEF), fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                    }
                    Spacer(Modifier.height(18.dp))
                    HorizontalDivider(color = profileLine)
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProfileStat("♧", current.bookmarkedCount.toString(), "Bookmarked", Color(0xFFC58BFF), Modifier.weight(1f))
                        ProfileStat("▣", current.completedCount.toString(), "Completed", Color(0xFF00DBB3), Modifier.weight(1f))
                        ProfileStat("♕", "${current.quizAverage}%", "Quiz Avg", Color(0xFFFFC62B), Modifier.weight(1f))
                    }
                }
            }
            Surface(color = profilePanel, shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, profileLine), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("✧  Active Interests for TF-IDF\n    Recommendations", color = Color(0xFFD8E2F1),
                            fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Surface(color = Color(0xFF182C5B), shape = RoundedCornerShape(5.dp),
                            border = BorderStroke(1.dp, Color(0xFF284FA3))) {
                            Text(current.difficulty, color = Color(0xFF91BFFF), fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp))
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    InterestWrap(current.interests.toList(), selectable = false)
                    if (current.weakTopics.isNotEmpty()) {
                        Spacer(Modifier.height(15.dp))
                        Text("⚠  Identified weak topics (auto-boosted):  ${current.weakTopics.joinToString()}",
                            color = Color(0xFFFFC534), fontSize = 12.sp)
                    }
                }
            }
            ProfileLink("♧", "Saved Bookmarks", "${current.bookmarkedCount} books", onBookmarksClick)
            ProfileLink("↗", "Reading Progress & Milestones", "${current.inProgressCount} in progress", onProgressClick)
            Spacer(Modifier.height(4.dp))
            Surface(onClick = onLogoutClick, color = Color(0xFF230C19),
                border = BorderStroke(1.dp, Color(0xFF7F1D31)),
                shape = RoundedCornerShape(15.dp), modifier = Modifier.fillMaxWidth()) {
                Text("⇥  Logout from C++ Learn", color = Color(0xFFFF9AA7),
                    fontWeight = FontWeight.SemiBold, fontSize = 14.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.padding(19.dp))
            }
            Spacer(Modifier.height(16.dp))
        }
    }
    if (editing) {
        EditStudentProfileDialog(
            initial = current,
            onDismiss = { editing = false },
            onSave = { updated ->
                if (uid == null) {
                    profileError = "Please sign in before saving."
                } else if (!saving) {
                    saving = true
                    profileError = null
                    val changes = mapOf(
                        "fullName" to updated.fullName,
                        "course" to updated.course,
                        "difficulty" to updated.difficulty,
                        "interests" to updated.interests.toList()
                    )
                    db.collection("students").document(uid)
                        .set(changes, SetOptions.merge())
                        .addOnSuccessListener {
                            current = updated
                            onProfileSaved(updated)
                            editing = false
                            saving = false
                        }
                        .addOnFailureListener { error ->
                            profileError = "Save failed: ${error.localizedMessage}"
                            saving = false
                        }
                }
            }
        )
    }
}

@Composable
private fun ProfileStat(symbol: String, value: String, label: String, tint: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = profileField, shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, profileLine)) {
        Column(Modifier.padding(vertical = 14.dp, horizontal = 3.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(symbol, color = tint, fontSize = 23.sp)
            Spacer(Modifier.height(7.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(9.dp))
            Text(label, color = profileMuted, fontSize = 10.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ProfileLink(symbol: String, title: String, value: String, onClick: () -> Unit) {
    Surface(onClick = onClick, color = profilePanel, shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, profileLine), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(horizontal = 17.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(symbol, color = Color(0xFF8F9DFF), fontSize = 19.sp)
            Spacer(Modifier.width(12.dp))
            Text(title, color = Color(0xFFE0E8F6), fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp, modifier = Modifier.weight(1f))
            Text(value, color = profileMuted, fontSize = 12.sp)
            Text("  ›", color = profileMuted, fontSize = 22.sp)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InterestWrap(
    options: List<String>,
    selectable: Boolean,
    selected: Set<String> = emptySet(),
    onToggle: (String) -> Unit = {}
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val active = if (selectable) option in selected else true
            Surface(
                onClick = { if (selectable) onToggle(option) },
                color = if (active) Color(0xFF172B5A) else profileField,
                border = BorderStroke(1.dp, if (active) Color(0xFF2455C8) else profileLine),
                shape = RoundedCornerShape(9.dp)
            ) {
                Text((if (selectable && active) "✓  " else "") + option,
                    color = if (active) Color(0xFF9CC7FF) else Color(0xFFC5D1E3),
                    fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp))
            }
        }
    }
}

@Composable
private fun EditStudentProfileDialog(
    initial: StudentProfileData,
    onDismiss: () -> Unit,
    onSave: (StudentProfileData) -> Unit
) {
    var name by remember(initial) { mutableStateOf(initial.fullName) }
    var course by remember(initial) { mutableStateOf(initial.course) }
    var difficulty by remember(initial) { mutableStateOf(initial.difficulty) }
    var interests by remember(initial) { mutableStateOf(initial.interests) }

    Dialog(onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 14.dp)
                .fillMaxHeight(0.84f)
                .background(profilePanel, RoundedCornerShape(20.dp))
                .padding(18.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Edit Student Profile", color = Color.White, fontSize = 19.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("×", color = profileMuted, fontSize = 27.sp,
                    modifier = Modifier.clickable(onClick = onDismiss).padding(horizontal = 8.dp))
            }
            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = profileLine)
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                Spacer(Modifier.height(18.dp))
                EditLabel("Full Name")
                Spacer(Modifier.height(7.dp))
                EditTextBox(name) { name = it }
                Spacer(Modifier.height(16.dp))
                EditLabel("Degree Course")
                Spacer(Modifier.height(7.dp))
                EditTextBox(course) { course = it }
                Spacer(Modifier.height(18.dp))
                EditLabel("Preferred Difficulty Level")
                Spacer(Modifier.height(9.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    listOf("Beginner", "Intermediate", "Advanced").forEach { level ->
                        Surface(
                            onClick = { difficulty = level }, modifier = Modifier.weight(1f),
                            color = if (difficulty == level) Color(0xFF153675) else profileField,
                            border = BorderStroke(1.dp,
                                if (difficulty == level) Color(0xFF3C83FF) else profileLine),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(level, color = if (difficulty == level) Color(0xFFCCE1FF) else profileMuted,
                                fontSize = 11.sp, textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 13.dp, horizontal = 2.dp))
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                EditLabel("Topics of Interest (Adjusts Recommendation Ranking)")
                Spacer(Modifier.height(9.dp))
                Surface(color = profileField, shape = RoundedCornerShape(15.dp),
                    border = BorderStroke(1.dp, profileLine), modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.padding(10.dp)) {
                        InterestWrap(interestOptions, selectable = true, selected = interests,
                            onToggle = { option ->
                                interests = if (option in interests) interests - option else interests + option
                            })
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            HorizontalDivider(color = profileLine)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF223047)),
                    shape = RoundedCornerShape(13.dp)) {
                    Text("Cancel", fontSize = 12.sp)
                }
                Button(
                    onClick = {
                        onSave(initial.copy(fullName = name.trim(), course = course.trim(),
                            difficulty = difficulty, interests = interests))
                    },
                    enabled = name.isNotBlank() && course.isNotBlank(),
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = profileBlue),
                    shape = RoundedCornerShape(13.dp)
                ) {
                    Text("Save Changes & Recalculate", fontSize = 11.sp, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun EditLabel(text: String) {
    Text(text, color = Color(0xFFD8E5F8), fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
}

@Composable
private fun EditTextBox(value: String, onValueChange: (String) -> Unit) {
    Surface(color = profileField, shape = RoundedCornerShape(13.dp),
        border = BorderStroke(1.dp, profileLine), modifier = Modifier.fillMaxWidth()) {
        BasicTextField(
            value = value, onValueChange = onValueChange, singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 14.dp)
        )
    }
}
