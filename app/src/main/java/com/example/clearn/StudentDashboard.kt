
package com.example.clearn

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// APP COLORS
private val Navy = Color(0xFF050B1E)
private val PanelColor = Color(0xFF101A30)
private val Blue = Color(0xFF2685FF)
private val Muted = Color(0xFF99A9C4)
private val Orange = Color(0xFFFFB900)
private val Cyan = Color(0xFF22D3EE)

// FIRESTORE DAILY TIP DATA
private data class DailyCppTip(
    val title: String = "",
    val topic: String = "",
    val description: String = "",
    val code: String = "",
    val date: String = ""
)

private enum class StudentPage {
    HOME, LIBRARY, QUIZ, TIPS, BOOKMARKS, PROFILE
}

@Composable
fun StudentDashboard(
    onLogout: () -> Unit = {}
) {
    var page by remember {
        mutableStateOf(StudentPage.HOME)
    }

    Scaffold(
        containerColor = Navy,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF0E172A)
            ) {
                val items = listOf(
                    Triple(
                        StudentPage.HOME,
                        "Home",
                        Icons.Outlined.Home
                    ),
                    Triple(
                        StudentPage.LIBRARY,
                        "Library",
                        Icons.Outlined.MenuBook
                    ),
                    Triple(
                        StudentPage.QUIZ,
                        "Quiz",
                        Icons.Outlined.CheckCircle
                    ),
                    Triple(
                        StudentPage.TIPS,
                        "Tips",
                        Icons.Outlined.Lightbulb
                    ),
                    Triple(
                        StudentPage.PROFILE,
                        "Profile",
                        Icons.Outlined.Person
                    )
                )

                items.forEach { (destination, label, icon) ->
                    NavigationBarItem(
                        selected = page == destination,
                        onClick = {
                            page = destination
                        },
                        icon = {
                            Icon(
                                icon,
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(
                                label,
                                fontSize = 10.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Blue,
                            selectedTextColor = Color(0xFF93C5FD),
                            indicatorColor = Color(0xFF132B56),
                            unselectedIconColor = Muted,
                            unselectedTextColor = Muted
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (page) {
                StudentPage.HOME -> DashboardHome(
                    onNavigate = { page = it }
                )

                StudentPage.LIBRARY -> EBookLibraryScreen()

                StudentPage.QUIZ -> EmptyPage(
                    "C++ Quiz",
                    "Book-related quizzes will unlock after you finish reading their corresponding e-books."
                )

                StudentPage.TIPS -> TipsPage()

                StudentPage.BOOKMARKS -> EmptyPage(
                    "My Bookmarks",
                    "You haven't bookmarked any C++ books yet."
                )

                StudentPage.PROFILE -> StudentProfileScreen(
                    onLogoutClick = {
                        // Connect your existing logout function here.
                    }
                )
            }
        }
    }
}

// =====================================
// HOME DASHBOARD
// =====================================

@Composable
private fun DashboardHome(
    onNavigate: (StudentPage) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser

    val displayName = user?.displayName
        ?.substringBefore(" ")
        ?.takeIf { it.isNotBlank() }
        ?: "Student"

    var search by remember {
        mutableStateOf("")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {

        // STUDENT GREETING
        item {
            Column {
                Text(
                    "B.S. COMPUTER SCIENCE",
                    color = Muted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Hello, $displayName! 👋",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // SEARCH BAR
        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Search C++ books, topics, or authors...",
                        color = Muted,
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = Blue
                    )
                },
                trailingIcon = {
                    TextButton(
                        onClick = {
                            onNavigate(StudentPage.LIBRARY)
                        }
                    ) {
                        Text("Find")
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(17.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = PanelColor,
                    unfocusedContainerColor = PanelColor,
                    focusedBorderColor = Blue,
                    unfocusedBorderColor = Color(0xFF243149)
                )
            )
        }

        // QUICK ACTIONS
        item {
            Column {
                Text(
                    "QUICK ACTIONS",
                    color = Muted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickAction(
                        title = "E-Book",
                        subtitle = "Library",
                        icon = Icons.Outlined.MenuBook,
                        accent = Blue,
                        modifier = Modifier.weight(1f)
                    ) {
                        onNavigate(StudentPage.LIBRARY)
                    }

                    QuickAction(
                        title = "C++ Quiz",
                        subtitle = "Practice",
                        icon = Icons.Outlined.CheckCircle,
                        accent = Color(0xFF00D6A3),
                        modifier = Modifier.weight(1f)
                    ) {
                        onNavigate(StudentPage.QUIZ)
                    }

                    QuickAction(
                        title = "Tips &",
                        subtitle = "Tricks",
                        icon = Icons.Outlined.Lightbulb,
                        accent = Orange,
                        modifier = Modifier.weight(1f)
                    ) {
                        onNavigate(StudentPage.TIPS)
                    }

                    QuickAction(
                        title = "Bookmarks",
                        subtitle = "Saved",
                        icon = Icons.Outlined.BookmarkBorder,
                        accent = Color(0xFFBB73FF),
                        modifier = Modifier.weight(1f)
                    ) {
                        onNavigate(StudentPage.BOOKMARKS)
                    }
                }
            }
        }

        // READING PROGRESS
        item {
            DashboardPanel {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "◉  Reading Progress",
                        color = Blue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        "No reading progress yet",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        "Start reading a C++ book to track your progress.",
                        color = Muted,
                        fontSize = 12.sp
                    )

                    LinearProgressIndicator(
                        progress = { 0f },
                        modifier = Modifier.fillMaxWidth(),
                        color = Blue,
                        trackColor = Color(0xFF29354B)
                    )
                }
            }
        }

        // RECOMMENDED BOOKS
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "✧ Recommended Books",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    TextButton(
                        onClick = {
                            onNavigate(StudentPage.LIBRARY)
                        }
                    ) {
                        Text(
                            "See All ›",
                            color = Blue
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                DashboardPanel {
                    Text(
                        "Personalized C++ book recommendations will appear here once books are added.",
                        color = Muted,
                        fontSize = 13.sp,
                        lineHeight = 21.sp
                    )
                }
            }
        }

        // FIREBASE AI-GENERATED TIP OF THE DAY
        item {
            TipOfTheDayCard(
                onExploreTips = {
                    onNavigate(StudentPage.TIPS)
                }
            )
        }

        // QUIZ PROMOTION
        item {
            QuizPromotionCard(
                onStartQuiz = {
                    onNavigate(StudentPage.QUIZ)
                }
            )
        }
    }
}

// =====================================
// FIREBASE AI TIP OF THE DAY
// =====================================

@Composable
private fun TipOfTheDayCard(
    onExploreTips: () -> Unit
) {
    var dailyTip by remember {
        mutableStateOf<DailyCppTip?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    // Listen to daily_tips/current in Firestore
    DisposableEffect(Unit) {
        val listener = FirebaseFirestore.getInstance()
            .collection("daily_tips")
            .document("current")
            .addSnapshotListener { snapshot, error ->

                isLoading = false

                if (error != null) {
                    errorMessage =
                        "Unable to load today's tip. Please check your connection."
                } else if (snapshot != null && snapshot.exists()) {
                    dailyTip = DailyCppTip(
                        title = snapshot.getString("title").orEmpty(),
                        topic = snapshot.getString("topic").orEmpty(),
                        description = snapshot.getString("description").orEmpty(),
                        code = snapshot.getString("code").orEmpty(),
                        date = snapshot.getString("date").orEmpty()
                    )

                    errorMessage = null
                } else {
                    dailyTip = null
                    errorMessage = "No daily tip available yet."
                }
            }

        onDispose {
            listener.remove()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF101525)
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFF71350D)
        )
    ) {
        Column(
            modifier = Modifier.padding(19.dp)
        ) {

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = Orange,
                        modifier = Modifier.size(20.dp)
                    )

                    Text(
                        "C++ Tip of the Day",
                        color = Orange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                if (dailyTip != null && errorMessage == null) {
                    androidx.compose.material3.Surface(
                        shape = RoundedCornerShape(30.dp),
                        color = Color(0xFF42200F),
                        border = BorderStroke(
                            1.dp,
                            Color(0xFF9A4309)
                        )
                    ) {
                        Text(
                            dailyTip!!.topic,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            color = Color(0xFFFFC020),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(Modifier.height(17.dp))

            when {
                isLoading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Orange,
                            strokeWidth = 2.dp
                        )

                        Text(
                            "Loading today's AI-generated C++ tip...",
                            color = Muted,
                            fontSize = 13.sp
                        )
                    }
                }

                errorMessage != null -> {
                    Text(
                        errorMessage.orEmpty(),
                        color = Color(0xFFFFB4AB),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }

                dailyTip != null -> {
                    val tip = dailyTip!!

                    // TIP TITLE
                    Text(
                        tip.title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 23.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    // TIP DESCRIPTION
                    Text(
                        tip.description,
                        color = Color(0xFFCBD5E1),
                        fontSize = 14.sp,
                        lineHeight = 24.sp
                    )

                    Spacer(Modifier.height(14.dp))

                    // AI-GENERATED CODE EXAMPLE
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(
                                min = 100.dp,
                                max = 260.dp
                            )
                            .background(
                                Color(0xFF03091C),
                                RoundedCornerShape(15.dp)
                            )
                            .border(
                                1.dp,
                                Color(0xFF1E293B),
                                RoundedCornerShape(15.dp)
                            )
                            .horizontalScroll(rememberScrollState())
                            .verticalScroll(rememberScrollState())
                            .padding(13.dp)
                    ) {
                        Text(
                            text = tip.code,
                            color = Cyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 20.sp,
                            softWrap = false
                        )
                    }

                    Spacer(Modifier.height(15.dp))

                    // CARD FOOTER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                "Topic: ${tip.topic}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            if (tip.date.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))

                                Text(
                                    "Updated: ${tip.date}",
                                    color = Muted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        TextButton(
                            onClick = onExploreTips,
                            contentPadding = PaddingValues(
                                horizontal = 4.dp,
                                vertical = 4.dp
                            )
                        ) {
                            Text(
                                "Explore Tips →",
                                color = Orange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// =====================================
// TEST YOUR C++ KNOWLEDGE CARD
// =====================================

@Composable
private fun QuizPromotionCard(
    onStartQuiz: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0C1539)
        ),
        border = BorderStroke(
            1.dp,
            Color(0xFF1E40AF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                androidx.compose.material3.Surface(
                    color = Color(0xFF1B3475),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        "✓ Adaptive Evaluation",
                        color = Color(0xFF93C5FD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        )
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(
                    "Test Your C++ Knowledge",
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(5.dp))

                Text(
                    "Quizzes detect weak topics to refine and boost your book recommendations!",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            Button(
                onClick = onStartQuiz,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563FF)
                ),
                contentPadding = PaddingValues(
                    horizontal = 15.dp,
                    vertical = 12.dp
                )
            ) {
                Text(
                    "Start Quiz",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// =====================================
// QUICK ACTION BUTTON
// =====================================

@Composable
private fun QuickAction(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .height(145.dp)
            .background(
                PanelColor,
                RoundedCornerShape(18.dp)
            )
            .border(
                1.dp,
                Color(0xFF223049),
                RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick)
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    accent.copy(alpha = 0.13f),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = accent
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            title,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            subtitle,
            color = Muted,
            fontSize = 10.sp,
            textAlign = TextAlign.Center
        )
    }
}

// =====================================
// REUSABLE DASHBOARD PANEL
// =====================================

@Composable
private fun DashboardPanel(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                PanelColor,
                RoundedCornerShape(20.dp)
            )
            .border(
                1.dp,
                Color(0xFF223049),
                RoundedCornerShape(20.dp)
            )
            .padding(18.dp),
        content = content
    )
}

// =====================================
// EMPTY PAGE PLACEHOLDER
// =====================================

@Composable
private fun EmptyPage(
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            title,
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(14.dp))

        Text(
            message,
            color = Muted,
            fontSize = 14.sp,
            lineHeight = 23.sp,
            textAlign = TextAlign.Center
        )
    }
}

// =====================================
// C++ TIPS & TRICKS PAGE
// =====================================

private fun tipCategory(tip: DailyCppTip): String {
    val text = "${tip.title} ${tip.topic} ${tip.description}".lowercase()
    return when {
        listOf("pointer", "unique_ptr", "shared_ptr", "memory", "new/delete")
            .any { it in text } -> "Pointer Tips"
        listOf("vector", "iterator", "container", "std::size", "stl", "std::map")
            .any { it in text } -> "STL"
        listOf("mistake", "pitfall", "undefined behavior", "bug", "avoid")
            .any { it in text } -> "Common Mistakes"
        listOf("modern c++", "constexpr", "lambda", "structured binding", "concepts")
            .any { it in text } -> "Modern C++"
        else -> "Best Practices"
    }
}

@Composable
private fun TipsPage() {
    val db = remember { FirebaseFirestore.getInstance() }
    var currentTip by remember { mutableStateOf<DailyCppTip?>(null) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Reads the same Firestore document as the Home Tip of the Day card.
    DisposableEffect(db) {
        val registration = db.collection("daily_tips")
            .document("current")
            .addSnapshotListener { snapshot, exception ->
                loading = false
                if (exception != null) {
                    error = "Could not load tips: ${exception.localizedMessage}"
                } else {
                    error = null
                    currentTip = if (snapshot?.exists() == true) {
                        DailyCppTip(
                            title = snapshot.getString("title").orEmpty(),
                            topic = snapshot.getString("topic").orEmpty(),
                            description = snapshot.getString("description").orEmpty(),
                            code = snapshot.getString("code").orEmpty(),
                            date = snapshot.getString("date").orEmpty()
                        )
                    } else null
                }
            }
        onDispose { registration.remove() }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 13.dp, end = 13.dp, top = 18.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        item {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Text(
                        "C++ Tips & Tricks",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Icon(
                        Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = Orange,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(Modifier.height(5.dp))
                Text(
                    "Quick insights, modern conventions, and pitfalls to avoid",
                    color = Muted,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }

        item { HorizontalDivider(color = Color(0xFF1E293B)) }

        when {
            loading -> item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Orange)
                }
            }
            error != null -> item {
                DashboardPanel {
                    Text(error.orEmpty(), color = Color(0xFFFFB4AB), fontSize = 13.sp)
                }
            }
            currentTip != null -> item { CppTipDetailCard(currentTip!!) }
            else -> item {
                DashboardPanel {
                    Text(
                        "No AI-generated tip available yet.",
                        color = Muted,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CppTipDetailCard(tip: DailyCppTip) {
    val clipboard = LocalClipboardManager.current
    val category = tipCategory(tip)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF101A30)),
        border = BorderStroke(1.dp, Color(0xFF25334C))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFF16295B), RoundedCornerShape(7.dp))
                        .border(1.dp, Color(0xFF2563EB), RoundedCornerShape(7.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        tip.topic.ifBlank { "C++" },
                        color = Color(0xFF93C5FD),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF3B1D0A), RoundedCornerShape(40.dp))
                        .border(1.dp, Color(0xFF92400E), RoundedCornerShape(40.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(category, color = Orange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                tip.title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 23.sp
            )
            Spacer(Modifier.height(9.dp))
            Text(
                tip.description,
                color = Color(0xFFCBD5E1),
                fontSize = 13.sp,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF050B1E), RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFF293B59), RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFF111C33),
                            RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
                        )
                        .padding(start = 13.dp, end = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "</>  C++ Snippet",
                        color = Color(0xFFB5C7E7),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    TextButton(onClick = { clipboard.setText(AnnotatedString(tip.code)) }) {
                        Icon(
                            Icons.Outlined.ContentCopy,
                            contentDescription = "Copy C++ code",
                            tint = Blue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text("Copy", color = Blue, fontSize = 12.sp)
                    }
                }
                HorizontalDivider(color = Color(0xFF334155))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(14.dp)
                ) {
                    Text(
                        tip.code.ifBlank { "// No code example available" },
                        color = Cyan,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 19.sp,
                        softWrap = false
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "Topic: ${tip.topic.ifBlank { "C++" }}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (tip.date.isNotBlank()) {
                Spacer(Modifier.height(5.dp))
                Text("Updated: ${tip.date}", color = Muted, fontSize = 11.sp)
            }
        }
    }
}

// =====================================
// STUDENT PROFILE PAGE
// =====================================

@Composable
private fun ProfilePage(
    onLogout: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            "Student Profile",
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        DashboardPanel {
            Text(
                user?.displayName ?: "Student",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                user?.email ?: "No email available",
                color = Muted
            )
        }

        Button(
            onClick = {
                auth.signOut()
                onLogout()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E5BFF)
            )
        ) {
            Text("Log Out")
        }
    }
}
