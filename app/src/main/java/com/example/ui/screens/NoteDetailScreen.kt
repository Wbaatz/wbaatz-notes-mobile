package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.NoteProgress
import com.example.data.model.PdfNote
import com.example.ui.components.WbaatzAdBanner
import com.example.ui.components.WbaatzAdInterstitial
import com.example.ui.components.GoogleAdMobPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: PdfNote,
    progress: NoteProgress?,
    onBack: () -> Unit,
    onSaveProgress: (NoteProgress) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Interactive Ads state
    var showInterstitialAd by remember { mutableStateOf(true) }

    // PDF Reader states
    var currentPageIndex by remember { mutableStateOf(0) }
    val pageCount = note.contentPages.size

    // Viewer Mode: true = Single Page, false = Continuous Scroll
    var isSinglePageMode by remember { mutableStateOf(true) }

    // Zoom scale: 1.0f (Normal), 1.25f (Large), 1.5f (Extra Large), 0.85f (Compact)
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    // Text Search state in PDF Doc
    var searchDocQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Bookmark and Completion states (initialized from progress)
    var isBookmarked by remember(progress) { mutableStateOf(progress?.isBookmarked == true) }
    var isCompleted by remember(progress) { mutableStateOf(progress?.isCompleted == true) }

    // Notepad state
    var notepadText by remember(progress) { mutableStateOf(progress?.userNotes ?: "") }
    var showNotepad by remember { mutableStateOf(false) }

    // Interstitial Ad Display (Simulated Google Mobile Ads)
    WbaatzAdInterstitial(
        show = showInterstitialAd,
        onDismiss = { showInterstitialAd = false }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = note.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                        Text(
                            text = "PDF Document Viewer",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.75f))
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    // Search toggle button
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.SearchOff else Icons.Default.Search,
                            contentDescription = "Search text",
                            tint = Color.White
                        )
                    }

                    // Bookmark action
                    IconButton(
                        onClick = {
                            isBookmarked = !isBookmarked
                            val updatedProgress = (progress ?: NoteProgress(noteId = note.id)).copy(
                                isBookmarked = isBookmarked
                            )
                            onSaveProgress(updatedProgress)
                            Toast.makeText(
                                context,
                                if (isBookmarked) "Saved to bookmarks!" else "Removed from bookmarks",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        modifier = Modifier.testTag("detail_bookmark_${note.id}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = Color.White
                        )
                    }

                    // Download PDF action
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(note.pdfUrl))
                            context.startActivity(intent)
                            Toast.makeText(context, "Opening source document in browser...", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download, 
                            contentDescription = "Download source",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary // Green
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFE8F5E9)) // Light sage green viewer background
        ) {
            // Text Search Bar (Optional overlay)
            AnimatedVisibility(visible = isSearchActive) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FindInPage,
                            contentDescription = "Find text",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        OutlinedTextField(
                            value = searchDocQuery,
                            onValueChange = { searchDocQuery = it },
                            placeholder = { Text("Find text on current page...") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(8.dp),
                            trailingIcon = {
                                if (searchDocQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchDocQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // PDF Control Bar (Zoom, Scroll Mode)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Page Display Controls (Only if in Single Page Mode)
                    if (isSinglePageMode) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { if (currentPageIndex > 0) currentPageIndex-- },
                                enabled = currentPageIndex > 0
                            ) {
                                Icon(Icons.Default.KeyboardArrowLeft, "Prev")
                            }
                            Text(
                                text = "Page ${currentPageIndex + 1} of $pageCount",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            IconButton(
                                onClick = { if (currentPageIndex < pageCount - 1) currentPageIndex++ },
                                enabled = currentPageIndex < pageCount - 1
                            ) {
                                Icon(Icons.Default.KeyboardArrowRight, "Next")
                            }
                        }
                    } else {
                        Text(
                            text = "Continuous Scroll Mode",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    // Viewer Controls: Zoom and Mode Toggles
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Zoom Out
                        IconButton(
                            onClick = {
                                when (zoomScale) {
                                    1.5f -> zoomScale = 1.25f
                                    1.25f -> zoomScale = 1.0f
                                    1.0f -> zoomScale = 0.85f
                                }
                            },
                            enabled = zoomScale > 0.85f
                        ) {
                            Icon(Icons.Default.ZoomOut, "Zoom Out", modifier = Modifier.size(18.dp))
                        }

                        // Zoom Level Indicator
                        Text(
                            text = "${(zoomScale * 100).toInt()}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Zoom In
                        IconButton(
                            onClick = {
                                when (zoomScale) {
                                    0.85f -> zoomScale = 1.0f
                                    1.0f -> zoomScale = 1.25f
                                    1.25f -> zoomScale = 1.5f
                                }
                            },
                            enabled = zoomScale < 1.5f
                        ) {
                            Icon(Icons.Default.ZoomIn, "Zoom In", modifier = Modifier.size(18.dp))
                        }

                        // Toggle Layout Mode: Continuous vs Page Swiping
                        IconButton(
                            onClick = { isSinglePageMode = !isSinglePageMode }
                        ) {
                            Icon(
                                imageVector = if (isSinglePageMode) Icons.Default.ListAlt else Icons.Default.Layers,
                                contentDescription = "Scroll Mode",
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Document Scroll Space
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // PDF Document Pages container
                if (isSinglePageMode) {
                    val page = note.contentPages[currentPageIndex]
                    PdfPageSheet(
                        pageNumber = page.pageNumber,
                        title = page.title,
                        body = page.body,
                        codeSnippet = page.codeSnippet,
                        zoomScale = zoomScale,
                        searchQuery = searchDocQuery,
                        context = context
                    )
                } else {
                    // Continuous mode: Render all pages together separated by a realistic margin
                    note.contentPages.forEach { page ->
                        PdfPageSheet(
                            pageNumber = page.pageNumber,
                            title = page.title,
                            body = page.body,
                            codeSnippet = page.codeSnippet,
                            zoomScale = zoomScale,
                            searchQuery = searchDocQuery,
                            context = context
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Video Lecture Quick Link
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFE0E0), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Source Lecture",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )
                                Text(
                                    text = "Watch explanatory video tutorial",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(note.youtubeUrl))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary, // Green button
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Play Video", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lesson Progress Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Syllabus Progress Tracker",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Mark this study guide as completed when you finish analyzing this topic.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = { checked ->
                                isCompleted = checked
                                val updatedProgress = (progress ?: NoteProgress(noteId = note.id)).copy(
                                    isCompleted = isCompleted
                                )
                                onSaveProgress(updatedProgress)
                                Toast.makeText(
                                    context,
                                    if (checked) "Marked as completed! Excellent work! 🚀" else "Removed from completed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.testTag("checkbox_complete_${note.id}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Study Notes Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showNotepad = !showNotepad }
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "My Personal Exam Notes",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )
                                Text(
                                    text = "Write algorithms, pseudocode drafts, or summaries.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        }

                        Icon(
                            imageVector = if (showNotepad) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }

                // Notepad Expandable Notepad area
                AnimatedVisibility(visible = showNotepad) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            OutlinedTextField(
                                value = notepadText,
                                onValueChange = { notepadText = it },
                                placeholder = { Text("Draft your algorithms, pseudocode solutions, or exam formulas here. Your inputs are saved securely in local storage.") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .testTag("study_notepad_input"),
                                maxLines = 10
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val updatedProgress = (progress ?: NoteProgress(noteId = note.id)).copy(
                                        userNotes = notepadText
                                    )
                                    onSaveProgress(updatedProgress)
                                    Toast.makeText(context, "Syllabus notes saved persistently!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .testTag("save_notepad_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary, // Green button
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Draft Notes")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Bottom Banner Ad (Google AdMob simulation)
            GoogleAdMobPlaceholder(modifier = Modifier.padding(horizontal = 16.dp))
            WbaatzAdBanner()
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

/**
 * Renders a single PDF Page Sheet with beautiful document texture, subtle shadow, and page header.
 */
@Composable
fun PdfPageSheet(
    pageNumber: Int,
    title: String,
    body: String,
    codeSnippet: String?,
    zoomScale: Float,
    searchQuery: String,
    context: Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, shape = RoundedCornerShape(4.dp))
            .border(0.5.dp, Color.LightGray, RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White // Pure white page background
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // PDF Page Header Line
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "A Level / O Level CS Exam Notes",
                    fontSize = (10 * zoomScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Text(
                    text = "Page $pageNumber",
                    fontSize = (10 * zoomScale).sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            HorizontalDivider(
                color = Color(0xFFEEEEEE),
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // PDF Document Title
            Text(
                text = title,
                fontSize = (20 * zoomScale).sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                lineHeight = (26 * zoomScale).sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Body text with highlighted search query if applicable
            val highlightedText = remember(body, searchQuery) {
                buildAnnotatedString {
                    if (searchQuery.isBlank() || !body.contains(searchQuery, ignoreCase = true)) {
                        append(body)
                    } else {
                        var currentIndex = 0
                        val lowerBody = body.lowercase()
                        val lowerSearch = searchQuery.lowercase()
                        
                        while (currentIndex < body.length) {
                            val nextIndex = lowerBody.indexOf(lowerSearch, currentIndex)
                            if (nextIndex == -1) {
                                append(body.substring(currentIndex))
                                break
                            } else {
                                append(body.substring(currentIndex, nextIndex))
                                withStyle(style = SpanStyle(background = Color.Yellow, color = Color.Black, fontWeight = FontWeight.Bold)) {
                                    append(body.substring(nextIndex, nextIndex + searchQuery.length))
                                }
                                currentIndex = nextIndex + searchQuery.length
                            }
                        }
                    }
                }
            }

            Text(
                text = highlightedText,
                fontSize = (14 * zoomScale).sp,
                color = Color(0xFF212121), // Charcoal black print color
                lineHeight = (22 * zoomScale).sp
            )

            // Code Snippet container
            codeSnippet?.let { code ->
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2D302E), RoundedCornerShape(6.dp))
                        .border(1.dp, Color.DarkGray, RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SYLLABUS CODE REFERENCE",
                                color = Color.LightGray,
                                fontSize = (9 * zoomScale).sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )

                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Copied Syllabus Code", code)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Syllabus code copied to clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy code",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = code,
                            color = Color(0xFFA5D6A7), // Greenish syntax text
                            fontFamily = FontFamily.Monospace,
                            fontSize = (11 * zoomScale).sp,
                            lineHeight = (15 * zoomScale).sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
