package com.example.clearn

import androidx.compose.ui.text.TextStyle


import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private val Navy = Color(0xFF060B1D)
private val Panel = Color(0xFF10182B)
private val ChipBg = Color(0xFF1E2A40)
private val Blue = Color(0xFF195FFF)
private val Muted = Color(0xFF94A3B8)
private val White = Color(0xFFF8FAFC)

data class LibraryBook(
    val id: Int,
    val title: String,
    val author: String,
    val topic: String,
    val difficulty: String,
    val year: Int,
    val description: String = ""
)

// No books are included yet. Pass your real dataset later.
private val emptyLibraryBooks: List<LibraryBook> = emptyList()

private val topics = listOf(
    "All Topics",
    "C++ Fundamentals",
    "Variables and Data Types",
    "Functions",
    "Arrays",
    "Pointers",
    "Object-Oriented Programming",
    "Classes",
    "Inheritance",
    "Polymorphism",
    "STL",
    "Data Structures"
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EBookLibraryScreen(
    books: List<LibraryBook> = emptyLibraryBooks,
    onBookClick: (LibraryBook) -> Unit = {}
) {
    var search by remember { mutableStateOf("") }
    var selectedTopic by remember { mutableStateOf("All Topics") }
    var selectedDifficulty by remember { mutableStateOf("All") }
    var selectedYear by remember { mutableStateOf<Int?>(null) }
    var showFilter by remember { mutableStateOf(false) }
    var sortExpanded by remember { mutableStateOf(false) }
    var sortMode by remember { mutableStateOf("Recommended (TF-IDF)") }

    val filteredBooks = remember(
        books, search, selectedTopic, selectedDifficulty,
        selectedYear, sortMode
    ) {
        val matching = books.filter { book ->
            val matchesSearch = search.isBlank() ||
                    listOf(
                        book.title, book.author, book.topic,
                        book.description, book.difficulty,
                        book.year.toString()
                    ).any { it.contains(search.trim(), ignoreCase = true) }

            matchesSearch &&
                    (selectedTopic == "All Topics" || book.topic == selectedTopic) &&
                    (selectedDifficulty == "All" || book.difficulty == selectedDifficulty) &&
                    (selectedYear == null || book.year == selectedYear)
        }

        when (sortMode) {
            "Title (A-Z)" -> matching.sortedBy { it.title }
            "Newest First" -> matching.sortedByDescending { it.year }
            "Oldest First" -> matching.sortedBy { it.year }
            else -> matching
        }
    }

    // TF-IDF ordering will be connected when recommendation scores exist.
    // For now, Recommended retains the original dataset order.
    val displayedBooks = filteredBooks

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Navy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "C++ E-Book Library",
                        color = White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Browse ${books.size} C++ academic e-books",
                        color = Muted,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(Modifier.width(8.dp))

                Surface(
                    onClick = { sortMode = "Recommended (TF-IDF)" },
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF152C60),
                    border = BorderStroke(1.dp, Blue)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp, vertical = 10.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF60A5FA),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Recommended",
                            color = White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .background(Panel, RoundedCornerShape(16.dp))
                        .border(
                            1.dp,
                            Color(0xFF202B43),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Search,
                        contentDescription = "Search",
                        tint = Muted
                    )
                    Spacer(Modifier.width(10.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (search.isEmpty()) {
                            Text(
                                "Search by title, author, topic, keyword...",
                                color = Color(0xFF64748B),
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                        BasicTextField(
                            value = search,
                            onValueChange = { search = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = White,
                                fontSize = 14.sp
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Surface(
                    onClick = { showFilter = true },
                    shape = RoundedCornerShape(16.dp),
                    color = Panel,
                    border = BorderStroke(1.dp, Color(0xFF202B43)),
                    modifier = Modifier.size(52.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Tune,
                            contentDescription = "Filter e-books",
                            tint = Muted
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topics.forEach { topic ->
                    TopicChip(
                        text = topic,
                        selected = selectedTopic == topic,
                        onClick = { selectedTopic = topic }
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
        }

        HorizontalDivider(color = Color(0xFF1E293B))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Showing ${displayedBooks.size} C++ e-books",
                        color = Muted,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Box {
                        Surface(
                            onClick = { sortExpanded = true },
                            color = Panel,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp, Color(0xFF26334C)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 9.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    sortMode,
                                    color = White,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                                Icon(
                                    Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = "Sort",
                                    tint = White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false }
                        ) {
                            listOf(
                                "Recommended (TF-IDF)",
                                "Title (A-Z)",
                                "Newest First",
                                "Oldest First"
                            ).forEach { mode ->
                                DropdownMenuItem(
                                    text = { Text(mode) },
                                    onClick = {
                                        sortMode = mode
                                        sortExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (displayedBooks.isEmpty()) {
                item {
                    Text(
                        if (books.isEmpty()) "No C++ e-books added yet." else "No e-books match your filters.",
                        color = Muted,
                        modifier = Modifier.padding(vertical = 30.dp)
                    )
                }
            }

            items(displayedBooks, key = { it.id }) { book ->
                Surface(
                    onClick = { onBookClick(book) },
                    color = Panel,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp, Color(0xFF253149)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            book.title,
                            color = White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            "By ${book.author}",
                            color = Muted,
                            fontSize = 13.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "${book.topic}  •  ${book.difficulty}  •  ${book.year}",
                            color = Color(0xFF60A5FA),
                            fontSize = 12.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }

    if (showFilter) {
        FilterEBooksDialog(
            books = books,
            initialTopic = selectedTopic,
            initialDifficulty = selectedDifficulty,
            initialYear = selectedYear,
            search = search,
            onDismiss = { showFilter = false },
            onApply = { topic, difficulty, year ->
                selectedTopic = topic
                selectedDifficulty = difficulty
                selectedYear = year
                showFilter = false
            }
        )
    }
}

@Composable
private fun TopicChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) Blue else Panel,
        shape = RoundedCornerShape(24.dp)
    ) {
        Text(
            text = text,
            color = if (selected) White else Muted,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 9.dp
            ),
            maxLines = 1
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterEBooksDialog(
    books: List<LibraryBook>,
    initialTopic: String,
    initialDifficulty: String,
    initialYear: Int?,
    search: String,
    onDismiss: () -> Unit,
    onApply: (String, String, Int?) -> Unit
) {
    var topic by remember(initialTopic) {
        mutableStateOf(initialTopic)
    }
    var difficulty by remember(initialDifficulty) {
        mutableStateOf(initialDifficulty)
    }
    var year by remember(initialYear) {
        mutableStateOf(initialYear)
    }

    val resultCount = books.count { book ->
        (topic == "All Topics" || book.topic == topic) &&
                (difficulty == "All" || book.difficulty == difficulty) &&
                (year == null || book.year == year) &&
                (search.isBlank() || listOf(
                    book.title, book.author, book.topic,
                    book.description, book.difficulty,
                    book.year.toString()
                ).any { it.contains(search.trim(), ignoreCase = true) })
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .fillMaxHeight(0.92f)
                .background(
                    Panel,
                    RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Outlined.FilterAlt,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA)
                )

                Spacer(Modifier.width(10.dp))

                Text(
                    "Filter C++ E-Books",
                    color = White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = Muted
                    )
                }
            }

            HorizontalDivider(
                color = Color(0xFF253149),
                modifier = Modifier.padding(vertical = 14.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                FilterHeading("Filter by C++ Topic")

                Spacer(Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    topics.forEach { item ->
                        FilterChipItem(
                            text = item,
                            selected = topic == item,
                            onClick = { topic = item }
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                FilterHeading("Difficulty Level")

                Spacer(Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("All", "Beginner", "Intermediate", "Advanced")
                        .forEach { item ->
                            FilterChipItem(
                                text = item,
                                selected = difficulty == item,
                                onClick = { difficulty = item }
                            )
                        }
                }

                Spacer(Modifier.height(28.dp))

                FilterHeading("Publication Year (2021 – 2026)")

                Spacer(Modifier.height(12.dp))

                val years = listOf<Int?>(
                    null, 2026, 2025, 2024, 2023, 2022, 2021
                )

                years.chunked(4).forEach { rowYears ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowYears.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) {
                                FilterChipItem(
                                    text = item?.toString() ?: "All Years",
                                    selected = year == item,
                                    onClick = { year = item },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        repeat(4 - rowYears.size) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            HorizontalDivider(
                color = Color(0xFF253149),
                modifier = Modifier.padding(vertical = 14.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        topic = "All Topics"
                        difficulty = "All"
                        year = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChipBg
                    ),
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier.height(52.dp)
                ) {
                    Text("Reset", color = White)
                }

                Button(
                    onClick = {
                        onApply(topic, difficulty, year)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Blue
                    ),
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                ) {
                    Text(
                        "Show $resultCount Results",
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterHeading(text: String) {
    Text(
        text = text,
        color = Color(0xFFCBD5E1),
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun FilterChipItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = if (selected) Color(0xFF123675) else ChipBg,
        border = BorderStroke(
            1.dp,
            if (selected) Color(0xFF3B82F6)
            else Color(0xFF2A3951)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 12.dp
            )
        ) {
            Text(
                text = text,
                color = if (selected) Color(0xFFBFDBFE) else Muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
