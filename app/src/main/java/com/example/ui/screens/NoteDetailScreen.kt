package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.NoteProgress
import com.example.data.model.PdfNote
import com.example.ui.components.WbaatzAdBanner
import com.example.ui.components.WbaatzAdInterstitial
import com.example.ui.components.GoogleAdMobPlaceholder
import com.example.ui.components.PdfViewer
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    note: PdfNote,
    progress: NoteProgress?,
    onBack: () -> Unit,
    onSaveProgress: (NoteProgress) -> Unit,
    accessToken: String? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Interactive Ads state
    var showInterstitialAd by remember { mutableStateOf(true) }

    // Bookmark and Completion states
    var isBookmarked by remember(progress) { mutableStateOf(progress?.isBookmarked == true) }
    var isCompleted by remember(progress) { mutableStateOf(progress?.isCompleted == true) }

    // Notepad state
    var notepadText by remember(progress) { mutableStateOf(progress?.userNotes ?: "") }
    var showNotepad by remember { mutableStateOf(false) }

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
                            text = if (note.isLiveApi) "Live PDF Viewer" else "Syllabus Guide",
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
                    IconButton(
                        onClick = {
                            isBookmarked = !isBookmarked
                            onSaveProgress((progress ?: NoteProgress(noteId = note.id)).copy(isBookmarked = isBookmarked))
                            Toast.makeText(context, if (isBookmarked) "Saved!" else "Removed", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("detail_bookmark_${note.id}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(note.pdfUrl))
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(Icons.Default.OpenInNew, "Open External", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFE8F5E9))
        ) {
            // PDF Viewer Area
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (note.localPath != null) {
                    PdfViewer(File(note.localPath))
                } else if (note.contentPages.isNotEmpty()) {
                    // Fallback for offline notes with summary pages
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp)) {
                        note.contentPages.forEach { page ->
                            PdfPageSheet(page.pageNumber, page.title, page.body, page.codeSnippet, 1.0f, "", context)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("PDF content not available locally.")
                    }
                }
            }

            // Persistence and Notes
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp)) {
                LessonProgressCard(note, isCompleted, context, onSaveProgress) { isCompleted = it }
                Spacer(modifier = Modifier.height(16.dp))
                NotepadCard(note, notepadText, showNotepad, context, onSaveProgress, { notepadText = it }, { showNotepad = it })
                Spacer(modifier = Modifier.height(16.dp))
                
                // Ads
                GoogleAdMobPlaceholder()
                WbaatzAdBanner()
            }
        }
    }
}

@Composable
fun LessonProgressCard(note: PdfNote, isCompleted: Boolean, context: Context, onSaveProgress: (NoteProgress) -> Unit, onUpdateState: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Syllabus Progress", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text("Mark as completed when done.", style = MaterialTheme.typography.bodySmall)
            }
            Checkbox(checked = isCompleted, onCheckedChange = { 
                onUpdateState(it)
                onSaveProgress(NoteProgress(noteId = note.id, isCompleted = it))
                Toast.makeText(context, if (it) "Done! 🚀" else "Removed.", Toast.LENGTH_SHORT).show()
            })
        }
    }
}

@Composable
fun NotepadCard(note: PdfNote, text: String, isVisible: Boolean, context: Context, onSaveProgress: (NoteProgress) -> Unit, onTextChange: (String) -> Unit, onToggle: (Boolean) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onToggle(!isVisible) }.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EditNote, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Personal Notes", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Draft algorithms or summaries.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Icon(if (isVisible) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null, tint = Color.Gray)
        }
    }
    AnimatedVisibility(visible = isVisible) {
        Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(value = text, onValueChange = onTextChange, placeholder = { Text("Draft notes here...") }, modifier = Modifier.fillMaxWidth().height(120.dp))
                Button(onClick = {
                    onSaveProgress(NoteProgress(noteId = note.id, userNotes = text))
                    Toast.makeText(context, "Saved!", Toast.LENGTH_SHORT).show()
                }, modifier = Modifier.align(Alignment.End).padding(top = 8.dp)) { Text("Save") }
            }
        }
    }
}

@Composable
fun PdfPageSheet(pageNumber: Int, title: String, body: String, codeSnippet: String?, zoomScale: Float, searchQuery: String, context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth().shadow(4.dp, shape = RoundedCornerShape(4.dp)).border(0.5.dp, Color.LightGray, RoundedCornerShape(4.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
            Text(text = "Page $pageNumber", fontSize = (10 * zoomScale).sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
            Text(text = title, fontSize = (18 * zoomScale).sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = body, fontSize = (14 * zoomScale).sp, color = Color(0xFF212121), lineHeight = (20 * zoomScale).sp)
            codeSnippet?.let { code ->
                Spacer(modifier = Modifier.height(12.dp))
                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF2D302E), RoundedCornerShape(6.dp)).padding(12.dp)) {
                    Text(text = code, color = Color(0xFFA5D6A7), fontFamily = FontFamily.Monospace, fontSize = (11 * zoomScale).sp)
                }
            }
        }
    }
}
